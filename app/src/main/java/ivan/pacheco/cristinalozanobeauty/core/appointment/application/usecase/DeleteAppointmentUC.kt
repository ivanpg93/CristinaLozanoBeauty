package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.AppointmentNotFound
import javax.inject.Inject

class DeleteAppointmentUC @Inject constructor(private val repository: AppointmentRepository) {

    fun execute(eventId: String, clientId: String): Completable {
        return repository.list(clientId).flatMapCompletable { list ->
            list.find { it.event?.id == eventId }?.let { appointment ->
                repository.delete(appointment, clientId)
            } ?: Completable.error(AppointmentNotFound())
        }
    }

}