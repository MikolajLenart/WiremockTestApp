package com.example.wiremocktestapp.dagger

import com.example.wiremocktestapp.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.HttpUrl

@Component(modules = [ActivityModule::class, AndroidSupportInjectionModule::class])
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<App>(){

        @BindsInstance
        abstract fun baseUrl(url: HttpUrl): Builder
    }
}