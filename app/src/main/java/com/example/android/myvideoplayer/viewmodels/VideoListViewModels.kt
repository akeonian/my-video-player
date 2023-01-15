package com.example.android.myvideoplayer.viewmodels

import androidx.lifecycle.*
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.source.VideoSource
import kotlinx.coroutines.launch

class VideoListViewModels(private val videoSource: VideoSource): ViewModel() {

    private var hasLoaded = false
    private val _videoList = MutableLiveData(listOf<VideoData>())
    val videoList: LiveData<List<VideoData>> = _videoList

    fun loadData() {
        if (!hasLoaded) {
            refreshData()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val list = videoSource.getVideos()
            _videoList.postValue(list)
            hasLoaded = true
        }
    }

    class Factory(private val videoSource: VideoSource) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideoListViewModels::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VideoListViewModels(videoSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel calss")
        }
    }
}