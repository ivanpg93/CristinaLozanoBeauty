package ivan.pacheco.cristinalozanobeauty.shared.remote

import com.google.firebase.firestore.FirebaseFirestore

object Firestore {

    // Firestore singleton instance
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

}