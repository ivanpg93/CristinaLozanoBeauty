package ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.ClientListDTO

fun interface DeleteClientWebService {
    fun deleteClient(client: ClientListDTO): Completable
}