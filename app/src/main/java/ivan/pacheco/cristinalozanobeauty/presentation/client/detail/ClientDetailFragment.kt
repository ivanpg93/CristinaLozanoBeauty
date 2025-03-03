package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ivan.pacheco.cristinalozanobeauty.databinding.ClientDetailFragmentBinding

class ClientDetailFragment: Fragment() {

    private var _binding: ClientDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClientDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getClientLD().observe(viewLifecycleOwner) { client ->
            setActionBarTitle("${client.firstName} ${client.lastName}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setActionBarTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

}