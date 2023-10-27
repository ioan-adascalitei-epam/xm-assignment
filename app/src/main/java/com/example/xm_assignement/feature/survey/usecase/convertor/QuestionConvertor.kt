package com.example.xm_assignement.feature.survey.usecase.convertor

import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.data.model.QuestionDto

object QuestionConvertor {

    fun dtoToQuestion(dto: QuestionDto): Question = Question(dto.id, dto.question)
}