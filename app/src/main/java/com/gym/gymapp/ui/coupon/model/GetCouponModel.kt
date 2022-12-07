package com.gym.gymapp.ui.coupon.model

data class GetCouponModel(
    val `data`: List<CouponData>,
    val success: Boolean
)

data class CouponData(
    val coupon_code: String,
    val created_at: String,
    val description: String,
    val discount: String,
    val id: Int,
    val inr_amount: Any,
    val select_type: String,
    val sub_title: String,
    val title: String
)