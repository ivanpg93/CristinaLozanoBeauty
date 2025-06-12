package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

fun interface CreateAppointmentWebService {
    fun fetch(appointment: Appointment): Completable
}