package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.databinding.ItemAppointmentHistoryBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toHour
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName

class AppointmentHistoryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemAppointmentHistoryBinding.bind(view)

    fun bind(
        appointment: Appointment,
        onItemSelected: (Appointment) -> Unit,
        onItemDeleted: (Appointment) -> Unit
    ) {
        binding.txtService.text = appointment.service?.toDisplayName()
        binding.txtTime.text = "${appointment.event?.startDateTime?.toHour()} - ${appointment.event?.endDateTime?.toHour()}"
        binding.txtDate.text = appointment.event?.startDateTime?.toDate().toString()
        binding.btnDelete.setOnClickListener { onItemDeleted(appointment) }
        binding.root.setOnClickListener { onItemSelected(appointment) }
    }

}