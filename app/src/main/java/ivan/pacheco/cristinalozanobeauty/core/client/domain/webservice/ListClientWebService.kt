package ivan.pacheco.cristinalozanobeauty.core.client.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client

fun interface ListClientWebService {
    fun listClient(): Single<List<Client>>
}