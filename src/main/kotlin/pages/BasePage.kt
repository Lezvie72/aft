package pages

import io.qameta.allure.model.Status
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import pages.core.actions.*
import ru.yandex.qatools.htmlelements.annotations.Name
import utils.helpers.attachScreenshot
import utils.helpers.getJSExecutor
import utils.helpers.getName
import utils.helpers.updateStep


@Suppress("UNCHECKED_CAST")
open class BasePage(
    val driver: WebDriver
) : WebDriver by driver {

    init {
        PageFactory.initElements(
            HtmlElementBlockDecorator<BasePage>(CustomHtmlElementLocatorFactory(driver, null), this, null), this
        )
        wait(30L) {
            until("Page didnt load in 30 seconds") {
                getJSExecutor(driver).executeScript("return document.readyState") == "complete"
            }
        }
    }

    private val waitActions = WaitActions(this, driver)
    fun <T1> wait(body: (WaitActions<WebDriver>).() -> T1) = waitActions.run(body)
    fun <T1> wait(timeOut: Long, body: (WaitActions<WebDriver>).() -> T1) = WaitActions(this, driver, timeOut).run(body)
    fun <T1> nonCriticalWait(timeOut: Long = getTimeoutInSeconds(), body: (WaitActions<WebDriver>).() -> T1) = try {
        WaitActions(this, driver, timeOut).run(body)
    } catch (e: TimeoutException) {
        attachScreenshot("Non critical waiting failed ${this.javaClass.name}", driver)
        updateStep {
            status = Status.BROKEN
        }
    }

    private val elementActions = ElementActions(this, driver)
    fun <T1> e(body: (ElementActions<WebDriver>).() -> T1) = elementActions.run(body)

    private val checkActions = CheckActions(this, driver)
    fun <T1> check(body: (CheckActions<WebDriver>).() -> T1) = checkActions.run(body)

    private val assertActions = AssertActions(this, driver)
    fun assert(body: (AssertActions<WebDriver>).() -> Unit) = assertActions.run(body)

    private val alertActions = Alerts(this, driver)
    fun alert(body: (Alerts<WebDriver>).() -> Unit) = alertActions.run(body)

    private val prerequisiteActions = PrerequisiteActions(this, driver)
    fun <T1> prerequisite(body: (PrerequisiteActions<WebDriver>).() -> T1) = prerequisiteActions.run(body)

    inline fun <reified T : WebElement> findElementByName(name: String, searchContext: Any = this): T {
        val f = searchContext::class.java.declaredFields.find {
            it.isAnnotationPresent(Name::class.java)
                    && it.getAnnotation(Name::class.java).value.toLowerCase() == name.toLowerCase()
        }
            ?: error("Page ${searchContext.getName()} doesn't contain element $name")
        f.isAccessible = true
        return f.get(searchContext) as T
    }

    open fun getTimeoutInSeconds(): Long {
        return 20L
    }
}