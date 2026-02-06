package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentMessageReminderBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class MessageReminderFragment: Fragment() {

    private companion object {
        const val DATE_TIME_FORMATTER_PATTERN = "EEEE dd/MM/yyyy"
    }

    private var _binding: FragmentMessageReminderBinding? = null
    private val binding get() = _binding!!
    private val vm: MessageReminderViewModel by viewModels()
    private var clientList: List<ClientListDTO> = listOf()
    private var selectedClient: ClientListDTO? = null
    private var tomorrow: LocalDate = LocalDate.now().plusDays(1)
    private var selectedDate: LocalDate = tomorrow
    private val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN, Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentMessageReminderBinding.inflate(layoutInflater)

        // Hide keyboard
        hideAutomatically(binding.root, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Selected date
        setupDateSelector(tomorrow)

        // Appointments
        val appointmentAdapter = AppointmentClientListAdapter({ appointmentClient ->
            vm.actionSendAppointmentReminder(appointmentClient)
        })
        binding.rvAppointments.adapter = appointmentAdapter

        // Clients
        val birthdayAdapter = ClientBirthdayListAdapter({ clientBirthday ->
            vm.actionSendBirthdayReminder(clientBirthday)
        })
        binding.rvBirthdays.adapter = birthdayAdapter

        // Clients
        vm.getClientsLD().observe(viewLifecycleOwner) { clients -> clientList = clients }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error) }

        // Appointments
        vm.getAppointmentClientListLD().observe(viewLifecycleOwner) { list ->
            appointmentAdapter.reload(list)
        }

        // Birthdays
        vm.getClientBirthdayListLD().observe(viewLifecycleOwner) { clientBirthdays ->
            birthdayAdapter.reload(clientBirthdays)
        }

        // Button previous day
        binding.btnPreviousDay.setOnClickListener { setupDateSelector(selectedDate.minusDays(1)) }

        // Button next day
        binding.btnNextDay.setOnClickListener { setupDateSelector(selectedDate.plusDays(1)) }

        // Button send message
        binding.btnSendMessage.setOnClickListener { sendMessage(selectedClient) }

        // Input select client
        setupClientSelector(
            binding.etSelectedClientText,
            { selectedClient },
            { selectedClient = it }
        )

        // Load appointments from tomorrow by default
        vm.loadAppointmentsForDate(selectedDate)

        vm.loadBirthdaysForDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applySelectedDate() {
        val formatted = selectedDate.format(formatter)
        binding.txtSelectedDate.text = formatted.replaceFirstChar { it.uppercase() }
    }

    private fun sendMessage(client: ClientListDTO?) {

        // Hide keyboard
        hide(binding.btnSendMessage)

        if (client == null) {
            showError(requireContext(), getString(R.string.message_reminder_select_client))
            return
        }

        // Send message
        vm.actionSendNextAppointmentReminder(client)
    }

    private fun setupDateSelector(newDate: LocalDate) {
        selectedDate = newDate
        applySelectedDate()
        vm.loadAppointmentsForDate(selectedDate)

        // Manage visibility of previous button
        if (selectedDate.isAfter(tomorrow)) {
            binding.btnPreviousDay.visibility = View.VISIBLE
        } else {
            binding.btnPreviousDay.visibility = View.INVISIBLE
        }
    }

    private fun setupClientSelector(
        editText: EditText,
        getSelectedClient: () -> ClientListDTO?,
        onSelected: (ClientListDTO) -> Unit
    ) {
        editText.setOnClickListener {
            showClientChoiceDialog(clientList, getSelectedClient(), onSelected) { client ->
                editText.setText(String.format("%s %s", client.firstName, client.lastName))
            }
        }
    }

    private fun showClientChoiceDialog(
        clients: List<ClientListDTO>,
        selectedClient: ClientListDTO?,
        onSelected: (ClientListDTO) -> Unit,
        onDisplayText: (ClientListDTO) -> Unit
    ) {
        val sortedClients = clients.sortedBy { it.firstName + it.lastName }
        val clientNames = sortedClients.map { "${it.firstName} ${it.lastName}" }.toTypedArray()
        val selectedIndex = sortedClients.indexOfFirst { it.id == selectedClient?.id }
        var tempSelectedIndex = selectedIndex

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_calendar_event_select_client))
            .setSingleChoiceItems(clientNames, selectedIndex) { _, index ->
                tempSelectedIndex = index
            }
            .setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                tempSelectedIndex.takeIf { it >= 0 }?.let { i ->
                    val client = sortedClients[i]
                    onSelected(client)
                    onDisplayText(client)
                }
                dialog.dismiss()
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

}