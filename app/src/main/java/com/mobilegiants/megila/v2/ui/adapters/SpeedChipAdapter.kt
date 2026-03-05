package com.mobilegiants.megila.v2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilegiants.megila.v2.R

class SpeedChipAdapter(
    private val items: List<String>,
    initialSelected: Int,
    private val onSelected: (Int) -> Unit
) : RecyclerView.Adapter<SpeedChipAdapter.ChipViewHolder>() {

    var selectedPosition: Int = initialSelected
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_speed_chip, parent, false) as TextView
        return ChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            val old = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(old)
            notifyItemChanged(selectedPosition)
            onSelected(selectedPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    class ChipViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(text: String, isSelected: Boolean) {
            textView.text = text
            if (isSelected) {
                textView.setBackgroundResource(R.drawable.bg_speed_chip_selected)
                textView.setTextColor(textView.context.getColor(R.color.dialog_speed_selected_text))
            } else {
                textView.setBackgroundResource(R.drawable.bg_speed_chip_unselected)
                textView.setTextColor(textView.context.getColor(R.color.dialog_speed_unselected_text))
            }
        }
    }
}
