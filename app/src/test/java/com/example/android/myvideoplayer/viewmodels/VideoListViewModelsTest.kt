package com.example.android.myvideoplayer.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.myvideoplayer.MainCoroutineRule
import com.example.android.myvideoplayer.getOrAwaitValue
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.source.VideoSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class VideoListViewModelsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var videoSource: FakeVideoSource
    private lateinit var viewModel: VideoListViewModels

    @Before
    fun init() {
        videoSource = FakeVideoSource()
        viewModel = VideoListViewModels(videoSource)
    }

    @Test
    fun loadData_updatesLiveData() {
        // Given - initial viewModel

        // When - loadData called
        viewModel.loadData()

        // Then - data should be loaded
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST1))
    }

    @Test
    fun loadData_doesNotLoadIfAlreadyLoaded() {

        // Given - data loaded
        viewModel.loadData()
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST1))

        // When - data changed and load called again
        videoSource.changeList(FakeVideoSource.LIST2)
        viewModel.loadData()

        // Then - data not loaded again
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST1))
    }

    @Test
    fun refreshData_updatesLiveData() {
        // Given - initial viewModel

        // When - loadData called
        viewModel.refreshData()

        // Then - data should be loaded
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST1))

    }

    @Test
    fun refreshData_loadsEvenIfAlreadyLoaded() {

        // Given - data loaded
        viewModel.loadData()
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST1))

        // When - data changed and load called again
        videoSource.changeList(FakeVideoSource.LIST2)
        viewModel.refreshData()

        // Then - dataLoaded
        assertThat(viewModel.videoList.getOrAwaitValue(), `is`(FakeVideoSource.LIST2))
    }
}

class FakeVideoSource: VideoSource {

    private var list = LIST1

    override suspend fun getVideos(): List<VideoData> {
        return list
    }

    override suspend fun getVideo(id: String): VideoData? = list.find { it.id == id }

    fun changeList(newList: List<VideoData>) {
        list = newList
    }

    companion object {

        val LIST1 = listOf(
            VideoData("1", "Title 1", "thumbnailUri", "mediaUri"),
            VideoData("2", "Title 2", "thumbnailUri", "mediaUri")
        )
        val LIST2 = listOf(
            VideoData("1", "Title 1", "thumbnailUri", "mediaUri"),
            VideoData("2", "Title 2", "thumbnailUri", "mediaUri"),
            VideoData("3", "Title 2", "thumbnailUri", "mediaUri")
        )
    }

}