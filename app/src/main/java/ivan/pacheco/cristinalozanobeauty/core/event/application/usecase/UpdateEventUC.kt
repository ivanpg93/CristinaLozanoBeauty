package ivan.pacheco.cristinalozanobeauty.core.event.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEventDTO
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.toCalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import javax.inject.Inject

class UpdateEventUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val appointmentRepository: AppointmentRepository
) {

    fun execute(
        calendarEventDTO: CalendarEventDTO,
        token: String
    ): Completable {

        // Map calendar event
        val calendarEvent = calendarEventDTO.toCalendarEvent()

        // Update calendar event
        return eventRepository.updateEvent(calendarEvent, token)
            .flatMapCompletable { updatedEvent ->

                // Get appointment by event id
                appointmentRepository.find(updatedEvent.id)
                    .flatMapCompletable { appointment ->

                        // Update appointment
                        val updatedAppointment = appointment.copy(event = updatedEvent)
                        appointmentRepository.update(updatedAppointment)
                    }
            }
    }

}