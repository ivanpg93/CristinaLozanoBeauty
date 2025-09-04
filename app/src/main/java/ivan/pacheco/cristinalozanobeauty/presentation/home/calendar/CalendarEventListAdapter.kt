package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO

class CalendarEventListAdapter(): RecyclerView.Adapter<CalendarEventListViewHolder>() {

    private var clientList: List<ClientListDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventListViewHolder {
        return CalendarEventListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calendar_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarEventListViewHolder, position: Int) {
        holder.bind(clientList[position])
    }

    override fun getItemCount(): Int = clientList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(clientList: List<ClientListDTO>) {
        this.clientList = clientList
        notifyDataSetChanged()
    }

}