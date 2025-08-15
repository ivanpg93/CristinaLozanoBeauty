package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import java.time.LocalDate
import java.time.LocalTime

data class CalendarEventDTO(
    val id: String,
    val text: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val service: Service? = null,
    var assisted: Boolean = false
)

fun CalendarEventDTO.toCalendarEvent(): CalendarEvent {
    return CalendarEvent(
        id = id,
        summary = text,
        startDateTime = "${date}T${startTime}",
        endDateTime = "${date}T${endTime}",
        service = service,
        assisted = assisted
    )
}
