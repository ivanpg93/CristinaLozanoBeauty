package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent

data class Appointment(
    val id: String,
    val event: CalendarEvent,
    val service: Service,
    val clientId: String
) {

    fun toMap() = mapOf(
        "id" to id,
        "event" to event,
        "service" to service.name,
        "clientId" to clientId
    )

}
