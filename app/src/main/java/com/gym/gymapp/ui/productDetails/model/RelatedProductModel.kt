package com.gym.gymapp.ui.productDetails.model

import com.gym.gymapp.ui.productListing.model.ProductData

data class RelatedProductModel(
    val data: List<ProductData>,
    val success: Boolean
)

