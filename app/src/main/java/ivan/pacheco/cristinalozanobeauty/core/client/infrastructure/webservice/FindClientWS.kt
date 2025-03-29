package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.FindClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class FindClientWS: FindClientWebService {

    private companion object {
        const val CLIENTS = "clients"
    }

    override fun fetch(id: String): Single<Client> = Single.create { emitter ->

        // Find client by id
        Firestore.db.collection(CLIENTS).document(id).get()
            .addOnSuccessListener { result ->
                val client = result.toObject(Client::class.java)
                if (client != null) emitter.onSuccess(client)
                else emitter.onError(ClientNotFound())
            }
            .addOnFailureListener { exception -> emitter.onError(exception) }
    }

}

class ClientNotFound : Exception()