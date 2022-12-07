package com.gym.gymapp.ui.ActivePacks.model

data class AttendanceStatsModel(
    val data: List<AttendanceData>,
    val success: Boolean
)

data class AttendanceData(
    val attendance_list: List<Attendance>,
    val organization_addresse: String,
    val organization_name: String,
    val package_id: Int,
    val package_image: String,
    val package_name: String,
    val service_provider_name: String,
    val service_provider_phone_number: String,
    val total_absent: Int,
    val total_present: Int
)

data class Attendance(
    val attendance_date: String,
    val present_status: String
)