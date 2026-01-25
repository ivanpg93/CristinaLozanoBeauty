package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.databinding.ItemAppointmentHistoryBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toHour
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName

class AppointmentHistoryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemAppointmentHistoryBinding.bind(view)
    private val context = view.context

    fun bind(
        appointment: Appointment,
        onItemUpdated: (Appointment) -> Unit,
        onItemDeleted: (Appointment) -> Unit
    ) {
        val event = appointment.event
        binding.txtService.text = event?.service?.toDisplayName()
        if (appointment.event?.assisted == true){
            binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assisted_event))
            binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.green_700)
        } else {
            binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_assisted_event))
            binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
        }
        binding.txtTime.text = String.format("%s - %s", event?.startDateTime?.toHour(), event?.endDateTime?.toHour())
        binding.txtDate.text = event?.startDateTime?.toDate().toString()

        binding.ivAssisted.setOnClickListener { onItemUpdated(appointment) }
        binding.btnDelete.setOnClickListener { onItemDeleted(appointment) }
    }

}