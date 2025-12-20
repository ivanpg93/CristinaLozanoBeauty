package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.model.AnalyticsEvent
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class CreateAppointmentUC @Inject constructor(
    private val repository: AppointmentRepository,
    private val analyticsRepository: AnalyticsRepository
) {

    fun execute(
        event: CalendarEvent,
        clientId: String
    ): Completable {

        // Build appointment. Id will set from Firebase
        val appointment = Appointment(
            "",
            event
        )

        return repository.create(appointment, clientId).doOnComplete {
            analyticsRepository.logEvent(AnalyticsEvent.AppointmentCreated(event))
        }
    }

}