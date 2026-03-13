package com.example.recommender.ui.add

// AddTitleFragment.kt


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracker.R
import com.example.recommender.data.MediaType
import com.example.recommender.data.SearchResult
import com.example.lifetracker.databinding.FragmentAddTitlerecBinding
import com.example.recommender.ui.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class AddTitleFragment : Fragment() {

    private var _binding: FragmentAddTitlerecBinding? = null
    private val binding get() = _binding!!


    // ← Now uses the centralized ViewModelFactory
    // which already wires repository + dataStore inside it
    private val viewModel: AddTitleViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var adapter: SearchResultAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTitlerecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToggle()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter { result -> showRatingDialog(result) }
        binding.recyclerSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResults.adapter = adapter
    }

    private fun setupToggle() {
        // Restore toggle state from ViewModel (which restored from DataStore)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedType.collect { type ->
                val buttonId = if (type == MediaType.MOVIE) R.id.btn_movies else R.id.btn_books
                binding.toggleType.check(buttonId)
            }
        }

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = if (checkedId == R.id.btn_movies) MediaType.MOVIE else MediaType.BOOK
                viewModel.setSelectedType(type)
                adapter.submitList(emptyList())
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.search(binding.etSearch.text.toString())
                true
            } else false
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { state ->
                when (state) {
                    is SearchUiState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                    }
                    is SearchUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvError.visibility = View.GONE
                        adapter.submitList(emptyList())
                    }
                    is SearchUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.results)
                    }
                    is SearchUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = state.message
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveState.collect { state ->
                when (state) {
                    is SaveUiState.Saved -> {
                        Toast.makeText(requireContext(), "Added to your list! ✓", Toast.LENGTH_SHORT).show()
                        viewModel.resetSaveState()
                    }
                    is SaveUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetSaveState()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showRatingDialog(result: SearchResult) {
        val ratingBar = android.widget.RatingBar(requireContext()).apply {
            numStars = 5
            stepSize = 0.5f
            rating = 3f
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Rate \"${result.title}\"")
            .setMessage("Add to your watched list with a rating")
            .setView(ratingBar)
            .setPositiveButton("Save") { _, _ ->
                viewModel.saveItem(result, ratingBar.rating)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ForYouFragment.kt — same pattern, different binding