package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

data class GoogleCalendarEvent(
    val id: String,
    val summary: String,
    val description: String?,
    val start: DateTimeData,
    val end: DateTimeData,
    val extendedProperties: ExtendedProperties?= null
)

data class DateTimeData(val dateTime: String)