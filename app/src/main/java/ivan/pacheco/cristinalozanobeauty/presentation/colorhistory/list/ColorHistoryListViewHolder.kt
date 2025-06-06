package ivan.pacheco.cristinalozanobeauty.presentation.colorhistory.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ivan.pacheco.cristinalozanobeauty.core.color.domain.model.Color
import ivan.pacheco.cristinalozanobeauty.databinding.ItemColorHistoryBinding
import ivan.pacheco.cristinalozanobeauty.presentation.utils.DateUtils
import ivan.pacheco.cristinalozanobeauty.presentation.utils.FormUtils.toDisplayName

class ColorHistoryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemColorHistoryBinding.bind(view)

    fun bind(
        color: Color,
        onItemSelected: (Color) -> Unit,
        onItemDeleted: (Color) -> Unit
    ) {
        binding.txtBrand.text = color.brand?.toDisplayName()
        binding.txtReference.text = color.reference
        binding.txtDate.text = DateUtils.formatDate(color.date)
        binding.btnDelete.setOnClickListener { onItemDeleted(color) }
        binding.root.setOnClickListener { onItemSelected(color) }
    }

}