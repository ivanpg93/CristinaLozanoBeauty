package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import java.util.Date

data class Appointment(
    val id: String,
    val date: Date,
    val service: Service,
    val clientId: String
)
