package com.example.wiremocktestapp.dagger

import com.example.wiremocktestapp.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [ApiModule::class])
    abstract fun bindMainAvtivity(): MainActivity
}