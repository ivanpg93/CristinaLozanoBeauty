package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class UpdateAppointmentWS: UpdateAppointmentWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
        const val EVENTS_INDEX = "eventsIndex"
        const val CLIENT_ID = "clientId"
        const val APPOINTMENT_ID = "appointmentId"
        const val EVENT = "event"
    }

    override fun fetch(appointment: Appointment): Completable = Completable.create { emitter ->
        val eventId = appointment.event?.id
        if (eventId.isNullOrBlank()) {
            emitter.onError(IllegalArgumentException(AppointmentNotFound()))
            return@create
        }

        // Find document by event id
        Firestore.db.collection(EVENTS_INDEX)
            .document(eventId)
            .get()
            .addOnSuccessListener { indexDoc ->
                val clientId = indexDoc.getString(CLIENT_ID)
                val appointmentId = indexDoc.getString(APPOINTMENT_ID)

                if (clientId.isNullOrEmpty() || appointmentId.isNullOrEmpty()) {
                    emitter.onError(AppointmentNotFound())
                    return@addOnSuccessListener
                }

                val docRef = Firestore.db.collection(CLIENTS)
                    .document(clientId)
                    .collection(APPOINTMENTS)
                    .document(appointmentId)

                // Updates event data
                val updates = mapOf(EVENT to appointment.event.toMap())

                docRef.update(updates)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            }
            .addOnFailureListener { emitter.onError(it) }
    }

}