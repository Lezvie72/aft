package pages.core.actions

import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.WebDriverWait
import pages.BasePage
import pages.core.annotations.Action
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader
import java.time.Duration
import java.util.function.Function

@Suppress("UNCHECKED_CAST")
class WaitActions<T : WebDriver>(page: BasePage, driver: T, val timeOutInSeconds: Long = page.getTimeoutInSeconds()) :
    BaseActions<BasePage, T>(page, driver) {

    val wait = WebDriverWait(driver, timeOutInSeconds)


    @Action("wait to be presented")
    fun untilPresented(name: String) {
        val e: WebElement = page.findElementByName(name)
        untilPresented(e)
    }

    @Action("Wait any object with name")
    inline fun <reified T : TypifiedElement> untilPresentedAnyWithText(objectText: String, objectName: String): T {
        val by = By.xpath("//*[contains(text(), '$objectText')]")
        return untilPresented(by, objectName)
    }

    @Step("Wait '{e.name}' to be presented")
    fun <T : WebElement> untilPresented(e: T): T {
        return wait.until(ExpectedConditions.visibilityOf(e) as Function<in WebDriver, WebElement>) as T
    }

    @Step("Wait '{e.name}' to dissappear")
    fun untilInvisibility(e: WebElement) {
        wait.until(ExpectedConditions.invisibilityOf(e))
    }

    @Step("Wait locator '{l.toString}' to be presented")
    inline fun <reified T : WebElement> untilPresented(l: By, name: String = ""): T {
        val e = wait.until(ExpectedConditions.presenceOfElementLocated(l) as Function<in WebDriver, WebElement>)

        return if (TypifiedElement::class.java.isAssignableFrom(T::class.java)) {
            HtmlElementLoader.createTypifiedElement(
                T::class.java.asSubclass(
                    TypifiedElement::class.java
                ), e, name
            ) as T
        } else e as T

    }

    @Step("Wait locator '{l.toString}' to be presented")
    inline fun <reified T : WebElement> untilPresented(l: By, context: SearchContext, name: String = ""): T {
        val e = until("Element with locator $l wasn't found in context $context") {
            context.findElement(l)
        }

        return if (TypifiedElement::class.java.isAssignableFrom(T::class.java)) {
            HtmlElementLoader.createTypifiedElement(
                T::class.java.asSubclass(
                    TypifiedElement::class.java
                ), e, name
            ) as T
        } else e as T

    }

    @Step("Wait for frame be presented")
    fun untilFrameIsAvailableAndSwitchToIt(id: Int) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(id))
    }

    inline fun <reified R : Any?> until(
        message: String,
        timeout: Long = timeOutInSeconds,
        noinline func: T.() -> R
    ): R {
        val wait = FluentWait<T>(this.driver)
            .withMessage(message)
            .ignoring(NoSuchElementException::class.java, TimeoutException::class.java)
            .ignoring(StaleElementReferenceException::class.java)
            .pollingEvery(Duration.ofSeconds(1))
            .withTimeout(Duration.ofSeconds(timeout))
        return wait.until(func)
    }

    inline fun <reified R : Any?> until(
        message: String,
        conditions: ExpectedCondition<R>,
        timeout: Long = timeOutInSeconds
    ): R {
        val wait = WebDriverWait(this.driver, timeout)
            .withMessage(message)
            .pollingEvery(Duration.ofSeconds(1))
            .withTimeout(Duration.ofSeconds(timeout))
        return wait.until(conditions)
    }

}