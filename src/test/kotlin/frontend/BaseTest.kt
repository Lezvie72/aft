package frontend

import extensions.FrontendInitMethods
import io.qameta.allure.Allure
import io.qameta.allure.Attachment
import io.qameta.allure.model.Status
import models.user.classes.DefaultUser
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import pages.AuthorizationProvider
import pages.BasePage
import pages.core.actions.AssertActions
import pages.core.actions.PrerequisiteActions
import utils.Constants
import utils.Environment
import utils.helpers.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level


//@Execution(ExecutionMode.CONCURRENT)
@Extensions(
    ExtendWith(FrontendInitMethods::class)
//    ExtendWith(JiraExtension::class),
//    ExtendWith(LockHandlerExtension::class)
)
@Tag("Regress")
open class BaseTest {

    companion object {
        const val USER_ADDRESS_LOCK = "User addresses"

        private fun ChromeOptions.applyTracing(): ChromeOptions {
            val logPrefs = LoggingPreferences()
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL)
            val perfLogPrefs: MutableMap<String, Any> = mutableMapOf()
            perfLogPrefs["traceCategories"] = "network"
            perfLogPrefs["enableNetwork"] = true
            perfLogPrefs["enablePage"] = false
            return this.apply {
                setExperimentalOption("perfLoggingPrefs", perfLogPrefs)
                setCapability("goog:loggingPrefs", logPrefs)
            }
        }

        fun newDriver(): ChromeDriver {
            val options = ChromeOptions().apply {
//                addArguments("--headless")
                addArguments("--window-size=1920,1080")
                addArguments("--disable-dev-shm-usage")
                addArguments("--no-sandbox")
                addArguments("--disable-gpu")
            }

            if (System.getProperty("traceNetwork", "false") == "true") {
                options.applyTracing()
            }

            return ChromeDriver(options).apply {
                manage().window().maximize()
                manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS)
                manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS)
//                get(Environment.atm_front_url)
            }
        }

        fun <T1> prerequisite(body: (PrerequisiteActions<WebDriver>).() -> T1) {
            val driver = newDriver()
            try {
                PrerequisiteActions<WebDriver>(BasePage(driver), driver).run(body)
            } catch (e: Exception) {
            } finally {
                driver.quit()
            }
        }

        fun <T1> prerequisite(driver: WebDriver, body: (PrerequisiteActions<WebDriver>).() -> T1) =
            PrerequisiteActions(BasePage(driver), driver).run(body)

    }

    fun createDriver(): WebDriver {
        return newDriver().also { drivers.add(it) }
    }

    private val drivers: MutableList<WebDriver> = mutableListOf()

    protected inline fun <reified T : BasePage> openPage(
        noinline auth: AuthorizationProvider<out BasePage>.() -> Unit = {
            submit(Users.USER)
        }
    ): T = utils.helpers.openPage(driver, auth)

    val driver by lazy {
        createDriver()
    }

    fun WebDriver.openTab(): String {
        (this as JavascriptExecutor).executeScript("window.open()")
        this.get(Environment.atm_front_url)
        this.switchTo().window(windowHandles.last())
        return this.windowHandle
    }

    fun WebDriver.switchTab(handle: String): WebDriver {
        this.switchTo().window(handle)
        return this
    }

    fun newUser() = DefaultUser(
        email = generateEmail(),
        password = Constants.DEFAULT_PASSWORD,
        project = 1
    )

    private val prerequisiteActions = PrerequisiteActions(BasePage(driver), driver)
    fun <T1> prerequisite(body: (PrerequisiteActions<WebDriver>).() -> T1) = prerequisiteActions.run(body)

    private val assertActions = AssertActions(BasePage(driver), driver)
    private var testPassed: Boolean = true
    fun softAssert(body: AssertActions<WebDriver>.() -> Unit) {
        try {
            assertActions.run(body)
        } catch (e: AssertionError) {
            attachScreenshot("Soft assert was failed.", driver)
            updateStep {
                status = Status.FAILED
            }
            Allure.addAttachment("Soft assert was failed", e.localizedMessage)
            testPassed = false
        }
    }

    @BeforeEach
    fun setup() {
        testPassed = true
    }

    @AfterEach
    fun close() {
        drivers.forEach {
            try {
                takeScreenshot(it)
                it.getPerformance().forEach { log ->
                    attach(log.first, log.second)
                }
            } catch (e: Exception) {

            } finally {
                it.quit()
                Assert.assertTrue("One or more steps was failed", testPassed)
            }
        }
    }

    @Attachment("Screenshot")
    fun takeScreenshot(driver: WebDriver = this.driver): ByteArray {
        return (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

}