package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

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
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.UpdateClientUC
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.NailDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.SkinDisorder
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val uc: UpdateClientUC,
    state: SavedStateHandle
): ViewModel(), Navigation {

    companion object {
        const val ARG_CLIENT_ID = "id"
    }

    // LiveData
    override val navigationLD = MutableLiveData<Destination>()
    private val clientLD = MutableLiveData<Client>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getClientLD(): LiveData<Client> = clientLD

    init {
        loadData()
    }

    // Actions
    fun actionUpdateClient(
        id: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        birthdate: Date?,
        profession: String,
        town: String,
        nailDisorderList: List<NailDisorder>,
        skinDisorderList: List<SkinDisorder>,
        treatment: String,
        allergy: String,
        diabetes: Boolean,
        poorCoagulation: Boolean,
        others: String
    ) {
        // Check mandatory fields before continue to create client
        if (!checkMandatoryFields(listOf(firstName, lastName, phone))) {
            errorLD.value = R.string.client_form_error_mandatory_fields
            return
        }

        // Update client action
        uc.execute(
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
            treatment,
            allergy,
            diabetes,
            poorCoagulation,
            others
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableCompletableObserver() {
                override fun onComplete() { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_form_error_create }
            })
    }

    private fun loadData() {
        repository.find(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableSingleObserver<Client>() {
                override fun onSuccess(client: Client) { clientLD.value = client }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_detail_error_find }
            })
    }

    /**
     * Check if mandatory fields are empty
     */
    private fun checkMandatoryFields(mandatoryFields: List<String>) = !mandatoryFields.any { it.isBlank() }

}