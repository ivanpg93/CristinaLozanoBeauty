package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.DeleteColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class DeleteColorHistoryWS: DeleteColorHistoryWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val COLORS = "colors"
    }

    override fun deleteColor(clientId: String, color: Color): Completable = Completable.create { emitter ->
        Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(COLORS)
            .document(color.id)
            .delete()
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}