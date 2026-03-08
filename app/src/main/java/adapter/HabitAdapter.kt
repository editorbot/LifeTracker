package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracker.databinding.ItemHabitBinding
import model.Habit

// adapter/HabitAdapter.kt
class HabitAdapter(
    private val onHabitClick: (Int) -> Unit  // callback for toggle
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val habits = mutableListOf<Habit>()

    // ViewHolder — holds references to views for ONE list item
    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.binding.tvHabitName.text = habit.name
        holder.binding.tvHabitName.alpha = if (habit.isCompleted) 0.4f else 1.0f
        holder.binding.cbHabit.isChecked = habit.isCompleted

        // Click anywhere on the item to toggle
        holder.binding.root.setOnClickListener {
            onHabitClick(position)
        }
    }

    override fun getItemCount() = habits.size

    // Call this from Activity when LiveData updates
    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}