package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.databinding.ItemClientBinding

class ClientListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemClientBinding.bind(view)

    fun bind(
        client: ClientListDTO,
        onItemSelected: (ClientListDTO) -> Unit,
        onItemDeleted: (ClientListDTO) -> Unit
    ) {
        binding.txtName.text = String.format("%s %s", client.firstName, client.lastName)
        binding.txtPhone.text = client.phone.removePrefix("+34")
        binding.btnDelete.setOnClickListener { onItemDeleted(client) }
        binding.root.setOnClickListener { onItemSelected(client) }
    }

}