package ivan.pacheco.cristinalozanobeauty.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import kotlinx.coroutines.runBlocking
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private var idToken: String? = null

    // LiveData
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()
    private val eventsLD = MutableLiveData<List<CalendarEvent>>()

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getEventsLD(): LiveData<List<CalendarEvent>> = eventsLD

    fun onGoogleAccountReady(account: GoogleSignInAccount) {
        idToken = account.idToken
        onDateSelected(Date().toString())
    }

    fun onDateSelected(date: String) {
        idToken?.let { token ->
            calendarRepository.getEventsForDate(date, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableSingleObserver<List<CalendarEvent>>() {
                    override fun onSuccess(events: List<CalendarEvent>) { eventsLD.value = events }
                    override fun onError(e: Throwable) { errorLD.value = R.string.client_list_error_list }
                })
        }
    }

}

interface CalendarRepository {
    fun getEventsForDate(date: String, token: String): Single<List<CalendarEvent>>
}

interface GoogleCalendarApi {
    @GET("calendar/v3/calendars/primary/events")
    suspend fun getEvents(
        @Header("Authorization") authHeader: String,
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String,
        @Query("singleEvents") singleEvents: Boolean = true,
        @Query("orderBy") orderBy: String = "startTime"
    ): GoogleCalendarResponse
}

data class GoogleCalendarResponse(
    val items: List<GoogleCalendarEvent>
)

data class GoogleCalendarEvent(
    val id: String,
    val summary: String,
    val description: String?,
    val start: DateTimeData,
    val end: DateTimeData
)

data class DateTimeData(
    val dateTime: String
)

class CalendarDataRepository @Inject constructor(
    private val googleCalendarApi: GoogleCalendarApi
) : CalendarRepository {

    override fun getEventsForDate(date: String, token: String): Single<List<CalendarEvent>> {
        val timeMin = "${date}T00:00:00Z"
        val timeMax = "${date}T23:59:59Z"
        return Single.fromCallable {
            val response = runBlocking {
                googleCalendarApi.getEvents(
                    authHeader = "Bearer $token",
                    timeMin = timeMin,
                    timeMax = timeMax
                )
            }
            response.items.map { item ->
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
}
