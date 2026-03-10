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
import com.example.lifetracker.adapter.HabitAdapter
import com.example.lifetracker.databinding.FragmentHomeBinding
import com.example.lifetracker.viewmodel.HabitViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel — scoped to Activity so Stats can access same data
    private val viewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HabitAdapter(
            onHabitClick = { habit -> viewModel.toggleHabit(habit) },
            onHabitLongClick = { habit ->
                // Navigate to detail, passing habit id
                val action = HomeFragmentDirections
                    .actionHomeToDetail(habit.id)
                findNavController().navigate(action)
            }
        )

        binding.rvHabits.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }



        binding.btnAdd.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addHabit(name)
                binding.etHabitName.text.clear()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { habits ->
                    adapter.updateHabits(habits)
                }
            }
        }

// Add swipe to delete
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val habit = adapter.getHabitAt(viewHolder.adapterPosition)
                viewModel.deleteHabit(habit)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvHabits)
    }

    // Critical — avoid memory leaks in Fragments
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}