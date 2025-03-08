package ivan.pacheco.cristinalozanobeauty.presentation.client.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.ClientFormFragmentBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.getTrimmedText
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.isCorrectMobilePhone
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class ClientFormFragment: Fragment() {

    private var _binding: ClientFormFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientFormViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = ClientFormFragmentBinding.inflate(layoutInflater)

        // Hide keyboard
        hideAutomatically(binding.root, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable register button only if all form fields are filled
        val requiredFields = listOf(binding.etName, binding.etLastName, binding.etPhone, binding.etEmail)
        FormUtils.automaticFormButton(requiredFields, binding.btnSave)

        // Navigation
        vm.navigationLD.observe(viewLifecycleOwner) { destination -> navigate(destination) }

        // Button save client
        binding.btnSave.setOnClickListener { saveAction() }

        binding.etPhoneText.addTextChangedListener(object : TextWatcher {

            // Do nothing
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefix = getString(R.string.client_form_prefix_phone)
                if (!s.toString().startsWith(prefix)) {
                    binding.etPhoneText.setText(prefix)

                    // Mueve el cursor al final para escribir a partir del prefijo
                    binding.etPhoneText.setSelection(binding.etPhoneText.text?.length ?: 0)
                }
            }

            // Do nothing
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveAction() {

        if (!validateForm()) return

        // Hide keyboard
        hide(binding.btnSave)

        vm.actionSave(
            binding.etNameText.getTrimmedText(),
            binding.etLastNameText.getTrimmedText(),
            binding.etPhoneText.getTrimmedText(),
            binding.etEmailText.getTrimmedText(),
        )
    }

    private fun validateForm(): Boolean {
        return binding.etPhoneText.isCorrectMobilePhone()
    }

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}