package com.gym.gymapp.ui.slugs.model

data class SlugModel(
    val data: List<SlugData>,
    val success: Boolean
)

data class SlugData(
    val created_at: String,
    val description: String,
    val id: Int,
    val page: String,
    val page_title: String
)