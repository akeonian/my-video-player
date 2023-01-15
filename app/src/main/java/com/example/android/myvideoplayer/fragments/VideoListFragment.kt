package com.example.android.myvideoplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.android.myvideoplayer.App
import com.example.android.myvideoplayer.R
import com.example.android.myvideoplayer.adapters.VideoListAdapter
import com.example.android.myvideoplayer.databinding.FragmentVideoListBinding
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.viewmodels.MainPlayerViewModel
import com.example.android.myvideoplayer.viewmodels.VideoListViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VideoListFragment: Fragment() {

    private val viewModel : VideoListViewModels by viewModels {
        VideoListViewModels.Factory(
            (activity?.application as App).videoSource)
    }
    private val mainViewModel : MainPlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVideoListBinding.inflate(
            inflater, container, false)
        val adapter = VideoListAdapter {
            showOptionsDialog(it)
        }
        binding.recyclerView.adapter = adapter
        viewModel.videoList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.loadData()
        return binding.root
    }

    private fun showOptionsDialog(video: VideoData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_video_list_option))
            .setItems(R.array.video_list_options) { _, index: Int ->
                when(index) {
                    0 -> mainViewModel.playVideo(video)
                    1 -> mainViewModel.addToQueue(video)
                    else -> throw IllegalArgumentException("Wrong Index=$index")
                }
            }
            .show()
    }

}