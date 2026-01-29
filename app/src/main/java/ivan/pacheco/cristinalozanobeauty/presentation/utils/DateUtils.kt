package ivan.pacheco.cristinalozanobeauty.presentation.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
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
    private const val ADULT_AGE = 18

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

    fun parseToZonedDateTime(dateTimeStr: String, zoneId: ZoneId): ZonedDateTime {
        return try {
            OffsetDateTime.parse(dateTimeStr).atZoneSameInstant(zoneId)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(dateTimeStr, localDateTimeFormatter).atZone(zoneId)
        }
    }

    fun String.toLocalDate(): LocalDate {
        return try {
            LocalDate.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (_: DateTimeParseException) {
            LocalDate.parse(this, dateFormatter)
        }
    }

    fun String.toLocalDateTime(): LocalDate {
        return try {
            OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, dateFormatter).toLocalDate()
        }
    }

    fun String.toLocalTime(): LocalTime {
        return try {
            OffsetDateTime.parse(this,DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalTime()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, dateFormatter).toLocalTime()
        }
    }

    fun String.toDate(): String {
        return try {
            OffsetDateTime.parse(this,DateTimeFormatter.ISO_OFFSET_DATE_TIME).format(dateFormatter)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, localDateTimeFormatter).format(dateFormatter)
        }
    }

    fun String.toHour(): String {
        return try {
            OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalTime().format(timeFormatter)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(this, localDateTimeFormatter).toLocalTime().format(timeFormatter)
        }
    }

    fun isAdult(birthDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean =
        birthDate.plusYears(ADULT_AGE.toLong()) <= today

    fun LocalDate.toFormattedString(): String = this.format(dateFormatter)
    fun LocalDate.toEpochMillisForDatePicker(): Long = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    fun Long.toLocalDateFromDatePicker(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

}
