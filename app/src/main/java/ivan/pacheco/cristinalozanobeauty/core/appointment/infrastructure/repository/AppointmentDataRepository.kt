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
import javax.inject.Inject

class AppointmentDataRepository @Inject constructor(
    private val listWS: ListAppointmentWebService,
    private val findWS: FindAppointmentWebService,
    private val createWS: CreateAppointmentWebService,
    private val updateWS: UpdateAppointmentWebService,
    private val deleteWS: DeleteAppointmentWebService
): AppointmentRepository {

    override fun list(clientId: String): Single<List<Appointment>> {
        return listWS.fetch(clientId)
            .map { appointmentList ->
                appointmentList.map { appointment ->
                    Appointment(
                        appointment.id,
                        appointment.event,
                        appointment.service
                    )
                }.sortedByDescending { it.event?.startDateTime }
            }
    }

    override fun find(id: String, clientId: String): Single<Appointment> = findWS.fetch(id, clientId)
    override fun create(appointment: Appointment, clientId: String): Completable = createWS.fetch(appointment, clientId)
    override fun update(appointment: Appointment, clientId: String): Completable = updateWS.fetch(appointment, clientId)
    override fun delete(eventId: String): Completable = deleteWS.fetch(eventId)

}