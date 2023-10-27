package com.example.xm_assignement.di

import com.example.xm_assignement.data.XMSurveyApi
import com.example.xm_assignement.data.repository.SurveyRepository
import com.example.xm_assignement.data.repository.SurveyRepositoryImpl
import com.example.xm_assignement.feature.survey.usecase.GetSurveyQuestionsUseCase
import com.example.xm_assignement.feature.survey.usecase.SubmitAnswerUseCase
import com.example.xm_assignement.feature.survey.usecase.convertor.QuestionConvertor
import com.example.xm_assignement.feature.survey.usecase.impl.GetSurveyQuestionsUseCaseImpl
import com.example.xm_assignement.feature.survey.usecase.impl.SubmitAnswerUseCaseImpl
import com.example.xm_assignement.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://xm-assignment.web.app"

    @Singleton
    @Provides
    fun provideXMSurveyApi(): XMSurveyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(XMSurveyApi::class.java)

    @Singleton
    @Provides
    fun provideSurveyRepository(api: XMSurveyApi): SurveyRepository = SurveyRepositoryImpl(api)

    @Singleton
    @Provides
    fun provideQuestionConvertor(): QuestionConvertor = QuestionConvertor

    @Singleton
    @Provides
    fun provideGetQuestionsUseCase(
        repository: SurveyRepository,
        convertor: QuestionConvertor
    ): GetSurveyQuestionsUseCase =
        GetSurveyQuestionsUseCaseImpl(repository, convertor)

    @Singleton
    @Provides
    fun provideSubmitAnswerUseCase(repository: SurveyRepository): SubmitAnswerUseCase =
        SubmitAnswerUseCaseImpl(repository)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}