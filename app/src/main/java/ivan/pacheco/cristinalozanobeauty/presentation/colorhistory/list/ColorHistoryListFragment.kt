package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentColorHistoryListBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading

@AndroidEntryPoint
class ColorHistoryListFragment: Fragment() {

    private var _binding: FragmentColorHistoryListBinding? = null
    private val binding get() = _binding!!
    private val vm: ColorHistoryListViewModel by viewModels()
    private lateinit var clientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentColorHistoryListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ColorHistoryListAdapter(
            onItemSelected = { color -> navigate(Destination.ColorHistoryDetail(clientId, color.id)) },
            onItemDeleted = { color ->
                val (dialog, applyColors) = DialogUtils.createDialog(
                    requireContext(),
                    getString(R.string.dialog_delete_color_title),
                    String.format("%s - %s", color.brand?.toDisplayName(), color.reference)
                ) { vm.actionDeleteColor(color) }

                // Colors for buttons
                dialog.setOnShowListener { applyColors() }

                dialog.show()
            }
        )
        binding.rvColors.adapter = adapter

        // Load colors
        vm.getColorsLD().observe(viewLifecycleOwner) { colors -> adapter.reload(colors) }

        // Load client id
        vm.getClientIdLD().observe(viewLifecycleOwner) { clientId = it }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}

        // Action add client
        binding.btnAddColor.setOnClickListener { navigate(Destination.ColorHistoryForm(clientId)) }
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
            is Destination.ColorHistoryDetail -> {
                findNavController().navigate(
                    ColorHistoryListFragmentDirections.actionColorHistoryListFragmentToColorsHistoryDetailFragment(
                        destination.clientId,
                        destination.colorId
                    )
                )
            }
            is Destination.ColorHistoryForm -> {
                findNavController().navigate(
                    ColorHistoryListFragmentDirections.actionColorHistoryListFragmentToColorsHistoryFormFragment(
                        destination.clientId
                    )
                )
            }
            else -> {} // Do nothing
        }
    }

}