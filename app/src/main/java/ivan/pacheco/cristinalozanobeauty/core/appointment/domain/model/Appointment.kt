package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import java.util.Date

data class Appointment(
    val id: String,
    val date: Date,
    val service: Service,
    val clientId: String
)

enum class Service {
    MANICURE, PEDICURE
}
