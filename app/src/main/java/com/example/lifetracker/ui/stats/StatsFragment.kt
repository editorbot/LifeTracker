package com.example.lifetracker.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.lifetracker.data.db.CategoryStat
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.databinding.FragmentStatsBinding
import com.example.lifetracker.model.Category
import com.example.lifetracker.viewmodel.HabitViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.collections.map


// ui/stats/StatsFragment.kt
@AndroidEntryPoint
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPieChart()
        setupBarChart()
        observeStats()
    }

    private fun observeStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Streak
                launch {
                    viewModel.currentStreak.collect { streak ->
                        binding.tvStreak.text = streak.toString()
                    }
                }

                // Weekly habits — drives summary + bar chart
                launch {
                    viewModel.weeklyHabits.collect { habits ->
                        updateWeeklySummary(habits)
                        updateBarChart(habits)
                    }
                }

                // Category stats — drives pie chart
                launch {
                    viewModel.categoryStats.collect { stats ->
                        updatePieChart(stats)
                    }
                }
            }
        }
    }

    private fun updateWeeklySummary(habits: List<HabitEntity>) {
        val total = habits.size
        val completed = habits.count { it.isCompleted }
        val missed = total - completed
        val percentage = if (total > 0) (completed * 100) / total else 0

        binding.tvTotal.text = "📋 Total this week: $total"
        binding.tvCompleted.text = "✅ Completed: $completed"
        binding.tvMissed.text = "❌ Missed: $missed"
        binding.tvCompletionRate.text = "$percentage%"

        // Productive hours — sum of (endTime - startTime) for completed habits
        val productiveMillis = habits
            .filter { it.isCompleted && it.startTime != null && it.endTime != null }
            .sumOf { (it.endTime!! - it.startTime!!) }

        val hours = TimeUnit.MILLISECONDS.toHours(productiveMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(productiveMillis) % 60
        binding.tvProductiveHours.text = "🕐 Productive Hours: ${hours}h ${minutes}m"
    }

    // ─── Pie Chart ────────────────────────────────────────────────

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            transparentCircleRadius = 45f
            setHoleColor(Color.WHITE)
            legend.isEnabled = true
            legend.textSize = 12f
            setEntryLabelTextSize(11f)
            setUsePercentValues(true)
            animateY(1000)  // satisfying entrance animation
        }
    }

    private fun updatePieChart(stats: List<CategoryStat>) {
        if (stats.isEmpty()) {
            binding.pieChart.setNoDataText("Complete habits to see breakdown")
            binding.pieChart.invalidate()
            return
        }

        val entries = stats.map { stat ->
            PieEntry(stat.count.toFloat(), stat.category.label)
        }

        val colors = stats.map { stat ->
            Color.parseColor(
                when (stat.category) {
                    Category.WORK -> "#FF6B6B"
                    Category.STUDY -> "#4ECDC4"
                    Category.PERSONAL -> "#45B7D1"
                    Category.LEISURE -> "#96CEB4"
                }
            )
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 11f
            valueTextColor = Color.WHITE
            sliceSpace = 3f
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            highlightValues(null)
            invalidate()
        }
    }

    // ─── Bar Chart ────────────────────────────────────────────────

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            animateY(1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                // Day labels Mon-Sun
                valueFormatter = IndexAxisValueFormatter(
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                )
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
                setDrawGridLines(true)
            }

            axisRight.isEnabled = false
        }
    }

    private fun updateBarChart(habits: List<HabitEntity>) {
        // Group completed habits by day of week
        val completedByDay = FloatArray(7) { 0f }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        habits.filter { it.isCompleted }.forEach { habit ->
            try {
                val date = sdf.parse(habit.date)
                val cal = Calendar.getInstance().apply { time = date!! }
                // DAY_OF_WEEK: Sun=1, Mon=2... → convert to Mon=0 index
                val dayIndex = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7
                completedByDay[dayIndex]++
            } catch (e: Exception) {
                // skip malformed dates
            }
        }

        val entries = completedByDay.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "Completed").apply {
            color = Color.parseColor("#4ECDC4")
            valueTextSize = 10f
        }

        binding.barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.6f }
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}