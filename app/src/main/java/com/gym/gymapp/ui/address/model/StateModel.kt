package com.gym.gymapp.ui.address.model

data class  StateModel(
    val data: List<StateData>,
    val success: Boolean
)

data class StateData(
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}