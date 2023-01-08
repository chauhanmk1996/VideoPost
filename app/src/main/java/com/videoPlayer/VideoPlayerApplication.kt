package com.videoPlayer

import android.app.Application

class VideoPlayerApplication : Application() {

    var mInstance: VideoPlayerApplication? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}