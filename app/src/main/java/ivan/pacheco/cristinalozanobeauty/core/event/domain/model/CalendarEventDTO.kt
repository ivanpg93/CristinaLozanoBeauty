package ivan.pacheco.cristinalozanobeauty.core.event.domain.model

import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CalendarEventDTO(
    val id: String,
    val text: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val service: Appointment.Service? = null,
    val assisted: Boolean = true
) {

    fun mapToCalendarEvent(): CalendarEvent {
        val startDateTime = LocalDateTime.of(date, startTime)
        val endDateTime = LocalDateTime.of(date, endTime)
        return CalendarEvent(
            id = id,
            summary = text,
            startDateTime = startDateTime.toString(),
            endDateTime = endDateTime.toString(),
            service = service,
            assisted = assisted
        )
    }

    fun mapToClientListDTO(): ClientListDTO {
        val parts = text.split(" ")
        val firstName = parts.getOrNull(0) ?: ""
        val firstLastName = parts.getOrNull(1) ?: ""
        return ClientListDTO(id, firstName, firstLastName, "")
    }

}
