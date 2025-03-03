package ivan.pacheco.cristinalozanobeauty.presentation.client.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client

class ClientListAdapter(
    private val onItemSelected: (Client) -> Unit
) : RecyclerView.Adapter<ClientListViewHolder>() {

    private var clientList: List<Client> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientListViewHolder {
        return ClientListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_client, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ClientListViewHolder, position: Int) {
        holder.bind(clientList[position], onItemSelected)
    }

    override fun getItemCount(): Int = clientList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(clientList: List<Client>) {
        this.clientList = clientList
        notifyDataSetChanged()
    }

}