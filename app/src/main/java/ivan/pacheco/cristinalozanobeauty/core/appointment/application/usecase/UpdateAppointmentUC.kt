package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.SecureTokenDataStore
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UpdateAppointmentUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val repository: AppointmentRepository,
    @ApplicationContext private val context: Context
) {

    fun execute(event: CalendarEvent): Completable {

        // Get token for Google Calendar
        return Single.fromCallable { runBlocking { SecureTokenDataStore.readToken(context) } }

            // Update calendar event
            .flatMap { token -> eventRepository.updateEvent(event, token) }
            .flatMapCompletable { updatedEvent ->

                // Get appointment by event id
                repository.find(updatedEvent.id)
                    .flatMapCompletable { appointment ->

                        // Update appointment
                        val updatedAppointment = appointment.copy(event = updatedEvent)
                        repository.update(updatedAppointment)
                    }
            }
    }

}