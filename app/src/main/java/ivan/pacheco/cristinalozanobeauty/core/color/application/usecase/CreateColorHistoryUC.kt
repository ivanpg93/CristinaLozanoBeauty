package ivan.pacheco.cristinalozanobeauty.core.color.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import java.util.Date
import javax.inject.Inject

class CreateColorHistoryUC @Inject constructor(private val repository: ColorsHistoryRepository) {

    fun execute(
        name: String,
        date: Date,
        clientId: String
    ): Completable {

        // Build color. Id will set from Firebase
        val color = Color(
            "",
            name,
            date,
        )

        return repository.create(clientId, color)
    }

}