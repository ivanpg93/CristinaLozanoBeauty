package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import java.util.Date

class ClientDetailViewModel(
    state: SavedStateHandle
): ViewModel() {

    companion object {
        const val ARG_CLIENT_ID = "id"
    }

    private val clientLD = MutableLiveData<Client>()
    private val clientId: String

    fun getClientLD(): LiveData<Client> = clientLD

    init {

        // Use client id to retrieve client information
        clientId = state.getLiveData<String>(ARG_CLIENT_ID).value!!

        loadData()
    }

    private fun loadData() {
        //val client = repository.find(clientId)
        clientLD.value = Client(
            "id client 1",
            "Cristina",
            "Lozano Palà",
            "651951937",
            "cristinalozano.07@gmail.com",
            Date(),
            "",
            "",
            listOf(),
            listOf(),
            "",
            "",
            listOf(
                Appointment(
                    "id cita",
                    Date(),
                    Service.MANICURE,
                    "client id"
                )
            )
        )
    }

}