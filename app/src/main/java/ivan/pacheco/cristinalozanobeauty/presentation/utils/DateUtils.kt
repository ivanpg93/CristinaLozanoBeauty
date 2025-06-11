package ivan.pacheco.cristinalozanobeauty.presentation.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"
    private const val TIME_ZONE = "UTC"

    fun formatDate(timestamp: Long, pattern: String = DATE_FORMAT): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDate(date: Date, pattern: String = DATE_FORMAT): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun parseDate(dateString: String, pattern: String = DATE_FORMAT): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone(TIME_ZONE)
            sdf.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }

    fun String.toLocalDate(): LocalDate {
        return try {
            OffsetDateTime.parse(this).toLocalDate()
        } catch (e: DateTimeParseException) {
            LocalDateTime.parse(this).toLocalDate()
        }
    }

}