package ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

fun interface FindColorHistoryWebService {
    fun fetch(id: String, clientId: String): Single<Color>
}