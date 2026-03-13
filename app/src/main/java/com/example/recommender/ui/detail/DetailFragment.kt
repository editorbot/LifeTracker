package com.example.recommender.ui.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recommender.data.MediaItem
import com.example.recommender.data.MediaType
import com.example.recommender.databinding.FragmentDetailBinding
import com.example.recommender.ui.ViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch


class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadItem(args.mediaItemId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.item.collect { item ->
                item ?: return@collect
                binding.tvDetailTitle.text = item.title
                binding.tvDetailType.text =
                    if (item.type == MediaType.MOVIE) "🎬 Movie" else "📚 Book"
                binding.ratingBarDetail.rating = item.rating

                binding.chipGroupDetail.removeAllViews()
                item.genres.forEach { genre ->
                    val chip = Chip(requireContext())
                    chip.text = genre
                    chip.isClickable = false
                    binding.chipGroupDetail.addView(chip)
                }

                Glide.with(this@DetailFragment)
                    .load(item.posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.imgDetailPoster)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}