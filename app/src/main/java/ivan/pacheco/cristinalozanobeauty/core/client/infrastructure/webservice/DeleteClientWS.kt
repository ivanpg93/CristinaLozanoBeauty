package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class DeleteClientWS: DeleteClientWebService {

    private companion object {
        const val CLIENTS = "clients"
    }

    override fun fetch(client: ClientListDTO): Completable = Completable.create { emitter ->
        Firestore.db.collection(CLIENTS)
            .document(client.id)
            .delete()
            .addOnSuccessListener { emitter.onComplete() }
            .addOnFailureListener { error -> emitter.onError(error) }
    }

}