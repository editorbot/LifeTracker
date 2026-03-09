package com.example.lifetracker.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracker.adapter.HabitAdapter
import com.example.lifetracker.databinding.FragmentHomeBinding
import com.example.lifetracker.viewmodel.HabitViewModel


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
            onHabitClick = { position -> viewModel.toggleHabit(position) },
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

        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            adapter.updateHabits(habits)
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addHabit(name)
                binding.etHabitName.text.clear()
            }
        }
    }

    // Critical — avoid memory leaks in Fragments
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}