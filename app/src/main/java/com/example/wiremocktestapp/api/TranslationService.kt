package com.example.wiremocktestapp.api

import com.example.wiremocktestapp.api.model.Translate
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationService {
    @GET("gapi/translate?from=eng&dest=pol&format=json&pretty=true")
    fun translate(@Query("phrase") phrase: String): Observable<Translate>
}