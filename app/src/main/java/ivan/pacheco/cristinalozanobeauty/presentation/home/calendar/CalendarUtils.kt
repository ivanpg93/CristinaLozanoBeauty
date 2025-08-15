package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
fun View.makeVisible() { visibility = View.VISIBLE }
fun View.makeInVisible() { visibility = View.INVISIBLE }
fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)