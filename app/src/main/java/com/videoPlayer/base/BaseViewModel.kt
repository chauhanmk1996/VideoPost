package com.videoPlayer.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.videoPlayer.connection.RetrofitUtil
import com.videoPlayer.constant.ApiConstant
import com.videoPlayer.utils.LiveDataEvent
import io.reactivex.disposables.Disposable
import java.io.FileNotFoundException

open class BaseViewModel : ViewModel() {

    var disposable: Disposable? = null
    var loadingLiveData: MutableLiveData<LiveDataEvent<Boolean>> = MutableLiveData()
    var errorLiveData: MutableLiveData<LiveDataEvent<String>> = MutableLiveData()
    var messageLiveData: MutableLiveData<LiveDataEvent<String>> = MutableLiveData()
    var logLiveData: MutableLiveData<LiveDataEvent<String>> = MutableLiveData()
    var cameraAndStoragePermission = MutableLiveData<LiveDataEvent<Boolean>>()
    var storagePermission = MutableLiveData<LiveDataEvent<Boolean>>()

    val apiService by lazy {
        RetrofitUtil.create(ApiConstant.BASE_URL_FOR_PRODUCTION)
    }

    fun onError(throwable: Throwable) {
        showLoading(false)
        var message = ""
        if (throwable is FileNotFoundException) {
            message = "File not found"
        }
        errorLiveData.postValue(LiveDataEvent(message))
    }

    fun showLoading(visible: Boolean) {
        loadingLiveData.postValue(LiveDataEvent(visible))
    }

    fun showToast(message: String) {
        messageLiveData.postValue(LiveDataEvent(message))
    }

    fun showLog(log: String) {
        logLiveData.postValue(LiveDataEvent(log))
    }
}