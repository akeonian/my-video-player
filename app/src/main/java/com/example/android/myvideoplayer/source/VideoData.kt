package com.example.android.myvideoplayer.source

import androidx.recyclerview.widget.DiffUtil

data class VideoData(
    val id: String,
    val title: String,
    val thumbnailUri: String,
    val mediaUri: String
) {
    companion object {
        val diffCallback = object: DiffUtil.ItemCallback<VideoData>() {
            override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
                return oldItem == newItem
            }

        }
    }
}
