package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.DialogMonthPickerBinding
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class MonthYearPickerDialog(
    private val initialYear: Int = LocalDate.now().year,
    private val initialMonth: Int = LocalDate.now().monthValue,
    private val onMonthSelected: (year: Int, month: Int) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogMonthPickerBinding.inflate(layoutInflater)
        val locale = Locale.getDefault()

        val months = Month.entries.map {
            it.getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { char -> char.titlecase(locale) }
        }.toTypedArray()

        binding.apply {

            // Set NumberPickers
            npMonth.minValue = 0
            npMonth.maxValue = months.size - 1
            npMonth.value = initialMonth - 1
            npMonth.displayedValues = months

            val currentYear = LocalDate.now().year
            npYear.minValue = currentYear - 5
            npYear.maxValue = currentYear + 5
            npYear.value = initialYear

            // Buttons
            btnCancel.setOnClickListener { dismiss() }
            btnAccept.setOnClickListener {
                val selectedYear = npYear.value
                val selectedMonth = npMonth.value + 1
                onMonthSelected(selectedYear, selectedMonth)
                dismiss()
            }
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.FormDatePicker)
            .setView(binding.root)
            .create()

        return dialog
    }

}