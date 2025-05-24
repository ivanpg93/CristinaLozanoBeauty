package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentMessageBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class MessageFragment: Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val vm: MessageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentMessageBinding.inflate(layoutInflater)

        // Hide keyboard
        hideAutomatically(binding.root, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.datePicker.firstDayOfWeek = Calendar.MONDAY
        binding.timePicker.setIs24HourView(true)
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendMessage() {

        // Hide keyboard
        hide(binding.btnSend)

        // Get form data
        val name = binding.txtName.text.toString()
        val telephone = binding.txtTlf.text.toString()

        // Check valid telephone
        if (!isValidPhone(telephone)) {
            showError(requireContext(), "Debes introducir un número de teléfono correcto")
            return
        }

        // Build date
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        // Build time
        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute

        // Send message
        vm.actionSendMessage(name, telephone, day, month, year, hour, minute)
    }

    private fun isValidPhone(phone: String): Boolean {
        val regex = "^(6|7)\\d{8}$".toRegex()
        return phone.matches(regex)
    }

}