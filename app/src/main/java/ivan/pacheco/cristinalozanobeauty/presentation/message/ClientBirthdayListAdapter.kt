package ivan.pacheco.cristinalozanobeauty.presentation.message

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.ClientListDTO

class ClientBirthdayListAdapter(
    private val onItemSelected: (ClientListDTO) -> Unit,
): RecyclerView.Adapter<ClientBirthdayListViewHolder>() {

    private var clientBirthdayList: List<ClientListDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientBirthdayListViewHolder {
        return ClientBirthdayListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_client_birthday, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ClientBirthdayListViewHolder, position: Int) {
        holder.bind(clientBirthdayList[position], onItemSelected)
    }

    override fun getItemCount(): Int = clientBirthdayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(clientBirthdayList: List<ClientListDTO>) {
        this.clientBirthdayList = clientBirthdayList
        notifyDataSetChanged()
    }

}