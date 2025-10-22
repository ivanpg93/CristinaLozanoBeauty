package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.normalizeForSearch
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val repository: ClientRepository
): ViewModel() {

    private companion object {
        const val DELAYS_QUERY = 300L
    }

    // LiveData
    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = SingleLiveEvent<Int>()
    private var allClients: List<ClientListDTO> = listOf()
    private val searchSubject = BehaviorSubject.createDefault("")
    private val enabledFilterSubject = BehaviorSubject.createDefault(true)
    private val compositeDisposable = CompositeDisposable()

    // Getters
    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    // Actions
    init {
        observeFilters()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onSearchQueryChanged(query: String) { searchSubject.onNext(query) }
    fun onEnabledFilterChanged(enabled: Boolean) { enabledFilterSubject.onNext(enabled) }

    fun loadData() {
        repository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<ClientListDTO>>() {
                override fun onSuccess(clientList: List<ClientListDTO>) {
                    allClients = clientList
                    // Al cambiar allClients, force applyFilters con los valores actuales de BehaviorSubjects
                    applyFilters(searchSubject.value ?: "", enabledFilterSubject.value ?: true)
                }

                override fun onError(e: Throwable) {
                    errorLD.value = R.string.client_list_error_list
                }
            })
    }

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

    private fun observeFilters() {
        compositeDisposable.add(
            Observable.combineLatest(
                    searchSubject
                        .debounce(DELAYS_QUERY, TimeUnit.MILLISECONDS)
                        .map { it.trim() }
                        .distinctUntilChanged()
                        .startWith(searchSubject),
                    enabledFilterSubject.distinctUntilChanged()
                        .startWith(enabledFilterSubject)
            ) { query, enabled -> Pair(query, enabled) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (query, enabled) ->
                    applyFilters(query, enabled)
                }
        )
    }

    /**
     * Filter clients by query and enabled filter
     */
    private fun applyFilters(query: String, filterEnabled: Boolean) {
        var filtered = allClients.filter { it.enabled == filterEnabled }
        if (query.isNotBlank()) {
            val normalized = query.trim().normalizeForSearch()
            filtered = filtered.filter { client ->
                client.firstName.normalizeForSearch().contains(normalized) ||
                        client.lastName.normalizeForSearch().contains(normalized) ||
                        client.phone.contains(normalized)
            }
        }
        clientsLD.value = filtered
    }

}