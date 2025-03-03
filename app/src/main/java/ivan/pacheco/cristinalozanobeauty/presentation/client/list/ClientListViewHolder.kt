package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.databinding.ItemClientBinding

class ClientListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemClientBinding.bind(view)

    fun bind(client: Client, onItemSelected: (Client) -> Unit) {
        binding.txtName.text = "${client.firstName} ${client.lastName}"
        binding.txtPhone.text = client.phone
        binding.root.setOnClickListener { onItemSelected(client) }
    }

}