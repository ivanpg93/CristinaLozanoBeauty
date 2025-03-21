package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class DeleteClientWS: DeleteClientWebService {

    override fun deleteClient(client: ClientListDTO): Completable {
        return Completable.create { emitter ->
            Firestore.db.collection("clients")
                .document(client.id)
                .delete()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

}