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

        val adapter = ClientListAdapter(onItemSelected = { client ->
            navigate(Destination.ClientDetail(client.id))
        })
        binding.rvClients.adapter = adapter

        // Load clients
        vm.getClientsLD().observe(viewLifecycleOwner) { clients -> adapter.reload(clients) }

        // Action add client
        binding.btnAddClient.setOnClickListener { navigate(Destination.ClientForm) }
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