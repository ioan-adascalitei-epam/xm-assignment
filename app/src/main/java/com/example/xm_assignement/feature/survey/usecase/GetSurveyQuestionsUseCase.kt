package com.example.xm_assignement.feature.survey.usecase

import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.util.Resource

interface GetSurveyQuestionsUseCase {

    suspend fun get(): Resource<List<Question>>
}