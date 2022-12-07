package com.gym.gymapp.ui.productListing.model

data class FilterSubCategoryModel(
        val data: List<FilterSubCategoryData>,
    val success: Boolean
)

data class FilterSubCategoryData(
    val category_id: String,
    val created_at: String,
    val id: Int,
    val image: String,
    val name: String,
    val slug: String,
    var checked: Boolean,
)