package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.AppointmentClient

class AppointmentClientListAdapter(
    private val onItemSelected: (AppointmentClient) -> Unit,
): RecyclerView.Adapter<AppointmentClientListViewHolder>() {

    private var appointmentResumeList: List<AppointmentClient> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentClientListViewHolder {
        return AppointmentClientListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_appointment_client, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AppointmentClientListViewHolder, position: Int) {
        holder.bind(appointmentResumeList[position], onItemSelected)
    }

    override fun getItemCount(): Int = appointmentResumeList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(appointmentList: List<AppointmentClient>) {
        this.appointmentResumeList = appointmentList
        notifyDataSetChanged()
    }

}