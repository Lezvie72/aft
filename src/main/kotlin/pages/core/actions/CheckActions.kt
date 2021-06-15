package pages.core.actions

import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import pages.BasePage
import utils.helpers.containsIgnoreCaseXpath
import utils.helpers.equalsIgnoreCaseXpath

class CheckActions<T : WebDriver>(page: BasePage, driver: T) : BaseActions<BasePage, T>(page, driver) {

    @Step("Check: Is element containing text presented")
    fun isElementContainingTextPresented(
        partialText: String,
        timeoutInSeconds: Long = page.getTimeoutInSeconds()
    ): Boolean {
        val locator = containsIgnoreCaseXpath("*", "text()", partialText)
        return isElementPresented(locator, timeoutInSeconds)
    }

    @Step("Check: Is element with text presented")
    fun isElementWithTextPresented(text: String, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        val locator = By.xpath("//*[text()='$text']")
        return isElementPresented(locator, timeoutInSeconds)
    }

    @Step("Check: Is element with text presented")
    fun isElementWithTextPresentedIgnoreCase(
        text: String,
        timeoutInSeconds: Long = page.getTimeoutInSeconds()
    ): Boolean {
        val locator = equalsIgnoreCaseXpath("*", "text()", text)
        return isElementPresented(locator, timeoutInSeconds)
    }

    @Step("Check: element '{e.name}' contains text {text}")
    fun isElementContainsText(
        e: WebElement,
        text: String,
        timeoutInSeconds: Long = page.getTimeoutInSeconds(),
        ignoreCase: Boolean = false
    ): Boolean {
        return page.wait(timeoutInSeconds) {
            untilPresented(e)
        }.text.contains(text, ignoreCase)
    }

    @Step("Check: element '{e.name}' is enabled")
    fun isElementEnabled(e: WebElement, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        val el = page.wait(timeoutInSeconds) {
            untilPresented(e)
        }
        return el.isEnabled &&
                el.getAttribute("disabled") != "true" &&
                !el.getAttribute("class").contains("ant-pagination-disabled")
    }

    @Step("Check: is '{e.name}' presented")
    fun isElementPresented(e: WebElement, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return try {
            page.wait(timeoutInSeconds) {
                untilPresented(e)
            }
            true
        } catch (e: TimeoutException) {
            false
        } catch (e: StaleElementReferenceException) {
            false
        }
    }

    @Step("Check: is '{e.name}' no longer displayed")
    fun isElementGone(e: WebElement, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return try {
            page.wait(1L) {
                untilInvisibility(e)
            }
            true
        } catch (e: TimeoutException) {
            e.cause is NoSuchElementException
        }
    }

    @Step("Check: is element presented by locator")
    fun isElementPresented(l: By, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return try {
            page.wait(timeoutInSeconds) {
                untilPresented<WebElement>(l)
            }
            true
        } catch (e: TimeoutException) {
            false
        }
    }

    @Step("Check: is element presented by locator")
    fun isElementNotPresented(l: By, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return try {
            page.wait(timeoutInSeconds) {
                untilPresented<WebElement>(l)
            }
            false
        } catch (e: TimeoutException) {
            true
        }
    }

    @Step("Check: is element contains attribute")
    fun isElementContainsAttribute(
        attribute: String,
        attributeValue: String,
        timeoutInSeconds: Long = page.getTimeoutInSeconds()
    ): Boolean {
        val locator = containsIgnoreCaseXpath("*", attribute, attributeValue)
        return isElementPresented(locator, timeoutInSeconds)
    }

    fun WebElement.isPresented(timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return isElementPresented(this, timeoutInSeconds)
    }

    @Step("Check radio button '{e.name}' is selected")
    fun isSelectedRadioButton(e: WebElement): Boolean {
        val elem = e.getAttribute("class")
        return elem.contains("sdex-radio-checked")

    }

    @Step("If element '{e.name}' is presented")
    inline fun <reified T1 : WebElement, T2 : Any> ifElementPresented(
        e: T1,
        timeoutInSeconds: Long = page.getTimeoutInSeconds(),
        f: (T1) -> T2
    ): T2? {
        return if (e.isPresented(timeoutInSeconds)) {
            f.invoke(e)
        } else {
            null
        }
    }

    @Step("Check: url matches {regex}")
    fun urlMatches(regex: String, timeoutInSeconds: Long = page.getTimeoutInSeconds()): Boolean {
        return try {
            page.wait(timeoutInSeconds) {
                until("", ExpectedConditions.urlMatches(regex))
            }
        } catch (e: TimeoutException) {
            false
        }
    }

    inline fun <reified T1 : WebElement, T2 : Any> T1.ifPresented(
        timeoutInSeconds: Long = page.getTimeoutInSeconds(),
        f: (T1) -> T2
    ): T2? {
        return ifElementPresented(this, timeoutInSeconds, f)
    }

}