package ivan.pacheco.cristinalozanobeauty.core.client.domain.repository

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single

interface ClientDocumentRepository {
    fun getMinorConsentUrl(clientId: String): Single<String>
    fun uploadMinorConsent(clientId: String, url: Uri): Single<String>
    fun deleteMinorConsent(clientId: String): Completable
}