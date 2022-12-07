package com.gym.gymapp.ui.productDetails.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class PackageDetailModel(
    val data: List<PackageDetailsData>,
    val success: Boolean
)


@Entity(tableName = "cartTable")
data class PackageDetailsData(
    @PrimaryKey
    var id: Int? = 0,
    var category_id: String = "",
    var created_at: String = "",
    var description: String = "",
    var name: String = "",
    var package_gallry: List<String>? = null,
    var cart_status: Boolean = false,
    var priceduration: List<PriceDuration>? = null,
    var package_start_time: List<PackageStartTime>? = null,
    var service_provider_id: String = "",
    var slug: String = "",
    var sub_category_id: String = "",
    var thumbnail: String = "",
    var vendor_email: String = "",
    var vendor_name: String = "",
    var vendor_phone_number: String? = "",
    var vendor_profile: String? = "",
    var latitude: String? = "",
    var longitude: String? = ""
)

@Entity(tableName = "wishListTable")
data class WishListPackageData(
    @PrimaryKey
    var id: Int? = 0,
    var category_id: String="",
    var created_at: String="",
    var description: String="",
    var name: String="",
    var package_gallry: List<String>? = null,
    var priceduration: List<PriceDuration>? = null,
    var service_provider_id: String="",
    var slug: String="",
    var sub_category_id: String="",
    var thumbnail: String="",
    var vendor_email: String="",
    var vendor_name: String="",
    var vendor_phone_number: String="",
    var vendor_profile: String=""
)

data class PriceDuration(
    var status: Boolean = false,
    val month: String,
    val price: String
)

data class PackageStartTime(
    val end_time: String,
    val package_buy_status: String,
    val start_time: String,
    var status: Boolean = false,
    val total_limit: String,
    val total_pending_limit: Int
)
