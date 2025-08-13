package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.DeleteAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class DeleteAppointmentWS: DeleteAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
        const val EVENTS_INDEX = "eventsIndex"
        const val CLIENT_ID = "clientId"
        const val APPOINTMENT_ID = "appointmentId"
    }

    override fun fetch(eventId: String): Completable {
        val db = Firestore.db
        return Completable.create { emitter ->
            db.collection(EVENTS_INDEX).document(eventId).get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        emitter.onError(AppointmentNotFound())
                        return@addOnSuccessListener
                    }

                    // Get client_id and appointment_id documents
                    val clientId = doc.getString(CLIENT_ID)
                    val appointmentId = doc.getString(APPOINTMENT_ID)

                    // Ensure documents exist
                    if (clientId.isNullOrEmpty() || appointmentId.isNullOrEmpty()) {
                        emitter.onError(AppointmentNotFound())
                        return@addOnSuccessListener
                    }

                    // Run batch for delete appointment and index from db
                    db.runBatch { batch ->

                        // Get refs for appointment and event
                        val appointmentRef = db.collection(CLIENTS)
                            .document(clientId)
                            .collection(APPOINTMENTS)
                            .document(appointmentId)
                        val eventIndexRef = db.collection(EVENTS_INDEX).document(eventId)

                        // Delete appointment and index
                        batch.delete(appointmentRef)
                        batch.delete(eventIndexRef)
                    }
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener { emitter.onError(it) }
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

}