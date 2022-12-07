package com.gym.gymapp.ui.commonViewModel.model

data class DistanceMatrixModel(
    val destination_addresses: List<String>,
    val origin_addresses: List<String>,
    val rows: List<Row>,
    val status: String
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Element(
    val distance: Distance,
    val duration: Duration,
    val status: String
)

data class Row(
    val elements: List<Element>
)