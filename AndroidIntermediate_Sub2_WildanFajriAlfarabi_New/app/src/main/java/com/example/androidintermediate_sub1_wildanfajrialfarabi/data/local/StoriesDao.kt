package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM list_story_item")
    fun getAllStories(): PagingSource<Int,ListStoryItem>

    @Query("SELECT * FROM list_story_item")
    suspend fun getListStoryItem(): List<ListStoryItem>

    @Query("DELETE FROM list_story_item")
    suspend fun deleteAll()
}