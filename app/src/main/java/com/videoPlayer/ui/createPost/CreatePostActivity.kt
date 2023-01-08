package com.videoPlayer.ui.createPost

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.videoPlayer.R
import com.videoPlayer.base.BaseActivity
import com.videoPlayer.constant.IntentConstant
import com.videoPlayer.databinding.ActivityCreatePostBinding
import com.videoPlayer.ui.MainViewModel
import com.videoPlayer.utilImage.compressImageFile
import com.videoPlayer.utilImage.getRealPath
import com.videoPlayer.utils.findImageMultipart
import com.videoPlayer.utils.findNameFromString
import com.videoPlayer.utils.findVideoMultipart
import okhttp3.MultipartBody
import java.io.File

class CreatePostActivity :
    BaseActivity<ActivityCreatePostBinding, MainViewModel>(R.layout.activity_create_post) {

    override fun getBindingVariable(): Int {
        return R.layout.activity_create_post
    }

    private val mViewModel: MainViewModel by viewModels()
    private var dataType: Int = 0

    private val imageResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.data?.apply {
                    val file: File = compressImageFile(this@CreatePostActivity, this)
                    val name = this.toString().findNameFromString()
                    when (dataType) {
                        IntentConstant.IMAGE -> {
                            mViewModel.mImageNameLiveData.value = name
                            val multipartArr: ArrayList<MultipartBody.Part> = ArrayList()
                            multipartArr.add(findImageMultipart(file, "images[]"))
                            mViewModel.mImageMultipartLiveData.value = multipartArr
                        }

                        IntentConstant.THUMBNAIL_IMAGE -> {
                            mViewModel.mThumbNameLiveData.value = name
                            mViewModel.mThumbnailMultipartLiveData.value =
                                findImageMultipart(file, "video_thumbnail")
                        }
                    }
                }
            }
        }

    private val videoResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.data?.apply {
                    getRealPath(this@CreatePostActivity, this)?.apply {
                        mViewModel.mVideoNameLiveData.value = this.findNameFromString()
                        val file = File(this)
                        mViewModel.mVideoMultipartLiveData.value = findVideoMultipart(file, "videos")
                    }
                }
            }
        }

    override fun initViews(viewBinding: ActivityCreatePostBinding) {
        observer()
        setupClickListener(viewBinding)
    }

    private fun observer() {
        mViewModel.createPostResponse.observe(this) {
            if (it != null) {
                showToast("Post created successfully")
                finish()
            }
        }
    }

    private fun setupClickListener(viewBinding: ActivityCreatePostBinding) {
        with(viewBinding) {
            setOnItemClick {
                when (it.id) {
                    R.id.et_image -> {
                        dataType = IntentConstant.IMAGE
                        picImage()
                    }

                    R.id.et_video -> {
                        pickVideo()
                    }

                    R.id.et_thumb_image -> {
                        dataType = IntentConstant.THUMBNAIL_IMAGE
                        picImage()
                    }

                    R.id.btn_save -> {
                        mViewModel.createPostApi()
                    }
                }
            }
        }
    }

    private fun picImage() {
        if (!checkDeviceStoragePermission()) {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            imageResultLauncher.launch(galleryIntent)
        } else {
            storagePermission()
        }
    }

    private fun pickVideo() {
        if (!checkDeviceStoragePermission()) {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "video/*"
            galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            videoResultLauncher.launch(galleryIntent)
        } else {
            storagePermission()
        }
    }

    override fun getViewModel(): MainViewModel {
        return mViewModel
    }
}