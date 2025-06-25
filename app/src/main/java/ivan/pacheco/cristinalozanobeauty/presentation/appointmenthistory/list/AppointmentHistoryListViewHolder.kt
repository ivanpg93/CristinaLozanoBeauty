package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.databinding.ItemAppointmentHistoryBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils.toLocalDate
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName

class AppointmentHistoryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemAppointmentHistoryBinding.bind(view)

    fun bind(
        appointment: Appointment,
        onItemSelected: (Appointment) -> Unit,
        onItemDeleted: (Appointment) -> Unit
    ) {
        binding.txtService.text = appointment.service?.toDisplayName()
        binding.txtDate.text = appointment.event?.startDateTime?.toLocalDate().toString()//DateUtils.formatDate(appointment.event.startDateTime)
        binding.btnDelete.setOnClickListener { onItemDeleted(appointment) }
        binding.root.setOnClickListener { onItemSelected(appointment) }
    }

}