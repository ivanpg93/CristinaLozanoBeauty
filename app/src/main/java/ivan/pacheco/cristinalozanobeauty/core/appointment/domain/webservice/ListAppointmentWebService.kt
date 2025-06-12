package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

fun interface ListAppointmentWebService {
    fun fetch(): Single<List<Appointment>>
}