package com.gym.gymapp.ui.organisationsList.model

data class FilterCitiesModel(
    val data: List<FilterCitiesData>,
    val success: Boolean
)

data class FilterCitiesData(
    val city_id: Int,
    val image: String,
    val name: String,
    val state_id: String
)