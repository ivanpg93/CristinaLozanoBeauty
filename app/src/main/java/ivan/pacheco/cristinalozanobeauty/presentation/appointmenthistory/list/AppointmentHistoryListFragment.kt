package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentAppointmentHistoryListBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toHour
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading

@AndroidEntryPoint
class AppointmentHistoryListFragment: Fragment() {

    private var _binding: FragmentAppointmentHistoryListBinding? = null
    private val binding get() = _binding!!
    private val vm: AppointmentHistoryListViewModel by viewModels()
    private lateinit var clientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentAppointmentHistoryListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Past appointments
        val madeAdapter = AppointmentHistoryListAdapter(
            onItemSelected = { appointment ->
                // TODO: navigate(Destination.ColorHistoryDetail(clientId, appointment.id))
            },
            onItemUpdated = { appointment -> vm.actionUpdateAppointment(appointment) },
            onItemDeleted = { appointment ->
                val (dialog, applyColors) = DialogUtils.createDialog(
                    requireContext(),
                    getString(R.string.dialog_delete_appointment_title),
                    String.format("%s - %s - %s",
                        appointment.event?.service?.toDisplayName(),
                        appointment.event?.startDateTime?.toHour(),
                        appointment.event?.startDateTime?.toDate()
                    )
                ) { vm.actionDeleteAppointment(appointment) }

                // Colors for buttons
                dialog.setOnShowListener { applyColors() }

                dialog.show()
            }
        )
        binding.rvMadeAppointments.adapter = madeAdapter

        // Pending appointments
        val pendingAdapter = AppointmentHistoryListAdapter(
            onItemSelected = { appointment ->
                // TODO: navigate(Destination.ColorHistoryDetail(clientId, appointment.id))
            },
            onItemUpdated = { appointment -> vm.actionUpdateAppointment(appointment) },
            onItemDeleted = { appointment ->
                val (dialog, applyColors) = DialogUtils.createDialog(
                    requireContext(),
                    getString(R.string.dialog_delete_appointment_title),
                    String.format("%s - %s - %s",
                        appointment.event?.service?.toDisplayName(),
                        appointment.event?.startDateTime?.toHour(),
                        appointment.event?.startDateTime?.toDate()
                    )
                ) { vm.actionDeleteAppointment(appointment) }

                // Colors for buttons
                dialog.setOnShowListener { applyColors() }

                dialog.show()
            }
        )
        binding.rvPendingAppointments.adapter = pendingAdapter

        // Load made appointments
        vm.getMadeAppointmentListLD().observe(viewLifecycleOwner) { appointments ->
            binding.txtMadeAppointmentsEmpty.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
            madeAdapter.reload(appointments)
        }

        // Load pending appointments
        vm.getPendingAppointmentListLD().observe(viewLifecycleOwner) { appointments ->
            binding.txtPendingAppointmentsEmpty.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
            pendingAdapter.reload(appointments)
        }

        // Load client id
        vm.getClientIdLD().observe(viewLifecycleOwner) { clientId = it }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}
    }

    override fun onResume() {
        super.onResume()

        // Load appointments when arrive this screen
        vm.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigate(destination: Destination) {
        when (destination) {
            else -> {} // Do nothing
        }
    }

}