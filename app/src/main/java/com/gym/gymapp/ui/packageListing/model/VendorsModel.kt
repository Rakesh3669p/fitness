package com.gym.gymapp.ui.packageListing.model

data class VendorsModel(
    val data: List<VendorsData>,
    val success: Boolean
)

data class VendorsData(
    val created_at: String,
    val id: Int,
    val name: String,
    val name_of_organization: String,
    val organization_icon: String,
    val phone_number: String,
    val profile: String,
    val type_of_organization: String
)