package com.example.lifetracker.ui.detail

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
import androidx.navigation.fragment.navArgs
import com.example.lifetracker.databinding.FragmentHabitDetailBinding
import com.example.lifetracker.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HabitDetailFragment : Fragment() {

    private var _binding: FragmentHabitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by activityViewModels()

    // Safe Args generates this — type safe argument retrieval
    private val args: HabitDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Correct — Flow style
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitsForToday.collect { habits ->
                    val habit = habits.find { it.id == args.habitId }
                    habit?.let {
                        binding.tvHabitName.text = it.title
                        binding.tvStatus.text = if (it.isCompleted) "✅ Done today" else "⬜ Not done yet"
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}