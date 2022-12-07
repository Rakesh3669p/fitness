package com.gym.gymapp.ui.vendors.model

data class VendorDetailsModel(
    val data: List<VendorDetailsData>,
    val success: Boolean
)

data class VendorDetailsData(
    val about_the_organization: Any,
    val additional_services: Any,
    val email: String,
    val gst_registration_no: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val name_of_organization: String,
    val organization_icon: String,
    val organization_list: List<Organization>,
    val packages_list: List<Packages>,
    val phone_number: String,
    val profile: String,
    val start_list: List<Start>,
    val image_gallerys: List<String>,
    val total_member: Int,
    val total_organization: Int,
    val total_package: Int,
    val total_start: Int,
    val total_trainer: Int,
    val trainer_list: List<Trainer>,
    val type_of_organization: String
)

data class Trainer(
    val name: String,
    val profile: String,
    val trainer_id: Int
)

data class Start(
    val name: String,
    val profile: String,
    val star_icon_id: Int
)

data class Packages(
    val duration: Any,
    val name: String,
    val package_id: Int,
    val price: Any,
    val thumbnail: String
)

data class Organization(
    val organization_id: Int,
    val organization_image: String,
    val organization_name: String
)