package com.videoPlayer.ui.showPost

data class ShowPostResponse(
    val data: ArrayList<Data>,
    val message: String,
    val status: Int
)

data class Data(
    val id: Int,
    val user_id: Int,
    val videos: String?,
    val video_thumbnail: String,
    val discription: String
)