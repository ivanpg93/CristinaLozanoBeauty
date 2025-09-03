package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.DeleteAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase.UpdateAppointmentUC
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import javax.inject.Inject

@HiltViewModel
class AppointmentHistoryListViewModel @Inject constructor(
    private val repository: AppointmentRepository,
    private val updateAppointmentUC: UpdateAppointmentUC,
    private val deleteAppointmentUC: DeleteAppointmentUC,
    state: SavedStateHandle
): ViewModel() {

    companion object {
        const val ARG_CLIENT_ID = "clientId"
    }

    // LiveData
    private val madeAppointmentListLD = MutableLiveData<List<Appointment>>()
    private val pendingAppointmentListLD = MutableLiveData<List<Appointment>>()
    private val clientIdLD = MutableLiveData<String>()
    private val isLoadingLD = MutableLiveData<Boolean>()
    private val errorLD = MutableLiveData<Int>()

    // Use client id to retrieve client information
    private val clientId: String = state.getLiveData<String>(ARG_CLIENT_ID).value!!

    // Getters
    fun getMadeAppointmentListLD(): LiveData<List<Appointment>> = madeAppointmentListLD
    fun getPendingAppointmentListLD(): LiveData<List<Appointment>> = pendingAppointmentListLD
    fun getClientIdLD(): LiveData<String> = clientIdLD
    fun isLoadingLD(): LiveData<Boolean> = isLoadingLD
    fun getErrorLD(): LiveData<Int> = errorLD

    init {
        clientIdLD.value = clientId
    }

    // Actions
    fun actionUpdateAppointment(appointment: Appointment) {
        appointment.event?.let { event ->
            updateAppointmentUC.execute(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { loadData() }
                    override fun onError(e: Throwable) { errorLD.value = R.string.appointment_history_list_error_update }
                })
        }
    }

    fun actionDeleteAppointment(appointment: Appointment) {
        appointment.event?.id?.let { eventId ->
            deleteAppointmentUC.execute(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLD.value = true }
                .doFinally { isLoadingLD.value = false }
                .subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() { loadData() }
                    override fun onError(e: Throwable) { errorLD.value = R.string.appointment_history_list_error_delete }
                })
        }
    }

    fun loadData() {
        loadAppointments(repository.listPast(clientId)) { madeAppointmentListLD.value = it }
        loadAppointments(repository.listPending(clientId)) { pendingAppointmentListLD.value = it }
    }

    private fun loadAppointments(
        single: Single<List<Appointment>>,
        onSuccess: (List<Appointment>) -> Unit
    ) {
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingLD.value = true }
            .doFinally { isLoadingLD.value = false }
            .subscribe(object : DisposableSingleObserver<List<Appointment>>() {
                override fun onSuccess(appointmentList: List<Appointment>) = onSuccess(appointmentList)
                override fun onError(e: Throwable) { errorLD.value = R.string.appointment_history_list_error_list }
            })
    }

}