package com.gym.gymapp.ui.organisationsList.model

data class OrganisationListModel(
    val `data`: List<OrganisationData>,
    val success: Boolean
)

data class OrganisationData(
    val city_id: String,
    val city_name: String,
    val description: String,
    val id: Int,
    val category_id: String,
    val latitude: String,
    val longitude: String,
    val organization_addresse: String,
    val organization_image: String,
    val organization_name: String,
    val state_id: String,
    val state_name: String
)