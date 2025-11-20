package ivan.pacheco.cristinalozanobeauty.core.message.application.usecase

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.AppointmentClient
import ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service.WhatsAppMessageWorker
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

class SendAppointmentReminderUC @Inject constructor(
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val MESSAGE = "message"
        const val DATE_FORMAT = "%02d/%02d/%d"
        const val TIME_FORMAT = "%02d:%02d"
    }

    fun execute(appointmentClient: AppointmentClient): Completable {
        return Completable.create { emitter ->
            val appointmentDateTime = OffsetDateTime.parse(appointmentClient.appointment.event?.startDateTime)

            // Format date time for message
            val localDateTime =
                appointmentDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            val formattedDate = DATE_FORMAT.format(
                localDateTime.dayOfMonth,
                localDateTime.monthValue,
                localDateTime.year
            )
            val formattedTime = TIME_FORMAT.format(
                localDateTime.hour,
                localDateTime.minute
            )

            // Build message
            val message = context.getString(
                R.string.message_reminder_appointment_message,
                appointmentClient.client.firstName,
                formattedDate,
                formattedTime
            )

            // WorkManager data
            val phoneNumber = appointmentClient.client.phone.removePrefix("+")
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