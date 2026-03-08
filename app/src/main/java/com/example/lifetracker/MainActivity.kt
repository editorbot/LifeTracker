package com.example.lifetracker

import adapter.HabitAdapter
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifetracker.databinding.ActivityMainBinding
import model.Habit
import viewmodel.HabitViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var habitAdapter: HabitAdapter

    // ViewModel survives rotation — notice how clean this is
    private val viewModel: HabitViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1. Initialize it FIRST
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeHabits()
        setupClickListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter { position ->
            viewModel.toggleHabit(position)
        }
        binding.rvHabits.apply {
            adapter = habitAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeHabits() {
        // This re-runs every time LiveData changes (add, toggle, rotation)
        viewModel.habits.observe(this) { habits ->
            habitAdapter.updateHabits(habits)
        }
    }

    private fun setupClickListeners() {
        binding.btnAdd.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addHabit(name)
                binding.etHabitName.text.clear()
            }
        }
    }
}