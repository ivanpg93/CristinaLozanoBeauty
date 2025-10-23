package ivan.pacheco.cristinalozanobeauty.core.color.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import java.util.Date
import javax.inject.Inject

class CreateColorsHistoryUC @Inject constructor(private val repository: ColorsHistoryRepository) {

    fun execute(
        brand: Color.NailPolishBrand,
        reference: String,
        date: Date,
        clientId: String
    ): Completable {

        // Build color. Id will set from Firebase
        val color = Color(
            "",
            brand,
            reference,
            date,
        )

        return repository.create(color, clientId)
    }

}