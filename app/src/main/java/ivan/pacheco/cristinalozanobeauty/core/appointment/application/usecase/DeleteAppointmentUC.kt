package ivan.pacheco.cristinalozanobeauty.core.appointment.application.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.event.domain.repository.EventRepository
import ivan.pacheco.cristinalozanobeauty.shared.remote.SecureTokenDataStore
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DeleteAppointmentUC @Inject constructor(
    private val eventRepository: EventRepository,
    private val repository: AppointmentRepository,
    @ApplicationContext private val context: Context
) {

    fun execute(eventId: String): Completable {
        return Single.fromCallable {
            runBlocking {
                SecureTokenDataStore.readToken(context)
            }
        }
            .flatMapCompletable { token ->
                eventRepository.deleteEvent(eventId, token).andThen(repository.delete(eventId))
            }
    }

}