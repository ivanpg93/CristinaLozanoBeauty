package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.CreateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class CreateColorHistoryWS: CreateColorHistoryWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val COLORS = "colors"
    }

    override fun fetch(color: Color, clientId: String): Completable {

        // Create id for color document
        val colorId = Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(COLORS)
            .document()
            .id

        // Update id color
        val colorData = color.copy(id = colorId)

        return Completable.create { emitter ->
            Firestore.db.collection(CLIENTS)
                .document(clientId)
                .collection(COLORS)
                .document(colorId)
                .set(colorData.toMap())
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}