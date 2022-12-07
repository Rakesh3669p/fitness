package com.gym.gymapp.ui.loginSignUp.LoginScreen.model

data class SocialLoginModel(
    val data: SocialLoginData,
    val success: Boolean,
    val token: String
)

data class SocialLoginData(
    val address: Any,
    val area_id: String,
    val city_id: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val image: String,
    val name: String,
    val phone_number: Any,
    val pincode: String,
    val state_id: String
)