package ivan.pacheco.cristinalozanobeauty.core.event.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent

interface CalendarRepository {
    fun getEventsForDate(date: String, token: String): Single<List<CalendarEvent>>
    fun createEvent(event: CalendarEvent, token: String): Single<CalendarEvent>
    fun deleteEvent(eventId: String, token: String): Completable
}