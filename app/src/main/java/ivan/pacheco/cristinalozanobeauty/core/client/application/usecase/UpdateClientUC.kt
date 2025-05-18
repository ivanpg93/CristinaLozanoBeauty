package ivan.pacheco.cristinalozanobeauty.core.client.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import java.util.Date
import javax.inject.Inject

class UpdateClientUC @Inject constructor(private val repository: ClientRepository) {

    fun execute(
        id: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        birthdate: Date?,
        profession: String,
        town: String,
        nailDisorderList: List<NailDisorder>,
        skinDisorderList: List<SkinDisorder>,
        serviceList: List<Service>,
        allergy: String,
        diabetes: Boolean,
        poorCoagulation: Boolean,
        others: String
    ): Completable {

        // Build client
        val client = Client(
            id,
            firstName,
            lastName,
            phone,
            email,
            birthdate,
            profession,
            town,
            nailDisorderList,
            skinDisorderList,
            serviceList,
            allergy,
            diabetes,
            poorCoagulation,
            others
        )

        return repository.update(client)
    }

}