package com.example.xm_assignement.feature.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.xm_assignement.databinding.ActivityMainBinding
import com.example.xm_assignement.feature.survey.ui.SurveyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartSurvey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
    }
}