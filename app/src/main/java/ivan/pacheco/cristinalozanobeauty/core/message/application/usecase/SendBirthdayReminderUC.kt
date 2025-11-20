package ivan.pacheco.cristinalozanobeauty.core.message.application.usecase

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service.WhatsAppMessageWorker
import javax.inject.Inject

class SendBirthdayReminderUC @Inject constructor(
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val MESSAGE = "message"
    }

    fun execute(client: ClientListDTO): Completable {
        return Completable.create { emitter ->

            val message = context.getString(R.string.message_reminder_birthday_message, client.firstName)

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