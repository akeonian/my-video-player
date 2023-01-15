package com.example.android.myvideoplayer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.myvideoplayer.source.VideoData

private const val TAG = "PlayerViewModel"

class MainPlayerViewModel: ViewModel() {

    private var _currentItemIndex = MutableLiveData(0)
    private var _playWhenReady = MutableLiveData(true)
    private var _playbackPosition = MutableLiveData(0L)
    val playWhenReady: LiveData<Boolean> = _playWhenReady
    val currentItemIndex: LiveData<Int> = _currentItemIndex
    val playbackPosition: LiveData<Long> = _playbackPosition

    val currentItem: LiveData<VideoData?> = Transformations.map(currentItemIndex) {
        if (it != null && it < queue.value!!.size) queue.value!![it] else null
    }

    private val _queue = MutableLiveData<List<VideoData>>(emptyList())
    val queue : LiveData<List<VideoData>> = _queue

    private val _clearPlayEvent = MutableLiveData<Event<VideoData>>()
    val clearPlayEvent: LiveData<Event<VideoData>> = _clearPlayEvent

    private val _addToQueueEvent = MutableLiveData<Event<VideoData>>()
    val addToQueueEvent: LiveData<Event<VideoData>> = _addToQueueEvent

    fun saveState(playing: Boolean, position: Long, itemIndex: Int) {
        _playWhenReady.value = playing
        _playbackPosition.value = position
        _currentItemIndex.value = itemIndex
    }

    fun playVideo(videoData: VideoData) {
        _queue.postValue(listOf(videoData))
        _clearPlayEvent.postValue(Event(videoData))
    }

    fun addToQueue(videoData: VideoData) {
        _queue.postValue(queue.value!! + videoData)
        _addToQueueEvent.postValue(Event(videoData))
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}