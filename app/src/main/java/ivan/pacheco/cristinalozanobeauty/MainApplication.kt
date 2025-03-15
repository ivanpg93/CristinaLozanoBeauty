package ivan.pacheco.cristinalozanobeauty

import android.app.Application
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import ivan.pacheco.cristinalozanobeauty.shared.remote.FirebaseManager

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseManager.init(this)
        WorkManager.getInstance(this)
    }

}