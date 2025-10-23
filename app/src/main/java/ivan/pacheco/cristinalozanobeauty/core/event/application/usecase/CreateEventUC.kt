package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.CreateAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEventDTO
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import javax.inject.Inject

class CreateEventUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val createAppointmentUC: CreateAppointmentUC
) {

    fun execute(event: CalendarEventDTO, client: ClientListDTO, token: String): Completable {
        val newEvent = event.mapToCalendarEvent()
        return eventRepository.createEvent(newEvent, token)
            .flatMapCompletable { calendarEvent -> createAppointmentUC.execute(calendarEvent, client.id) }
    }

}