package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.toMap

data class Appointment(
    val id: String = "",
    val event: CalendarEvent? = null,
    val service: Service? = null,
) {

    fun toMap() = mapOf(
        "id" to id,
        "event" to event?.toMap(),
        "service" to service?.name
    )

}
