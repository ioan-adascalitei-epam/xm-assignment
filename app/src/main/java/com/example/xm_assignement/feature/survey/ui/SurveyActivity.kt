package com.example.xm_assignement.feature.survey.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.xm_assignement.R
import com.example.xm_assignement.databinding.ActivitySurveyBinding
import com.example.xm_assignement.feature.survey.viewmodel.SurveyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private val viewModel: SurveyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnSubmit.setOnClickListener {
                val answer = binding.etAnswer.text.toString()
                if (answer.isNullOrBlank()) {
                    Toast.makeText(this@SurveyActivity, "Answer incomplete", Toast.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.uiIntent(SurveyViewModel.QuestionScreenEvent.SubmitEvent(binding.etAnswer.text.toString()))
                }
            }

            tvPrevious.setOnClickListener {
                viewModel.uiIntent(SurveyViewModel.QuestionScreenEvent.PreviousQuestionEvent)
            }

            tvNext.setOnClickListener {
                viewModel.uiIntent(SurveyViewModel.QuestionScreenEvent.NextQuestionEvent)
            }

            btnRetry.setOnClickListener {
                viewModel.uiIntent(SurveyViewModel.QuestionScreenEvent.RetryEvent(binding.etAnswer.text.toString()))
            }
            etAnswer.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    btnSubmit.isEnabled = s.toString().isNotBlank() and etAnswer.isEnabled
                }

                override fun afterTextChanged(s: Editable?) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnSubmit.isEnabled = s.toString().isNotBlank() and etAnswer.isEnabled
                }
            })
        }

        lifecycleScope.launch {
            viewModel.screenState.collect { renderScreenState(it) }
        }
    }

    private fun renderScreenState(state: SurveyViewModel.ScreenState) {
        handleQuestionsCount(state.questionCount)
        binding.tvQuestionsSubmitted.text =
            getString(R.string.survey_question_submitted, state.questionSubmitted)
        state.question?.let { question ->
            with(binding) {
                tvQuestion.text = question.question
                etAnswer.setText(question.answer.orEmpty())
                etAnswer.isEnabled =
                    question.answer.isNullOrBlank() or question.isSubmitted.not()
                btnSubmit.isEnabled =
                    question.isSubmitted.not() or question.answer.isNullOrBlank()
                btnSubmit.text =
                    if (question.isSubmitted) getText(R.string.survey_submitted) else
                        getText(R.string.survey_submit)
            }
        }

        when (state.submitState) {
            SurveyViewModel.SubmitState.Empty -> {
                with(binding) {
                    tvSuccess.isVisible = false
                    ctFail.isVisible = false
                    surveyProgress.isVisible = false
                }
            }

            SurveyViewModel.SubmitState.Fail -> {
                with(binding) {
                    tvSuccess.isVisible = false
                    ctFail.isVisible = true
                    surveyProgress.isVisible = false
                }
            }

            SurveyViewModel.SubmitState.Loading -> {
                with(binding) {
                    tvSuccess.isVisible = false
                    ctFail.isVisible = false
                    surveyProgress.isVisible = true
                }
            }

            SurveyViewModel.SubmitState.Success -> {
                with(binding) {
                    tvSuccess.isVisible = true
                    ctFail.isVisible = false
                    surveyProgress.isVisible = false
                }
            }
        }
    }

    private fun handleQuestionsCount(questionCount: Pair<Int, Int>) {
        with(binding) {
            tvQuestionCounter.text =
                getString(R.string.survey_header, questionCount.first + 1, questionCount.second)
            tvPrevious.isEnabled = questionCount.first > 0
            tvNext.isEnabled = questionCount.first + 1 < questionCount.second
        }
    }
}