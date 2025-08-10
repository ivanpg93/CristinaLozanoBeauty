package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.databinding.EventItemViewBinding
import java.time.LocalDate
import java.time.LocalTime

data class Event(val id: String, val text: String, val date: LocalDate, val startTime: LocalTime, val endTime: LocalTime)

class EventsAdapter(val onClick: (Event) -> Unit): RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(
            EventItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(viewHolder: EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class EventsViewHolder(private val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onClick(events[bindingAdapterPosition])
            }
        }

        fun bind(event: Event) {
            binding.txtEventTitle.text = event.text
            binding.txtEventTime.text = "${event.startTime} - ${event.endTime}"
        }
    }

}