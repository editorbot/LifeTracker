package com.example.recommender.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommender.data.local.MediaDatabase
import com.example.recommender.data.remote.waste.RetrofitClient
import com.example.recommender.data.remote.books.BooksRetrofitClient
import com.example.recommender.data.remote.tmdb.TmdbRetrofitClient
import com.example.recommender.data.repository.MediaRepository
import com.example.recommender.databinding.FragmentHomeBinding
import com.example.recommender.ui.ViewModelFactory
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = com.example.recommender.ui.home.MediaAdapter { clickedItem ->
            val action = HomeFragmentDirections.actionHomeToDetail(clickedItem.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savedItems.collect { items ->
                adapter.submitList(items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//    private fun getFakeData() = listOf(
//        MediaItem(1, "Inception", listOf("Thriller", "Sci-Fi", "Drama"), 4.5f, MediaType.MOVIE),
//        MediaItem(2, "The Alchemist", listOf("Fiction", "Adventure"), 5.0f, MediaType.BOOK),
//        MediaItem(3, "Interstellar", listOf("Sci-Fi", "Drama"), 4.0f, MediaType.MOVIE),
//        MediaItem(4, "Atomic Habits", listOf("Self-Help", "Biography"), 4.5f, MediaType.BOOK),
//        MediaItem(5, "The Dark Knight", listOf("Action", "Crime", "Drama"), 5.0f, MediaType.MOVIE)
//    )
