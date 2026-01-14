package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ivan.pacheco.cristinalozanobeauty.presentation.main.MainActivity
import java.io.File

object FragmentUtils {

    fun Fragment.showLoading(isLoading: Boolean) {
        if (isLoading) (activity as? MainActivity)?.showLoading()
        else (activity as? MainActivity)?.hideLoading()
    }

    fun Fragment.showError(error: Int) { DialogUtils.showError(requireContext(), getString(error)) }
    fun Fragment.showAlert(message: Int) { DialogUtils.showAlert(requireContext(), getString(message)) }

    fun Fragment.openPdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(intent)
    }

    fun Fragment.openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(intent)
    }

}