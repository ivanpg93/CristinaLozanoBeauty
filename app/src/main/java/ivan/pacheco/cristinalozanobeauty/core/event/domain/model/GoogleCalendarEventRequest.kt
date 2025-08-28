package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

data class GoogleCalendarEventRequest(
    val summary: String,
    val description: String?,
    val start: EventDateTime,
    val end: EventDateTime,
    val extendedProperties: ExtendedProperties
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String = "Europe/Madrid"
)

data class ExtendedProperties(
    val private: Map<String, String>? = null,
    val shared: Map<String, String>? = null
)