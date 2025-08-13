package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.databinding.EventItemViewBinding
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: String,
    val text: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    var assisted: Boolean? = null
)

class EventsAdapter(
    val onClick: (Event) -> Unit,
    val deleteAction: (Event) -> Unit
): RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private lateinit var context: Context
    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        context = parent.context
        return EventsViewHolder(
            EventItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                val event = events[bindingAdapterPosition]
                event.assisted = !(event.assisted ?: false)
                notifyItemChanged(bindingAdapterPosition)
                onClick(event)
            }
        }

        fun bind(event: Event) {
            binding.txtEventTitle.text = event.text
            if (event.assisted == true){
                binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assisted_event))
                binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.green_700)
            } else {
                binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_assisted_event))
                binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
            }
            binding.txtEventTime.text = "${event.startTime} - ${event.endTime}"
            binding.btnDelete.setOnClickListener { deleteAction(event) }
        }
    }

}