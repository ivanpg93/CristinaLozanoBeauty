package ivan.pacheco.cristinalozanobeauty.core.client.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client

interface ClientRepository {
    fun list(): Single<List<ClientListDTO>>
    fun find(id: String): Single<Client>
    fun create(client: Client): Completable
    fun update(client: Client): Completable
    fun delete(client: ClientListDTO): Completable
}