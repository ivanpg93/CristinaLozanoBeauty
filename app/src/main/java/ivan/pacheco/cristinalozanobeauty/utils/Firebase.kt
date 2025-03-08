package ivan.pacheco.cristinalozanobeauty.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ivan.pacheco.cristinalozanobeauty.BuildConfig

object FirebaseManager {

    fun init(appContext: Context) {
        FirebaseApp.initializeApp(appContext)

        // Enable Crashlytics and Analytics only in production
        val isProduction = !BuildConfig.DEBUG
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = isProduction
        FirebaseAnalytics.getInstance(appContext).setAnalyticsCollectionEnabled(isProduction)
    }

}