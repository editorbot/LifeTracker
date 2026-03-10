package com.example.lifetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.databinding.ItemHabitBinding
import com.example.lifetracker.model.Habit

// adapter/HabitAdapter.kt
class HabitAdapter(
    private val onHabitClick: (HabitEntity) -> Unit, // callback for toggle
    private val onHabitLongClick: (HabitEntity) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val habits = mutableListOf<HabitEntity>()

    // ViewHolder — holds references to views for ONE list item
    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        val holder = HabitViewHolder(binding)

        // Set once here, not every time in onBindViewHolder
        holder.binding.root.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_ID.toInt()) {
                onHabitClick(habits[position])
            }
        }

        holder.binding.root.setOnLongClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_ID.toInt()) {
                onHabitLongClick(habits[position])
            }
            true
        }

        return holder
    }


    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.binding.tvHabitName.text = habit.name
        holder.binding.tvHabitName.alpha = if (habit.isCompleted) 0.4f else 1.0f
        holder.binding.cbHabit.isChecked = habit.isCompleted


    }

    override fun getItemCount() = habits.size

    // Call this from Activity when LiveData updates
    fun updateHabits(newHabits: List<HabitEntity>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
    fun getHabitAt(position: Int): HabitEntity {
        return habits[position]
    }
}