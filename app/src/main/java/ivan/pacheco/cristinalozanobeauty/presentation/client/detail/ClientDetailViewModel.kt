package ivan.pacheco.cristinalozanobeauty.presentation.client.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.UpdateClientUC
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientDocumentRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val documentRepository: ClientDocumentRepository,
    private val uc: UpdateClientUC,
    state: SavedStateHandle
): ViewModel(), Navigation {

    private companion object {
        const val ARG_CLIENT_ID = "id"
    }

    // LiveData
    override val navigationLD = SingleLiveEvent<Destination>()
    private val clientLD = MutableLiveData<Client>()
    private val documentPathLD = SingleLiveEvent<String>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = SingleLiveEvent<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun getClientLD(): LiveData<Client> = clientLD
    fun getDocumentPathLD(): LiveData<String> = documentPathLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    init {
        loadData()
    }

    // Actions
    fun actionUpdateClient(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        birthdate: Date?,
        profession: String,
        town: String,
        nailDisorderList: List<Client.NailDisorder>,
        skinDisorderList: List<Client.SkinDisorder>,
        allergy: String,
        diabetes: Boolean,
        poorCoagulation: Boolean,
        others: String,
        frequency: Int,
        enabled: Boolean,
        destination: Destination
    ) {
        // Check mandatory fields before continue to create client
        if (!checkMandatoryFields(listOf(firstName, lastName, phone))) {
            errorLD.value = R.string.client_form_error_mandatory_fields
            return
        }

        // Update client action
        uc.execute(
            clientId,
            firstName,
            lastName,
            phone,
            email,
            birthdate,
            profession,
            town,
            nailDisorderList,
            skinDisorderList,
            allergy,
            diabetes,
            poorCoagulation,
            others,
            frequency,
            enabled
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableSingleObserver<Client>() {
                override fun onSuccess(client: Client) {
                    clientLD.value = client
                    navigationLD.value = destination
                }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_detail_error_update }
            })
    }

    fun actionLoadMinorConsentDocument(clientId: String) {
        documentRepository.getMinorConsentUrl(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<String>() {
                override fun onSuccess(url: String) { documentPathLD.value = url }
                override fun onError(e: Throwable) { documentPathLD.value = "" }
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