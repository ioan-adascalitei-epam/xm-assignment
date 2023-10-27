package com.example.xm_assignement.survey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xm_assignement.data.model.AnswerDto
import com.example.xm_assignement.data.model.Question
import com.example.xm_assignement.survey.repository.SurveyRepository
import com.example.xm_assignement.util.DispatcherProvider
import com.example.xm_assignement.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val surveyRepo: SurveyRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class SurveyEvent {
        object SubmitSuccess : SurveyEvent()
        object SubmitFail : SurveyEvent()
        object Loading : SurveyEvent()
        object Empty : SurveyEvent()
    }

    private val _answerEvent = MutableStateFlow<SurveyEvent>(SurveyEvent.Empty)
    val answerEvent: StateFlow<SurveyEvent> = _answerEvent

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion

    private val _questionsSubmitted = MutableStateFlow(0)
    val questionsSubmitted: StateFlow<Int> = _questionsSubmitted

    private val _questionNumber = MutableStateFlow(Pair(1, 1))
    val questionNumber: StateFlow<Pair<Int, Int>> = _questionNumber

    private lateinit var questions: List<Question>

    init {
        viewModelScope.launch(dispatchers.io) {
            when (val response = surveyRepo.getQuestions()) {
                is Resource.Error -> {
                    print(response.error)
                }

                is Resource.Success -> {
                    questions = response.data!!
                    _questionNumber.value = Pair(0, questions.size)
                    _currentQuestion.value = questions[0]
                }
            }
        }
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch(dispatchers.io) {
            val currentQuestion = _currentQuestion.value
            currentQuestion!!.answer = answer
            when (val response = surveyRepo.submitAnswer(AnswerDto(currentQuestion.id, answer))) {
                is Resource.Error -> {
                    _answerEvent.value = SurveyEvent.SubmitFail
                }

                is Resource.Success -> {
                    _answerEvent.value = SurveyEvent.SubmitSuccess
                    _currentQuestion.value?.isSubmitted = true
                    _currentQuestion.value?.answer = answer
                }
            }
        }
    }

    fun nextQuestion() {
        val index = _questionNumber.value.first + 1
        if (index < _questionNumber.value.second) {
            _questionNumber.value = Pair(index, questions.size)
            _currentQuestion.value = questions[index]
        }
    }

    fun previousQuestion() {
        val index = _questionNumber.value.first - 1
        if (index >= 0 && index < _questionNumber.value.second) {
            _questionNumber.value = Pair(index, questions.size)
            _currentQuestion.value = questions[_questionNumber.value.first]
        }
    }

}