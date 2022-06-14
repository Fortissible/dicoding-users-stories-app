package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
}