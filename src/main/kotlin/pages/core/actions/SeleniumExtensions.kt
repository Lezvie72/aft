package pages.core.actions

import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import ru.yandex.qatools.htmlelements.element.TypifiedElement

@Step("Find element by xpath {xpathStr}")
fun WebDriver.isEnabledSafety(element: WebElement, xpathStr: String): Boolean {
    return try {
        return element.findElement(By.xpath(xpathStr)).isEnabled
    } catch (e: StaleElementReferenceException) {
        false
    } catch (e: NoSuchElementException) {
        false
    } catch (e: TimeoutException) {
        false
    }
}

@Step("Find element by xpath {xpathStr}")
fun WebDriver.isEnabledSafety(driver: WebDriver, xpathStr: String, timeOutInSeconds: Long = 5): Boolean {
    val wait = WebDriverWait(driver, timeOutInSeconds)
    return try {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathStr))).isEnabled
    } catch (e: StaleElementReferenceException) {
        false
    } catch (e: NoSuchElementException) {
        false
    } catch (e: TimeoutException) {
        false
    }
}

@Step("Find element by xpath {xpathStr}")
fun WebDriver.isVisibilitySafety(driver: WebDriver, xpathStr: String, timeOutInSeconds: Long = 5): Boolean {
    val wait = WebDriverWait(driver, timeOutInSeconds)
    return try {
        return wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpathStr)))).isDisplayed
    } catch (e: StaleElementReferenceException) {
        false
    } catch (e: NoSuchElementException) {
        false
    } catch (e: TimeoutException) {
        false
    }
}

@Step("Find or wait element and return state")
fun WebDriver.isElementHide(driver: WebDriver, element: TypifiedElement, timeOutInSeconds: Long = 5): Boolean {
    val wait = WebDriverWait(driver, timeOutInSeconds)
    return try {
        return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed
    } catch (e: StaleElementReferenceException) {
        false
    } catch (e: NoSuchElementException) {
        false
    } catch (e: TimeoutException) {
        false
    }
}