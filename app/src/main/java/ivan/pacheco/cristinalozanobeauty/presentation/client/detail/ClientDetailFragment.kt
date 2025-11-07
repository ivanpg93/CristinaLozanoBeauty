package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.databinding.FragmentClientDetailBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DialogUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.getTrimmedText
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.isCorrectMobilePhone
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showAlert
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showError
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FragmentUtils.showLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hide
import ivan.pacheco.cristinalozanobeauty.presentation.utils.KeyboardUtils.hideAutomatically

@AndroidEntryPoint
class ClientDetailFragment: Fragment() {

    private var _binding: FragmentClientDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: ClientDetailViewModel by viewModels()
    private val selectedNailDisorders = mutableSetOf<Client.NailDisorder>()
    private val selectedSkinDisorders = mutableSetOf<Client.SkinDisorder>()
    private lateinit var clientId: String
    private var originalClient: Client? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientDetailBinding.inflate(inflater, container, false)

        // Hide keyboard
        hideAutomatically(binding.root, requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // On back pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            saveChangesDialog(Destination.Back)
        }

        // Navigation
        vm.navigationLD.observe(viewLifecycleOwner) { destination -> navigate(destination) }

        // Loading
        vm.isLoadingLD().observe(viewLifecycleOwner) { isLoading -> showLoading(isLoading) }

        // Error
        vm.getErrorLD().observe(viewLifecycleOwner) { error -> showError(error) }

        // Client
        vm.getClientLD().observe(viewLifecycleOwner) { client ->
            clientId = client.id
            originalClient = client.copy()
            setActionBarTitle("${client.firstName} ${client.lastName}")
            loadData(client)
        }

        // Button colors history
        binding.btnColorHistory.setOnClickListener { saveChangesDialog(Destination.ColorHistoryList(clientId)) }

        // Button events history
        binding.btnEventHistory.setOnClickListener { saveChangesDialog(Destination.AppointmentHistoryList(clientId)) }

        // Button save client
        binding.btnSave.setOnClickListener { saveAction(Destination.Back) }

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

            // Set selected date
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                binding.etBirthdayText.setText(DateUtils.formatDate(selectedDate))
            }

            datePicker.show(childFragmentManager, "")
        }

        // Input nail disorder
        setupMultiChoiceInput(
            binding.etNailDisorderText,
            R.string.client_form_select_nail_disorder,
            Client.NailDisorder.entries.toTypedArray(),
            selectedNailDisorders
        )

        // Input skin disorder
        setupMultiChoiceInput(
            binding.etSkinDisorderText,
            R.string.client_form_select_skin_disorder,
            Client.SkinDisorder.entries.toTypedArray(),
            selectedSkinDisorders
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun saveChangesDialog(destination: Destination) {

        // Navigate without save if form not changed
        if (!hasFormChanged()) {
            navigate(destination)
            return
        }

        // Create dialog
        val dialog = DialogUtils.createDialog(
            requireContext(),
            getString(R.string.client_form_save_changes_title),
            getString(R.string.client_form_save_changes)
        ) { saveAction(destination) }

        // Customize listener for buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                navigate(destination)
            }
        }

        // Show dialog
        dialog.show()
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

            // Update selectedSkinDisorders list
            selectedSkinDisorders.clear()
            selectedSkinDisorders.addAll(client.skinDisorderList)
            etSkinDisorderText.setText(selectedSkinDisorders.formatSelection())

            etAllergyText.setText(client.allergy)
            etOthersText.setText(client.others)

            rbDiabetesYes.isChecked = client.hasDiabetes
            rbDiabetesNo.isChecked = !client.hasDiabetes

            rbPoorCoagulationYes.isChecked = client.hasPoorCoagulation
            rbPoorCoagulationNo.isChecked = !client.hasPoorCoagulation

            switchActionDisabled.isChecked = !client.enabled
        }
    }

    /**
     * If there is value, it is displayed.
     * If not, get date from 18 years ago is displayed
     */
    private fun getInitialDate(dateStr: String): Long? {
        return dateStr.takeIf { it.isNotBlank() }?.let { DateUtils.parseDate(it)?.time }
    }

    private fun hasFormChanged(): Boolean {
        val client = originalClient ?: return false
        return client.isFormChanged()
    }

    private fun saveAction(destination: Destination) {

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

        // Update client action
        vm.actionUpdateClient(
            binding.etNameText.getTrimmedText(),
            binding.etLastNameText.getTrimmedText(),
            binding.etPhoneText.getTrimmedText(),
            binding.etEmailText.getTrimmedText(),
            DateUtils.parseDate(binding.etBirthdayText.getTrimmedText()),
            binding.etProfessionText.getTrimmedText(),
            binding.etTownText.getTrimmedText(),
            selectedNailDisorders.toList(),
            selectedSkinDisorders.toList(),
            binding.etAllergyText.getTrimmedText(),
            hasDiabetes,
            hasPoorCoagulation,
            binding.etOthersText.getTrimmedText(),
            !binding.switchActionDisabled.isChecked,
            destination
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
            option.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }
        }
    }

    private fun <T: Enum<T>> showMultiChoiceDialog(
        title: String,
        enumValues: Array<T>,
        selectedOptions: Set<T>,
        onSelected: (List<T>) -> Unit
    ) {
        val displayOptions = enumValues
            .map { it to it.name.replace("_", " ").lowercase()
                .replaceFirstChar { c -> c.titlecase() } }
            .sortedBy { it.second }

        val items = displayOptions.map { it.second }.toTypedArray()
        val checkedItems = displayOptions.map { it.first in selectedOptions }.toBooleanArray()
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
                val item = displayOptions[index].first
                if (isChecked) selectedList.add(item) else selectedList.remove(item)
            }
            .setPositiveButton(getString(R.string.accept)) { _, _ ->
                onSelected(displayOptions.map { it.first }.filter { it in selectedList })
            }
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
            is Destination.ColorHistoryList -> {
                findNavController().navigate(
                    ClientDetailFragmentDirections.actionClientDetailFragmentToColorHistoryListFragment(
                        destination.clientId
                    )
                )
            }
            is Destination.AppointmentHistoryList -> {
                findNavController().navigate(
                    ClientDetailFragmentDirections.actionClientDetailFragmentToAppointmentHistoryListFragment(
                        destination.clientId
                    )
                )
            }
            else -> {} // Do nothing
        }
    }

    private fun Client.isFormChanged(): Boolean {
        val fieldsToCompare = listOf(
            firstName to binding.etNameText.getTrimmedText(),
            lastName to binding.etLastNameText.getTrimmedText(),
            phone to binding.etPhoneText.getTrimmedText(),
            email to binding.etEmailText.getTrimmedText(),
            birthday?.let { DateUtils.formatDate(it) } to binding.etBirthdayText.getTrimmedText(),
            profession to binding.etProfessionText.getTrimmedText(),
            town to binding.etTownText.getTrimmedText(),
            allergy to binding.etAllergyText.getTrimmedText(),
            others to binding.etOthersText.getTrimmedText(),
            hasDiabetes to binding.rbDiabetesYes.isChecked,
            hasPoorCoagulation to binding.rbPoorCoagulationYes.isChecked
        )

        val listsToCompare = listOf(
            nailDisorderList.toSet() to selectedNailDisorders,
            skinDisorderList.toSet() to selectedSkinDisorders
        )

        return fieldsToCompare.any { it.first != it.second } || listsToCompare.any { it.first != it.second }
    }

}