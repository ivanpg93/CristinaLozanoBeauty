package ivan.pacheco.cristinalozanobeauty.core.client.application.usecase

import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import java.util.Date
import javax.inject.Inject

class UpdateClientUC @Inject constructor(
    private val repository: ClientRepository
) {

    fun execute(
        id: String,
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
        diabetes: Boolean,
        poorCoagulation: Boolean,
        others: String,
        enabled: Boolean
    ): Single<Client> {

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
            others,
            enabled = enabled
        )

        return repository.update(client)
    }

}