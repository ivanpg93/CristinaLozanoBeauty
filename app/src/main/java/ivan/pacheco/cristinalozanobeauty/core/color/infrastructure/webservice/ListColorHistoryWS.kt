package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.ListColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class ListColorHistoryWS: ListColorHistoryWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val COLORS = "colors"
    }

    override fun fetch(clientId: String): Single<List<Color>> {
        return Single.create { emitter ->

            // Get client colors history
            Firestore.db.collection(CLIENTS)
                .document(clientId)
                .collection(COLORS)
                .get()
                .addOnSuccessListener { result ->
                    val colors = result.documents.mapNotNull { it.toObject(Color::class.java) }
                    emitter.onSuccess(colors)
                }
                .addOnFailureListener { exception -> emitter.onError(exception) }
        }
    }

}