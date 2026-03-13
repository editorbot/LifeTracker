package com.example.recommender.ui.add


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recommender.data.SearchResult
import com.example.recommender.databinding.ItemSearchResultBinding


class SearchResultAdapter(
    private val onSaveClick: (SearchResult) -> Unit
) : ListAdapter<SearchResult, SearchResultAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SearchResult) {
            binding.tvSearchTitle.text = result.title
            binding.tvSearchMeta.text = result.authorOrDirector ?: ""
            binding.tvSearchSummary.text = result.summary ?: ""
            binding.tvMatchScore.text = result.genres.take(3).joinToString(" • ")

            Glide.with(binding.root)
                .load(result.posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgPoster)

            binding.btnSave.setOnClickListener { onSaveClick(result) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchResult>() {
        override fun areItemsTheSame(a: SearchResult, b: SearchResult) =
            a.externalId == b.externalId
        override fun areContentsTheSame(a: SearchResult, b: SearchResult) = a == b
    }
}
