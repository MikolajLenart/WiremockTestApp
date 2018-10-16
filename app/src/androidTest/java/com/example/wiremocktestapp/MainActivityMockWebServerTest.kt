package com.example.wiremocktestapp

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import com.example.wiremocktestapp.dagger.DaggerAppComponent
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityMockWebServerTest {

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

    val server = MockWebServer()

    @Before
    fun setUp() {
        server.enqueue(MockResponse().setBody(json))
        val application = InstrumentationRegistry.getTargetContext().applicationContext
        val component = DaggerAppComponent
                .builder()
                .baseUrl(server.url("/"))
                .create(application as App)
        component.inject(application)
        activityRule.launchActivity(Intent())
    }


    @After
    fun tearDown() {
        server.shutdown()
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