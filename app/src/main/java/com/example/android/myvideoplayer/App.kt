package com.example.android.myvideoplayer

import android.app.Application
import com.example.android.myvideoplayer.source.LocalVideoSource
import kotlinx.coroutines.Dispatchers

class App: Application() {

    val videoSource by lazy {
        LocalVideoSource(contentResolver, Dispatchers.IO)
    }

}