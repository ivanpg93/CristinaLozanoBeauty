package ivan.pacheco.cristinalozanobeauty.core.event.infrastructure.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.EventDateTime
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.ExtendedProperties
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEventRequest
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.parseToZonedDateTime
import ivan.pacheco.cristinalozanobeauty.shared.remote.GoogleCalendarApi
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate
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
        const val SERVICE = "service"
        const val ASSISTED = "assisted"
    }

    override fun getEventsForDate(startDate: String, endDate: String, token: String): Single<List<CalendarEvent>> {
        return Single.fromCallable {
            val start = LocalDate.parse(startDate).atStartOfDay().atZone(ZoneId.of(UTC))
            val end = LocalDate.parse(endDate).atTime(LocalTime.MAX).atZone(ZoneId.of(UTC))

            val response = runBlocking {
                googleCalendarApi.getEvents(
                    authHeader = "$BEARER $token",
                    timeMin = start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    timeMax = end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
            }

            // Filter events only with end date time
            response.items
                .filter { it.end.dateTime != null }
                .map { item -> parseEvent(item) }
        }
    }

    override fun createEvent(event: CalendarEvent, token: String): Single<CalendarEvent> {
        return Single.fromCallable {
            val zoneId = ZoneId.of(SPAIN_ZONE)

            // Parse dates
            val startZoned = parseToZonedDateTime(event.startDateTime, zoneId)
            val endZoned = parseToZonedDateTime(event.endDateTime, zoneId)

            // Format dates with offset
            val startFormatted = startZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val endFormatted = endZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val request = GoogleCalendarEventRequest(
                summary = event.summary,
                description = event.description,
                start = EventDateTime(dateTime = startFormatted, timeZone = SPAIN_ZONE),
                end = EventDateTime(dateTime = endFormatted, timeZone = SPAIN_ZONE),
                extendedProperties = buildExtendedProperties(event.service, event.assisted)
            )

            val response = runBlocking { googleCalendarApi.createEvent("$BEARER $token", request) }

            // Get service and assisted from extendedProperties events
            val (service, assisted) = parseExtendedProperties(response.extendedProperties)

            CalendarEvent(
                id = response.id,
                summary = response.summary,
                description = response.description,
                startDateTime = response.start.dateTime,
                endDateTime = response.end.dateTime,
                service = service,
                assisted = assisted
            )
        }
    }

    override fun updateEvent(event: CalendarEvent, token: String): Single<CalendarEvent> {
        return Single.fromCallable {
            val zoneId = ZoneId.of(SPAIN_ZONE)

            // Parse dates
            val startZoned = parseToZonedDateTime(event.startDateTime, zoneId)
            val endZoned = parseToZonedDateTime(event.endDateTime, zoneId)

            // Format dates with offset
            val startFormatted = startZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val endFormatted = endZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val request = GoogleCalendarEventRequest(
                summary = event.summary,
                description = event.description,
                start = EventDateTime(dateTime = startFormatted, timeZone = SPAIN_ZONE),
                end = EventDateTime(dateTime = endFormatted, timeZone = SPAIN_ZONE),
                extendedProperties = buildExtendedProperties(event.service, event.assisted)
            )

            val response = runBlocking { googleCalendarApi.updateEvent("$BEARER $token", event.id, request) }

            // Get service and assisted from extendedProperties events
            val (service, assisted) = parseExtendedProperties(response.extendedProperties)

            CalendarEvent(
                id = response.id,
                summary = response.summary,
                description = null,
                startDateTime = response.start.dateTime,
                endDateTime = response.end.dateTime,
                service = service,
                assisted = assisted
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

    private fun buildExtendedProperties(service: Service?, assisted: Boolean): ExtendedProperties =
        ExtendedProperties(
            private = mapOf(
                SERVICE to (service?.name ?: ""),
                ASSISTED to assisted.toString()
            )
        )

    private fun parseExtendedProperties(props: ExtendedProperties?): Pair<Service?, Boolean> {
        if (props?.private.isNullOrEmpty()) return null to false
        val service = props.private[SERVICE]?.let { runCatching { Service.valueOf(it) }.getOrNull() }
        val assisted = props.private[ASSISTED]?.toBoolean() ?: false
        return service to assisted
    }

    private fun parseEvent(item: GoogleCalendarEvent): CalendarEvent {
        val (serviceFromProps, assistedFromProps) = parseExtendedProperties(item.extendedProperties)
        if (serviceFromProps != null || assistedFromProps) {
            return CalendarEvent(
                id = item.id,
                summary = item.summary,
                description = item.description,
                startDateTime = item.start.dateTime,
                endDateTime = item.end.dateTime,
                service = serviceFromProps,
                assisted = assistedFromProps
            )
        }

        // Old format (only service without assisted)
        val service = runCatching { Service.valueOf(item.description ?: "") }.getOrNull()

        return CalendarEvent(
            id = item.id,
            summary = item.summary,
            description = item.description,
            startDateTime = item.start.dateTime,
            endDateTime = item.end.dateTime,
            service = service,
            assisted = false
        )
    }

}
