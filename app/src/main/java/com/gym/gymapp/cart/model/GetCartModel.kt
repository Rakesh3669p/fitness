package com.gym.gymapp.cart.model

data class GetCartModel(
    val data: List<GetCartData>,
    val success: Boolean
)

data class GetCartData(
    val id: Int,
    val duration: String,
    val thumbnail: String,
    val description: String,
    val package_id: Int,
    val image: String,
    val package_name: String,
    val package_price: String,
    val priceduration: List<Any>
)