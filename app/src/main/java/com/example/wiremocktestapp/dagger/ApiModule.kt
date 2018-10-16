package com.example.wiremocktestapp.dagger

import com.example.wiremocktestapp.api.ApiConfiguration
import com.example.wiremocktestapp.api.TranslationService
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl

@Module
class ApiModule {

    @Provides
    fun provideService(baseUrl: HttpUrl)
            = ApiConfiguration().createConfiguration(baseUrl).create(TranslationService::class.java)
}