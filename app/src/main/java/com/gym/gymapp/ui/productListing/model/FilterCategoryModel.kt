package com.gym.gymapp.ui.productListing.model

data class FilterCategoryModel(
    val `data`: List<FilterCategoryData>,
    val success: Boolean
)

data class FilterCategoryData(
    val category_name: String,
    val created_at: String,
    val id: Int,
    val image: String,
    val mobile_image: String,
    val slug: String,
    var checked: Boolean,
    val starting_price: Any,
    val sub_category: List<FilterSubCategoryData>
)

data class SubCategory(
    val id :String,
    val category_id :String,
    val name :String,
    val slug :String,
    val image :String,
    val created_at :String,
    val checked :Boolean,
)
