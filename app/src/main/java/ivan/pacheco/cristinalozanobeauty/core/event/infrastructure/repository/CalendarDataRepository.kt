package ivan.pacheco.cristinalozanobeauty.core.event.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.CalendarRepository
import ivan.pacheco.cristinalozanobeauty.presentation.home.GoogleCalendarApi
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalendarDataRepository @Inject constructor(
    private val googleCalendarApi: GoogleCalendarApi
) : CalendarRepository {

    override fun getEventsForDate(date: String, token: String): Single<List<CalendarEvent>> {
        return Single.fromCallable {
            val dateFormatted = LocalDate.parse(date)
            val timeMin = dateFormatted.atStartOfDay().atZone(ZoneId.of("UTC")).format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val timeMax = dateFormatted.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC")).format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val response = runBlocking {
                googleCalendarApi.getEvents(
                    authHeader = "Bearer $token",
                    timeMin = timeMin,
                    timeMax = timeMax
                )
            }
            response.items
                .filter { it.start?.dateTime != null }
                .map { item ->
                CalendarEvent(
                    id = item.id,
                    summary = item.summary,
                    description = item.description,
                    startDateTime = item.start.dateTime,
                    endDateTime = item.end.dateTime
                )
            }
        }
    }

    override fun createEvent(event: CalendarEvent, token: String): Single<CalendarEvent> {
        return Single.fromCallable {
            val zoneId = ZoneId.of("Europe/Madrid")

            val start = LocalDateTime.parse(event.startDateTime)
            val end = LocalDateTime.parse(event.endDateTime)

            val startFormatted = start.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val endFormatted = end.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val request = GoogleCalendarEventRequest(
                summary = event.summary,
                description = event.description,
                start = EventDateTime(dateTime = startFormatted, timeZone = "Europe/Madrid"),
                end = EventDateTime(dateTime = endFormatted, timeZone = "Europe/Madrid")
            )

            val response = runBlocking {
                googleCalendarApi.createEvent("Bearer $token", request)
            }

            CalendarEvent(
                id = response.id,
                summary = response.summary,
                description = response.description,
                startDateTime = response.start.dateTime,
                endDateTime = response.end.dateTime
            )
        }
    }

    override fun deleteEvent(eventId: String, token: String): Completable {
        return Completable.fromCallable {
            val response = runBlocking {
                googleCalendarApi.deleteEvent("Bearer $token", eventId)
            }
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }
    }

}

data class GoogleCalendarEventRequest(
    val summary: String,
    val description: String?,
    val start: EventDateTime,
    val end: EventDateTime
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String = "Europe/Madrid"
)
