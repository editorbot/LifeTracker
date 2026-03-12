package com.example.lifetracker.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.lifetracker.adapter.HabitAdapter
import com.example.lifetracker.databinding.FragmentHomeBinding
import com.example.lifetracker.viewmodel.HabitViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// ui/home/HomeFragment.kt
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by activityViewModels()
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onHabitClick = { habit -> viewModel.toggleHabit(habit) },
            onHabitLongClick = { habit ->
                val action = HomeFragmentDirections.actionHomeToDetail(habit.id)
                findNavController().navigate(action)
            }
        )

        binding.rvHabits.apply {
            adapter = habitAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Smooth animations on DiffUtil updates
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // Swipe to delete
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val habit = habitAdapter.getHabitAt(viewHolder.adapterPosition)
                viewModel.deleteHabit(habit)
                // Snackbar undo — good UX
                Snackbar.make(binding.root, "Habit deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { viewModel.addHabit(habit.title) }
                    .show()
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvHabits)
    }

    private fun setupClickListeners() {
        // FAB replaces the old btnAdd
        binding.fabAddHabit.setOnClickListener {
            AddHabitBottomSheet().show(parentFragmentManager, "AddHabit")
        }
    }

    private fun observeHabits() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitsForToday.collect { habits ->
                    habitAdapter.submitList(habits) // DiffUtil handles the rest
                    binding.tvEmptyState.visibility =
                        if (habits.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}