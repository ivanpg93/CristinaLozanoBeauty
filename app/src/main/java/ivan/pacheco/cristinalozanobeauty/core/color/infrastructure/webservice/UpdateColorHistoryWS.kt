package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.UpdateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class UpdateColorHistoryWS: UpdateColorHistoryWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val COLORS = "colors"
    }

    override fun fetch(color: Color, clientId: String): Completable = Completable.create { emitter ->
        Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(COLORS)
            .document(color.id)
            .set(color.toMap())
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}