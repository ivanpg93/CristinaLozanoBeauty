package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.AppointmentNotFound
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.CalendarRepository
import javax.inject.Inject

class DeleteEventUC @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val appointmentRepository: AppointmentRepository
) {

    fun execute(eventId: String, clientId: String, token: String): Completable {
        return calendarRepository.deleteEvent(eventId, token)
            /*.andThen(
            appointmentRepository.list(clientId).flatMapCompletable { list ->
                list.find { it.event?.id == eventId }?.let { appointment ->
                    appointmentRepository.delete(appointment, clientId)
                } ?: Completable.error(AppointmentNotFound())
            }
        )*/
    }

}