package com.gym.gymapp.ui.homeScreens.blogScreens.model

data class GetBlogsModel(
    val data: List<BlogsData>,
    val success: Boolean
)

data class BlogsData(
    val BlogsData: String,
    val blog_name: String,
    val view_count: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val slug: String
)