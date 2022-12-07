package com.gym.gymapp.ui.address.model

data class GetAddressModel(
    val data: List<AddressData>,
    val success: Boolean
)

data class AddressData(
    val active_address: String,
    val address: String,
    val address_type: String,
    val alternate_phone: String,
    val city_id: String,
    val city_name: String,
    val created_at: String,
    val id: Int,
    val pincode: String,
    val state_id: String,
    val state_name: String
)