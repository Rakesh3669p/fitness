package com.gym.gymapp.ui.address.model

data class CityModel(
    val data: List<CityData>,
    val success: Boolean
)

data class CityData(
    val id: Any,
    val name: Any,
    val state_id: Any,
){
    override fun toString(): String {
        return name.toString()
    }
}

