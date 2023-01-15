package com.example.android.myvideoplayer.source

interface VideoSource {

    suspend fun getVideos(): List<VideoData>

    suspend fun getVideo(id: String): VideoData?

}