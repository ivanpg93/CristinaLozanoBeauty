package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.CreateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.DeleteAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.FindAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.ListAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.model.AnalyticsEvent
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.repository.AnalyticsRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class AppointmentDataRepository @Inject constructor(
    private val listWS: ListAppointmentWebService,
    private val findWS: FindAppointmentWebService,
    private val createWS: CreateAppointmentWebService,
    private val updateWS: UpdateAppointmentWebService,
    private val deleteWS: DeleteAppointmentWebService,
    private val analyticsRepository: AnalyticsRepository
): AppointmentRepository {

    override fun listPending(clientId: String): Single<List<Appointment>> = list(clientId).map { filterPendingList(it) }
    override fun listPast(clientId: String): Single<List<Appointment>> = list(clientId).map { filterPastList(it) }
    override fun find(eventId: String): Single<Appointment> = findWS.fetch(eventId)
    override fun create(appointment: Appointment, clientId: String): Completable = createWS.fetch(appointment, clientId)
    override fun delete(eventId: String): Completable = deleteWS.fetch(eventId)

    override fun update(appointment: Appointment): Completable =
        updateWS.fetch(appointment).doOnComplete {
            analyticsRepository.logEvent(AnalyticsEvent.AppointmentAttendanceUpdated(appointment))
        }

    private fun list(clientId: String): Single<List<Appointment>> {
        return listWS.fetch(clientId)
            .map { appointmentList ->
                appointmentList.map { appointment ->
                    Appointment(appointment.id, appointment.event)
                }.sortedBy { it.event?.startDateTime }
            }
    }

    private fun filterPendingList(appointmentList: List<Appointment>): List<Appointment> {
        val now = OffsetDateTime.now()
        return appointmentList.filter { appointment ->
            appointment.event?.startDateTime?.let { start ->
                OffsetDateTime.parse(start).isAfter(now)
            } == true
        }.sortedBy { it.event?.startDateTime }
    }

    private fun filterPastList(appointmentList: List<Appointment>): List<Appointment> {
        val now = OffsetDateTime.now()
        return appointmentList.filter { appointment ->
            appointment.event?.startDateTime?.let { start ->
                OffsetDateTime.parse(start).isBefore(now)
            } == true
        }.sortedByDescending { it.event?.startDateTime }
    }

}