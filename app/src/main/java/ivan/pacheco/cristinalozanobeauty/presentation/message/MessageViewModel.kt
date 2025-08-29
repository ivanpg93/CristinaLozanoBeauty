package ivan.pacheco.cristinalozanobeauty.presentation.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.AppointmentNotFound
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.message.application.usecase.SendMessageUC
import javax.inject.Inject

@HiltViewModel
class MessageViewModel@Inject constructor(
    private val clientRepository: ClientRepository,
    private val sendMessageUC: SendMessageUC
): ViewModel() {

    // LiveData
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()
    private val clientsLD = MutableLiveData<List<ClientListDTO>>()

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD

    init {
        loadClients()
    }

    fun actionSendMessage(client: ClientListDTO) {
        sendMessageUC.execute(client)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() { }
                override fun onError(e: Throwable) {
                    when(e) {
                        is AppointmentNotFound -> errorLD.value = R.string.message_appointment_not_found
                        else -> errorLD.value = R.string.message_sending_reminder_error
                    }
                }
            })
    }

    private fun loadClients() {
        clientRepository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<ClientListDTO>>() {
                override fun onSuccess(clients: List<ClientListDTO>) { clientsLD.value = clients }
                override fun onError(error: Throwable) { errorLD.value = R.string.client_list_error_list }
            })
    }

}