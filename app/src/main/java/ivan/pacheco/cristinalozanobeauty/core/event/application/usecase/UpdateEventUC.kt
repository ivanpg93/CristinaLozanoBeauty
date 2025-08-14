package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.UpdateAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.CalendarEventDTO
import ivan.pacheco.cristinalozanobeauty.presentation.home.calendar.toCalendarEvent
import javax.inject.Inject

class UpdateEventUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val updateAppointmentUC: UpdateAppointmentUC
) {

    fun execute(
        calendarEventDTO: CalendarEventDTO,
        token: String
    ): Single<String> {
        val calendarEvent = calendarEventDTO.toCalendarEvent()
        return eventRepository.updateEvent(calendarEvent, token)
            .flatMap { calendarEvent -> updateAppointmentUC.execute(calendarEvent) }
    }

}