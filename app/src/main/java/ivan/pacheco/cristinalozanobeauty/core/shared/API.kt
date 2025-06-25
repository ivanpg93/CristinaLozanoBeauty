package ivan.pacheco.cristinalozanobeauty.core.shared

import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEventRequest
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

object API {
    const val GOOGLE_CALENDAR_BASE_URL = "https://www.googleapis.com/"
    const val GOOGLE_CALENDAR_EVENTS_PATH = "calendar/v3/calendars/primary/events"
}

interface GoogleCalendarApi {
    @GET(API.GOOGLE_CALENDAR_EVENTS_PATH)
    suspend fun getEvents(
        @Header("Authorization") authHeader: String,
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String,
        @Query("singleEvents") singleEvents: Boolean = true,
        @Query("orderBy") orderBy: String = "startTime"
    ): GoogleCalendarResponse

    @POST(API.GOOGLE_CALENDAR_EVENTS_PATH)
    suspend fun createEvent(
        @Header("Authorization") authHeader: String,
        @Body event: GoogleCalendarEventRequest
    ): GoogleCalendarEvent

    @DELETE("${API.GOOGLE_CALENDAR_EVENTS_PATH}/{eventId}")
    suspend fun deleteEvent(
        @Header("Authorization") authHeader: String,
        @Path("eventId") eventId: String
    ): Response<Unit>
}