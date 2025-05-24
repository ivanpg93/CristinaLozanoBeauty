package ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

fun interface FindColorHistoryWebService {
    fun fetch(clientId: String, id: String): Single<Color>
}