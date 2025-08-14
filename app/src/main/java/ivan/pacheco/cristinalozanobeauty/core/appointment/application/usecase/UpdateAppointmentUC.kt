package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import javax.inject.Inject

class UpdateAppointmentUC @Inject constructor(
    private val repository: AppointmentRepository
) {

    fun execute(event: CalendarEvent): Single<String> {

        // Get appointment by event id
        return repository.find(event.id)
            .map { appointment -> appointment.copy(event = event) }
            .flatMap { updatedAppointment -> repository.update(updatedAppointment) }
    }

}