package ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

fun interface DeleteColorHistoryWebService {
    fun deleteColor(clientId: String, color: Color): Completable
}