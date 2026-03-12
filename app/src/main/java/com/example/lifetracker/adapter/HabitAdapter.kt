package com.example.lifetracker.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.databinding.ItemHabitBinding
import com.example.lifetracker.model.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// adapter/HabitAdapter.kt
class HabitAdapter(
    private val onHabitClick: (HabitEntity) -> Unit,
    private val onHabitLongClick: (HabitEntity) -> Unit
) : ListAdapter<HabitEntity, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {
    // ListAdapter replaces RecyclerView.Adapter
    // It manages the list internally using DiffUtil automatically

    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val holder = HabitViewHolder(binding)

        holder.binding.root.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_ID.toInt()) {
                onHabitClick(getItem(position)) // getItem() from ListAdapter
            }
        }

        holder.binding.root.setOnLongClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_ID.toInt()) {
                onHabitLongClick(getItem(position))
            }
            true
        }

        return holder
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = getItem(position)
        val binding = holder.binding

        // Title
        binding.tvHabitName.text = habit.title
        binding.tvHabitName.alpha = if (habit.isCompleted) 0.4f else 1.0f

        // Checkbox
        binding.cbHabit.isChecked = habit.isCompleted

        // Priority color bar on left side of card
        val priorityColor = Color.parseColor(habit.priority.colorHex)
        binding.viewPriorityBar.setBackgroundColor(priorityColor)

        // Category emoji + label
        binding.tvCategory.text = "${habit.category.iconRes} ${habit.category.label}"

        // Time display — only show if time was set
        if (habit.startTime != null && habit.endTime != null) {
            binding.tvTime.visibility = View.VISIBLE
            binding.tvTime.text = "${formatTime(habit.startTime)} - ${formatTime(habit.endTime)}"
        } else {
            binding.tvTime.visibility = View.GONE
        }

        // Strike through title if completed
        binding.tvHabitName.paintFlags = if (habit.isCompleted) {
            binding.tvHabitName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.tvHabitName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    // No more updateHabits() — ListAdapter handles this
    // Just call adapter.submitList(habits) from Fragment

    fun getHabitAt(position: Int): HabitEntity = getItem(position)

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}