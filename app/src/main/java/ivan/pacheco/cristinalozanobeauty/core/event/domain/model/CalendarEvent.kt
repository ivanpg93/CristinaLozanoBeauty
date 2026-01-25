package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalTime

data class CalendarEvent(
    val id: String = "",
    val summary: String = "",
    val description: String? = null,
    val startDateTime: String = "",
    val endDateTime: String = "",
    val service: Appointment.Service? = null,
    val assisted: Boolean = true
) {

    fun mapToDTO(): CalendarEventDTO {
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

    fun toMap() = mapOf(
        "id" to id,
        "summary" to summary,
        "description" to description,
        "startDateTime" to startDateTime,
        "endDateTime" to endDateTime,
        "service" to service?.name,
        "assisted" to assisted
    )

}