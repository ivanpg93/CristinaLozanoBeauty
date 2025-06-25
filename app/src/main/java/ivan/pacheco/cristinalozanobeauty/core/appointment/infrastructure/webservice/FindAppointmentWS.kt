package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.FindAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class FindAppointmentWS: FindAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
    }

    override fun fetch(id: String, clientId: String): Single<Appointment> = Single.create { emitter ->

        // Find client by id
        Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(APPOINTMENTS)
            .document(id).get()
            .addOnSuccessListener { result ->
                val client = result.toObject(Appointment::class.java)
                if (client != null) emitter.onSuccess(client)
                else emitter.onError(AppointmentNotFound())
            }
            .addOnFailureListener { exception -> emitter.onError(exception) }
    }

}

class AppointmentNotFound : Exception()