package com.example.xm_assignement.feature.survey.usecase.impl

import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.data.repository.SurveyRepository
import com.example.xm_assignement.feature.survey.usecase.GetSurveyQuestionsUseCase
import com.example.xm_assignement.feature.survey.usecase.convertor.QuestionConvertor
import com.example.xm_assignement.util.Resource

class GetSurveyQuestionsUseCaseImpl(
    private val surveyRepository: SurveyRepository,
    private val dtoConvertor: QuestionConvertor
) : GetSurveyQuestionsUseCase {
    override suspend fun get(): Resource<List<Question>> =
        when (val response = surveyRepository.getQuestions()) {
            is Resource.Error -> {
                Resource.Error(response.error!!)
            }

            is Resource.Success -> {
                Resource.Success(response.data!!.map { dtoConvertor.dtoToQuestion(it) })
            }
        }
}