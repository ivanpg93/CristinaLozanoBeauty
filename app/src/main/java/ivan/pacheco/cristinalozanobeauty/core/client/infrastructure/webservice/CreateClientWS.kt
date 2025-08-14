package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.toMap
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class CreateClientWS: CreateClientWebService {

    private companion object {
        const val CLIENTS = "clients"
    }

    override fun fetch(client: Client): Completable {

        // Create id for client document
        val clientId = Firestore.db.collection(CLIENTS).document().id

        // Update id client
        val clientData = client.copy(id = clientId)

        return Completable.create { emitter ->
            Firestore.db.collection(CLIENTS)
                .document(clientId)
                .set(clientData.toMap())
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}