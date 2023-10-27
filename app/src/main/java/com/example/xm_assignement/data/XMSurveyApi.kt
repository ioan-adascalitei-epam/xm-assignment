package com.example.xm_assignement.data

import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.QuestionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface XMSurveyApi {

    @GET("/questions")
    suspend fun getQuestions(): Response<List<QuestionDto>>

    @POST("/question/submit")
    suspend fun submitResponse(@Body answerDto: AnswerDto): Response<Any>
}