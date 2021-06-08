package pages.core.actions

import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.FluentWait
import pages.BasePage
import pages.core.annotations.Action
import pages.htmlelements.elements.AtmAdminSelect
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.AtmSelectLazy
import pages.htmlelements.elements.SdexSelect
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import utils.helpers.*
import utils.isChecked
import java.time.Duration

class ElementActions<T : WebDriver>(page: BasePage, driver: T) : BaseActions<BasePage, T>(page, driver) {

    @Action("click")
    fun click(name: String) {
        val e: WebElement = page.findElementByName(name)
        click(e)
    }

    @Step("Click on element '{e.name}'")
    fun click(e: WebElement) {
        page.wait {
            untilPresented(e)
        }.scrollIntoView()
        try {
            e.click()
        } catch (ex: ElementClickInterceptedException) {
            e.clickJS()
        }

    }

    @Step("Click on element '{e.name}' until it disappear")
    fun clickUntilGone(e: WebElement, timeoutInSeconds: Long = 15, pollingEveryInSeconds: Long = 5) {
        page.e {
            until(
                "Element '${e.getName()} didn't disappear in $timeoutInSeconds seconds",
                timeoutInSeconds,
                pollingEveryInSeconds
            ) {
                click(e)
                page.check {
                    isElementGone(e)
                }
            }
        }
    }

    @Step("Click on element '{e.name}' until it presented")
    fun clickUntilElementIsPresented(
        e: WebElement,
        textElement: String,
        timeoutInSeconds: Long,
        pollingEveryInSeconds: Long
    ) {
        page.e {
            until(
                "Element '${e.getName()} didn't disappear in $timeoutInSeconds seconds",
                timeoutInSeconds,
                pollingEveryInSeconds
            ) {
                click(e)
                page.check {
                    isElementWithTextPresented(textElement)
                }
            }
        }
    }

    @Step("Click on element '{e.name}' until it presented")
    fun clickElementUntilOtherElementIsPresented(
        e: WebElement,
        textElement: String,
        timeoutInSeconds: Long,
        pollingEveryInSeconds: Long
    ) {
        page.e {
            until(
                "Element '${e.getName()} didn't disappear in $timeoutInSeconds seconds",
                timeoutInSeconds,
                pollingEveryInSeconds
            ) {
                click(e)
                page.check {
                    isElementWithTextPresented(textElement)
                }
            }
        }
    }

    @Action("type value")
    fun sendKeys(name: String, value: String) {
        val e: WebElement = page.findElementByName(name)
        sendKeys(e, value)
    }

    @Step("Type value '{value}' in '{e.name}'")
    fun sendKeys(e: WebElement, value: String) {
        page.wait {
            untilPresented(e)
        }.apply {
            try {
                clear()
            } catch (ex: Exception) {
            }
        }.scrollIntoView().sendKeys(value)
    }

    @Step("Enters value '{value}' in '{e.name}'")
    fun sendKeysAndSubmit(e: WebElement, value: String) {
        sendKeys(e, value)
        e.to<SdexSelect>(e.getName()).selectByPartialText(value, page)
    }

    @Step("Enters value '{value}' in '{e.name}' and press RETURN")
    fun sendKeysAndReturn(e: WebElement, value: String) {
        sendKeys(e, "$value${Keys.RETURN}")
    }

    @Step("Enters value '{value}' in '{e.name}' and press ENTER")
    fun sendKeysAndEnter(e: WebElement, value: String) {
        sendKeys(e, "$value${Keys.ENTER}")
    }

    @Action("select value")
    fun select(name: String, value: String) {
        val e: WebElement = page.findElementByName(name)
        select(e, value)
    }

    @Step("Select '{value}' from '{e.name}'")
    fun select(e: WebElement, value: String) {
        when (e) {
            is AtmSelect -> e.selectByText(value, page)
            is AtmAdminSelect -> e.selectByText(value, page)
            is AtmSelectLazy -> e.selectByText(value, page)
            else -> {
                click(e)
                val option = page.wait {
                    untilPresented<WebElement>(containsIgnoreCaseXpath("*", "text()", value))
                }.to<Button>("Option").scrollIntoView()
                click(option)
            }
        }
    }

    @Action("select partial value")
    fun selectPartial(name: String, value: String) {
        val e: WebElement = page.findElementByName(name)
        selectPartial(e, value)
    }

    @Step("Select partial '{value}' from '{e.name}'")
    fun selectPartial(e: WebElement, value: String) {
        when (e) {
            is SdexSelect -> e.selectByPartialText(value, page)
            else -> {
                click(e)
                val option = page.wait {
                    untilPresented<WebElement>(containsIgnoreCaseXpath("*", "text()", value))
                }.scrollIntoView()
                click(option)
            }
        }
    }

    @Step("Set checkbox '{e.name}' to '{value}'")
    fun setCheckbox(e: CheckBox, value: Boolean) {
        if (e.isChecked() != value) {
            until("Couldn't set ${e.name} to $value") {
                click(e)
                e.isChecked() == value
            }
        }
    }

    @Step("Check '{text}' in '{e.name}'")
    fun checkText(e: WebElement, text: String) {
        e.getAttribute(text)
    }

    @Step("Paste data in field")
    fun pasteData(e: WebElement) {
        e.sendKeys(Keys.CONTROL, "v")
    }

    @Step("Delete data in field")
    fun deleteData(e: WebElement) {
        val str = e.text.length
        for (i in 0..str)
            e.sendKeys(Keys.BACK_SPACE)
    }

    @Step("Copy data from field")
    fun copyData(e: WebElement) {
        e.sendKeys(Keys.CONTROL, "a")
        e.sendKeys(Keys.CONTROL, "c")
    }

    @Step("Press Enter button")
    fun pressEnter(e: WebElement) {
        e.sendKeys(Keys.ENTER)
    }

    @Action("clear")
    fun clear(name: String) {
        val e: WebElement = page.findElementByName(name)
        clear(e)
    }

    @Step("Clears field '{e.name}'")
    fun clear(e: WebElement) {
        e.clear()
    }

    inline fun <reified R : Any?> until(
        message: String,
        timeout: Long = 10,
        pollingEvery: Long = 1,
        noinline func: ElementActions<T>.() -> R
    ): R {
        val wait = FluentWait<ElementActions<T>>(this)
            .withMessage(message)
            .ignoring(NoSuchElementException::class.java, TimeoutException::class.java)
            .pollingEvery(Duration.ofSeconds(pollingEvery))
            .withTimeout(Duration.ofSeconds(timeout))
        return wait.until(func)
    }

    fun WebElement.clickJS() {
        getJSExecutor(driver).executeScript("arguments[0].click();", this)
    }

    inline fun <reified T : WebElement> T.scrollIntoView(): T = scrollIntoView(driver)
}