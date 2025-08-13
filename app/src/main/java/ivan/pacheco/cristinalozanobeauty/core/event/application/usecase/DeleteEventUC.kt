package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.DeleteAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val deleteAppointmentUC: DeleteAppointmentUC
) {

    fun execute(eventId: String, token: String): Completable {
        return eventRepository.deleteEvent(eventId, token)
            .andThen(deleteAppointmentUC.execute(eventId))
    }

}