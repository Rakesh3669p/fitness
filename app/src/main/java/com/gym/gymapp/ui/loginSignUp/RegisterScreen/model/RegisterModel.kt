package com.gym.gymapp.ui.loginSignUp.RegisterScreen.model


data class RegisterModel(
    val data: UserData,
    val message: String,
    val success: Boolean,
    val otp: String,
)
data class UserData(
    val created_at: String,
    val email: String,
    val id: Int,
    val image: String,
    val name: String,
    val phone_number: String
)
