package com.example.xm_assignement.survey

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
                    viewModel.submitAnswer(binding.etAnswer.text.toString())
                }
            }

            tvPrevious.setOnClickListener {
                viewModel.previousQuestion()
            }

            tvNext.setOnClickListener {
                viewModel.nextQuestion()
            }

            btnRetry.setOnClickListener {
                viewModel.submitAnswer(binding.etAnswer.text.toString())
            }
            etAnswer.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    btnSubmit.isEnabled = s.toString().isNotBlank()
                }

                override fun afterTextChanged(s: Editable?) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnSubmit.isEnabled = s.toString().isNotBlank()
                }
            })
        }

        lifecycleScope.launch {
            viewModel.currentQuestion.collect { q ->
                q?.let { question ->
                    setupQuestionState()
                    with(binding) {
                        tvQuestion.text = question.question
                        etAnswer.setText(question.answer.orEmpty())
                        btnSubmit.isEnabled =
                            question.isSubmitted or question.answer.isNullOrBlank().not()
                        btnSubmit.text =
                            if (question.isSubmitted) getText(R.string.survey_submitted) else
                                getText(R.string.survey_submit)
                    }
                }
            }
        }

        lifecycleScope.launch {

            viewModel.answerEvent.collect { event ->
                when (event) {
                    is SurveyViewModel.SurveyEvent.SubmitSuccess -> {
                        binding.tvSuccess.isVisible = true
                        binding.surveyProgress.isVisible = false
                    }

                    is SurveyViewModel.SurveyEvent.SubmitFail -> {
                        binding.ctFail.isVisible = true
                        binding.surveyProgress.isVisible = false

                    }

                    SurveyViewModel.SurveyEvent.Loading -> {
                        binding.surveyProgress.isVisible = true
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.questionNumber.collect { pair ->
                binding.tvQuestionCounter.text =
                    getString(R.string.survey_header, pair.first + 1, pair.second)
                binding.tvPrevious.isEnabled = pair.first > 0
                binding.tvNext.isEnabled = pair.first + 1 < pair.second
            }
        }

        lifecycleScope.launch {
            viewModel.questionsSubmitted.collect {
                binding.tvQuestionsSubmitted.text =
                    getString(R.string.survey_question_submitted, it)
            }
        }
    }

    private fun setupQuestionState() {
        with(binding) {
            tvSuccess.isVisible = false
            ctFail.isVisible = false
        }
    }
}