package com.example.recommender.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.example.recommender.data.MediaItem
import com.example.recommender.data.MediaType
import com.example.lifetracker.databinding.ItemMediarecBinding


class MediaAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, MediaAdapter.MediaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediarecBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MediaViewHolder(
        private val binding: ItemMediarecBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaItem) {
            binding.tvTitle.text = item.title
            binding.tvType.text = if (item.type == MediaType.MOVIE) "🎬 Movie" else "📚 Book"
            binding.ratingBar.rating = item.rating

            // Dynamically add genre chips
            binding.chipGroupGenres.removeAllViews()
            item.genres.forEach { genre ->
                val chip = Chip(binding.root.context)
                chip.text = genre
                chip.isClickable = false
                binding.chipGroupGenres.addView(chip)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(old: MediaItem, new: MediaItem) = old.id == new.id
        override fun areContentsTheSame(old: MediaItem, new: MediaItem) = old == new
    }
}