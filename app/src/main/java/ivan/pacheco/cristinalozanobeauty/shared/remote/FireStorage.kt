package ivan.pacheco.cristinalozanobeauty.shared.remote

import com.google.firebase.storage.FirebaseStorage

object FireStorage {

    private const val CLIENT_DOCUMENTS = "client_documents"
    private const val MINOR_CONSENT_PDF = "minor_consent.pdf"

    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    fun clientMinorDocument(clientId: String): String = "$CLIENT_DOCUMENTS/$clientId/$MINOR_CONSENT_PDF"

}