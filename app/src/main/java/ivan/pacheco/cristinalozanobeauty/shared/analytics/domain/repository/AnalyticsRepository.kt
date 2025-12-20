package ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.repository

import ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.model.AnalyticsEvent

fun interface AnalyticsRepository {
    fun logEvent(event: AnalyticsEvent)
}