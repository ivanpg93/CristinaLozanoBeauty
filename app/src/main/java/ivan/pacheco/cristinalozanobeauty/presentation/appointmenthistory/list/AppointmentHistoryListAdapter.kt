package ivan.pacheco.cristinalozanobeauty.presentation.appointmenthistory.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

class AppointmentHistoryListAdapter(
    private val onItemSelected: (Appointment) -> Unit,
    private val onItemUpdated: (Appointment) -> Unit,
    private val onItemDeleted: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentHistoryListViewHolder>() {

    private var appointmentHistoryList: List<Appointment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentHistoryListViewHolder {
        return AppointmentHistoryListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_appointment_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AppointmentHistoryListViewHolder, position: Int) {
        holder.bind(appointmentHistoryList[position], onItemSelected, onItemUpdated, onItemDeleted)
    }

    override fun getItemCount(): Int = appointmentHistoryList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(appointmentHistoryList: List<Appointment>) {
        this.appointmentHistoryList = appointmentHistoryList
        notifyDataSetChanged()
    }

}