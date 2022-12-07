package com.gym.gymapp.ui.achievements.model

data class AchievementsModel(
    val data: List<AchievementsData>,
    val success: Boolean
)
data class AchievementsData(
    val complete_date: String,
    val created_at: String,
    val day_count: Int,
    val description: String,
    val id: Int,
    val start_date: String,
    val status: String,
    val title: String
)
