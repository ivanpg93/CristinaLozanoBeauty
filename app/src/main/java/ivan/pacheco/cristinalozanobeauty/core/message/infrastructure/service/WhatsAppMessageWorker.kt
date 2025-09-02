package ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters

class WhatsAppMessageWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val MESSAGE = "message"
    }

    override fun doWork(): Result {
        val phoneNumber = inputData.getString(PHONE_NUMBER) ?: return Result.failure()
        val message = inputData.getString(MESSAGE) ?: return Result.failure()

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}".toUri()

                // Need this for execute in background thread
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            applicationContext.startActivity(intent)
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

}
