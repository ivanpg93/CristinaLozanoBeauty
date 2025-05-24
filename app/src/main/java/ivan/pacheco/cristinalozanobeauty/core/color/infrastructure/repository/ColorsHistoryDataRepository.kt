package ivan.pacheco.cristinalozanobeauty.core.color.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.CreateColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.DeleteColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.FindColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.ListColorHistoryWebService
import ivan.pacheco.cristinalozanobeauty.core.color.domain.webservice.UpdateColorHistoryWebService
import javax.inject.Inject

class ColorsHistoryDataRepository@Inject constructor(
    private val listWS: ListColorHistoryWebService,
    private val findWS: FindColorHistoryWebService,
    private val createWS: CreateColorHistoryWebService,
    private val updateWS: UpdateColorHistoryWebService,
    private val deleteWS: DeleteColorHistoryWebService
): ColorsHistoryRepository {

    override fun list(clientId: String): Single<List<Color>> {
        return listWS.fetch(clientId)
            .map { colorList ->
                colorList.map { color ->
                    Color(
                        color.id,
                        color.name,
                        color.date,
                    )
                }.sortedBy { it.date }
            }
    }

    override fun find(clientId: String, id: String): Single<Color> = findWS.fetch(clientId, id)
    override fun create(clientId: String, color: Color): Completable = createWS.fetch(clientId, color)
    override fun update(clientId: String, color: Color): Completable = updateWS.fetch(clientId, color)
    override fun delete(clientId: String, color: Color): Completable = deleteWS.deleteColor(clientId, color)

}