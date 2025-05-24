package ivan.pacheco.cristinalozanobeauty.core.color.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

interface ColorsHistoryRepository {
    fun list(clientId: String): Single<List<Color>>
    fun find(id: String, clientId: String): Single<Color>
    fun create(color: Color, clientId: String): Completable
    fun update(color: Color, clientId: String): Completable
    fun delete(color: Color, clientId: String): Completable
}