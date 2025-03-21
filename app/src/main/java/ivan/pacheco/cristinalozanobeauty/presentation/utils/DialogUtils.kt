package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import ivan.pacheco.cristinalozanobeauty.R

object DialogUtils {

    fun createDialog(
        context: Context,
        title: String,
        message: String,
        color: Int = ContextCompat.getColor(context, R.color.gold),
        action: () -> Unit
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.accept) { _, _ -> action() }
            .create()

        // Set color of buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(color)
        }

        dialog.show()
        return dialog
    }

    fun showSuccess(
        context: Context,
        message: String,
        color: Int = ContextCompat.getColor(context, R.color.gold)
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.information)
            .setMessage(message)
            .setPositiveButton(R.string.accept, null)
            .create()

        // Set color of buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(color)
        }

        dialog.show()
        return dialog
    }

    fun showError(
        context: Context,
        message: String,
        color: Int = ContextCompat.getColor(context, R.color.gold)
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.accept, null)
            .create()

        // Set color of buttons
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(color)
        }

        dialog.show()
        return dialog
    }

}