package ivan.pacheco.cristinalozanobeauty.presentation.utils

import androidx.fragment.app.Fragment
import ivan.pacheco.cristinalozanobeauty.presentation.main.MainActivity

object FragmentUtils {

    fun Fragment.showLoading(isLoading: Boolean) {
        if (isLoading) (activity as? MainActivity)?.showLoading()
        else (activity as? MainActivity)?.hideLoading()
    }

    fun Fragment.showError(error: Int) { DialogUtils.showError(requireContext(), getString(error)) }
    fun Fragment.showAlert(message: Int) { DialogUtils.showAlert(requireContext(), getString(message)) }

}