package com.example.xm_assignement.data.repository

import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.QuestionDto
import com.example.xm_assignement.util.Resource

interface SurveyRepository {

    suspend fun getQuestions(): Resource<List<QuestionDto>>

    suspend fun submitAnswer(answerDto: AnswerDto): Resource<Unit>
}