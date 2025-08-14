package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice

import io.reactivex.Completable

fun interface DeleteAppointmentWebService {
    fun fetch(eventId: String): Completable
}