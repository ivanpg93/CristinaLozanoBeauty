package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.color.application.usecase.CreateColorsHistoryUC
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ColorsHistoryFormViewModel @Inject constructor(
    private val uc: CreateColorsHistoryUC,
    state: SavedStateHandle
): ViewModel(), Navigation {

    private companion object {
        const val ARG_CLIENT_ID = "clientId"
    }

    // LiveData
    override val navigationLD = MutableLiveData<Destination>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    // Actions
    fun actionSave(
        name: String,
        date: Date?
    ) {
        // Check mandatory fields before continue to create client
        if (!checkMandatoryFields(listOf(name, date.toString())) || date == null) {
            errorLD.value = R.string.color_history_form_error_mandatory_fields
            return
        }

        // Create client action
        uc.execute(name, date, clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableCompletableObserver() {
                override fun onComplete() { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = R.string.color_history_form_error_create }
            })
    }

    /**
     * Check if mandatory fields are empty
     */
    private fun checkMandatoryFields(mandatoryFields: List<String>) = !mandatoryFields.any { it.isBlank() }

}