package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class ListClientWS: ListClientWebService {

    private companion object {
        const val CLIENTS = "clients"
    }

    override fun fetch(): Single<List<Client>> {
        return Single.create { emitter ->

            // Get clients
            Firestore.db.collection(CLIENTS).get()
                .addOnSuccessListener { result ->
                    val clients = result.documents.mapNotNull { it.toObject(Client::class.java) }
                    emitter.onSuccess(clients)
                }
                .addOnFailureListener { exception -> emitter.onError(exception) }
        }
    }

}