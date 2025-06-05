package ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

fun interface ListColorHistoryWebService {
    fun fetch(clientId: String): Single<List<Color>>
}