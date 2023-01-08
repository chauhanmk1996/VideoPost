package com.videoPlayer.ui

import androidx.lifecycle.MutableLiveData
import com.videoPlayer.base.BaseViewModel
import com.videoPlayer.ui.createPost.CreatePostResponse
import com.videoPlayer.ui.showPost.ShowPostResponse
import com.videoPlayer.utils.AppValidator
import com.videoPlayer.utils.LiveDataEvent
import com.videoPlayer.utils.getRequestBody
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class MainViewModel : BaseViewModel() {

    companion object {
        const val TAG = "MainViewModel"
    }

    val mNameLiveData = MutableLiveData<String>()
    val mDescLiveData = MutableLiveData<String>()
    val mImageNameLiveData = MutableLiveData<String>()
    val mVideoNameLiveData = MutableLiveData<String>()
    val mThumbNameLiveData = MutableLiveData<String>()

    val mImageMultipartLiveData = MutableLiveData<ArrayList<MultipartBody.Part>>()
    val mVideoMultipartLiveData = MutableLiveData<MultipartBody.Part>()
    val mThumbnailMultipartLiveData = MutableLiveData<MultipartBody.Part>()

    val createPostResponse = MutableLiveData<LiveDataEvent<CreatePostResponse>>()
    val showPostResponse = MutableLiveData<LiveDataEvent<ShowPostResponse>>()

    fun createPostApi() {
        if (isValidDetail()) {
            showLoading(true)
            showLog("$TAG, CreatePostApi")
            disposable = apiService.createPostApi(
                name = mNameLiveData.value?.getRequestBody(),
                user_id = "92".getRequestBody(),
                post_type = "1".getRequestBody(),
                discription = mDescLiveData.value?.getRequestBody(),
                images = mImageMultipartLiveData.value,
                videos = mVideoMultipartLiveData.value,
                video_thumbnail = mThumbnailMultipartLiveData.value,
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onCreatePostSuccess(it) }, { onError(it) })
        }
    }

    private fun onCreatePostSuccess(it: CreatePostResponse) {
        showLoading(false)
        showLog("$TAG, $it")
        createPostResponse.postValue(LiveDataEvent(it))
    }


    private fun isValidDetail(): Boolean {
        if (mNameLiveData.value.isNullOrEmpty()) {
            showToast("Please enter name")
            return false
        }

        if (!AppValidator.isValidName(mNameLiveData.value!!)) {
            showToast("Please enter valid name")
            return false
        }

        if (mDescLiveData.value.isNullOrEmpty()) {
            showToast("Please enter description")
            return false
        }

        if (mImageMultipartLiveData.value == null) {
            showToast("Please upload image")
            return false
        }

        if (mVideoMultipartLiveData.value == null) {
            showToast("Please upload video")
            return false
        }

        if (mThumbnailMultipartLiveData.value == null) {
            showToast("Please upload thumbnail")
            return false
        }
        return true
    }

    fun showPostApi() {
        showLoading(true)
        showLog("$TAG, ShowPostApi")
        disposable = apiService.showPostApi(
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onShowPostSuccess(it) }, { onError(it) })
    }

    private fun onShowPostSuccess(it: ShowPostResponse) {
        showLoading(false)
        showLog("$TAG, $it")
        showPostResponse.postValue(LiveDataEvent(it))
    }
}