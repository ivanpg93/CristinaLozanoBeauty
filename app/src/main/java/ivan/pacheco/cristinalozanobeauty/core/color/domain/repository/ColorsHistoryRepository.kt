package ivan.pacheco.cristinalozanobeauty.core.color.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

interface ColorsHistoryRepository {
    fun list(clientId: String): Single<List<Color>>
    fun find(clientId: String, id: String): Single<Color>
    fun create(clientId: String, color: Color): Completable
    fun update(clientId: String, color: Color): Completable
    fun delete(clientId: String, color: Color): Completable
}