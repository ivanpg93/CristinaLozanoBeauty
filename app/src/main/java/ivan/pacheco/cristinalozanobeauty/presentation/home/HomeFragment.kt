package ivan.pacheco.cristinalozanobeauty.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.toEvent
import ivan.pacheco.cristinalozanobeauty.databinding.Example3CalendarDayBinding
import ivan.pacheco.cristinalozanobeauty.databinding.Example3CalendarHeaderBinding
import ivan.pacheco.cristinalozanobeauty.databinding.Example3FragmentBinding
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.Event
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.Example3EventsAdapter
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.addStatusBarColorUpdate
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.dpToPx
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.getColorCompat
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.inputMethodManager
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.makeGone
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.makeInVisible
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.makeVisible
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.example_3_fragment) {

    private var _binding: Example3FragmentBinding? = null
    private val binding get() = _binding!!
    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.example_3_dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                vm.deleteEvent(it.id)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private var selectedDate: LocalDate = LocalDate.now()
    private val today = LocalDate.now()

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()
    private val vm: HomeViewModel by viewModels()

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var client: GoogleSignInClient
    private val SIGN_IN_REQUEST_CODE = 1001
    private val RECOVERABLE_REQUEST_CODE = 2001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = Example3FragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoogleSignIn()
        silentSignIn()

        vm.getEventsLD().observe(viewLifecycleOwner) { eventList ->
            // Limpia el mapa actual
            events.clear()

            // Agrupa los eventos por fecha
            eventList.groupBy { it.startDateTime.toLocalDate() }
                .forEach { (date, eventsForDate) ->
                    events[date] = eventsForDate.map { it.toEvent() }
                }

            // Notifica al calendario que algo cambió
            binding.exThreeCalendar.notifyCalendarChanged()

            // Si ya hay una fecha seleccionada, actualiza el adaptador
            updateAdapterForDate(selectedDate)
        }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error) }

        vm.getRecoverableExceptionLD().observe(viewLifecycleOwner) { exception ->
            exception.intent?.let { startActivityForResult(it, RECOVERABLE_REQUEST_CODE) }
        }

        // Calendar
        addStatusBarColorUpdate(R.color.gold)
        applyInsets(binding)
        binding.exThreeRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        binding.exThreeCalendar.monthScrollListener = {
            binding.toolbar.title = if (it.yearMonth.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth).replaceFirstChar { letter -> letter.titlecase() }
            } else {
                titleFormatter.format(it.yearMonth)
            }
            // Select the first day of the visible month.
            selectDate(it.yearMonth.atDay(1))
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)
        val endMonth = currentMonth.plusMonths(50)
        configureBinders(daysOfWeek)
        binding.exThreeCalendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            // Show today's events initially.
            binding.exThreeCalendar.post { selectDate(today) }
        }
        binding.exThreeAddButton.setOnClickListener { selectedDate?.let { showAddEventDialog(it) } }

        // Aplicar insets si quieres
        applyInsets(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("217437853819-ohqt67v4ioejvkjvkha2dlmj6gh1sr36.apps.googleusercontent.com")
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
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
                } catch (e: ApiException) {
                    startActivityForResult(client.signInIntent, SIGN_IN_REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SIGN_IN_REQUEST_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    vm.onGoogleAccountReady(account)
                } catch (e: ApiException) {
                    Toast.makeText(
                        requireContext(),
                        "Fallo al iniciar sesión: ${e.statusCode}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            RECOVERABLE_REQUEST_CODE -> {
                // Intenta de nuevo obtener el token tras el consentimiento
                GoogleSignIn.getLastSignedInAccount(requireContext())?.let { account ->
                    vm.onGoogleAccountReady(account)
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
            vm.onDateSelected(selectedDate.toString())
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

    override fun onStart() {
        super.onStart()

        // Configurar el AppBar y Toolbar del fragmento
        val color = requireContext().getColorCompat(R.color.gold)
        binding.toolbar.setBackgroundColor(color)
        binding.activityAppBar.setBackgroundColor(color)
        binding.activityAppBar.makeVisible()
        binding.activityAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> { height = WRAP_CONTENT }
    }

    override fun onStop() {
        super.onStop()

        // Resetear colores o visibilidad si quieres
        val color = requireContext().getColorCompat(R.color.gold)
        binding.toolbar.setBackgroundColor(color)
        binding.activityAppBar.setBackgroundColor(color)

        // Aquí decides si ocultar el appBar o dejar visible
        binding.activityAppBar.makeGone()
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = Example3CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.exThreeCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    when (data.date) {
                        today -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.makeInVisible()
                        }

                        selectedDate -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.makeInVisible()
                        }

                        else -> {
                            textView.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                            textView.background = null
                            dotView.isVisible = events[data.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.exThreeCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = true
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.first().toString()
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

    private fun applyInsets(binding: Example3FragmentBinding) {
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

    private fun showAddEventDialog(selectedDate: LocalDate) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_event, null)

        val titleEditText = dialogView.findViewById<EditText>(R.id.eventTitleEditText)
        val startTimeEditText = dialogView.findViewById<EditText>(R.id.startTimeEditText)
        val endTimeEditText = dialogView.findViewById<EditText>(R.id.endTimeEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo evento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = titleEditText.text.toString()
                val startTime = startTimeEditText.text.toString()
                val endTime = endTimeEditText.text.toString()

                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val start = LocalDateTime.of(selectedDate, LocalTime.parse(startTime, formatter))
                val end = LocalDateTime.of(selectedDate, LocalTime.parse(endTime, formatter))

                val newEvent = CalendarEvent(
                    id = UUID.randomUUID().toString(),
                    summary = title,
                    description = null,
                    startDateTime = start.toString(),
                    endDateTime = end.toString()
                )

                // Llama a tu método para guardarlo (en VM o directamente)
                vm.saveEvent(newEvent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


}
