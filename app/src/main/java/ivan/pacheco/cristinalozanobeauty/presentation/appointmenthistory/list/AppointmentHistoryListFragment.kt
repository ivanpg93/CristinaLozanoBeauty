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
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
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

        val adapter = AppointmentHistoryListAdapter(
            onItemSelected = { appointment -> navigate(Destination.ColorHistoryDetail(clientId, appointment.id)) },
            onItemDeleted = { appointment ->
                DialogUtils.createDialog(
                    requireContext(),
                    getString(R.string.dialog_delete_appointment_title),
                    getString(R.string.dialog_delete_appointment_message)
                ) { vm.actionDeleteAppointment(appointment) }
            }
        )
        binding.rvColors.adapter = adapter

        // Load colors
        vm.getAppointmentListLD().observe(viewLifecycleOwner) { colors -> adapter.reload(colors) }

        // Load client id
        vm.getClientIdLD().observe(viewLifecycleOwner) { clientId = it }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}

        // Action add client
        //binding.btnAddColor.setOnClickListener { navigate(Destination.ColorHistoryForm(clientId)) }
    }

    override fun onResume() {
        super.onResume()

        // Load clients when arrive this screen
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