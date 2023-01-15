package com.example.android.myvideoplayer.source

import android.content.ContentResolver
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class LocalVideoSource(
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher
): VideoSource {

    override suspend fun getVideos(): List<VideoData> = withContext(ioDispatcher) {
        val videoList = mutableListOf<VideoData>()
        val projection = arrayOf(
            Video.Media._ID,
            Video.Media.TITLE
        )
        val cursor = contentResolver.query(
            Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        cursor?.let {
            val idColumn = it.getColumnIndexOrThrow(Video.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(Video.Media.TITLE)
            while (it.moveToNext()) {
                val id = it.getString(idColumn)
                val title = it.getString(titleColumn)
                val mediaUri = Uri.withAppendedPath(
                    Video.Media.EXTERNAL_CONTENT_URI, id).toString()
                videoList.add(VideoData(id, title, mediaUri, mediaUri))
            }
            it.close()
        }
        videoList
    }

    override suspend fun getVideo(vId: String): VideoData? = withContext(ioDispatcher) {
        var videoData: VideoData? = null
        val projection = arrayOf(
            Video.Media._ID,
            Video.Media.TITLE
        )
        val selection = "${Video.Media._ID}=?"
        val selectionArgs = arrayOf(vId)
        val cursor = contentResolver.query(
            Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        cursor?.let {
            val idColumn = it.getColumnIndexOrThrow(Video.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(Video.Media.TITLE)
            if (it.moveToFirst()) {
                val id = it.getString(idColumn)
                val title = it.getString(titleColumn)
                val mediaUri = Uri.withAppendedPath(
                    Video.Media.EXTERNAL_CONTENT_URI, id).toString()
                videoData = VideoData(
                    id, title, mediaUri, mediaUri)
            }
            it.close()
        }
        videoData
    }
}