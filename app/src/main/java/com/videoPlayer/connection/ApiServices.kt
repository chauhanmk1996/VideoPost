package com.videoPlayer.connection

import com.videoPlayer.constant.ApiConstant
import com.videoPlayer.ui.createPost.CreatePostResponse
import com.videoPlayer.ui.showPost.ShowPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface ApiServices {

    @Multipart
    @POST(ApiConstant.CREATE_POST)
    fun createPostApi(
        @Part("name") name: RequestBody?,
        @Part("user_id") user_id: RequestBody?,
        @Part("post_type") post_type: RequestBody?,
        @Part("discription") discription: RequestBody?,
        @Part images: ArrayList<MultipartBody.Part>? = null,
        @Part videos: MultipartBody.Part? = null,
        @Part video_thumbnail: MultipartBody.Part? = null,
    ): Observable<CreatePostResponse>

    @FormUrlEncoded
    @POST(ApiConstant.POST_LISTING)
    fun showPostApi(
        @Field("user_id") user_id: Int = 92
    ): Observable<ShowPostResponse>
}