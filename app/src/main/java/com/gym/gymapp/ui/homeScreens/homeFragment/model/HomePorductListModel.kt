package com.gym.gymapp.ui.homeScreens.homeFragment.model

import com.gym.gymapp.ui.productListing.model.ProductData

data class HomeProductListModel(
    val data: List<ProductData>,
    val success: Boolean
)

