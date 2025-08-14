package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.CreateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class CreateAppointmentWS: CreateAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
        const val EVENTS_INDEX = "eventsIndex"
        const val CLIENT_ID = "clientId"
        const val APPOINTMENT_ID = "appointmentId"
    }

    override fun fetch(appointment: Appointment, clientId: String): Completable {

        // Create id for appointment document
        val appointmentId = Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(APPOINTMENTS)
            .document()
            .id

        // Update id appointment
        val appointmentData = appointment.copy(id = appointmentId)

        // Run batch for create appointment and index into db
        return Completable.create { emitter ->
            Firestore.db.runBatch { batch ->

                // Save appointment
                val appointmentRef = Firestore.db.collection(CLIENTS)
                    .document(clientId)
                    .collection(APPOINTMENTS)
                    .document(appointmentId)
                batch.set(appointmentRef, appointmentData.toMap())

                // Save index
                val eventId = appointment.event?.id
                if (eventId != null) {
                    val indexRef = Firestore.db.collection(EVENTS_INDEX).document(eventId)
                    batch.set(indexRef, mapOf(
                        CLIENT_ID to clientId,
                        APPOINTMENT_ID to appointmentId
                    ))
                }
            }
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}