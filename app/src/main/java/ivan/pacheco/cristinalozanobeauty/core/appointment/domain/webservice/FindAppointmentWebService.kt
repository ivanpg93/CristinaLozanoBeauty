package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

fun interface FindAppointmentWebService {
    fun fetch(eventId: String): Single<Appointment>
}