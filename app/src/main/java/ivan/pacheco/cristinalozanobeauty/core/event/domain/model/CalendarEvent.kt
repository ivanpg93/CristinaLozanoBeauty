package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

data class CalendarEvent(
    val id: String,
    val summary: String,
    val description: String?,
    val startDateTime: String,
    val endDateTime: String
)