package com.videoPlayer.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun findImageMultipart(currentImage: File, type: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        type,
        currentImage.name,
        currentImage.asRequestBody("image/*".toMediaTypeOrNull())
    )
}

fun findVideoMultipart(currentImage: File, type: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        type,
        currentImage.name,
        currentImage.asRequestBody("videos/*".toMediaTypeOrNull())
    )
}

fun String.getRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun String.findNameFromString(): String {
    val list: List<String> = this.split("/")
    return "${list[list.size - 2]}/${list[list.size - 1]}"
}