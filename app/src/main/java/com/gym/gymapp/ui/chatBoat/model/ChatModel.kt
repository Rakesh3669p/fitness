package com.gym.gymapp.ui.chatBoat.model

data class ChatModel(
    val data: List<ChatData>,
    val success: Boolean
)

data class ChatData(
    val id: Int?=0,
    val options: List<String>,
    val question: String?="",
    val right: Boolean = false,
    val answer: String?="",

)