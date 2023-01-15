package com.example.android.myvideoplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.viewmodels.EventObserver
import com.example.android.myvideoplayer.viewmodels.MainPlayerViewModel

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1234
    private val _player = MutableLiveData<ExoPlayer?>(null)
    private val viewModel: MainPlayerViewModel by viewModels()

    val player : LiveData<ExoPlayer?> = _player

    private val permissionHelper = PermissionHelper(arrayOf(
        PermissionHelper.Permission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            "Read Permission is needed to read video files",
            true
        )
    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionAndInitialize()
    }

    private fun requestPermissionAndInitialize(){
        val b =  permissionHelper.askForPermission(this, REQUEST_CODE) { _, _ -> finish() }
        if (b) {
            setContentView(R.layout.activity_main)

            viewModel.clearPlayEvent.observe(this, EventObserver {
                player.value?.apply {
                    setMediaItem(it.mediaItem)
                    viewModel.saveState(playWhenReady, currentPosition, 0)
                }
            })
            viewModel.addToQueueEvent.observe(this, EventObserver {
                player.value?.addMediaItem(it.mediaItem)
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        requestPermissionAndInitialize()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player.value == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        _player.value?.let { exoPlayer ->
            viewModel.saveState(
                exoPlayer.playWhenReady,
                exoPlayer.currentPosition,
                exoPlayer.currentMediaItemIndex
            )
            exoPlayer.release()
            _player.value = null
        }
    }

    private fun initializePlayer() {
        val exoPlayer = ExoPlayer.Builder(this)
            .build()
        viewModel.queue.value?.let {
            exoPlayer.addVideoDataList(it)
        }
        exoPlayer.playWhenReady = viewModel.playWhenReady.value!!
        exoPlayer.seekTo(viewModel.currentItemIndex.value!!, viewModel.playbackPosition.value!!)
        exoPlayer.prepare()
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                viewModel.saveState(
                    exoPlayer.playWhenReady,
                    exoPlayer.currentPosition,
                    exoPlayer.currentMediaItemIndex
                )
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                viewModel.saveState(
                    playWhenReady,
                    exoPlayer.currentPosition,
                    exoPlayer.currentMediaItemIndex
                )
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                viewModel.saveState(
                    exoPlayer.playWhenReady,
                    newPosition.positionMs,
                    exoPlayer.currentMediaItemIndex
                )
            }
        })
        _player.value = exoPlayer
    }

    private val VideoData.mediaItem get() = MediaItem.fromUri(mediaUri)

    private fun ExoPlayer.addVideoDataList(videoData: List<VideoData>) {
        addMediaItems(videoData.map { it.mediaItem })
    }

}