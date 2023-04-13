package com.example.android.myvideoplayer.viewmodels

import androidx.lifecycle.*
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.source.VideoSource
import kotlinx.coroutines.launch

class VideoListViewModels(private val videoSource: VideoSource): ViewModel() {

    private var hasLoaded = false
    private val _videoList = MutableLiveData(listOf<VideoData>())
    val videoList: LiveData<List<VideoData>> = _videoList
    private val _listState = MutableLiveData(ListState.LOADING)
    val listState: LiveData<ListState> = _listState

    fun loadData() {
        if (!hasLoaded) {
            refreshData()
        }
    }

    fun refreshData() {
        _listState.value = ListState.LOADING
        viewModelScope.launch {
            try {
                val list = videoSource.getVideos()
                val state = if (list.isEmpty()) ListState.EMPTY else ListState.SUCCESS
                _listState.postValue(state)
                _videoList.postValue(list)
                hasLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
                _listState.postValue(ListState.ERROR)
                _videoList.postValue(emptyList())
            }
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

enum class ListState{
    EMPTY, LOADING, ERROR, SUCCESS
}