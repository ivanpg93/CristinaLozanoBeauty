package ivan.pacheco.cristinalozanobeauty.core.color.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import java.util.Date
import javax.inject.Inject

class UpdateColorsHistoryUC @Inject constructor(private val repository: ColorsHistoryRepository) {

    fun execute(
        id: String,
        brand: Color.NailPolishBrand,
        reference: String,
        date: Date,
        clientId: String
    ): Completable {

        // Build color
        val color = Color(
            id,
            brand,
            reference,
            date
        )

        return repository.update(color, clientId)
    }

}