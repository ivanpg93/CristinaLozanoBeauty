package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.databinding.ClientDetailFragmentBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.getTrimmedText
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.isCorrectMobilePhone
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class ClientDetailFragment: Fragment() {

    private var _binding: ClientDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientDetailViewModel by viewModels()
    private val selectedNailDisorders = mutableSetOf<NailDisorder>()
    private val selectedSkinDisorders = mutableSetOf<SkinDisorder>()
    private lateinit var clientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClientDetailFragmentBinding.inflate(inflater, container, false)

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

        vm.getClientLD().observe(viewLifecycleOwner) { client ->
            clientId = client.id
            setActionBarTitle("${client.firstName} ${client.lastName}")
            loadData(client)
        }

        // Button save client
        binding.btnSave.setOnClickListener { saveAction() }

        // Input phone
        val prefix = getString(R.string.client_form_prefix_phone)
        binding.etPhoneText.addTextChangedListener { phoneText ->
            if (phoneText != null && !phoneText.startsWith(prefix)) {
                binding.etPhoneText.setText(prefix)
                binding.etPhoneText.setSelection(prefix.length)
            }
        }

        // Input date picker
        binding.etBirthdayText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.ClientFormDatePicker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()

            datePicker.show(childFragmentManager, "")

            // Set selected date
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val dateString = DateUtils.formatDate(selectedDate)
                binding.etBirthdayText.setText(dateString)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setActionBarTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    private fun loadData(client: Client) {
        with(binding) {
            etNameText.setText(client.firstName)
            etLastNameText.setText(client.lastName)
            etPhoneText.setText(client.phone)
            etEmailText.setText(client.email)
            etBirthdayText.setText(client.birthday?.let { DateUtils.formatDate(it) })
            etProfessionText.setText(client.profession)
            etTownText.setText(client.town)

            // Update selectedNailDisorders list
            selectedNailDisorders.clear()
            selectedNailDisorders.addAll(client.nailDisorderList)
            etNailDisorderText.setText(selectedNailDisorders.formatSelection())

            // Update selectedNailDisorders list
            selectedSkinDisorders.clear()
            selectedSkinDisorders.addAll(client.skinDisorderList)
            etSkinDisorderText.setText(selectedSkinDisorders.formatSelection())

            etTreatmentText.setText(client.treatment)
            etAllergyText.setText(client.allergy)
        }
    }

    private fun saveAction() {
        if (!validateForm()) return

        // Hide keyboard
        hide(binding.btnSave)

        vm.actionUpdateClient(
            clientId,
            binding.etNameText.getTrimmedText(),
            binding.etLastNameText.getTrimmedText(),
            binding.etPhoneText.getTrimmedText(),
            binding.etEmailText.getTrimmedText(),
            DateUtils.parseDate(binding.etBirthdayText.getTrimmedText()),
            binding.etProfessionText.getTrimmedText(),
            binding.etTownText.getTrimmedText(),
            selectedNailDisorders.toList(),
            selectedSkinDisorders.toList(),
            binding.etTreatmentText.getTrimmedText(),
            binding.etAllergyText.getTrimmedText()
        )
    }

    /**
     * Check if phone is correct number only if exists
     */
    private fun validateForm(): Boolean =
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

    private fun <T : Enum<T>> Collection<T>.formatSelection(): String {
        return joinToString(", ") { option ->
            option.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}