package com.example.android.myvideoplayer.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.myvideoplayer.R
import com.example.android.myvideoplayer.databinding.ListItemVideoBinding
import com.example.android.myvideoplayer.source.VideoData

class VideoListAdapter(
    private val onItemClick: (VideoData) -> Unit
): ListAdapter<VideoData, VideoListAdapter.ViewHolder>(VideoData.diffCallback) {

    class ViewHolder(private val binding: ListItemVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(videoData: VideoData) {
            binding.title.text = videoData.title
            Glide.with(itemView)
                .asBitmap()
                .load(Uri.parse(videoData.thumbnailUri))
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken)
                .into(binding.thumbnail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}