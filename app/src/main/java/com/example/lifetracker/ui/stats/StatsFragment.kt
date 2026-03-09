package com.example.lifetracker.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lifetracker.databinding.FragmentStatsBinding
import com.example.lifetracker.viewmodel.HabitViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            val total = habits.size
            val completed = habits.count { it.isCompleted }
            val percentage = if (total > 0) (completed * 100) / total else 0

            binding.tvTotal.text = "Total Habits: $total"
            binding.tvCompleted.text = "Completed Today: $completed"
            binding.tvPercentage.text = "Completion Rate: $percentage%"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}