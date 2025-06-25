package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.Event
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate

data class CalendarEvent(
    val id: String = "",
    val summary: String = "",
    val description: String? = null,
    val startDateTime: String = "",
    val endDateTime: String = ""
)

fun CalendarEvent.toEvent(): Event {
    return Event(
        id = this.id,
        text = this.summary,
        date = this.startDateTime.toLocalDate()
    )
}

fun CalendarEvent.toMap() = mapOf(
    "id" to id,
    "summary" to summary,
    "description" to description,
    "startDateTime" to startDateTime,
    "endDateTime" to endDateTime
)