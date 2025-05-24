package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
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

        // Create color history action
        vm.actionSave(
            binding.etNameText.getTrimmedText(),
            DateUtils.parseDate(binding.etDateText.getTrimmedText()),
        )
    }

    private fun navigate(destination: Destination) {
        when(destination) {
            is Destination.Back -> findNavController().popBackStack()
            else -> {} // Do nothing
        }
    }

}