package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

data class GoogleCalendarEventRequest(
    val summary: String,
    val description: String?,
    val start: EventDateTime,
    val end: EventDateTime
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String = "Europe/Madrid"
)
