package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.toMap
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.UpdateClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class UpdateClientWS: UpdateClientWebService {

    private companion object {
        const val CLIENTS = "clients"
    }

    override fun fetch(client: Client): Completable = Completable.create { emitter ->
        Firestore.db.collection(CLIENTS)
            .document(client.id)
            .set(client.toMap())
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}