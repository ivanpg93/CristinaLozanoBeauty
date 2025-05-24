package ivan.pacheco.cristinalozanobeauty.presentation.client.form

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentClientFormBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.getTrimmedText
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.isCorrectMobilePhone
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showAlert
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class ClientFormFragment: Fragment() {

    private var _binding: FragmentClientFormBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientFormViewModel by viewModels()
    private val selectedNailDisorders = mutableSetOf<NailDisorder>()
    private val selectedSkinDisorders = mutableSetOf<SkinDisorder>()
    private val selectedServices = mutableSetOf<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentClientFormBinding.inflate(layoutInflater)

        // Hide keyboard
        hideAutomatically(binding.root, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        vm.navigationLD.observe(viewLifecycleOwner) { destination -> navigate(destination) }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error)}

        // Button save client
        binding.btnSave.setOnClickListener { saveAction() }

        // Input phone
        val prefix = getString(R.string.client_form_prefix_phone)
        binding.etPhoneText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val text = binding.etPhoneText.text?.toString()
                if (text.isNullOrEmpty()) {
                    binding.etPhoneText.setText(prefix)
                    binding.etPhoneText.setSelection(prefix.length)
                }
            }
        }

        // Input date picker
        binding.etBirthdayText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.FormDatePicker)
                .setSelection(getInitialDate(binding.etBirthdayText.getTrimmedText()))
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build()

            datePicker.show(childFragmentManager, "")

            // Set selected date
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                binding.etBirthdayText.setText(DateUtils.formatDate(selectedDate))
            }
        }

        // Input nail disorder
        setupMultiChoiceInput(
            binding.etNailDisorderText,
            R.string.client_form_select_nail_disorder,
            NailDisorder.entries.toTypedArray(),
            selectedNailDisorders
        )

        // Input skin disorder
        setupMultiChoiceInput(
            binding.etSkinDisorderText,
            R.string.client_form_select_skin_disorder,
            SkinDisorder.entries.toTypedArray(),
            selectedSkinDisorders
        )

        // Input service
        setupMultiChoiceInput(
            binding.etServiceText,
            R.string.client_form_select_service,
            Service.entries.toTypedArray(),
            selectedServices
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * If there is value, it is displayed.
     * If not, get date from 18 years ago is displayed
     */
    private fun getInitialDate(dateStr: String): Long? {
        return dateStr.takeIf { it.isNotBlank() }?.let { DateUtils.parseDate(it)?.time }
    }

    private fun saveAction() {

        // Hide keyboard
        hide(binding.btnSave)

        // Input diabetes selector
        val hasDiabetes = when (binding.rgHasDiabetes.checkedRadioButtonId) {
            R.id.rb_diabetes_yes -> true
            R.id.rb_diabetes_no -> false
            else -> null
        }

        // Input poor coagulation selector
        val hasPoorCoagulation = when (binding.rgHasPoorCoagulation.checkedRadioButtonId) {
            R.id.rb_poor_coagulation_yes -> true
            R.id.rb_poor_coagulation_no -> false
            else -> null
        }

        // Validate phone
        if (!validatePhone()) {
            showAlert(R.string.client_form_error_phone)
            return
        }

        // Check mandatory fields
        if (hasDiabetes == null || hasPoorCoagulation == null) {
            showAlert(R.string.client_form_error_mandatory_fields)
            return
        }

        // Create client action
        vm.actionSave(
            binding.etNameText.getTrimmedText(),
            binding.etLastNameText.getTrimmedText(),
            binding.etPhoneText.getTrimmedText(),
            binding.etEmailText.getTrimmedText(),
            DateUtils.parseDate(binding.etBirthdayText.getTrimmedText()),
            binding.etProfessionText.getTrimmedText(),
            binding.etTownText.getTrimmedText(),
            selectedNailDisorders.toList(),
            selectedSkinDisorders.toList(),
            selectedServices.toList(),
            binding.etAllergyText.getTrimmedText(),
            hasDiabetes,
            hasPoorCoagulation,
            binding.etOthersText.getTrimmedText()
        )
    }

    /**
     * Check if phone is correct number only if exists
     */
    private fun validatePhone(): Boolean =
        binding.etPhoneText.text.toString().isEmpty() || binding.etPhoneText.isCorrectMobilePhone()

    private fun <T: Enum<T>> setupMultiChoiceInput(
        editText: EditText,
        titleResId: Int,
        options: Array<T>,
        selectedOptions: MutableSet<T>
    ) {
        editText.setOnClickListener {
            showMultiChoiceDialog(
                getString(titleResId),
                options,
                selectedOptions
            ) { selected ->
                selectedOptions.clear()
                selectedOptions.addAll(selected)
                editText.setText(selected.formatSelection())
            }
        }
    }

    private fun List<Enum<*>>.formatSelection(): String {
        return joinToString(", ") { option ->
            option.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    private fun <T: Enum<T>> showMultiChoiceDialog(
        title: String,
        enumValues: Array<T>,
        selectedOptions: Set<T>,
        onSelected: (List<T>) -> Unit
    ) {
        val items = enumValues.map { it.name.replace("_", " ").lowercase()
            .replaceFirstChar { c -> c.uppercase() } }.toTypedArray()
        val checkedItems = enumValues.map { it in selectedOptions }.toBooleanArray()
        val selectedList = selectedOptions.toMutableSet()

        val checkedColor = ContextCompat.getColor(requireContext(), R.color.gold)
        val uncheckedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(checkedColor, uncheckedColor)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMultiChoiceItems(items, checkedItems) { _, index, isChecked ->
                if (isChecked) selectedList.add(enumValues[index])
                else selectedList.remove(enumValues[index])
            }
            .setPositiveButton(getString(R.string.accept)) { _, _ -> onSelected(selectedList.toList()) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}