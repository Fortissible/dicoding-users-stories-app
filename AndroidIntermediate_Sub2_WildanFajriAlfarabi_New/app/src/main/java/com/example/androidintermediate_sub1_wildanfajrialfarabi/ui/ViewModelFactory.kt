package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.StoriesRepository
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.UserRepository
import com.example.androidintermediate_sub1_wildanfajrialfarabi.di.Injection

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
    private val storiesRepository: StoriesRepository,
    private val userRepository: UserRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(storiesRepository,userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance : ViewModelFactory ?= null
        fun getInstance(context: Context) : ViewModelFactory=
            instance?: synchronized(this){
                instance?: ViewModelFactory(
                    Injection.provideStoriesRepository(context),
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it}
    }
}