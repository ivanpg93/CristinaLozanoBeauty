package ivan.pacheco.cristinalozanobeauty.core.message.infrastructure.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class WhatsAppMessageWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val phoneNumber = inputData.getString("phoneNumber") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Necesario para ejecutarse en segundo plano
            }
            applicationContext.startActivity(intent)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
            Result.failure()
        }
    }
}
