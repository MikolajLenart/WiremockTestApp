package com.example.wiremocktestapp

import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.wiremocktestapp.api.TranslationService
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    val emaulatorLocalhost = "http://10.0.2.2:8080/"
    val localhost = "http://localhost:8080/"

    @Inject
    lateinit var service: TranslationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        translateButton.setOnClickListener {
            service.translate(translationInput.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onNext = {
                        translateResult.setText(it.tuc?.firstOrNull()?.phrase?.text)
                    }, onError = {
                        AlertDialog.Builder(this)
                                .setTitle("Alert")
                                .setMessage("unexpected error occurs")
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                    }
                    )
        }
    }
}
