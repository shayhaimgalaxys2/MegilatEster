package com.mobilegiants.megila.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobilegiants.megila.R
import com.mobilegiants.megila.data.Song

class SongsAdapter(
    private val onSongClick: (Int) -> Unit
) : ListAdapter<Song, SongsAdapter.SongViewHolder>(SongDiffCallback()) {

    private var selectedPosition = -1

    fun setSelectedPosition(position: Int) {
        val old = selectedPosition
        selectedPosition = position
        if (old >= 0) notifyItemChanged(old)
        if (position >= 0) notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song, position == selectedPosition)
        holder.itemView.setOnClickListener { onSongClick(position) }
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTv: TextView = itemView.findViewById(R.id.songTitle)

        fun bind(song: Song, isSelected: Boolean) {
            titleTv.text = song.title
            itemView.alpha = if (isSelected) 1f else 0.7f
        }
    }

    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
    }
}
