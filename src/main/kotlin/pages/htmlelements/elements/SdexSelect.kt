package pages.htmlelements.elements

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.BasePage
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import utils.helpers.containsIgnoreCaseXpath
import utils.helpers.to

class SdexSelect(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    val inputSelector = By.cssSelector("input")
    val menuSelector = By.cssSelector("ng-dropdown-panel, .sdex-menu-content")
    val clearButtonSelector = By.cssSelector("span[title='Clear all']")

    override fun clear() {
        if (this.findElement(inputSelector).getAttribute("value").isNotEmpty() || this.findElements(clearButtonSelector).size > 0) {
            this.findElement(clearButtonSelector).click()
        }
    }

    override fun sendKeys(vararg keysToSend: CharSequence?) {
        val input = this.findElement(inputSelector)
        input.sendKeys(*keysToSend)
    }

    fun selectBy(locator: By, page: BasePage) {
        page.e {
            val menu = until("Couldn't open modal selection list") {
                click(this@SdexSelect)
                page.wait {
                    untilPresented<WebElement>(menuSelector)
                }
            }
            val option = menu.findElement(locator).to<Button>("Option by locator")
            click(option)
        }
    }


    @Step("User selects '{text}' from context menu '{this.name}'")
    fun selectByText(text: String, page: BasePage) {
        selectBy(By.xpath(".//*[text() = '${text}']"), page)
    }

    @Step("User selects ~'{text}' from context menu '{this.name}'")
    fun selectByPartialText(text: String, page: BasePage) {
        selectBy(containsIgnoreCaseXpath("*", "text()", text.toLowerCase()), page)
    }
}