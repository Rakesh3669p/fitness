package com.gym.gymapp.ui.starIcon.model

data class StarIconModel(
    val data: List<StarIconData>,
    val success: Boolean
)


data class StarIconData(
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val organization_details: OrganizationDetails,
    val profile: String,
    val service_provider_details: ServiceProviderDetails,
    val star_icon_achievements: List<Any>
)

data class OrganizationDetails(
    val organization_addresse: String,
    val organization_id: Int,
    val organization_image: String,
    val organization_latitude: String,
    val organization_longitude: String,
    val organization_name: String
)

data class ServiceProviderDetails(
    val id: Int,
    val name: String,
    val name_of_organization: String,
    val organization_icon: String,
    val phone_number: String,
    val profile: String,
    val type_of_organization: String
)