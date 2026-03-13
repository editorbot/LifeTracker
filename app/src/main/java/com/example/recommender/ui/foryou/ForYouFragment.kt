package com.example.recommender.ui.foryou


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.lifetracker.R
import com.example.lifetracker.databinding.FragmentForYourecBinding
import com.example.recommender.data.MediaType
import com.example.recommender.ui.ViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch


class ForYouFragment : Fragment() {

    private var _binding: FragmentForYourecBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForYouViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val adapter = RecommendationCardAdapter()

    // All available genres — same as your Flutter app
    private val movieGenres = listOf(
        "Action", "Adventure", "Animation", "Comedy", "Crime",
        "Documentary", "Drama", "Family", "Fantasy", "History",
        "Horror", "Music", "Mystery", "Romance", "Science Fiction", "Thriller"
    )

    private val bookGenres = listOf(
        "Fiction", "Mystery", "Thriller", "Romance", "Science Fiction",
        "Fantasy", "Biography", "History", "Self-Help", "Horror",
        "Adventure", "Comedy", "Drama", "Crime"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYourecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerRecommendations.adapter = adapter

        setupToggle()
        setupGetRecommendationsButton()
        observeViewModel()
    }

    private fun setupToggle() {
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = if (checkedId == R.id.btn_movies)
                    MediaType.MOVIE else MediaType.BOOK
                viewModel.setSelectedType(type)
                rebuildChips(type)
                adapter.submitList(emptyList())
            }
        }
        // Build initial chips for MOVIE (default)
        rebuildChips(MediaType.MOVIE)
    }

    // Rebuilds the chip group based on type
    // Pre-selects genres from the user's watch history
    private fun rebuildChips(type: MediaType) {
        val genres = if (type == MediaType.MOVIE) movieGenres else bookGenres
        val preSelected = if (type == MediaType.MOVIE)
            viewModel.topMovieGenres.value.toSet()
        else
            viewModel.topBookGenres.value.toSet()

        binding.chipGroupGenres.removeAllViews()

        genres.forEach { genre ->
            val chip = Chip(requireContext()).apply {
                text = genre
                isCheckable = true
                isChecked = preSelected.contains(genre)
                setOnCheckedChangeListener { _, _ ->
                    if (type == MediaType.MOVIE) viewModel.toggleMovieGenre(genre)
                    else viewModel.toggleBookGenre(genre)
                }
            }
            binding.chipGroupGenres.addView(chip)
        }
    }

    private fun setupGetRecommendationsButton() {
        binding.btnGetRecommendations.setOnClickListener {
            viewModel.fetchRecommendations(viewModel.selectedType.value)
        }
    }

    private fun observeViewModel() {
        // Observe selected type to switch chip state labels
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedType.collect { type ->
                binding.tvGenreLabel.text =
                    if (type == MediaType.MOVIE)
                        "Pick your movie genres"
                    else
                        "Pick your book genres"
            }
        }

        // Observe movie recs state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieRecsState.collect { state ->
                if (viewModel.selectedType.value == MediaType.MOVIE) {
                    handleRecsState(state)
                }
            }
        }

        // Observe book recs state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookRecsState.collect { state ->
                if (viewModel.selectedType.value == MediaType.BOOK) {
                    handleRecsState(state)
                }
            }
        }

        // Rebuild chips once genre profile loads from DataStore
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.topMovieGenres.collect {
                if (viewModel.selectedType.value == MediaType.MOVIE) {
                    rebuildChips(MediaType.MOVIE)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.topBookGenres.collect {
                if (viewModel.selectedType.value == MediaType.BOOK) {
                    rebuildChips(MediaType.BOOK)
                }
            }
        }
    }

    private fun handleRecsState(state: RecommendUiState) {
        when (state) {
            is RecommendUiState.Idle -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility     = View.GONE
                binding.tvEmpty.visibility     = View.GONE
            }
            is RecommendUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.visibility     = View.GONE
                binding.tvEmpty.visibility     = View.GONE
                adapter.submitList(emptyList())
            }
            is RecommendUiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility     = View.GONE
                binding.tvEmpty.visibility     = View.GONE
                adapter.submitList(state.results)
            }
            is RecommendUiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility     = View.VISIBLE
                binding.tvError.text           = state.message
                binding.tvEmpty.visibility     = View.GONE
            }
            is RecommendUiState.NotEnoughData -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility     = View.GONE
                binding.tvEmpty.visibility     = View.VISIBLE
                adapter.submitList(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ForYouFragment.kt — same pattern, different binding