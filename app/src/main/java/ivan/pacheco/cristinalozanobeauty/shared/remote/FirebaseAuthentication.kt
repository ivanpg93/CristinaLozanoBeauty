package ivan.pacheco.cristinalozanobeauty.shared.remote

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthentication {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

}