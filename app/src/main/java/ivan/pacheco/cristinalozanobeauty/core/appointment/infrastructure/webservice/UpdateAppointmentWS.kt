package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class UpdateAppointmentWS: UpdateAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
    }

    override fun fetch(appointment: Appointment, clientId: String): Completable = Completable.create { emitter ->
        Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(APPOINTMENTS)
            .document(appointment.id)
            .set(appointment.toMap())
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}