package com.example.xm_assignement.feature.survey.usecase

import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.util.Resource

interface SubmitAnswerUseCase {

    suspend fun submitAnswer(question: Question): Resource<Unit>
}