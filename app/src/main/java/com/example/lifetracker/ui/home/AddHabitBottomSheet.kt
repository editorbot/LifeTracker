package com.example.lifetracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.lifetracker.databinding.BottomSheetAddHabitBinding
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import com.example.lifetracker.viewmodel.HabitViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ui/home/AddHabitBottomSheet.kt  ← New file in ui/home/
@AndroidEntryPoint
class AddHabitBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddHabitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by activityViewModels()

    private var selectedPriority = Priority.MEDIUM
    private var selectedCategory = Category.PERSONAL
    private var selectedStartTime: Long? = null
    private var selectedEndTime: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPriorityChips()
        setupCategoryChips()
        setupTimePickers()

        binding.btnSaveHabit.setOnClickListener {
            val title = binding.etHabitTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addHabit(
                    title = title,
                    priority = selectedPriority,
                    category = selectedCategory,
                    startTime = selectedStartTime,
                    endTime = selectedEndTime
                )
                dismiss()
            } else {
                binding.etHabitTitle.error = "Title cannot be empty"
            }
        }
    }

    private fun setupPriorityChips() {
        // Highlight selected chip
        val chips = mapOf(
            binding.chipHigh to Priority.HIGH,
            binding.chipMedium to Priority.MEDIUM,
            binding.chipLow to Priority.LOW
        )

        chips.forEach { (chip, priority) ->
            chip.setOnClickListener {
                selectedPriority = priority
                // Update chip appearance
                chips.keys.forEach { it.isChecked = false }
                chip.isChecked = true
            }
        }
        binding.chipMedium.isChecked = true // default
    }

    private fun setupCategoryChips() {
        val chips = mapOf(
            binding.chipWork to Category.WORK,
            binding.chipStudy to Category.STUDY,
            binding.chipPersonal to Category.PERSONAL,
            binding.chipLeisure to Category.LEISURE
        )

        chips.forEach { (chip, category) ->
            chip.setOnClickListener {
                selectedCategory = category
                chips.keys.forEach { it.isChecked = false }
                chip.isChecked = true
            }
        }
        binding.chipPersonal.isChecked = true // default
    }

    private fun setupTimePickers() {
        binding.btnStartTime.setOnClickListener {
            showTimePicker("Start Time") { timeInMillis ->
                selectedStartTime = timeInMillis
                binding.btnStartTime.text = formatTime(timeInMillis)
            }
        }

        binding.btnEndTime.setOnClickListener {
            showTimePicker("End Time") { timeInMillis ->
                selectedEndTime = timeInMillis
                binding.btnEndTime.text = formatTime(timeInMillis)
            }
        }
    }

    private fun showTimePicker(title: String, onTimeSelected: (Long) -> Unit) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .build()

        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, picker.hour)
                set(Calendar.MINUTE, picker.minute)
                set(Calendar.SECOND, 0)
            }
            onTimeSelected(calendar.timeInMillis)
        }

        picker.show(parentFragmentManager, title)
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}