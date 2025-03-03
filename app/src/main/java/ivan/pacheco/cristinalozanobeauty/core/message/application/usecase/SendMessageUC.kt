package ivan.pacheco.cristinalozanobeauty.core.message.application.usecase

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.reactivex.Completable
import ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service.WhatsAppMessageWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SendMessageUC @Inject constructor(private val workManager: WorkManager) {

    fun execute(name: String, telephone: String, day: Int, month: Int, year: Int, hour: Int, minute: Int): Completable {
        return Completable.create { emitter ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, day, hour, minute, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Calcular el retraso hasta 24h antes
            val delayMillis = calendar.timeInMillis - System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
            /*if (delayMillis <= 0) {
                emitter.onError(Exception("La fecha debe ser al menos 24h en el futuro"))
                return@create
            }*/

            // Formatear el mensaje
            val formattedDate = "%02d/%02d/%d".format(day, month + 1, year)
            val formattedTime = "%02d:%02d".format(hour, minute)
            val message = "Hola $name, te recordamos tu cita para el $formattedDate a las $formattedTime."

            val phoneNumber = "34$telephone" // Número de WhatsApp (sin '+')

            // Datos para el Worker
            val data = Data.Builder()
                .putString("phoneNumber", phoneNumber)
                .putString("message", message)
                .build()

            // Programar el mensaje usando WorkManager
            val workRequest = OneTimeWorkRequestBuilder<WhatsAppMessageWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            // Enqueue el trabajo
            workManager.enqueue(workRequest)

            // Emitir evento de éxito
            emitter.onComplete()
        }
    }

}