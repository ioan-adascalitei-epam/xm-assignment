package com.example.xm_assignement.feature.survey.usecase.impl

import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.data.repository.SurveyRepository
import com.example.xm_assignement.feature.survey.usecase.SubmitAnswerUseCase
import com.example.xm_assignement.util.Resource

class SubmitAnswerUseCaseImpl(
    private val surveyRepository: SurveyRepository
) : SubmitAnswerUseCase {
    override suspend fun submitAnswer(question: Question): Resource<Unit> =
        surveyRepository.submitAnswer(AnswerDto(question.id, question.answer.orEmpty()))

}