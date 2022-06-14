package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import androidx.paging.PagingSource
import com.example.androidintermediate_sub1_wildanfajrialfarabi.DataDummy
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.StoriesDao
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem

class FakeStoriesDao : StoriesDao {

    private var dataDummy = DataDummy.generateDummyStories()

    override suspend fun insertStories(stories: List<ListStoryItem>) {
        TODO("Not yet implemented")
    }

    override fun getAllStories(): PagingSource<Int, ListStoryItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getListStoryItem(): List<ListStoryItem> {
        return dataDummy
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}