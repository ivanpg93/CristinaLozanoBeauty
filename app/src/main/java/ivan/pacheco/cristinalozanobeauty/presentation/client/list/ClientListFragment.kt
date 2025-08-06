package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentClientListBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

@AndroidEntryPoint
class ClientListFragment: Fragment() {

    private var _binding: FragmentClientListBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentClientListBinding.inflate(layoutInflater)

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

        // Search clients listener
        binding.etSearch.addTextChangedListener { vm.onSearchQueryChanged(it.toString()) }
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

    private fun migrateFieldClients(oldField: String, newField: String) {
        val clientsRef = Firestore.db.collection("clients")

        clientsRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val value = document.get(oldField)
                if (value != null) {
                    val updates = mapOf(
                        newField to value,
                        oldField to FieldValue.delete()
                    )
                    document.reference.update(updates)
                }
            }
        }
    }

    private fun migrateFieldClientsToArray(oldField: String, newField: String) {
        val clientsRef = Firestore.db.collection("clients")

        clientsRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val value = document.get(oldField)
                if (value != null && value.toString().isNotEmpty()) {
                    val updates = mapOf(
                        newField to listOf(value),
                        oldField to FieldValue.delete()
                    )
                    document.reference.update(updates)
                }
            }
        }
    }

}