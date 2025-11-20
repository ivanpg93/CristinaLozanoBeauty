package ivan.pacheco.cristinalozanobeauty.presentation.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.BuildConfig
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEventDTO
import ivan.pacheco.cristinalozanobeauty.databinding.CalendarDayBinding
import ivan.pacheco.cristinalozanobeauty.databinding.CalendarHeaderBinding
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentHomeBinding
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.CalendarEventListAdapter
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.EventsAdapter
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.getColorCompat
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.makeInVisible
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.makeVisible
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toEpochMillisForDatePicker
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toFormattedString
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDateFromDatePicker
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.normalizeForSearch
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showAlert
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.MonthYearPickerDialog
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {

    private companion object {
        const val SCOPE_GOOGLE_CALENDAR = "https://www.googleapis.com/auth/calendar"
        const val TIME_FORMAT = "HH:mm"
        const val LIMIT_MAX_EVENTS_FOR_CELL = 5
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val today = LocalDate.now()
    private var selectedDate: LocalDate = today
    private val titleFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    private val eventsAdapter = EventsAdapter(
        onDetailClick = { event ->
            Firestore.db.collection("eventsIndex") // TODO: Clean architecture
                .document(event.id)
                .get()
                .addOnSuccessListener { indexDoc ->
                    indexDoc.getString("clientId")?.let { clientId ->
                        navigate(Destination.ClientDetail(clientId))
                    }
                }
        },
        onClick = { event -> showEventDialog(selectedDate, event) },
        assistedAction = { event -> vm.actionUpdateEvent(event) },
        deleteAction = { event ->

            // Create dialog
            val (dialog, applyColors) = DialogUtils.createDialog(
                requireContext(),
                getString(R.string.dialog_calendar_event_delete_message),
                String.format("%s - %s", event.text, event.startTime)
            ) { vm.actionDeleteEvent(event.id) }

            // Customize listener for buttons
            dialog.setOnShowListener {

                // Colors for buttons
                applyColors()

                // Positive action
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    dialog.dismiss()
                    vm.actionDeleteEvent(event.id)
                }
            }

            // Show dialog
            dialog.show()
        }
    )

    private val events = mutableMapOf<LocalDate, List<CalendarEventDTO>>()
    private val vm: HomeViewModel by viewModels()
    private var clientList: List<ClientListDTO> = listOf()
    private var selectedClient: ClientListDTO? = null

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var client: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var recoverableLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launcher for login
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    vm.onGoogleAccountReady(account)
                } catch (e: ApiException) {
                    Toast.makeText(requireContext(), "Fallo al iniciar sesión: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Launcher for retry login
        recoverableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

            // Get token when return
            GoogleSignIn.getLastSignedInAccount(requireContext())?.let { account ->
                vm.onGoogleAccountReady(account)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Google login from device account
        initGoogleSignIn()
        silentSignIn()

        // Events
        vm.getEventsLD().observe(viewLifecycleOwner) { eventList ->

            // Clear list
            events.clear()

            // Group events by date
            eventList.groupBy { it.startDateTime.toLocalDate() }
                .forEach { (date, eventsForDate) ->
                    events[date] = eventsForDate.map { it.mapToDTO() }
                }

            binding.exThreeCalendar.notifyCalendarChanged()
            updateAdapterForDate(selectedDate)
        }

        // Clients
        vm.getClientsLD().observe(viewLifecycleOwner) { clients -> clientList = clients }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error) }

        // Retry Google Auth
        vm.getRecoverableExceptionLD().observe(viewLifecycleOwner) { exception ->
            exception.intent?.let { recoverableLauncher.launch(it) }
        }

        // Calendar
        applyInsets(binding)
        binding.toolbar.inflateMenu(R.menu.calendar_menu)

        // Select month of year and load events for that month
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_select_date -> {
                    val dialog = MonthYearPickerDialog(
                        selectedDate.year,
                        selectedDate.monthValue
                    ) { year, month ->
                        val localDate = LocalDate.of(year, month, 1)
                        selectedDate = localDate
                        binding.exThreeCalendar.scrollToDate(selectedDate)
                        selectDate(selectedDate)
                        vm.actionLoadEvents(selectedDate)
                    }
                    dialog.show(childFragmentManager, "")
                    true
                }
                else -> false
            }
        }

        // List of events of day
        binding.exThreeRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        // Horizontal scroll for change month view
        binding.exThreeCalendar.monthScrollListener = { month ->
            binding.toolbar.title = titleFormatter.format(month.yearMonth).replaceFirstChar { letter -> letter.titlecase() }

            // Select current day for current month or first day of month
            val dateToSelect = when {
                selectedDate.year == month.yearMonth.year && selectedDate.month == month.yearMonth.month -> selectedDate
                month.yearMonth == YearMonth.from(today) -> today
                else -> month.yearMonth.atDay(1)
            }

            selectDate(dateToSelect)
            vm.actionLoadEvents(selectedDate)
        }

        // Setup calendar
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(60)
        val endMonth = currentMonth.plusMonths(60)
        configureBinders(daysOfWeek)
        binding.exThreeCalendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        // Button dialog create event
        binding.btnCreateEvent.setOnClickListener { showEventDialog(selectedDate) }
    }

    override fun onStart() {
        super.onStart()

        // Set app bar and toolbar
        val color = requireContext().getColorCompat(R.color.gold)
        binding.toolbar.setBackgroundColor(color)
        binding.activityAppBar.setBackgroundColor(color)
        binding.activityAppBar.makeVisible()
        binding.activityAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> { height = WRAP_CONTENT }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_ACCESS_TOKEN)
            .requestEmail()
            .requestScopes(Scope(SCOPE_GOOGLE_CALENDAR))
            .build()
        client = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    private fun silentSignIn() {
        val task = client.silentSignIn()
        if (task.isSuccessful) {
            val account = task.result
            vm.onGoogleAccountReady(account)
        } else {
            task.addOnCompleteListener { completedTask ->
                try {
                    val account = completedTask.getResult(ApiException::class.java)
                    vm.onGoogleAccountReady(account)
                } catch (_: ApiException) {
                    client.signOut().addOnCompleteListener {
                        if (isAdded) {
                            signInLauncher.launch(client.signInIntent)
                        }
                    }
                }
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(this@HomeFragment.events[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        @SuppressLint("ClickableViewAccessibility")
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)
            val eventsAdapter = CalendarEventListAdapter().also { binding.rvEvents.adapter = it }

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }

                // Propagate click to root of day cell
                // False to continues to handle scrolling
                binding.rvEvents.setOnTouchListener { _, event ->
                    binding.root.performClick()
                    false
                }
            }
        }

        binding.exThreeCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val dayOfMonth = container.binding.exThreeDayText
                val rvAdapter = container.eventsAdapter
                val extraEvents = container.binding.txtExtraEvents

                dayOfMonth.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    dayOfMonth.makeVisible()

                    // Get events from that day
                    val dayEvents = events[data.date] ?: emptyList()
                    val eventsForDay = dayEvents.map { it.mapToClientListDTO() }

                    // Show limit events
                    rvAdapter.reload(eventsForDay.take(LIMIT_MAX_EVENTS_FOR_CELL))

                    // Manage visibility of extra events
                    val extraCount = eventsForDay.size - LIMIT_MAX_EVENTS_FOR_CELL
                    extraEvents.text = if (extraCount > 0) "+$extraCount" else ""
                    extraEvents.visibility = if (extraCount > 0) View.VISIBLE else View.GONE

                    when (data.date) {
                        selectedDate -> {
                            dayOfMonth.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            dayOfMonth.setBackgroundResource(R.drawable.calendar_selected_day_background)
                        }

                        today -> {
                            dayOfMonth.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            dayOfMonth.setBackgroundResource(R.drawable.calendar_today_background)
                        }

                        else -> {
                            dayOfMonth.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                            dayOfMonth.background = null
                        }
                    }
                } else {
                    dayOfMonth.makeInVisible()
                    rvAdapter.reload(emptyList())
                    extraEvents.makeInVisible()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.exThreeCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {

                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = true
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index]
                                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                .replaceFirstChar { it.uppercase() }
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                        }
                }
            }
        }
    }

    private fun applyInsets(binding: FragmentHomeBinding) {
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root,
        ) { _, windowInsets ->
            val insets = windowInsets.getInsets(systemBars())
            binding.root.updatePadding(
                left = insets.left,
                right = insets.right,
                bottom = insets.bottom,
            )
            windowInsets
        }
    }

    private fun showEventDialog(selectedDate: LocalDate, event: CalendarEventDTO? = null) {
        val context = requireContext()
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_event, null)

        val dateInput = dialogView.findViewById<EditText>(R.id.et_event_date_text)
        val startTimeInput = dialogView.findViewById<EditText>(R.id.et_event_start_time_text)
        val endTimeInput = dialogView.findViewById<EditText>(R.id.et_event_end_time_text)
        val clientInput = dialogView.findViewById<EditText>(R.id.et_selected_client_text)
        val serviceInput = dialogView.findViewById<EditText>(R.id.et_selected_service_text)
        var selectedService: Appointment.Service? = null
        var oldClient: ClientListDTO? = null

        var currentDate = event?.date ?: selectedDate
        dateInput.setText(currentDate.toFormattedString())

        // Input select date
        dateInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.FormDatePicker)
                .setSelection(currentDate.toEpochMillisForDatePicker())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()

            // Set selected date
            datePicker.addOnPositiveButtonClickListener { millis ->
                val localDate = millis.toLocalDateFromDatePicker()
                currentDate = localDate
                dateInput.setText(localDate.toFormattedString())
            }

            datePicker.show(childFragmentManager, "")
        }

        // Input start time
        startTimeInput.setOnClickListener {
            showTimePicker { hour, minute -> startTimeInput.setText(String.format("%02d:%02d", hour, minute)) }
        }

        // Input end time
        endTimeInput.setOnClickListener {
            showTimePicker { hour, minute -> endTimeInput.setText(String.format("%02d:%02d", hour, minute)) }
        }

        // Input select client
        setupClientSelector(
            clientInput,
            clientList,
            { selectedClient },
            { selectedClient = it }
        )

        // Input select service
        setupSingleChoiceInput(
            serviceInput,
            R.string.dialog_calendar_event_select_service,
            Appointment.Service.entries.toTypedArray(),
            { selectedService }
        ) { selectedService = it }

        if (event != null) {
            dateInput.setText(event.date.toFormattedString())
            startTimeInput.setText(event.startTime.toString())
            endTimeInput.setText(event.endTime.toString())

            // Identify selected original client
            oldClient = clientList.find { client ->
                "${client.firstName} ${client.lastName}" == event.text.substringBefore(" - ")
            }
            selectedClient = oldClient
            clientInput.setText(oldClient?.let { "${it.firstName} ${it.lastName}" })

            // Identify selected service
            selectedService = event.service
            serviceInput.setText(event.service?.toDisplayName())
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.dialog_calendar_event_title))
            .setView(dialogView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.dialog_calendar_event_action_save), null)
            .create()

        dialog.setOnShowListener {

            // Customize color for buttons
            val goldColor = ContextCompat.getColor(context, R.color.gold)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(goldColor)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(goldColor)

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val clientName = listOfNotNull(selectedClient?.firstName, selectedClient?.lastName)
                    .joinToString(" ")
                val title = "$clientName - ${selectedService?.toDisplayName()}"
                val startTime = startTimeInput.text.toString()
                val endTime = endTimeInput.text.toString()

                if (title.isBlank() || startTime.isBlank() || endTime.isBlank()) {
                    showAlert(R.string.client_form_error_mandatory_fields)
                    return@setOnClickListener
                }

                if (selectedClient == null) {
                    showAlert(R.string.dialog_calendar_event_select_client)
                    return@setOnClickListener
                }

                if (selectedService == null) {
                    showAlert(R.string.dialog_calendar_event_select_service)
                    return@setOnClickListener
                }

                try {
                    val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
                    val startTime = LocalDateTime.of(currentDate, LocalTime.parse(startTime, formatter))
                    val endTime = LocalDateTime.of(currentDate, LocalTime.parse(endTime, formatter))

                    if (!startTime.isBefore(endTime)) {
                        showAlert(R.string.calendar_event_form_error_time)
                        return@setOnClickListener
                    }

                    // Check if event exists for update or create it
                    if (event != null) {
                        if (oldClient?.id != selectedClient?.id) {

                            // Delete event related to old client
                            vm.actionDeleteEvent(event.id)

                            // Create event related to new selected client
                            val newEvent = CalendarEventDTO(
                                id = "",
                                text = title,
                                date = currentDate,
                                startTime = startTime.toLocalTime(),
                                endTime = endTime.toLocalTime(),
                                service = selectedService,
                            )
                            selectedClient?.let { selectedClient ->
                                vm.actionCreateEvent(newEvent, selectedClient)
                            }
                        } else {

                            // Update event if selected client has not changed
                            val updatedEvent = event.copy(
                                text = "$clientName - ${selectedService.toDisplayName()}",
                                date = currentDate,
                                startTime = LocalTime.parse(
                                    startTimeInput.text.toString(),
                                    formatter
                                ),
                                endTime = LocalTime.parse(endTimeInput.text.toString(), formatter),
                                service = selectedService
                            )
                            vm.actionUpdateEvent(updatedEvent)
                        }
                    } else {

                        // Create new event if not exists
                        val newEvent = CalendarEventDTO(
                            id = "",
                            text = title,
                            date = currentDate,
                            startTime = startTime.toLocalTime(),
                            endTime = endTime.toLocalTime(),
                            service = selectedService,
                        )
                        selectedClient?.let { selectedClient ->
                            vm.actionCreateEvent(newEvent, selectedClient)
                        }
                    }
                    dialog.dismiss()
                } catch (_: Exception) { showAlert(R.string.calendar_event_form_error_time) }
            }
        }
        dialog.show()
    }

    private fun setupClientSelector(
        editText: EditText,
        clients: List<ClientListDTO>,
        getSelectedClient: () -> ClientListDTO?,
        onSelected: (ClientListDTO) -> Unit
    ) {
        editText.setOnClickListener {
            showClientChoiceDialog(clients, getSelectedClient(), onSelected) { fullName ->
                editText.setText(fullName)
            }
        }
    }

    private fun showClientChoiceDialog(
        clients: List<ClientListDTO>,
        selectedClient: ClientListDTO?,
        onSelected: (ClientListDTO) -> Unit,
        onDisplayText: (String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_client_searchable, null)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val listView = view.findViewById<ListView>(R.id.clientListView)

        // Client list sorted by name
        val sortedClients = clients.sortedBy { it.firstName + it.lastName }

        // List representing what is currently displayed on screen
        val displayedClients = sortedClients.toMutableList()

        // Create client list
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_client_choice,
            displayedClients.map { "${it.firstName} ${it.lastName}" }
        )
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // Select current client if exists
        val selectedIndexInSorted = sortedClients.indexOfFirst { it.id == selectedClient?.id }
        if (selectedIndexInSorted >= 0) {
            listView.setItemChecked(selectedIndexInSorted, true)
        }

        // Real-time filtering: updating displayedClients + adapter
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty().trim()
                displayedClients.clear()
                if (query.isEmpty()) {
                    displayedClients.addAll(sortedClients)
                } else {
                    val normalized = query.trim().normalizeForSearch()
                    displayedClients.addAll(
                        sortedClients.filter { client ->
                            val fullName = "${client.firstName} ${client.lastName}"
                            fullName.normalizeForSearch().contains(normalized) ||
                                    client.phone.contains(normalized)
                        }
                    )
                }

                // Update adapter
                adapter.clear()
                adapter.addAll(displayedClients.map { "${it.firstName} ${it.lastName}" })
                adapter.notifyDataSetChanged()

                // Reset item selected
                listView.clearChoices()

                // If previously selected client is still in new list, select it again
                selectedClient?.let { sel ->
                    val newIndex = displayedClients.indexOfFirst { it.id == sel.id }
                    if (newIndex >= 0) listView.setItemChecked(newIndex, true)
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_calendar_event_select_client))
            .setView(view)
            .setPositiveButton(getString(R.string.accept)) { dialogInterface, _ ->
                val checkedPosition = listView.checkedItemPosition
                if (checkedPosition >= 0 && checkedPosition < displayedClients.size) {
                    val client = displayedClients[checkedPosition]
                    onSelected(client)
                    onDisplayText("${client.firstName} ${client.lastName}")
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        // Customize color for buttons
        dialog.setOnShowListener {
            val goldColor = ContextCompat.getColor(requireContext(), R.color.gold)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(goldColor)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(goldColor)
        }

        dialog.show()
    }

    private fun <T : Enum<T>> setupSingleChoiceInput(
        editText: EditText,
        titleResId: Int,
        options: Array<T>,
        getSelectedOption: () -> T?,
        onSelected: (T) -> Unit
    ) {
        editText.setOnClickListener {
            showSingleChoiceDialog(
                getString(titleResId),
                options,
                getSelectedOption()
            ) { selected ->
                onSelected(selected)
                editText.setText(
                    selected.name
                        .replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                )
            }
        }
    }

    private fun <T: Enum<T>> showSingleChoiceDialog(
        title: String,
        enumValues: Array<T>,
        selectedOption: T?,
        onSelected: (T) -> Unit
    ) {
        val options = enumValues
            .map {
                it to it.name
                    .replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { c -> c.titlecase() }
            }
            .sortedBy { it.second }

        val selectedIndex = options.indexOfFirst { it.first == selectedOption }
        var tempSelectedIndex = selectedIndex

        val checkedColor = ContextCompat.getColor(requireContext(), R.color.gold)
        val uncheckedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(checkedColor, uncheckedColor)
        )

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(options.map { it.second }.toTypedArray(), selectedIndex) { _, index ->
                tempSelectedIndex = index
            }
            .setPositiveButton(getString(R.string.accept)) { dialogInterface, _ ->
                tempSelectedIndex.takeIf { it >= 0 }?.let {
                    onSelected(options[it].first)
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        // Customize color for buttons
        dialog.setOnShowListener {
            val goldColor = ContextCompat.getColor(requireContext(), R.color.gold)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(goldColor)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(goldColor)
        }

        dialog.show()
    }

    private fun showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val now = LocalTime.now()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(now.hour)
            .setMinute(now.minute)
            .setTheme(R.style.FormTimePicker)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()

        // Set selected time
        timePicker.addOnPositiveButtonClickListener { onTimeSelected(timePicker.hour, timePicker.minute) }

        timePicker.show(childFragmentManager, "")
    }

    private fun navigate(destination: Destination) {
        when (destination) {
            is Destination.ClientDetail -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionClientListFragmentToClientDetailFragment(
                        destination.clientId
                    )
                )
            }
            else -> {} // Do nothing
        }
    }

}