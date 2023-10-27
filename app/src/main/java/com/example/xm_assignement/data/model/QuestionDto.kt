package com.example.xm_assignement.data.model

data class QuestionDto(
    val id: Double,
    val question: String
) {
    fun toQuestion(): Question {
        return Question(id,question)
    }
}
