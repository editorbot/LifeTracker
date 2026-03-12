package com.example.lifetracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.lifetracker.data.preferences.UserPreferences
import com.example.lifetracker.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var userPreferences: UserPreferences
    // Remove manual instantiation: UserPreferences(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())

        // Load saved values into UI
        binding.etUserName.setText(userPreferences.userName)
        binding.switchDarkMode.isChecked = userPreferences.isDarkMode
        binding.switchNotifications.isChecked = userPreferences.areNotificationsEnabled

        // Just a toggle for now — SharedPreferences comes in Phase 3
        // Save immediately when toggled
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                userPreferences.isDarkMode = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
//            userPreferences.isDarkMode = isChecked
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            userPreferences.areNotificationsEnabled = isChecked
        }

        binding.btnSaveName.setOnClickListener {
            userPreferences.userName = binding.etUserName.text.toString().trim()
            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}