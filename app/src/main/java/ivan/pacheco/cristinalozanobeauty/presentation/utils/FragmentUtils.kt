package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ivan.pacheco.cristinalozanobeauty.presentation.main.MainActivity
import java.io.File
import java.io.FileOutputStream

object FragmentUtils {

    private const val PROVIDER = ".provider"
    private const val APPLICATION_PDF = "application/pdf"

    fun Fragment.showLoading(isLoading: Boolean) {
        if (isLoading) (activity as? MainActivity)?.showLoading()
        else (activity as? MainActivity)?.hideLoading()
    }

    fun Fragment.showError(error: Int) { DialogUtils.showError(requireContext(), getString(error)) }
    fun Fragment.showAlert(message: Int) { DialogUtils.showAlert(requireContext(), getString(message)) }

    fun Fragment.openPdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}$PROVIDER",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APPLICATION_PDF)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(intent)
    }

    fun Context.getPdfFromAssets(fileName: String): File {
        val outFile = File(filesDir, fileName)

        // If file already exists, return it
        if (outFile.exists()) {
            return outFile
        }

        assets.open(fileName).use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }

        return outFile
    }

}