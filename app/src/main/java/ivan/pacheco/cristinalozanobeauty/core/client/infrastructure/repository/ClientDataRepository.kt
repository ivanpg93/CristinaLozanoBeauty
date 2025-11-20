package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.FindClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.UpdateClientWebService
import javax.inject.Inject

class ClientDataRepository @Inject constructor(
    private val listWS: ListClientWebService,
    private val findWS: FindClientWebService,
    private val createWS: CreateClientWebService,
    private val updateWS: UpdateClientWebService,
    private val deleteWS: DeleteClientWebService
): ClientRepository {

    override fun list(): Single<List<ClientListDTO>> {
        return listWS.fetch()
            .map { clientList ->
                    clientList.map { client ->
                        ClientListDTO(
                            client.id,
                            client.firstName,
                            client.lastName,
                            client.phone,
                            client.birthday,
                            client.enabled
                        )
                    }.sortedBy { it.firstName }
            }
    }

    override fun find(id: String): Single<Client> = findWS.fetch(id)
    override fun create(client: Client): Completable = createWS.fetch(client)
    override fun update(client: Client): Single<Client> = updateWS.fetch(client)
    override fun delete(client: ClientListDTO): Completable = deleteWS.fetch(client)

}