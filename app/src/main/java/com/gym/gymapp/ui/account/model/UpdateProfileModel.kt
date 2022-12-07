package com.gym.gymapp.ui.account.model

data class UpdateProfileModel(
    val `data`: UpdateProfileData,
    val message: String,
    val success: Boolean
)

data class UpdateProfileData(
    val created_at: String,
    val email: String,
    val id: Int,
    val image: String,
    val name: String,
    val phone_number: Any,
    val refrel_code: String,
    val refrel_code_link: String
)