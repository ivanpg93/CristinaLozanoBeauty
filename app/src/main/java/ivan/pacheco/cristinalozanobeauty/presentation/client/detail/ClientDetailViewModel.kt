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
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientDocumentRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import ivan.pacheco.cristinalozanobeauty.presentation.utils.RxUtils.applySchedulers
import ivan.pacheco.cristinalozanobeauty.presentation.utils.RxUtils.withLoading
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
        minorConsentPath: String,
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
            enabled,
            minorConsentPath
        )
            .applySchedulers()
            .withLoading(isLoadingLD)
            .subscribe(object: DisposableSingleObserver<Client>() {
                override fun onSuccess(client: Client) {
                    clientLD.value = client
                    navigationLD.value = destination
                }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_detail_error_update }
            })
    }

    fun actionDeleteMinorConsentDocument(clientId: String) {
        clientLD.value?.let { client ->
            val updatedClient = client.copy(minorUrlDocument = "")
            documentRepository.deleteMinorConsent(clientId)
                .andThen(repository.update(updatedClient).ignoreElement())
                .applySchedulers()
                .withLoading(isLoadingLD)
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        documentPathLD.value = ""
                        clientLD.value = updatedClient
                    }
                    override fun onError(e: Throwable) { errorLD.value = R.string.pdf_sign_error_delete_document }
                })
        }
    }

    fun actionLoadMinorConsentDocument() {
        documentRepository.getMinorConsentUrl(clientId)
            .applySchedulers()
            .withLoading(isLoadingLD)
            .subscribe(object : DisposableSingleObserver<String>() {
                override fun onSuccess(url: String) { documentPathLD.value = url }
                override fun onError(e: Throwable) { documentPathLD.value = "" }
            })
    }

    private fun loadData() {
        repository.find(clientId)
            .applySchedulers()
            .withLoading(isLoadingLD)
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