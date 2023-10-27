package com.example.xm_assignement.feature.survey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.feature.survey.usecase.GetSurveyQuestionsUseCase
import com.example.xm_assignement.feature.survey.usecase.SubmitAnswerUseCase
import com.example.xm_assignement.util.DispatcherProvider
import com.example.xm_assignement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val getSurveyQuestionsUseCase: GetSurveyQuestionsUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class QuestionScreenEvent {
        data class SubmitEvent(val answer: String) : QuestionScreenEvent()
        data class RetryEvent(val answer: String) : QuestionScreenEvent()
        object NextQuestionEvent : QuestionScreenEvent()
        object PreviousQuestionEvent : QuestionScreenEvent()
    }

    data class ScreenState(
        var question: Question? = null,
        var questionSubmitted: Int = 0,
        var questionCount: Pair<Int, Int> = Pair(0, 0),
        var submitState: SubmitState = SubmitState.Empty
    )
    sealed class SubmitState {
        object Success : SubmitState()
        object Fail : SubmitState()
        object Loading : SubmitState()
        object Empty : SubmitState()
    }

    private lateinit var questions: List<Question>
    
    private var _screensState = MutableStateFlow(ScreenState())
    val screenState: StateFlow<ScreenState> = _screensState

    init {
        viewModelScope.launch(dispatchers.io) {
            when (val response = getSurveyQuestionsUseCase.get()) {
                is Resource.Error -> {
                    print(response.error)
                }

                is Resource.Success -> {
                    questions = response.data!!
                    val screenState = ScreenState(
                        question = questions[0],
                        questionCount = Pair(0, questions.size)
                    )
                    _screensState.value = screenState
                }
            }
        }
    }

    fun uiIntent(intent: QuestionScreenEvent) {
        when (intent) {
            QuestionScreenEvent.NextQuestionEvent -> {
                nextQuestion()
            }

            QuestionScreenEvent.PreviousQuestionEvent -> {
                previousQuestion()
            }

            is QuestionScreenEvent.RetryEvent -> {
                submitAnswer(intent.answer)
            }

            is QuestionScreenEvent.SubmitEvent -> {
                submitAnswer(intent.answer)
            }
        }
    }

    private fun submitAnswer(answer: String) {
        viewModelScope.launch(dispatchers.io) {
            val currentQuestion = _screensState.value.question
            currentQuestion!!.answer = answer
            when (submitAnswerUseCase.submitAnswer(currentQuestion)) {
                is Resource.Error -> {
                    _screensState.value = _screensState.value.copy(submitState = SubmitState.Fail)
                }

                is Resource.Success -> {
                    val currentState = ScreenState(
                        question = currentQuestion.apply {
                            isSubmitted = true
                            this.answer = answer
                        },
                        submitState = SubmitState.Success,
                        questionSubmitted = _screensState.value.questionSubmitted + 1,
                        questionCount = _screensState.value.questionCount
                    )
                    _screensState.value = currentState
                }
            }
        }
    }

    private fun nextQuestion() {
        val currentState = _screensState.value
        val index = currentState.questionCount.first + 1
        if (index < currentState.questionCount.second) {
            val newState = ScreenState(
                questions[index], currentState.questionSubmitted, Pair(index, questions.size)
            )
            _screensState.value = newState
        }
    }

    private fun previousQuestion() {
        val currentState = _screensState.value
        val index = currentState.questionCount.first - 1
        if (index >= 0 && index < currentState.questionCount.second) {
            val newState = ScreenState(
                questions[index], currentState.questionSubmitted, Pair(index, questions.size)
            )
            _screensState.value = newState
        }
    }
}