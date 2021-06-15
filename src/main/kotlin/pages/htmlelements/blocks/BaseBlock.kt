package pages.htmlelements.blocks

import io.qameta.allure.model.Status
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import pages.BasePage
import pages.core.actions.AssertActions
import pages.core.actions.CheckActions
import pages.core.actions.ElementActions
import pages.core.actions.WaitActions
import ru.yandex.qatools.htmlelements.element.HtmlElement
import utils.helpers.attachScreenshot
import utils.helpers.updateStep

open class BaseBlock<T : BasePage> : HtmlElement() {

    lateinit var page: T

    @Suppress("UNCHECKED_CAST")
    fun init(page: T) {
        this.page = page
    }

    fun <T1> wait(body: (WaitActions<WebDriver>).() -> T1) = page.wait(body)
    fun <T1> wait(timeOut: Long, body: (WaitActions<WebDriver>).() -> T1) =
        WaitActions(page, page.driver, timeOut).run(body)

    fun <T1> e(body: (ElementActions<WebDriver>).() -> T1) = page.e(body)
    fun <T1> check(body: (CheckActions<WebDriver>).() -> T1) = page.check(body)
    fun assert(body: (AssertActions<WebDriver>).() -> Unit) = page.assert(body)

    fun <T1> nonCriticalWait(timeOut: Long = 5L, body: (WaitActions<WebDriver>).() -> T1) = try {
        WaitActions(page, page.driver, timeOut).run(body)
    } catch (e: TimeoutException) {
        attachScreenshot("Non critical waiting failed", page.driver)
        updateStep {
            status == Status.BROKEN
        }
    }

}