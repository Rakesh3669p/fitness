package com.gym.gymapp.ui.orders.model

data class OrderHistoryModel(
    val `data`: List<OrdersData>,
    val success: Boolean
)


data class OrdersData(
    val organization_addresse: String,
    val organization_name: String,
    val package_duration: String,
    val package_expire_date: String,
    val package_id: Int,
    val package_image: String,
    val package_name: String,
    val package_price: String,
    val package_start_date: String,
    val package_status: String,
    val service_provider_name: String,
    val service_provider_phone_number: String
)