package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.FindColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class FindColorHistoryWS: FindColorHistoryWebService {

    private companion object {
        const val CLIENTS = "clients"
        const val COLORS = "colors"
    }

    override fun fetch(id: String, clientId: String): Single<Color> = Single.create { emitter ->

        // Find color by id
        Firestore.db.collection(CLIENTS)
            .document(clientId)
            .collection(COLORS)
            .document(id)
            .get()
            .addOnSuccessListener { result ->
                val color = result.toObject(Color::class.java)
                if (color != null) emitter.onSuccess(color)
                else emitter.onError(ColorNotFound())
            }
            .addOnFailureListener { exception -> emitter.onError(exception) }
    }

}

class ColorNotFound : Exception()