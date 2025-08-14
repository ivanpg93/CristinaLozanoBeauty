package ivan.pacheco.cristinalozanobeauty.shared.remote

import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarEventRequest
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.GoogleCalendarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

object API {
    const val GOOGLE_CALENDAR_BASE_URL = "https://www.googleapis.com/"
    const val GOOGLE_CALENDAR_EVENTS_PATH = "calendar/v3/calendars/primary/events"
    const val AUTHORIZATION = "Authorization"
    const val EVENT_ID = "eventId"
}

interface GoogleCalendarApi {
    @GET(API.GOOGLE_CALENDAR_EVENTS_PATH)
    suspend fun getEvents(
        @Header(API.AUTHORIZATION) authHeader: String,
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String,
        @Query("singleEvents") singleEvents: Boolean = true,
        @Query("orderBy") orderBy: String = "startTime"
    ): GoogleCalendarResponse

    @GET("${API.GOOGLE_CALENDAR_EVENTS_PATH}/{${API.EVENT_ID}}")
    suspend fun getEvent(
        @Header(API.AUTHORIZATION) authHeader: String,
        @Path(API.EVENT_ID) eventId: String
    ): Response<GoogleCalendarEvent>

    @POST(API.GOOGLE_CALENDAR_EVENTS_PATH)
    suspend fun createEvent(
        @Header(API.AUTHORIZATION) authHeader: String,
        @Body event: GoogleCalendarEventRequest
    ): GoogleCalendarEvent

    @PUT("${API.GOOGLE_CALENDAR_EVENTS_PATH}/{${API.EVENT_ID}}")
    suspend fun updateEvent(
        @Header(API.AUTHORIZATION) authHeader: String,
        @Path(API.EVENT_ID) eventId: String,
        @Body event: GoogleCalendarEventRequest
    ): GoogleCalendarEvent

    @DELETE("${API.GOOGLE_CALENDAR_EVENTS_PATH}/{${API.EVENT_ID}}")
    suspend fun deleteEvent(
        @Header(API.AUTHORIZATION) authHeader: String,
        @Path(API.EVENT_ID) eventId: String
    ): Response<Unit>
}