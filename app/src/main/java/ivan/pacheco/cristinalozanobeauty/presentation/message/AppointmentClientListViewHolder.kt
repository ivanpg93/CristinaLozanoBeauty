package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.AppointmentClient
import ivan.pacheco.cristinalozanobeauty.databinding.ItemAppointmentResumeBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toHour
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName

class AppointmentClientListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemAppointmentResumeBinding.bind(view)

    fun bind(
        appointmentClient: AppointmentClient,
        onItemSelected: (AppointmentClient) -> Unit,
    ) {
        val client = appointmentClient.client
        val event = appointmentClient.appointment.event
        binding.txtClient.text = String.format("%s %s", client.firstName, client.lastName)
        binding.txtService.text = event?.service?.toDisplayName()
        binding.txtTime.text = String.format("%s - %s", event?.startDateTime?.toHour(), event?.endDateTime?.toHour())
        binding.root.setOnClickListener { onItemSelected(appointmentClient) }
    }

}