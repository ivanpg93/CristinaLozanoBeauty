package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.client.application.usecase.ClientListDTO
import ivan.pacheco.cristinalozanobeauty.databinding.ItemClientBinding

class ClientListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemClientBinding.bind(view)

    fun bind(client: ClientListDTO, onItemSelected: (ClientListDTO) -> Unit) {
        binding.txtName.text = "${client.firstName} ${client.lastName}"
        binding.txtPhone.text = client.phone.removePrefix("+34")
        binding.root.setOnClickListener { onItemSelected(client) }
    }

}