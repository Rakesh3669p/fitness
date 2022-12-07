package com.gym.gymapp.ui.homeScreens.searchScreen.model

data class SearchSuggestionModel(
    val data: List<SearchData>,
    val success: Boolean
)

data class SearchData(
    val name: String
)