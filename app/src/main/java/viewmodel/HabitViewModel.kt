package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Habit
import kotlin.collections.mutableListOf

// viewmodel/HabitViewModel.kt
class HabitViewModel : ViewModel() {

    // LiveData holds the list — UI observes this
    private val _habits = MutableLiveData<MutableList<Habit>>(mutableListOf())
    val habits: LiveData<MutableList<Habit>> = _habits

    fun addHabit(name: String) {
        val currentList = _habits.value ?: mutableListOf()
        currentList.add(Habit(id = currentList.size, name = name))
        _habits.value = currentList  // triggers observer in UI
    }

    fun toggleHabit(position: Int) {
        val currentList = _habits.value ?: return
        currentList[position].isCompleted = !currentList[position].isCompleted
        _habits.value = currentList  // triggers UI refresh
    }
}