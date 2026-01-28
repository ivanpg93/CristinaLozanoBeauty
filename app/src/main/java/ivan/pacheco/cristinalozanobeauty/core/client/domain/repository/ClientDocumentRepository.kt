package ivan.pacheco.cristinalozanobeauty.core.client.domain.repository

import android.net.Uri
import io.reactivex.Single

interface ClientDocumentRepository {
    fun getMinorConsentUrl(clientId: String): Single<String>
    fun uploadMinorConsent(clientId: String, localUri: Uri): Single<String>
}