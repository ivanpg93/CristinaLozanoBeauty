package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.form

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
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.NailPolishBrand
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentColorsHistoryFormBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.getTrimmedText
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class ColorsHistoryFormFragment: Fragment() {

    private var _binding: FragmentColorsHistoryFormBinding? = null
    private val binding get() = _binding!!
    private val vm: ColorsHistoryFormViewModel by viewModels()
    private var selectedNailPolishBrand: NailPolishBrand? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentColorsHistoryFormBinding.inflate(layoutInflater)

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

        // Input date picker
        binding.etDateText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.FormDatePicker)
                .setSelection(getInitialDate(binding.etDateText.getTrimmedText()))
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build()

            datePicker.show(childFragmentManager, "")

            // Set selected date
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                binding.etDateText.setText(DateUtils.formatDate(selectedDate))
            }
        }

        // Input nail polish brand
        setupSingleChoiceInput(
            binding.etBrandText,
            R.string.color_history_form_select_nail_polish_brand,
            NailPolishBrand.entries.toTypedArray(),
            { selectedNailPolishBrand }
        ) { selectedNailPolishBrand = it }
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

        val brand = selectedNailPolishBrand ?: run {
            showError(R.string.color_history_form_error_mandatory_fields)
            return
        }

        // Create color history action
        vm.actionSave(
            brand,
            binding.etReferenceText.getTrimmedText(),
            DateUtils.parseDate(binding.etDateText.getTrimmedText()),
        )
    }

    private fun <T : Enum<T>> setupSingleChoiceInput(
        editText: EditText,
        titleResId: Int,
        options: Array<T>,
        getSelectedOption: () -> T?,
        onSelected: (T) -> Unit
    ) {
        editText.setOnClickListener {
            showSingleChoiceDialog(
                getString(titleResId),
                options,
                getSelectedOption()
            ) { selected ->
                onSelected(selected)
                editText.setText(selected.name.replace("_", " ").lowercase()
                    .replaceFirstChar { it.titlecase() })
            }
        }
    }

    private fun <T: Enum<T>> showSingleChoiceDialog(
        title: String,
        enumValues: Array<T>,
        selectedOption: T?,
        onSelected: (T) -> Unit
    ) {
        val options = enumValues
            .map { it to it.name.replace("_", " ")
                .lowercase()
                .replaceFirstChar { c -> c.titlecase() } }
            .sortedBy { it.second }

        val selectedIndex = options.indexOfFirst { it.first == selectedOption }
        var tempSelectedIndex = selectedIndex

        val checkedColor = ContextCompat.getColor(requireContext(), R.color.gold)
        val uncheckedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(checkedColor, uncheckedColor)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(options.map { it.second }.toTypedArray(), selectedIndex) { _, index ->
                tempSelectedIndex = index
            }
            .setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                tempSelectedIndex.takeIf { it >= 0 }?.let { onSelected(options[it].first) }
                dialog.dismiss()
            }
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