package com.videoPlayer.ui.showPost

import android.content.pm.PackageManager
import android.support.v4.media.session.MediaSessionCompat
import androidx.activity.viewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.videoPlayer.R
import com.videoPlayer.base.BaseActivity
import com.videoPlayer.databinding.ActivityVideoPlayerBinding
import com.videoPlayer.ui.MainViewModel

class VideoPlayerActivity :
    BaseActivity<ActivityVideoPlayerBinding, MainViewModel>(R.layout.activity_video_player) {

    override fun getBindingVariable(): Int {
        return R.layout.activity_video_player
    }

    private val mViewModel: MainViewModel by viewModels()
    private lateinit var simpleExoPlayer: ExoPlayer

    override fun initViews(viewBinding: ActivityVideoPlayerBinding) {
        observer(viewBinding)
        mViewModel.showPostApi()
    }

    private fun observer(viewBinding: ActivityVideoPlayerBinding) {
        mViewModel.showPostResponse.observe(this) {
            if (it != null) {
                it.peekContent().data[0].videos?.apply {
                    initializePlayer(viewBinding, this)
                }
            }
        }
    }

    private fun initializePlayer(viewBinding: ActivityVideoPlayerBinding,videoUrl: String) {
        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
        val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        simpleExoPlayer =
            ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
        simpleExoPlayer.addMediaSource(mediaSource)

        viewBinding.exoPlayerView.player = simpleExoPlayer
        viewBinding.exoPlayerView.requestFocus()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.prepare()

        val mediaSession = MediaSessionCompat(this, packageName)
        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(simpleExoPlayer)
        mediaSession.isActive = true
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
        simpleExoPlayer.release()
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            finishAndRemoveTask()
        }
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    override fun getViewModel(): MainViewModel {
        return mViewModel
    }
}