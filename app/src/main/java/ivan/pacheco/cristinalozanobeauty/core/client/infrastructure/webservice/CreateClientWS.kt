package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.shared.remote.Firestore

class CreateClientWS: CreateClientWebService {

    override fun createClient(client: Client): Completable {
        val clientId = Firestore.db.collection("clients").document().id // Genera un ID único
        val clientData = client.copy(id = clientId) // Actualiza el ID en el objeto `Client`

        return Completable.create { emitter ->
            Firestore.db.collection("clients")
                .document(clientId)
                .set(clientData.toMap()) // Convierte el objeto a Map
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

}