package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class ColorHistoryListViewModel @Inject constructor(
    private val repository: ColorsHistoryRepository,
    state: SavedStateHandle
): ViewModel() {

    companion object {
        const val ARG_CLIENT_ID = "clientId"
    }

    // LiveData
    private val colorsLD = MutableLiveData<List<Color>>()
    private val clientIdLD = MutableLiveData<String>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = SingleLiveEvent<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun getColorsLD(): LiveData<List<Color>> = colorsLD
    fun getClientIdLD(): LiveData<String> = clientIdLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    init {
        clientIdLD.value = clientId
    }

    // Actions
    fun actionDeleteColor(color: Color) {
        repository.delete(color, clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableCompletableObserver(){
                override fun onComplete() { loadData() }
                override fun onError(e: Throwable) { errorLD.value = R.string.color_history_list_error_delete }
            })
    }

    fun loadData() {
        repository.list(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<Color>>(){
                override fun onSuccess(colorList: List<Color>) { colorsLD.value = colorList }
                override fun onError(e: Throwable) { errorLD.value = R.string.color_history_list_error_list }
            })
    }

}