package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.CreateClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.DeleteClientWebService
import ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice.ListClientWebService
import javax.inject.Inject

class ClientDataRepository @Inject constructor(
    private val listWS: ListClientWebService,
    private val createWS: CreateClientWebService,
    private val deleteWS: DeleteClientWebService
): ClientRepository {

    override fun list(): Single<List<ClientListDTO>> {
        return listWS.listClient()
            .map { clientList ->
                clientList.map { client ->
                    ClientListDTO(
                        id = client.id,
                        firstName = client.firstName,
                        lastName = client.lastName,
                        phone = client.phone
                    )
                }
            }
    }

    override fun create(client: Client): Completable = createWS.createClient(client)

    override fun update(client: Client): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(client: ClientListDTO): Completable = deleteWS.deleteClient(client)

}