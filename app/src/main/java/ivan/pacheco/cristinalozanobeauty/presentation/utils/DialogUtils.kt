package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun showSuccess(context: Context, message: String): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Información")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .create()

        dialog.show()
        return dialog
    }

    fun showError(context: Context, message: String): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .create()

        dialog.show()
        return dialog
    }

}