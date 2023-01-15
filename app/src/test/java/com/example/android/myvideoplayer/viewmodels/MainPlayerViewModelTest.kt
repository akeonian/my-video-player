package com.example.android.myvideoplayer.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.myvideoplayer.getOrAwaitValue
import com.example.android.myvideoplayer.source.VideoData
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class MainPlayerViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainPlayerViewModel

    @Before
    fun initViewModel() {
        viewModel = MainPlayerViewModel()
    }

    @Test
    fun saveState_whenCalled_savesState() {
        // Given - current state
        val currentItem = 0
        val playWhenReady = false
        val playbackPosition = 0L

        // When - saveState Called with current state
        viewModel.saveState(playWhenReady, playbackPosition, currentItem)

        assertThat(viewModel.playWhenReady.value, `is`(playWhenReady))
        assertThat(viewModel.playbackPosition.value, `is`(playbackPosition))
        assertThat(viewModel.currentItemIndex.value, `is`(currentItem))
    }

    @Test
    fun playVideo_withVideoData_triggersClearPlayEvent() {
        // Given - initial state
        val videoData = VideoData(
            "id",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        )

        // When - called with videoData
        viewModel.playVideo(videoData)

        // Then event should contain the videoData
        val event = viewModel.clearPlayEvent.getOrAwaitValue()
        assertThat(event, `is`(notNullValue()))
        assertThat(event!!.getContentIfNotHandled(), `is`(videoData))
    }

    @Test
    fun addToQueue_withVideoData_triggersAddToQueueEvent() {
        // Given - initial state
        val videoData = VideoData(
            "id",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        )

        // When - called with videoData
        viewModel.addToQueue(videoData)

        // Then event should contain the videoData
        val event = viewModel.addToQueueEvent.getOrAwaitValue()
        assertThat(event, `is`(notNullValue()))
        assertThat(event!!.getContentIfNotHandled(), `is`(videoData))
    }

    @Test
    fun queueField_addToQueue_addsVideoToQueue() {
        // Given - data to add
        val videoData = VideoData(
            "id",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        )

        // When - data added to queue
        viewModel.addToQueue(videoData)

        assertThat(viewModel.queue.value?.contains(videoData), `is`(true))
    }

    @Test
    fun queueField_playVideo_clearsAddToQueue() {
        // Given - initially filled queue
        viewModel.addToQueue(VideoData(
            "id1",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        ))
        viewModel.addToQueue(VideoData(
            "id2",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        ))

        // When - playVideo called
        val videoData = VideoData(
            "id",
            "Title for video",
            "thumbnailUri",
            "mediaUri"
        )
        viewModel.playVideo(videoData)

        // Then - queue is updated properly
        val queue = viewModel.queue.value
        assertThat(queue, `is`(notNullValue()))
        assertThat(queue!!.size, `is`(1))
        assertThat(queue.contains(videoData), `is`(true))
    }

    @Test
    fun currentItemIndex_change_changesCurrentItem() {
        // Given - initially filled queue
        val video1 = VideoData(
            "id1",
            "Title 1",
            "thumbnailUri",
            "mediaUri"
        )
        viewModel.addToQueue(video1)
        val video2 = VideoData(
            "id2",
            "Title 2",
            "thumbnailUri",
            "mediaUri"
        )
        viewModel.addToQueue(video2)

        // When - currentItemIndex changes to 1
        viewModel.saveState(false, 0, 1)

        // Then - currentItem changes
        assertThat(viewModel.currentItem.getOrAwaitValue(), `is`(video2))
    }
}