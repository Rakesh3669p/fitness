package com.gym.gymapp.ui.socialRefer.model

data class ReferCodeModel(
    val `data`: List<ReferCodeData>,
    val success: Boolean
)

data class ReferCodeData(
    val html_content: String,
    val refrel_code: String
)