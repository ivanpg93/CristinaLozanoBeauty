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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Single
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
import ivan.pacheco.cristinalozanobeauty.presentation.utils.RxUtils.applySchedulers
import ivan.pacheco.cristinalozanobeauty.presentation.utils.RxUtils.toResult
import ivan.pacheco.cristinalozanobeauty.presentation.utils.RxUtils.withLoading
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import ivan.pacheco.cristinalozanobeauty.shared.remote.FirebaseAuthentication
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
    private val errorLD = SingleLiveEvent<Int>()
    private val eventsLD = MutableLiveData<List<CalendarEvent>>()
    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val recoverableExceptionLD = MutableLiveData<UserRecoverableAuthException>()

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD
    fun getEventsLD(): LiveData<List<CalendarEvent>> = eventsLD
    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD
    fun getRecoverableExceptionLD(): LiveData<UserRecoverableAuthException> = recoverableExceptionLD

    // Actions
    fun onGoogleAccountReady(account: GoogleSignInAccount) {

        // Firebase Auth for DB rules
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        FirebaseAuthentication.auth.signInWithCredential(credential)
            .addOnSuccessListener {

                // Get and store access token for Google Calendar
                getAccessToken(application.applicationContext, account)
                    .applySchedulers()
                    .withLoading(isLoadingLD)
                    .subscribe(object : DisposableCompletableObserver() {

                        // Load clients and events today
                        override fun onComplete() { loadData() }
                        override fun onError(e: Throwable) {
                            if (e is UserRecoverableAuthException) {
                                recoverableExceptionLD.value = e
                            } else {
                                errorLD.value = R.string.appointment_history_list_error_list
                            }
                        }
                    })
            }
            .addOnFailureListener { errorLD.value = R.string.appointment_history_list_error_list }

    }

    fun actionLoadEvents(date: LocalDate) {
        idToken?.let { token ->
            selectedDate = date
            val (startDate, endDate) = getMonthRange(selectedDate)
            eventRepository.getEventsForDate(startDate, endDate, token)
                .applySchedulers()
                .withLoading(isLoadingLD)
                .subscribe(object : DisposableSingleObserver<List<CalendarEvent>>() {
                    override fun onSuccess(events: List<CalendarEvent>) { eventsLD.value = events }
                    override fun onError(e: Throwable) { errorLD.value = R.string.appointment_history_list_error_list }
                })
        }
    }

    fun actionCreateEvent(event: CalendarEventDTO, client: ClientListDTO) {
        idToken?.let { token ->
            createEventUC.execute(event, client, token)
                .applySchedulers()
                .withLoading(isLoadingLD)
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { actionLoadEvents(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_create }
                })
        }
    }

    fun actionUpdateEvent(calendarEventDTO: CalendarEventDTO) {
        idToken?.let { token ->
            updateEventUC.execute(calendarEventDTO, token)
                .applySchedulers()
                .withLoading(isLoadingLD)
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { actionLoadEvents(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_update }
                })
        }
    }

    fun actionDeleteEvent(eventId: String) {
        idToken?.let { token ->
            deleteEventUC.execute(eventId, token)
                .applySchedulers()
                .withLoading(isLoadingLD)
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { actionLoadEvents(selectedDate) }
                    override fun onError(e: Throwable) { errorLD.value = R.string.calendar_event_form_error_delete }
                })
        }
    }

    private fun getAccessToken(context: Context, account: GoogleSignInAccount): Completable {
        return Completable.create { emitter: CompletableEmitter ->
            try {
                account.account?.let { account ->

                    // Get new token
                    val token = GoogleAuthUtil.getToken(context, account, SCOPE_OAUTH2)
                    idToken = token

                    // Save token
                    viewModelScope.launch {
                        try {
                            SecureTokenDataStore.saveToken(context, token)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    emitter.onComplete()
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

    private fun loadData() {
        idToken?.let { token ->
            val (startDate, endDate) = getMonthRange(selectedDate)

            Single.zip(
                clientRepository.list().toResult(),
                eventRepository.getEventsForDate(startDate, endDate, token).toResult()
            ) { clients, events ->
                Pair(clients, events)
            }
                .applySchedulers()
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe({ (clientRes, eventRes) ->

                    // Clients
                    clientRes.onSuccess { clientsLD.value = it }
                        .onFailure { errorLD.value = R.string.client_list_error_list }

                    // Events
                    eventRes.onSuccess { eventsLD.value = it }
                        .onFailure { errorLD.value = R.string.appointment_history_list_error_list }

                }, { errorLD.value = R.string.appointment_history_list_error_list })
        }
    }

}
