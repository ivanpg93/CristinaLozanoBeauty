package ivan.pacheco.cristinalozanobeauty.core.client.infrastructure.repository

import android.net.Uri
import com.google.firebase.storage.storageMetadata
import io.reactivex.Completable
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

    override fun uploadMinorConsent(clientId: String, url: Uri): Single<String> {
        return Single.create { emitter ->
            val path = FireStorage.clientMinorDocument(clientId)
            val ref = FireStorage.storage.reference.child(path)
            val metadata = storageMetadata { contentType = APPLICATION_PDF }

            ref.putFile(url, metadata)
                .addOnSuccessListener { emitter.onSuccess(path) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

    override fun deleteMinorConsent(clientId: String): Completable {
        return Completable.create { emitter ->
            val path = FireStorage.clientMinorDocument(clientId)
            val ref = FireStorage.storage.reference.child(path)

            ref.delete()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { error -> emitter.onError(error) }
        }
    }

}