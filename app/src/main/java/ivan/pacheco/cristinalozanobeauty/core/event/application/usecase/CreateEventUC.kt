package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.CreateAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.CalendarRepository
import javax.inject.Inject

class CreateEventUC @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val createAppointmentUC: CreateAppointmentUC
) {

    fun execute(event: CalendarEvent, service: Service, client: ClientListDTO, token: String): Completable {
        return calendarRepository.createEvent(event, token)
            .flatMapCompletable { createAppointmentUC.execute(event, service, client.id) }
    }

}