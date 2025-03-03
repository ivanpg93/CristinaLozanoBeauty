package ivan.pacheco.cristinalozanobeauty

import android.app.Application
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        WorkManager.getInstance(applicationContext)
    }

}