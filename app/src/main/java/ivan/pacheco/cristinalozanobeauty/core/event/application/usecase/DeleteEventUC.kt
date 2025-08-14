package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUC @Inject constructor(
    private val repository: EventRepository,
    private val appointmentRepository: AppointmentRepository
) {

    fun execute(eventId: String, token: String): Completable {
        return repository.deleteEvent(eventId, token)
            .andThen(appointmentRepository.delete(eventId))
    }

}