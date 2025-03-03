package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import java.util.Date

class ClientListViewModel : ViewModel() {

    private val clientsLD = MutableLiveData<List<Client>>()

    fun getClientsLD(): LiveData<List<Client>> = clientsLD

    init {
        loadData()
    }

    private fun loadData() {
        clientsLD.value = listOf(
            Client(
                "id client 1",
                "Cristina",
                "Lozano Palà",
                "651951937",
                "cristinalozano.07@gmail.com",
                listOf(
                    Appointment(
                        "id cita",
                        Date(),
                        Service.MANICURE,
                        "client id"
                    )
                )
            ),
            Client(
                "id client 2",
                "Iván",
                "Pacheco González",
                "652429855",
                "4civanpacheco@gmail.com",
                listOf(
                    Appointment(
                        "id cita",
                        Date(),
                        Service.PEDICURE,
                        "client id"
                    )
                )
            )
        )
    }

}