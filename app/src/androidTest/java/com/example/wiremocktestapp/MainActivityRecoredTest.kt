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
import com.github.tomakehurst.wiremock.core.WireMockApp
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import okhttp3.HttpUrl
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityRecoredTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    val idlingResourceRule = RxIdlingResourceRule()

    private var wireMockServer: WireMockServer? = null

    private val dataDirectory = "/data/data"
    private val wireMockDirectory = "wiremock"

    private val fileUtils: FileUtils = FileUtils(InstrumentationRegistry.getTargetContext())

    private val rootDirectory by lazy {
        dataDirectory + "/" + InstrumentationRegistry.getTargetContext().packageName + "/" + wireMockDirectory
    }

    private val mappingDirectory by lazy {
        rootDirectory + "/" + WireMockApp.MAPPINGS_ROOT
    }

    private val fileDirectory by lazy {
        rootDirectory + "/" + WireMockApp.FILES_ROOT
    }

    @Before
    fun setUp() {
        removeOldWireMockStubs()
        createWireMockFolderStructure()
        copyNewWireMockStubs()
        val application = InstrumentationRegistry.getTargetContext().applicationContext
        val component = DaggerAppComponent
                .builder()
                .baseUrl(HttpUrl.parse("http://localhost:8080")!!)
                .create(application as App)
        component.inject(application)
        startWireMock(application)
        activityRule.launchActivity(Intent())
    }

    fun startWireMock(application: App) {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8080)
                .withRootDirectory(rootDirectory))
        wireMockServer?.start()
    }

    @After
    fun tearDown() {
        wireMockServer?.stop()
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

    private fun removeOldWireMockStubs() {
        fileUtils.deleteRecursive(File(rootDirectory))
    }

    private fun createWireMockFolderStructure() {
        val wireMockFolderPaths = object : ArrayList<String>() {
            init {
                add(rootDirectory)
                add(mappingDirectory)
                add(fileDirectory)
            }
        }
        for (folderPath in wireMockFolderPaths) {
            val dir = File(folderPath)
            if (!dir.exists()) {
                dir.mkdir()
            }
        }
    }

    private fun copyNewWireMockStubs() {
        fileUtils.copyFileOrDir(wireMockDirectory)
    }
}