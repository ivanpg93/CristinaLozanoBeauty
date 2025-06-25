package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

fun interface DeleteAppointmentWebService {
    fun fetch(appointment: Appointment, clientId: String): Completable
}