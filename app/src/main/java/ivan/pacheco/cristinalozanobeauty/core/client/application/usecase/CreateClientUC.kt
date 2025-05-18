package ivan.pacheco.cristinalozanobeauty.core.client.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import java.util.Date
import javax.inject.Inject

class CreateClientUC @Inject constructor(private val repository: ClientRepository) {

    fun execute(
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
        hasDiabetes: Boolean,
        hasPoorCoagulation: Boolean,
        others: String
    ): Completable {

        // Build client. Id will set from Firebase
        val client = Client(
            "",
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
            hasDiabetes,
            hasPoorCoagulation,
            others
        )

        return repository.create(client)
    }

}