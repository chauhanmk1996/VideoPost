package com.videoPlayer.utils

class LiveDataEvent<T>(private val content: T) {
    fun peekContent(): T {
        return content
    }
}