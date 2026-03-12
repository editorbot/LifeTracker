package com.example.lifetracker.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lifetracker.R
import com.example.lifetracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1. Initialize it FIRST
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // This one line connects BottomNav to NavController
        binding.bottomNav.setupWithNavController(navController)

        // Hide bottom nav on detail screen
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.habitDetailFragment -> binding.bottomNav.visibility = View.GONE
//                else -> binding.bottomNav.visibility = View.VISIBLE
//            }
//        }
    }
}
