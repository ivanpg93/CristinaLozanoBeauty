package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.databinding.ItemClientBirthdayBinding

class ClientBirthdayListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemClientBirthdayBinding.bind(view)

    fun bind(
        clientBirthday: ClientListDTO,
        onItemSelected: (ClientListDTO) -> Unit,
    ) {
        binding.txtName.text = String.format("%s %s", clientBirthday.firstName, clientBirthday.lastName)
        binding.root.setOnClickListener { onItemSelected(clientBirthday) }
    }

}