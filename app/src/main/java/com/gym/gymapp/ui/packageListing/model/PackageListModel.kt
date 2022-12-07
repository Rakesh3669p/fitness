package com.gym.gymapp.ui.packageListing.model

data class PackageListModel(
    val data: List<PackageListData>,
    val success: Boolean
)

data class PackageListData(
    val id: Int,
    val category_name: String,
    val slug: String,
    val image: String,
    val mobile_image: String,
    val starting_price: String,
    val created_at: String
)