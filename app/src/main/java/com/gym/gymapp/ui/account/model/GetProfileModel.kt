package com.gym.gymapp.ui.account.model

data class GetProfileModel(
    val data: ProfileData,
    val success: Boolean
)
data class ProfileData(
    val created_at: String,
    val email: String,
    val id: Int,
    val image: String,
    val name: String,
    val phone_number: String,
    val refrel_code: String,
    val refrel_code_link: String
)