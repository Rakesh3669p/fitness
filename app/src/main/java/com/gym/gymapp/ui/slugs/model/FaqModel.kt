package com.gym.gymapp.ui.slugs.model

data class FaqModel(

    val data: List<FAQData>,
    val success: Boolean
)


data class FAQData(
    val description: String,
    val id: Int,
    val title: String,
    var showDesc:Boolean = false
)