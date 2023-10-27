package com.example.xm_assignement.data.model

data class Question(
    val id: Double,
    val question: String,
    var answer: String? = null,
    var isSubmitted: Boolean = false
)
