package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import ivan.pacheco.cristinalozanobeauty.R
import java.text.Normalizer

object FormUtils {

    fun EditText.getTrimmedText(): String = this.text.toString().trim()

    fun EditText.isCorrectMobilePhone(): Boolean {
        val phoneRegex = Regex("^(\\+34)?[67]\\d{8}$")
        return getTrimmedText().matches(phoneRegex)
    }

    /**
     * Ignore accents in comparison strings
     */
    fun String.normalizeForSearch() = Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()

    /**
     * Format enum value to String
     */
    fun Enum<*>.toDisplayName(): String =
        name
            .lowercase()
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }

    /**
     * Enable form button when required fields are filled
     * @param requiredFields: List of required input fields
     * @param button: Button to enable/disable
     */
    fun automaticFormButton(requiredFields: List<TextInputLayout>, button: Button) {

        // Check if all required fields are filled
        fun allFieldsNotEmpty() = requiredFields.all { it.editText?.text?.isNotBlank() == true }

        // Update button UI according to state
        fun updateButtonUI() {
            val colorRes = if (button.isEnabled) R.color.gold else R.color.gray
            button.setBackgroundColor(ContextCompat.getColor(button.context, colorRes))
        }

        // Disable button by default if some text is empty
        button.isEnabled = allFieldsNotEmpty()
        updateButtonUI()

        val textWatcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {

                // Enable button required fields are filled
                button.isEnabled = allFieldsNotEmpty()
                updateButtonUI()
            }

            // Do nothing
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Do nothing
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Listen required fields on text change
        requiredFields.forEach { field -> field.editText?.addTextChangedListener(textWatcher) }
    }

}