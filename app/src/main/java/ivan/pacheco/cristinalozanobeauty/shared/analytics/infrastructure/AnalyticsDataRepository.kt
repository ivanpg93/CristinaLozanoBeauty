package ivan.pacheco.cristinalozanobeauty.shared.analytics.infrastructure

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import ivan.pacheco.cristinalozanobeauty.BuildConfig
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.model.AnalyticsEvent
import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class AnalyticsDataRepository @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
): AnalyticsRepository {

    override fun logEvent(event: AnalyticsEvent) {
        if (!BuildConfig.DEBUG) {
            val bundle = Bundle().apply {
                event.params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                    }
                }
            }
            firebaseAnalytics.logEvent(event.name, bundle)
        }
    }

}