package com.example.wiremocktestapp

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.wiremocktestapp.dagger.DaggerAppComponent
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import okhttp3.HttpUrl
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityWireMockTest {

    private val json = "{\n" +
            "\t\"result\": \"ok\",\n" +
            "\t\"tuc\": [{\n" +
            "\t\t\"phrase\": {\n" +
            "\t\t\t\"text\": \"Android\",\n" +
            "\t\t\t\"language\": \"pl\"\n" +
            "\t\t},\n" +
            "\t\t\"meanings\": [],\n" +
            "\t\t\"meaningId\": 9080984824504891595,\n" +
            "\t\t\"authors\": [60172]\n" +
            "\t}],\n" +
            "\t\"phrase\": \"cup\",\n" +
            "\t\"from\": \"en\",\n" +
            "\t\"dest\": \"pl\",\n" +
            "\t\"authors\": {\n" +
            "\t\t\"60172\": {\n" +
            "\t\t\t\"U\": \"http://www.omegawiki.org/\",\n" +
            "\t\t\t\"id\": 60172,\n" +
            "\t\t\t\"N\": \"omegawiki\",\n" +
            "\t\t\t\"url\": \"https://glosbe.com/source/60172\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}"

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    val idlingResourceRule = RxIdlingResourceRule()

    @get:Rule
    val wireMockRule = WireMockRule()

    @Before
    fun setUp() {
        WireMock.stubFor(WireMock.get(WireMock.urlMatching(".*"))
                .willReturn(WireMock.aResponse()
                        .withBody(json)
                ))
        val application = InstrumentationRegistry.getTargetContext().applicationContext
        val component = DaggerAppComponent
                .builder()
                .baseUrl(HttpUrl.parse("http://localhost:8080")!!)
                .create(application as App)
        component.inject(application)
        activityRule.launchActivity(Intent())
    }

    @Test
    fun verifyTranslation() {
        //given
        Espresso.onView(ViewMatchers.withId(R.id.translationInput))
                .perform(ViewActions.typeText("word"), ViewActions.closeSoftKeyboard())

        //when
        Espresso.onView(ViewMatchers.withId(R.id.translateButton))
                .perform(ViewActions.click())

        //then
        Espresso.onView(ViewMatchers.withId(R.id.translateResult))
                .check(ViewAssertions.matches(ViewMatchers.withText("Android")))
    }
}