package com.example.recommender.ui.foryou

import com.example.recommender.data.remote.aws.dto.AwsRecommendation


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifetracker.databinding.ItemRecommendationrecBinding


class RecommendationCardAdapter :
    ListAdapter<AwsRecommendation, RecommendationCardAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendationrecBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRecommendationrecBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rec: AwsRecommendation) {
            binding.tvRecTitle.text       = rec.title
            binding.tvRecMeta.text        = rec.authorOrDirector ?: ""
            binding.tvRecSummary.text     = rec.summarySnippet ?: ""
            binding.tvRecScore.text       = "Match: ${rec.score ?: 0}"

            val posterUrl = rec.posterPath?.let {
                if (it.startsWith("http")) it
                else "https://image.tmdb.org/t/p/w342$it"
            }

            Glide.with(binding.root)
                .load(posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgRecPoster)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AwsRecommendation>() {
        override fun areItemsTheSame(a: AwsRecommendation, b: AwsRecommendation) =
            a.title == b.title
        override fun areContentsTheSame(a: AwsRecommendation, b: AwsRecommendation) =
            a == b
    }
}