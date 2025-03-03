package ivan.pacheco.cristinalozanobeauty.core.client.domain.model

import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

data class Client(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val appointmentList: List<Appointment>
)
