package com.example.wiremocktestapp

import com.example.wiremocktestapp.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import okhttp3.HttpUrl

open class  App: DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>
            = DaggerAppComponent
            .builder()
            .baseUrl(HttpUrl.parse("https://glosbe.com/")!!)
            .create(this)
}