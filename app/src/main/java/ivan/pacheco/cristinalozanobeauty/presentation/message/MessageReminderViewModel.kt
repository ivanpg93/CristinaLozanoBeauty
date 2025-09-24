package ivan.pacheco.cristinalozanobeauty.presentation.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.AppointmentClient
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.AppointmentNotFound
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.client.domain.repository.ClientRepository
import ivan.pacheco.cristinalozanobeauty.core.message.application.usecase.SendAppointmentReminderUC
import ivan.pacheco.cristinalozanobeauty.core.message.application.usecase.SendNextAppointmentReminderUC
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.SingleLiveEvent
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MessageReminderViewModel@Inject constructor(
    private val clientRepository: ClientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val sendNextAppointmentReminderUC: SendNextAppointmentReminderUC,
    private val sendAppointmentReminderUC: SendAppointmentReminderUC
): ViewModel() {

    // LiveData
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = SingleLiveEvent<Int>()
    private val clientsLD = MutableLiveData<List<ClientListDTO>>()
    private val appointmentClientListLD = MutableLiveData<List<AppointmentClient>>()

    // Getters
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD

    fun getErrorLD(): LiveData<Int> = errorLD
    fun getClientsLD(): LiveData<List<ClientListDTO>> = clientsLD
    fun getAppointmentClientListLD(): LiveData<List<AppointmentClient>> = appointmentClientListLD

    init {
        loadClients()
    }

    fun loadAppointmentsForDate(date: LocalDate) {
        clientRepository.list()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { clients ->
                val singles = clients.map { client ->
                    appointmentRepository.listPending(client.id)
                        .subscribeOn(Schedulers.io())
                        .map { appointments ->

                            // Relate each appointment to your client
                            appointments.map { appointment -> AppointmentClient(appointment, client) }
                        }
                }
                Single.zip(singles) { results ->
                    results.flatMap { it as List<AppointmentClient> }
                        .filter { it.appointment.event?.startDateTime?.toLocalDate() == date }
                        .sortedBy { it.appointment.event?.startDateTime }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<AppointmentClient>>() {
                override fun onSuccess(appointments: List<AppointmentClient>) { appointmentClientListLD.value = appointments }
                override fun onError(e: Throwable) { errorLD.value = R.string.message_appointment_not_found }
            })
    }

    fun actionSendAppointmentReminder(appoinmentClient: AppointmentClient) {
        sendAppointmentReminderUC.execute(appoinmentClient)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {} // Do nothing
                override fun onError(e: Throwable) { errorLD.value = R.string.message_sending_reminder_error }
            })
    }

    fun actionSendNextAppointmentReminder(client: ClientListDTO) {
        sendNextAppointmentReminderUC.execute(client)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {} // Do nothing
                override fun onError(e: Throwable) {
                    when(e) {
                        is AppointmentNotFound -> errorLD.value = R.string.message_appointment_not_found
                        else -> errorLD.value = R.string.message_sending_reminder_error
                    }
                }
            })
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