package com.gym.gymapp.ui.productListing.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gym.gymapp.data.Converter

data class ProductListModel(
    val data: MutableList<ProductData>,
    val success: Boolean
)

data class ProductData(
    val id: Int=0,
    val category_id: String?="",
    val created_at: String?="",
    val description: String?="",
    val duration: String?="",
    val name: String?="",
    val package_gallry: List<String>?= null,
    val price: String?="",
    val service_provider_id: String?="",
    val slug: String?="",
    val sub_category_id: String?="",
    val thumbnail: String?="",
    val vendor_email: String?="",
    val vendor_name: String?="",
    val vendor_phone_number: String?="",
    val vendor_profile: String?="",
    val user_id: String?="",
    val filter_type:String="",

)