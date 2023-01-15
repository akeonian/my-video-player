package com.example.android.myvideoplayer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.myvideoplayer.MainActivity
import com.example.android.myvideoplayer.R
import com.example.android.myvideoplayer.databinding.FragmentSmallPlayerBinding
import com.example.android.myvideoplayer.source.VideoData
import com.example.android.myvideoplayer.viewmodels.MainPlayerViewModel

class SmallPlayerFragment: Fragment() {

    private val viewModel : MainPlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSmallPlayerBinding.inflate(
            inflater, container, false)
        (requireActivity() as MainActivity).player.observe(viewLifecycleOwner) {
            binding.playerView.player = it
        }
        binding.root.setOnClickListener {
            findNavController().navigate(R.id.action_containerFragment_to_playerFragment)
        }
        viewModel.queue.observe(viewLifecycleOwner) {
            val visibility = if (it.isNullOrEmpty()) GONE else VISIBLE
            if (binding.root.visibility != visibility) {
                binding.root.visibility = visibility
            }
        }
        viewModel.currentItem.observe(viewLifecycleOwner) {
            Log.d(TAG, "VideoData=$it")
            it?.let {  videoData ->
                bindViews(binding, videoData)
            }
        }
        return binding.root
    }

    private fun bindViews(binding: FragmentSmallPlayerBinding, videoData: VideoData) {
        binding.title.text = videoData.title
    }

    companion object {
        private const val TAG = "SmallPlayerFragment"
    }
}