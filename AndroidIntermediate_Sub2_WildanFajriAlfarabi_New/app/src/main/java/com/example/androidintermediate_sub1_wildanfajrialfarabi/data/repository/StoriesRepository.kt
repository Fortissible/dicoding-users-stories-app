package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.StoriesDatabase
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.paging.StoriesRemoteMediator
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.RetrofitApiService

class StoriesRepository (
                        private val storiesDatabase: StoriesDatabase,
                        private val apiService: RetrofitApiService) {
    fun getStories(token : String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(
                    pageSize = 5,
                    enablePlaceholders = true
                ),
                remoteMediator = StoriesRemoteMediator(token,storiesDatabase, apiService),
                pagingSourceFactory = {
                    storiesDatabase.storiesDao().getAllStories()
                }
            ).liveData
    }

    suspend fun getStoriesFromDb(): List<ListStoryItem>{
        return storiesDatabase.storiesDao().getListStoryItem()
    }
}