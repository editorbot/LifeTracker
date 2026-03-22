package com.example.lifetracker.data.sync

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Habit
import com.example.lifetracker.data.db.HabitEntity
import javax.inject.Inject
import kotlin.jvm.java

// data/sync/SyncManager.kt
class SyncManager @Inject constructor() {

    // Push a new habit to DynamoDB
    fun createHabit(
        habit: HabitEntity,
        onSuccess: (String) -> Unit,  // returns remote id
        onFailure: (Exception) -> Unit
    ) {
        Log.d("SyncManager", "Attempting to create habit: ${habit.title}")
        // Build Habit model directly — no separate Input class needed
        val remoteHabit = Habit.builder()
            .title(habit.title)
            .priority(habit.priority.name)
            .category(habit.category.name)
            .isCompleted(habit.isCompleted)
            .date(habit.date)
            .createdAt(habit.createdAt.toString())
            .startTime(habit.startTime?.toString())
            .endTime(habit.endTime?.toString())
            .completedAt(habit.completedAt?.toString())  // ← Add this

            .build()

        Amplify.API.mutate(
            ModelMutation.create(remoteHabit),
            { response ->
                Log.d("SyncManager", "Response received: ${response.data}")
                Log.d("SyncManager", "Errors: ${response.errors}")
                val remoteId = response.data?.id
                if (remoteId != null) {
                    Log.d("SyncManager", "✅ Created successfully with id: $remoteId")
                    onSuccess(remoteId)
                } else {
                    Log.e("SyncManager", "❌ No remote id — errors: ${response.errors}")
                    onFailure(Exception("No remote id returned"))
                }
            },
            { error ->
                Log.e("SyncManager", "❌ API call failed: ${error.message}")
                Log.e("SyncManager", "Cause: ${error.cause}")
                Log.e("SyncManager", "Create failed: ${error.message}")
                onFailure(Exception(error.message))
            }
        )
    }

//    // Update existing habit in DynamoDB
//    fun updateHabit(
//        habit: HabitEntity,
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        val input = UpdateHabitInput.builder()
//            .id(habit.remoteId ?: return)
//            .isCompleted(habit.isCompleted)
//            .build()
//
//        Amplify.API.mutate(
//            ModelMutation.update(input),
//            { onSuccess() },
//            { error -> onFailure(Exception(error.message)) }
//        )
//    }

    fun updateHabit(
        habit: HabitEntity,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val remoteId = habit.remoteId
        if (remoteId == null) {
            onFailure(Exception("No remoteId found for habit"))
            return
        }

        // Build updated model with existing remoteId
        val remoteHabit = Habit.builder()
            .title(habit.title)
            .priority(habit.priority.name)
            .category(habit.category.name)
            .isCompleted(habit.isCompleted)
            .date(habit.date)
            .createdAt(habit.createdAt.toString())
            .startTime(habit.startTime?.toString())
            .endTime(habit.endTime?.toString())
            .completedAt(habit.completedAt?.toString())  // ← Add this
            .id(remoteId)   // ← must pass id for update
            .build()

        Amplify.API.mutate(
            ModelMutation.update(remoteHabit),
            { onSuccess() },
            { error ->
                Log.e("SyncManager", "Update failed: ${error.message}")
                onFailure(Exception(error.message))
            }
        )
    }

    fun deleteHabit(
        remoteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // For delete we only need the id
        val remoteHabit = Habit.builder()
            .title("")       // required fields need dummy values for builder
            .priority("")
            .category("")
            .isCompleted(false)
            .date("")
            .createdAt("")
            .completedAt(null)      // ← Add this (null is fine — delete ignores it)
            .id(remoteId)   // ← only this matters for delete
            .build()

        Amplify.API.mutate(
            ModelMutation.delete(remoteHabit),
            { onSuccess() },
            { error ->
                Log.e("SyncManager", "Delete failed: ${error.message}")
                onFailure(Exception(error.message))
            }
        )
    }

    fun fetchHabits(
        onSuccess: (List<Habit>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Amplify.API.query(
            ModelQuery.list(Habit::class.java),
            { response ->
                val habits = response.data?.items?.toList() ?: emptyList()
                onSuccess(habits)
            },
            { error ->
                Log.e("SyncManager", "Fetch failed: ${error.message}")
                onFailure(Exception(error.message))
            }
        )
    }
}