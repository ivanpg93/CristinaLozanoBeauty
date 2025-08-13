package ivan.pacheco.cristinalozanobeauty.core.event.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.EventDateTime
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEventRequest
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.GoogleCalendarApi
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class EventDataRepository @Inject constructor(
    private val googleCalendarApi: GoogleCalendarApi
) : EventRepository {

    private companion object {
        const val BEARER = "Bearer"
        const val SPAIN_ZONE = "Europe/Madrid"
        const val UTC = "UTC"
    }

    override fun getEventsForDate(startDate: String, endDate: String, token: String): Single<List<CalendarEvent>> {
        return Single.fromCallable {
            val start = LocalDate.parse(startDate)
            val end = LocalDate.parse(endDate)

            val timeMin = start.atStartOfDay()
                .atZone(ZoneId.of(UTC))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val timeMax = end.atTime(LocalTime.MAX)
                .atZone(ZoneId.of(UTC))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val response = runBlocking {
                googleCalendarApi.getEvents(
                    authHeader = "$BEARER $token",
                    timeMin = timeMin,
                    timeMax = timeMax
                )
            }
            response.items
                .filter { it.start.dateTime != null && it.end?.dateTime != null } // TODO: Fix nulls
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
            val zoneId = ZoneId.of(SPAIN_ZONE)

            val start = LocalDateTime.parse(event.startDateTime)
            val end = LocalDateTime.parse(event.endDateTime)

            val startFormatted = start.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val endFormatted = end.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val request = GoogleCalendarEventRequest(
                summary = event.summary,
                description = event.description,
                start = EventDateTime(dateTime = startFormatted, timeZone = SPAIN_ZONE),
                end = EventDateTime(dateTime = endFormatted, timeZone = SPAIN_ZONE)
            )

            val response = runBlocking { googleCalendarApi.createEvent("$BEARER $token", request) }

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
        return getEventById(eventId, token)
            .flatMapCompletable { response ->
                if (!response.isSuccessful) {
                    Completable.error(HttpException(response))
                } else {
                    Completable.fromCallable {
                        val deleteResponse = runBlocking {
                            googleCalendarApi.deleteEvent("$BEARER $token", eventId)
                        }
                        if (!deleteResponse.isSuccessful) {
                            throw HttpException(deleteResponse)
                        }
                    }
                }
            }
    }

    private fun getEventById(eventId: String, token: String): Single<Response<GoogleCalendarEvent>> =
        Single.fromCallable {
            runBlocking { googleCalendarApi.getEvent("$BEARER $token", eventId) }
        }

}
