package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

data class GoogleCalendarEventRequest(
    val summary: String,
    val description: String?,
    val start: EventDateTime,
    val end: EventDateTime,
    val attendees: List<EventAttendee>
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String = "Europe/Madrid"
)

data class EventAttendee(
    val email: String,
    val organizer: Boolean = false,
    val responseStatus: String? = null
)

fun GoogleCalendarEventRequest.toCalendarEvent(): CalendarEvent {
    val organizerResponse = attendees.firstOrNull { it.organizer }?.responseStatus
    return CalendarEvent(
        summary = summary,
        description = description,
        startDateTime = start.dateTime,
        endDateTime = end.dateTime,
        assisted = organizerResponse == "accepted"
    )
}
