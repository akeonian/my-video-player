package com.example.android.myvideoplayer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class Event<out T>(private val content: T) {

    private var isHandled = false

    fun getContentIfNotHandled(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            content
        }
    }

    fun peakData(): T = content
}

class EventObserver<T>(private val onUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(t: Event<T>?) {
        t?.getContentIfNotHandled()?.let(onUnhandledContent)
    }

}