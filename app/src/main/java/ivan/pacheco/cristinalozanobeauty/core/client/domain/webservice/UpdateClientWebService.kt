package ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client

fun interface UpdateClientWebService {
    fun fetch(client: Client): Completable
}