package com.gym.gymapp.ui.homeScreens.blogScreens.model

data class BlogsDetailsModel(
    val data: BlogsDetailsData,
    val success: Boolean
)

data class BlogsDetailsData(
    val blog_category_id: String,
    val blog_name: String,
    val created_at: String,
    val created_by: String,
    val description: String,
    val id: Int,
    val image: String,
    val slug: String
)