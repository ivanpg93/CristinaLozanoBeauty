package ivan.pacheco.cristinalozanobeauty.presentation.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"

    fun formatDate(timestamp: Long, pattern: String = DATE_FORMAT): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun parseDate(dateString: String, pattern: String = DATE_FORMAT): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }

}