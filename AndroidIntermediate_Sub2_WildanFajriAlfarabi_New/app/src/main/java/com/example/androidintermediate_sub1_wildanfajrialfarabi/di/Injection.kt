package com.example.androidintermediate_sub1_wildanfajrialfarabi.di

import android.content.Context
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.StoriesDatabase
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.APIConfig
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.StoriesRepository
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.UserRepository

object Injection {
    fun provideStoriesRepository(context: Context) : StoriesRepository{
        val database = StoriesDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return StoriesRepository(database,apiService)
    }

    fun provideUserRepository(context: Context) : UserRepository{
        val apiService = APIConfig.getApiService()
        return UserRepository(context, apiService)
    }
}