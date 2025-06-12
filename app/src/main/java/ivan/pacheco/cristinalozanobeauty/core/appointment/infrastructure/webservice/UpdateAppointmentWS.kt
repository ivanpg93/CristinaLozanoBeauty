package ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.webservice.UpdateAppointmentWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class UpdateAppointmentWS: UpdateAppointmentWebService {

    private companion object {
        const val APPOINTMENTS = "appointments"
    }

    override fun fetch(appointment: Appointment): Completable = Completable.create { emitter ->
        Firestore.db.collection(APPOINTMENTS)
            .document(appointment.id)
            .set(appointment.toMap())
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}