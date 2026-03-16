package com.example.recommender

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lifetracker.databinding.ActivityMainBinding
import com.example.recommender.auth.AmplifyAuthManager
import kotlinx.coroutines.launch
import com.example.lifetracker.R
import com.example.lifetracker.databinding.ActivityMainRecBinding
import com.example.lifetracker.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainRecBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainRecBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // This single line wires bottom nav to navigation component
        binding.bottomNav.setupWithNavController(navController)
        // Check if user is signed in — if not, go to login
//        lifecycleScope.launch {
//            if (!AmplifyAuthManager.isSignedIn()) {
//                navController.navigate(R.id.loginFragment)
//            }
//        }
        drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Hamburger icon toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  // ← this shows the 3-line icon

        // Handle item clicks
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Main -> {val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent) }
                R.id.mainrec -> { val intent = Intent(this, com.example.recommender.MainActivity::class.java)
                    startActivity(intent) }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}