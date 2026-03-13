package com.example.recommender.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Query("SELECT * FROM media_items ORDER BY dateAdded DESC")
    fun getAllItems(): Flow<List<MediaEntity>>   // Flow = auto-updates UI on change

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MediaEntity)

    @Delete
    suspend fun deleteItem(item: MediaEntity)

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getItemById(id: Int): MediaEntity?

    // Returns all genres concatenated — used to build preference profile
    @Query("SELECT genres FROM media_items WHERE type = :type")
    suspend fun getAllGenresForType(type: String): List<String>
}