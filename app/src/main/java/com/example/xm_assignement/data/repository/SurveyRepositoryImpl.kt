package com.example.xm_assignement.data.repository

import com.example.xm_assignement.data.XMSurveyApi
import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.data.model.QuestionDto
import com.example.xm_assignement.util.Resource
import java.lang.Exception
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(
    private val api: XMSurveyApi
) : SurveyRepository {

    override suspend fun getQuestions(): Resource<List<QuestionDto>> {
        return try {
            val response = api.getQuestions()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred!")
        }
    }

    override suspend fun submitAnswer(answerDto: AnswerDto): Resource<Unit> {
        return try {
            val response = api.submitResponse(answerDto)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred!")
        }
    }
}