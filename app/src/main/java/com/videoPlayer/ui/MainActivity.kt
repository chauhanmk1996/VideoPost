package com.videoPlayer.ui

import android.content.Intent
import androidx.activity.viewModels
import com.videoPlayer.R
import com.videoPlayer.base.BaseActivity
import com.videoPlayer.databinding.ActivityMainBinding
import com.videoPlayer.ui.createPost.CreatePostActivity
import com.videoPlayer.ui.showPost.VideoPlayerActivity

class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    override fun getBindingVariable(): Int {
        return R.layout.activity_main
    }

    private val mViewModel: MainViewModel by viewModels()

    override fun initViews(viewBinding: ActivityMainBinding) {
        setupClickListener(viewBinding)
    }

    private fun setupClickListener(viewBinding: ActivityMainBinding) {
        with(viewBinding) {
            setOnItemClick {
                when (it.id) {
                    R.id.btn_create_post -> {
                        startActivity(Intent(this@MainActivity, CreatePostActivity::class.java))
                    }

                    R.id.btn_show_post -> {
                        startActivity(Intent(this@MainActivity, VideoPlayerActivity::class.java))
                    }
                }
            }
        }
    }

    override fun getViewModel(): MainViewModel {
        return mViewModel
    }
}