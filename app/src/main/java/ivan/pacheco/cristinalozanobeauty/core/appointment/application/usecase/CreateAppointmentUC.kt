package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import javax.inject.Inject

class CreateAppointmentUC @Inject constructor(private val repository: AppointmentRepository) {

    fun execute(
        event: CalendarEvent,
        clientId: String
    ): Completable {

        // Build appointment. Id will set from Firebase
        val appointment = Appointment(
            "",
            event
        )

        return repository.create(appointment, clientId)
    }

}