package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val repository: ClientRepository
): ViewModel() {

    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val errorLD = MutableLiveData<String>()
    fun getErrorLD(): LiveData<String> = errorLD

    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD

    init {
        loadData()
    }

    private fun loadData() {
        repository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<ClientListDTO>>(){
                override fun onSuccess(clientList: List<ClientListDTO>) { clientsLD.value = clientList }
                override fun onError(e: Throwable) { errorLD.value = "No se ha podido registrar la clienta" }
            })
    }

}