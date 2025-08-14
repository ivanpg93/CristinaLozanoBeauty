package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.toMap

data class Appointment(
    val id: String = "",
    val event: CalendarEvent? = null,
) {

    fun toMap() = mapOf(
        "id" to id,
        "event" to event?.toMap()
    )

}
