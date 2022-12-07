package com.gym.gymapp.ui.ActivePacks.model

data class ActivePacksModel(
    val data: List<ActivePacksData>,
    val success: Boolean
)


data class ActivePacksData(
    val order_id: Int,
    val package_id: Int,
    val package_image: String,
    val package_name: String,
    val package_price: String,
    val package_duration: String,
    val package_start_date: String,
    val package_expire_date: String,
    val package_status: String,
    val service_provider_name: String,
    val service_provider_phone_number: String,
    val organization_name: String,
    val organization_addresse: String,
    val package_strat_end_time: String,
    val today_attendance_status: Boolean,
    val left_days: Int,
    val is_expire: Int,
    val attendance_status: Boolean,
)