package ivan.pacheco.cristinalozanobeauty.core.message.application.usecase

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository.AppointmentRepository
import ivan.pacheco.cristinalozanobeauty.core.appointment.infrastructure.webservice.AppointmentNotFound
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service.WhatsAppMessageWorker
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

class SendMessageUC @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val MESSAGE = "message"
    }

    fun execute(client: ClientListDTO): Completable {
        return getNextAppointmentFromToday(client.id)
            .flatMapCompletable { nextAppointment ->
                Completable.create { emitter ->
                    nextAppointment.event?.let { event ->
                        val appointmentDateTime = OffsetDateTime.parse(event.startDateTime)

                        // Format date time for message
                        val localDateTime = appointmentDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        val formattedDate = "%02d/%02d/%d".format(
                            localDateTime.dayOfMonth,
                            localDateTime.monthValue,
                            localDateTime.year
                        )
                        val formattedTime = "%02d:%02d".format(
                            localDateTime.hour,
                            localDateTime.minute
                        )

                        // Build message
                        val message = context.getString(R.string.message_reminder_appointment, formattedDate, formattedTime)

                        // WorkManager data
                        val phoneNumber = client.phone.removePrefix("+")
                        val data = Data.Builder()
                            .putString(PHONE_NUMBER, phoneNumber)
                            .putString(MESSAGE, message)
                            .build()

                        // Create and execute worker
                        val workRequest = OneTimeWorkRequestBuilder<WhatsAppMessageWorker>()
                            .setInputData(data)
                            .build()
                        workManager.enqueue(workRequest)

                        emitter.onComplete()
                    }
                }
            }
    }

    fun getNextAppointmentFromToday(clientId: String) =
        appointmentRepository.list(clientId)
            .map { list ->
                val now = OffsetDateTime.now()
                val futureAppointments = list.filter { appointment ->
                    appointment.event?.startDateTime?.let { start ->
                        val dateTime = OffsetDateTime.parse(start)
                        !dateTime.isBefore(now)
                    } ?: false
                }
                futureAppointments.firstOrNull() ?: throw AppointmentNotFound()
            }

}