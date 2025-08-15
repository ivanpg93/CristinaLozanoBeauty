package ivan.pacheco.cristinalozanobeauty.presentation.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private const val DATE_FORMAT = "dd/MM/yyyy"
    private const val TIME_FORMAT = "HH:mm"
    private const val TIME_ZONE = "UTC"
    private val localDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT)

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
        } catch (_: ParseException) {
            null
        }
    }

    fun String.toLocalDate(): LocalDate {
        return try {
            OffsetDateTime.parse(this).toLocalDate()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this).toLocalDate()
        }
    }

    fun String.toLocalTime(): LocalTime {
        return try {
            OffsetDateTime.parse(this).toLocalTime()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, localDateTimeFormatter).toLocalTime()
        }
    }

    fun String.toDate(): String {
        return try {
            OffsetDateTime.parse(this).format(dateFormatter)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, localDateTimeFormatter).format(dateFormatter)
        }
    }

    fun String.toHour(): String {
        return try {
            val odt = OffsetDateTime.parse(this)
            odt.toLocalTime().format(timeFormatter)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, localDateTimeFormatter).toLocalTime().format(timeFormatter)
        }
    }

    fun LocalDate.toFormattedString(): String {
        return this.format(dateFormatter)
    }

}