package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.normalizeForSearch
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val repository: ClientRepository
): ViewModel() {

    // LiveData

    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    private var allClients: List<ClientListDTO> = listOf()

    // Getters

    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    // Actions

    fun actionDeleteClient(client: ClientListDTO) {
        repository.delete(client)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableCompletableObserver(){
                override fun onComplete() { loadData() }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_list_error_delete }
            })
    }

    fun loadData(filter: String? = null) {
        if (!filter.isNullOrBlank()) {
            filterClients(filter)
            return
        }
        repository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<ClientListDTO>>(){
                override fun onSuccess(clientList: List<ClientListDTO>) {
                    allClients = clientList
                    clientsLD.value = clientList
                }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_list_error_list }
            })
    }

    private fun filterClients(filter: String) {
        val normalizedFilter = filter.trim().normalizeForSearch()
        clientsLD.value = allClients.filter { client ->
            client.firstName.normalizeForSearch().contains(normalizedFilter) ||
            client.lastName.normalizeForSearch().contains(normalizedFilter) ||
            client.phone.contains(normalizedFilter)
        }
    }

}