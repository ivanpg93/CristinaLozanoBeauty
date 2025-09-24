package ivan.pacheco.cristinalozanobeauty.core.client.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO

interface ClientRepository {
    fun list(filterEnabled: Boolean = true): Single<List<ClientListDTO>>
    fun find(id: String): Single<Client>
    fun create(client: Client): Completable
    fun update(client: Client): Single<Client>
    fun delete(client: ClientListDTO): Completable
}