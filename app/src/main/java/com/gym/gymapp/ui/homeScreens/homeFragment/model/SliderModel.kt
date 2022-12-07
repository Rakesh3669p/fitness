package com.gym.gymapp.ui.homeScreens.homeFragment.model

data class SliderModel(
    val data: List<SliderData>,
    val success: Boolean
)

data class SliderData(
    val created_at: String,
    val id: Int,
    val image: String,
    val slug: String,
    val title: String
)