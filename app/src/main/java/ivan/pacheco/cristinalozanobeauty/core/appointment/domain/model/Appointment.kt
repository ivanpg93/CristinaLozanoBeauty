package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent

data class Appointment(
    val id: String = "",
    val event: CalendarEvent? = null,
) {

    enum class Service {
        ARREGLO_DE_UÑA,
        ESMALTE_NORMAL,
        MANICURA_COMBINADA,
        NIVELACION,
        PEDICURA_COMPLETA,
        PEDICURA_JELLY,
        PEDICURA_SEMI,
        PRIMERA_PUESTA,
        RELLENO_ACRILICO,
        RELLENO_ACRYGEL,
        RETIRADA_DE_MATERIAL,
        SEMIPERMANENTE
    }

    fun toMap() = mapOf(
        "id" to id,
        "event" to event?.toMap()
    )

}
