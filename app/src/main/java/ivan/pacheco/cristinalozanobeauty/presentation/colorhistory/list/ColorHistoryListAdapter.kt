package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.R
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color

class ColorHistoryListAdapter(
    private val onItemSelected: (Color) -> Unit,
    private val onItemDeleted: (Color) -> Unit
) : RecyclerView.Adapter<ColorHistoryListViewHolder>() {

    private var colorHistoryList: List<Color> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHistoryListViewHolder {
        return ColorHistoryListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_color_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorHistoryListViewHolder, position: Int) {
        holder.bind(colorHistoryList[position], onItemSelected, onItemDeleted)
    }

    override fun getItemCount(): Int = colorHistoryList.size

    @SuppressLint("NotifyDataSetChanged")
    fun reload(colorHistoryList: List<Color>) {
        this.colorHistoryList = colorHistoryList
        notifyDataSetChanged()
    }

}