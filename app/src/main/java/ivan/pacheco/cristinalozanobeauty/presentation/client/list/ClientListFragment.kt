package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.ClientListFragmentBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading

@AndroidEntryPoint
class ClientListFragment: Fragment() {

    private var _binding: ClientListFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = ClientListFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ClientListAdapter(
            onItemSelected = { client -> navigate(Destination.ClientDetail(client.id)) },
            onItemDeleted = { client ->
                DialogUtils.createDialog(
                    requireContext(),
                    getString(R.string.dialog_delete_client_title),
                    getString(R.string.dialog_delete_client_message)
                ) { vm.actionDeleteClient(client) }
            }
        )
        binding.rvClients.adapter = adapter

        // Load clients
        vm.getClientsLD().observe(viewLifecycleOwner) { clients -> adapter.reload(clients) }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}

        // Action add client
        binding.btnAddClient.setOnClickListener { navigate(Destination.ClientForm) }
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
            is Destination.ClientDetail -> {
                findNavController().navigate(
                    ClientListFragmentDirections.actionClientListFragmentToClientDetailFragment(
                        destination.clientId
                    )
                )
            }
            is Destination.ClientForm -> { findNavController().navigate(R.id.clientFormFragment) }
            else -> {} // Do nothing
        }
    }

}