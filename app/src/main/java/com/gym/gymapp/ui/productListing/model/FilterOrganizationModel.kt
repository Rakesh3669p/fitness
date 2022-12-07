package com.gym.gymapp.ui.productListing.model

data class FilterOrganizationModel(
    val data: List<FilterOrganizationData>,
    val success: Boolean
)
data class FilterOrganizationData(
    val created_at: String,
    val id: Int,
    var checked: Boolean,
    val organization_addresse: String,
    val organization_image: String,
    val organization_name: String,
    val slug: String
)
