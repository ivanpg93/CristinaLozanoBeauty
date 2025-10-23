package ivan.pacheco.cristinalozanobeauty.core.client.application.usecase

import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
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
        nailDisorderList: List<Client.NailDisorder>,
        skinDisorderList: List<Client.SkinDisorder>,
        serviceList: List<Appointment.Service>,
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