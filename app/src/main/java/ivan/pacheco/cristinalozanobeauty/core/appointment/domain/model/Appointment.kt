package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent

data class Appointment(
    val id: String = "",
    val event: CalendarEvent? = null,
) {

    enum class Service(val duration: Double) {
        ARREGLO_DE_UÑA(0.25),
        ESMALTE_NORMAL(1.0),
        MANICURA_COMBINADA(1.0),
        NIVELACION(1.5),
        PEDICURA_COMPLETA(1.75),
        PEDICURA_JELLY(1.5),
        PEDICURA_SEMI(2.0),
        PRIMERA_PUESTA(1.5),
        RELLENO_ACRILICO(1.5),
        RELLENO_ACRYGEL(1.0),
        RETIRADA_DE_MATERIAL(1.25),
        SEMIPERMANENTE(1.0)
    }

    fun toMap() = mapOf(
        "id" to id,
        "event" to event?.toMap()
    )

}
