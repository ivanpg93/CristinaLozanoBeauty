package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.FindAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class FindAppointmentWS: FindAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
        const val EVENTS_INDEX = "eventsIndex"
        const val CLIENT_ID = "clientId"
        const val APPOINTMENT_ID = "appointmentId"
    }

    override fun fetch(eventId: String): Single<Appointment> = Single.create { emitter ->

        // Find appointment by event id
        Firestore.db.collection(EVENTS_INDEX).document(eventId).get()
            .addOnSuccessListener { indexDoc ->
                val clientId = indexDoc.getString(CLIENT_ID)
                val appointmentId = indexDoc.getString(APPOINTMENT_ID)

                if (clientId.isNullOrEmpty() || appointmentId.isNullOrEmpty()) {
                    emitter.onError(AppointmentNotFound())
                    return@addOnSuccessListener
                }


                // Find appointment
                Firestore.db.collection(CLIENTS)
                    .document(clientId)
                    .collection(APPOINTMENTS)
                    .document(appointmentId)
                    .get()
                    .addOnSuccessListener { appointmentDoc ->
                        val appointment = appointmentDoc.toObject(Appointment::class.java)
                        if (appointment != null) {
                            emitter.onSuccess(appointment)
                        } else {
                            emitter.onError(AppointmentNotFound())
                        }
                    }
                    .addOnFailureListener { exception -> emitter.onError(exception) }
            }
            .addOnFailureListener { exception -> emitter.onError(exception) }
    }

}

class AppointmentNotFound : Exception()