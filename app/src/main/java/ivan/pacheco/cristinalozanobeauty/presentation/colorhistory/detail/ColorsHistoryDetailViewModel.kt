package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.detail

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
import ivan.pacheco.cristinalozanobeauty.core.color.application.usecase.UpdateColorsHistoryUC
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.NailPolishBrand
import ivan.pacheco.cristinalozanobeauty.core.color.domain.repository.ColorsHistoryRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ColorsHistoryDetailViewModel @Inject constructor(
    private val repository: ColorsHistoryRepository,
    private val uc: UpdateColorsHistoryUC,
    state: SavedStateHandle
): ViewModel(), Navigation {

    private companion object {
        const val ARG_ID = "id"
        const val ARG_CLIENT_ID = "clientId"
    }

    // LiveData
    override val navigationLD = MutableLiveData<Destination>()
    private val colorLD = MutableLiveData<Color>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    // Use client id to retrieve client information
    private val colorId: String = state.getLiveData<String>(ARG_ID).value!!
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getColorLD(): LiveData<Color> = colorLD

    init {
        loadData()
    }

    // Actions
    fun actionUpdateColor(
        brand: NailPolishBrand,
        reference: String,
        date: Date?
    ) {
        // Check mandatory fields before continue to create client
        if (!checkMandatoryFields(listOf(brand.name, reference, date.toString())) || date == null) {
            errorLD.value = R.string.client_form_error_mandatory_fields
            return
        }

        // Update client action
        uc.execute(
            colorId,
            brand,
            reference,
            date,
            clientId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableCompletableObserver() {
                override fun onComplete() { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = R.string.color_history_form_error_create }
            })
    }

    private fun loadData() {
        repository.find(colorId, clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableSingleObserver<Color>() {
                override fun onSuccess(color: Color) { colorLD.value = color }
                override fun onError(e: Throwable) { errorLD.value = R.string.color_history_detail_error_find }
            })
    }

    /**
     * Check if mandatory fields are empty
     */
    private fun checkMandatoryFields(mandatoryFields: List<String>) = !mandatoryFields.any { it.isBlank() }

}