package com.videoPlayer.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.permissionx.guolindev.PermissionX
import com.videoPlayer.BR
import com.videoPlayer.utils.LiveDataEvent
import com.videoPlayer.utils.ProgressDialogUtil

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel>(
    @LayoutRes
    private val layoutId: Int
) : AppCompatActivity() {

    private lateinit var mViewDataBinding: T

    abstract fun initViews(viewBinding: T)
    abstract fun getBindingVariable(): Int
    abstract fun getViewModel(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        mViewDataBinding.setVariable(BR.viewModel, getViewModel())
        mViewDataBinding.lifecycleOwner = this
        initViews(mViewDataBinding)
        initObservers()
    }

    private fun initObservers() {
        getViewModel().loadingLiveData.observe(this) {
            showLoading(it.peekContent())
        }

        getViewModel().errorLiveData.observe(this) {
            showToast(it.peekContent())
        }

        getViewModel().messageLiveData.observe(this) {
            showToast(it.peekContent())
        }

        getViewModel().logLiveData.observe(this) {
            showLog(it.peekContent())
        }
    }

    private fun showLoading(visible: Boolean) {
        if (visible) {
            ProgressDialogUtil.getInstance().hideProgress()
            ProgressDialogUtil.getInstance().showProgress(this, false)
        } else {
            ProgressDialogUtil.getInstance().hideProgress()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun showLog(log: String) {
        Log.d("VideoPlayer", log)
    }

    fun checkDeviceStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraAndStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun cameraAndStoragePermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "We need camera and storage permissions",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                getViewModel().cameraAndStoragePermission.postValue(LiveDataEvent(allGranted))
            }
    }

    fun storagePermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "We need storage permissions",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                getViewModel().storagePermission.postValue(LiveDataEvent(allGranted))
            }
    }
}