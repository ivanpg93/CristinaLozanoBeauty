package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model

import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO

data class AppointmentClient(
    val appointment: Appointment,
    val client: ClientListDTO
)