package com.example.xm_assignement.survey.repository

import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.util.Resource

interface SurveyRepository {

    suspend fun getQuestions(): Resource<List<Question>>

    suspend fun submitAnswer(answerDto: AnswerDto): Resource<Unit>
}