package ivan.pacheco.cristinalozanobeauty.presentation.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.event.application.usecase.CreateEventUC
import ivan.pacheco.cristinalozanobeauty.core.event.application.usecase.DeleteEventUC
import ivan.pacheco.cristinalozanobeauty.core.event.application.usecase.UpdateEventUC
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEventDTO
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.SecureTokenDataStore
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val clientRepository: ClientRepository,
    private val createEventUC: CreateEventUC,
    private val updateEventUC: UpdateEventUC,
    private val deleteEventUC: DeleteEventUC,
    private val application: Application
) : ViewModel() {

    private companion object {
        const val SCOPE_OAUTH2 = "oauth2:https://www.googleapis.com/auth/calendar"
    }

    private var idToken: String? = null
    private var selectedDate: LocalDate = LocalDate.now()

    // LiveData
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()
    private val eventsLD = MutableLiveData<List<CalendarEvent>>()
    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val recoverableExceptionLD = MutableLiveData<UserRecoverableAuthException>()

    init {
        loadClients()
    }

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getEventsLD(): LiveData<List<CalendarEvent>> = eventsLD
    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD
    fun getRecoverableExceptionLD(): LiveData<UserRecoverableAuthException> = recoverableExceptionLD

    // Actions
    fun onGoogleAccountReady(account: GoogleSignInAccount) {
        val localDate = LocalDate.parse(LocalDate.now().toString())
        val (startDate, endDate) = getMonthRange(localDate)
        getAccessTokenRx(application.applicationContext, account)
            .flatMap { token -> eventRepository.getEventsForDate(startDate, endDate, token) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<CalendarEvent>>() {
                override fun onSuccess(events: List<CalendarEvent>) { eventsLD.value = events }
                override fun onError(e: Throwable) {
                    if (e is UserRecoverableAuthException) {
                        recoverableExceptionLD.value = e
                    } else {
                        errorLD.value = R.string.appointment_history_list_error_list
                    }
                }
            })
    }

    fun onDateSelected(date: LocalDate) {
        idToken?.let { token ->
            selectedDate = date
            val (startDate, endDate) = getMonthRange(selectedDate)
            eventRepository.getEventsForDate(startDate, endDate, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableSingleObserver<List<CalendarEvent>>() {
                    override fun onSuccess(events: List<CalendarEvent>) { eventsLD.value = events }
                    override fun onError(e: Throwable) { errorLD.value = R.string.appointment_history_list_error_list }
                })
        }
    }

    fun actionCreateEvent(event: CalendarEventDTO, client: ClientListDTO) {
        idToken?.let { token ->
            createEventUC.execute(event, client, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { onDateSelected(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_create }
                })
        }
    }

    fun actionUpdateEvent(calendarEventDTO: CalendarEventDTO) {
        idToken?.let { token ->
            updateEventUC.execute(calendarEventDTO, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableSingleObserver<String>() {
                    override fun onSuccess(eventId: String) { onDateSelected(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_update }
                })
        }
    }

    fun actionDeleteEvent(eventId: String) {
        idToken?.let { token ->
            deleteEventUC.execute(eventId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { onDateSelected(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_delete }
                })
        }
    }

    private fun getAccessTokenRx(context: Context, account: GoogleSignInAccount): Single<String> {
        return Single.create { emitter: SingleEmitter<String> ->
            try {
                account.account?.let { account ->
                    val token = GoogleAuthUtil.getToken(context, account, SCOPE_OAUTH2)
                    idToken = token

                    viewModelScope.launch {
                        try {
                            SecureTokenDataStore.saveToken(context, token)
                        } catch (e: Exception) {
                            // Log si falla el guardado, pero no cancelamos el Single
                            e.printStackTrace()
                        }
                    }

                    emitter.onSuccess(token)
                }
            } catch (e: Exception) { emitter.onError(e) }
        }.subscribeOn(Schedulers.io())
    }

    private fun getMonthRange(date: LocalDate): Pair<String, String> {
        val startOfMonth = date.withDayOfMonth(1)
        val endOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return startOfMonth.format(formatter) to endOfMonth.format(formatter)
    }

    private fun loadClients() {
        clientRepository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<ClientListDTO>>() {
                override fun onSuccess(clients: List<ClientListDTO>) { clientsLD.value = clients }
                override fun onError(error: Throwable) { errorLD.value = R.string.client_list_error_list }
            })
    }

}
