package ivan.pacheco.cristinalozanobeauty.presentation.pdf

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientDocumentRepository
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Destination
import ivan.pacheco.cristinalozanobeauty.presentation.utils.Navigation
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PdfSignViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val documentRepository: ClientDocumentRepository,
    state: SavedStateHandle
): ViewModel(), Navigation {

    private companion object {
        const val ARG_CLIENT_ID = "clientId"
    }

    // LiveData
    override val navigationLD = SingleLiveEvent<Destination>()
    private val clientLD = MutableLiveData<Client>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = SingleLiveEvent<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun getClientLD(): LiveData<Client> = clientLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    init {
        loadClient()
    }

    // Actions
    fun actionSaveSignedPdf(pdfUri: Uri) {
        documentRepository.uploadMinorConsent(clientId, pdfUri)
            .subscribeOn(Schedulers.io())
            .flatMap { storagePath ->
                clientRepository.find(clientId).flatMap { client ->
                    clientRepository.update(client.copy(minorUrlDocument = storagePath))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<Client>() {
                override fun onSuccess(client: Client) { navigationLD.value = Destination.Back }
                override fun onError(e: Throwable) { errorLD.value = R.string.pdf_sign_error_save_document }
            })
    }

    private fun loadClient() {
        clientRepository.find(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object: DisposableSingleObserver<Client>() {
                override fun onSuccess(client: Client) { clientLD.value = client }
                override fun onError(e: Throwable) { errorLD.value = R.string.client_detail_error_find }
            })
    }

}