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
        onItemSelected: (Appointment) -> Unit,
        onItemDeleted: (Appointment) -> Unit
    ) {
        binding.txtService.text = appointment.service?.toDisplayName()
        if (appointment.assisted == true){
            binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assisted_event))
            binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.green_700)
        } else {
            binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_assisted_event))
            binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
        }
        binding.txtTime.text = "${appointment.event?.startDateTime?.toHour()} - ${appointment.event?.endDateTime?.toHour()}"
        binding.txtDate.text = appointment.event?.startDateTime?.toDate().toString()
        binding.btnDelete.setOnClickListener { onItemDeleted(appointment) }
        binding.root.setOnClickListener { onItemSelected(appointment) }
    }

}