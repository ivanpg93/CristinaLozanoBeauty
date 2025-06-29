package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.ListAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class ListAppointmentWS: ListAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
    }

    override fun fetch(clientId: String): Single<List<Appointment>> {
        return Single.create { emitter ->

            // Get client appointments history
            Firestore.db.collection(CLIENTS)
                .document(clientId)
                .collection(APPOINTMENTS)
                .get()
                .addOnSuccessListener { result ->
                    val appointments = result.documents.mapNotNull { it.toObject(Appointment::class.java) }
                    emitter.onSuccess(appointments)
                }
                .addOnFailureListener { exception -> emitter.onError(exception) }
        }
    }

}