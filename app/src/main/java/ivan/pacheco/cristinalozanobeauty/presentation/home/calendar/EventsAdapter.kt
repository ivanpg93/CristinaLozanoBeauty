package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Service
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import ivan.pacheco.cristinalozanobeauty.databinding.EventItemViewBinding
import java.time.LocalDate
import java.time.LocalTime

data class CalendarEventDTO(
    val id: String,
    val text: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val service: Service? = null,
    var assisted: Boolean = false
)

fun CalendarEventDTO.toCalendarEvent(): CalendarEvent {
    return CalendarEvent(
        id = id,
        summary = text,
        startDateTime = "${date}T${startTime}",
        endDateTime = "${date}T${endTime}",
        service = service,
        assisted = assisted
    )
}

class EventsAdapter(
    val onClick: (CalendarEventDTO) -> Unit,
    val assistedAction: (CalendarEventDTO) -> Unit,
    val deleteAction: (CalendarEventDTO) -> Unit
): RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private lateinit var context: Context
    val events = mutableListOf<CalendarEventDTO>()

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
                notifyItemChanged(bindingAdapterPosition)
                onClick(event)
            }
        }

        fun bind(calendarEventDTO: CalendarEventDTO) {
            binding.txtEventTitle.text = calendarEventDTO.text
            if (calendarEventDTO.assisted){
                binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assisted_event))
                binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.green_700)
            } else {
                binding.ivAssisted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_assisted_event))
                binding.ivAssisted.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
            }
            binding.txtEventTime.text = "${calendarEventDTO.startTime} - ${calendarEventDTO.endTime}"

            binding.ivAssisted.setOnClickListener {
                calendarEventDTO.assisted = !calendarEventDTO.assisted
                assistedAction(calendarEventDTO)
                notifyItemChanged(bindingAdapterPosition)
            }
            binding.btnDelete.setOnClickListener { deleteAction(calendarEventDTO) }
        }
    }

}