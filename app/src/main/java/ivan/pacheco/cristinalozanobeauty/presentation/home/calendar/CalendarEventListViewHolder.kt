package ivan.pacheco.cristinalozanobeauty.presentation.home.calendar

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.databinding.ItemCalendarEventBinding

class CalendarEventListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCalendarEventBinding.bind(view)

    fun bind(client: ClientListDTO) {
        binding.txtClient.text = String.format("%s %s", client.firstName, client.lastName)
    }

}