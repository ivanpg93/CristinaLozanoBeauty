package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository

import android.net.Uri
import com.google.firebase.storage.storageMetadata
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientDocumentRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.FireStorage

class ClientDocumentDataRepository(): ClientDocumentRepository {

    private companion object {
        const val APPLICATION_PDF = "application/pdf"
    }

    override fun getMinorConsentUrl(clientId: String): Single<String> {
        return Single.create { emitter ->
            val path = FireStorage.clientMinorDocument(clientId)
            val ref = FireStorage.storage.reference.child(path)

            ref.downloadUrl
                .addOnSuccessListener { uri -> emitter.onSuccess(uri.toString()) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

    override fun uploadMinorConsent(clientId: String, localUri: Uri): Single<String> {
        return Single.create { emitter ->
            val path = FireStorage.clientMinorDocument(clientId)
            val ref = FireStorage.storage.reference.child(path)
            val metadata = storageMetadata { contentType = APPLICATION_PDF }

            ref.putFile(localUri, metadata)
                .addOnSuccessListener { emitter.onSuccess(path) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}