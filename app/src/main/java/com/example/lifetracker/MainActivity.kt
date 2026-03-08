package com.example.lifetracker

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifetracker.databinding.ActivityMainBinding
import model.Habit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val habitList = mutableListOf<Habit>()
    private lateinit var adapter: ArrayAdapter<String>
    private var idCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1. Initialize it FIRST
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ArrayAdapter just for now — you'll replace this in Phase 2
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        binding.lvHabits.adapter = adapter

        binding.btnAdd.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()
            if (name.isNotEmpty()) {
                val habit = Habit(id = idCounter++, name = name)
                habitList.add(habit)
                adapter.add(habit.name)
                adapter.notifyDataSetChanged()
                binding.etHabitName.text.clear()
            }
        }

        binding.lvHabits.setOnItemClickListener { _, _, position, _ ->
            habitList[position].isCompleted = !habitList[position].isCompleted
            // You'll see the UI limitation here — ListView won't reflect state change
            // This frustration is intentional. RecyclerView fixes this in Phase 2.
        }
    }
}