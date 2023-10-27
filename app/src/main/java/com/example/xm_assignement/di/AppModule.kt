package com.example.xm_assignement.di

import com.example.xm_assignement.data.XMSurveyApi
import com.example.xm_assignement.survey.repository.SurveyRepository
import com.example.xm_assignement.survey.repository.SurveyRepositoryImpl
import com.example.xm_assignement.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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