package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.DeleteAppointmentWS
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice.DeleteColorHistoryWS
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore.db

class DeleteClientWS: DeleteClientWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val APPOINTMENTS = "appointments"
        const val COLORS = "colors"
        const val EVENT = "event"
        const val ID = "id"
    }

    private val deleteAppointmentWS: DeleteAppointmentWS = DeleteAppointmentWS()
    private val deleteColorHistoryWS: DeleteColorHistoryWS = DeleteColorHistoryWS()
    private val disposables = CompositeDisposable()

    override fun fetch(client: ClientListDTO): Completable {

        // Get client document
        val clientRef = db.collection(CLIENTS).document(client.id)

        // Delete appointments collection
        val deleteAppointments = Completable.create { emitter ->
            clientRef.collection(APPOINTMENTS).get()
                .addOnSuccessListener { snap ->
                    val list = snap.documents.map { doc ->
                        val eventId = (doc.get(EVENT) as? Map<*, *>)?.get(ID) as? String
                        if (!eventId.isNullOrEmpty()) deleteAppointmentWS.fetch(eventId)
                        else Completable.complete()
                    }
                    Completable.merge(list).subscribe({
                        emitter.onComplete()
                    }, { emitter.onError(it) }).let { disposables.add(it) }
                }
                .addOnFailureListener { emitter.onError(it) }
        }

        // Delete colors collection
        val deleteColors = Completable.create { emitter ->
            clientRef.collection(COLORS).get()
                .addOnSuccessListener { snap ->
                    val list = snap.documents.mapNotNull { doc ->
                        doc.toObject(ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color::class.java)
                            ?.let { deleteColorHistoryWS.deleteColor(it, client.id) }
                    }
                    Completable.merge(list).subscribe({
                        emitter.onComplete()
                    }, { emitter.onError(it) }).let { disposables.add(it) }
                }
                .addOnFailureListener { emitter.onError(it) }
        }

        // Chain operations
        return deleteAppointments
            .andThen(deleteColors)
            .andThen(Completable.create { emitter ->
                clientRef.delete()
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            })
    }

}