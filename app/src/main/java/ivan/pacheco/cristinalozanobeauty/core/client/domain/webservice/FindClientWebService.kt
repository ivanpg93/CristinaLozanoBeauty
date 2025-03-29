package ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client

fun interface FindClientWebService {
    fun fetch(id: String): Single<Client>
}