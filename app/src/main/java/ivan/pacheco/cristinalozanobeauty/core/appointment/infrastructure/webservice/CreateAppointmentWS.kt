package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.CreateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class CreateAppointmentWS: CreateAppointmentWebService {

    private companion object {
        const val APPOINTMENTS = "appointments"
    }

    override fun fetch(appointment: Appointment): Completable {

        // Create id for client document
        val appointmentId = Firestore.db.collection(APPOINTMENTS).document().id

        // Update id client
        val appointmentData = appointment.copy(id = appointmentId)

        return Completable.create { emitter ->
            Firestore.db.collection(APPOINTMENTS)
                .document(appointmentId)
                .set(appointmentData.toMap())
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}