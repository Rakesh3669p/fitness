package com.gym.gymapp.ui.productListing.model

data class FilterVendorModel(
    val data: List<FilterVendorData>,
    val success: Boolean
)

data class FilterVendorData(
    val created_at: String,
    val id: Int,
    val name: String,
    val name_of_organization: String,
    val organization_icon: String,
    val phone_number: String,
    val profile: String,
    val type_of_organization: String
)