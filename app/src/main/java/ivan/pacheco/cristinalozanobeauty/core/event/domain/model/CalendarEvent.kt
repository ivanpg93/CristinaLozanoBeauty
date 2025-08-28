package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalTime

data class CalendarEvent(
    val id: String = "",
    val summary: String = "",
    val description: String? = null,
    val startDateTime: String = "",
    val endDateTime: String = "",
    val service: Service?= null,
    var assisted: Boolean = false
)

fun CalendarEvent.toDTO(): CalendarEventDTO {
    return CalendarEventDTO(
        id = id,
        text = summary,
        date = startDateTime.toLocalDate(),
        startTime = startDateTime.toLocalTime(),
        endTime = endDateTime.toLocalTime(),
        service = service,
        assisted = assisted
    )
}

fun CalendarEvent.toMap() = mapOf(
    "id" to id,
    "summary" to summary,
    "description" to description,
    "startDateTime" to startDateTime,
    "endDateTime" to endDateTime,
    "service" to service?.name,
    "assisted" to assisted
)