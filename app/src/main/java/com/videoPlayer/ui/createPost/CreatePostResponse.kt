package com.videoPlayer.ui.createPost

data class CreatePostResponse(
    val message: String,
    val data: Data,
    val status: Int
)

data class Data(
    val created_at: String,
    val discription: String,
    val group_id: Any,
    val id: Int,
    val images: String,
    val name: String,
    val post_type: String,
    val updated_at: String,
    val user_id: Int,
    val video_thumbnail: String,
    val videos: Any
)