package ivan.pacheco.cristinalozanobeauty.presentation.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    /**
     * Hide keyboard
     *
     * @param view: View that has made appear keyboard
     */
    fun hide(view: View) {
        view.post {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Listen when user taps outside the view to hide keyboard automatically
     *
     * @param view: View that has made appear keyboard
     * @param activity: The activity associated with the view
     */
    @SuppressLint("ClickableViewAccessibility")
    fun hideAutomatically(view: View, activity: Activity) {
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                activity.currentFocus?.let { hide(it) }
            }
            false
        }
    }

}