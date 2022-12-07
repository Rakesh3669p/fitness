package com.gym.gymapp.ui.loginSignUp.otp

data class OTPModel(
    val data: UserData,
    val success: Boolean,
    val token: String
)

data class UserData(
    val address: String,
    val area_id: String,
    val city_id: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val image: String,
    val name: String,
    val phone_number: String,
    val pincode: String,
    val state_id: String
)