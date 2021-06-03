package pages.htmlelements.elements

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.BasePage
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import utils.helpers.containsIgnoreCaseXpath

class AtmAdminSelect(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val menuSelector = By.xpath("//mat-option//span[@class='mat-option-text']")
    private val inputValue = By.ByXPath(".//input")

    fun selectBy(locator: By, page: BasePage, elementToDropFocus: WebElement? = null) {
        page.e {
            val menu = until("Couldn't open modal selection list", 21L) {
                elementToDropFocus?.let { click(it) }
                click(this@AtmAdminSelect)
                page.wait(3L) {
                    untilPresented<WebElement>(menuSelector)
                }
            }
            val option = page.wait {
                untilPresented<Button>(locator, menu)
            }
            click(option)
        }
    }

    @Step("User selects '{text}' from context menu '{this.name}'")
    fun selectByText(text: String, page: BasePage) {
        selectBy(By.xpath("//mat-option//span[text() = '${text}']|//mat-option//span[text() = ' $text ']"), page)
    }

    @Step("User selects '{text}' from context menu '{this.name}'")
    fun safeSelectByText(text: String, page: BasePage, elementToDropFocus: WebElement) {
        selectBy(
            By.xpath("//mat-option//span[text() = '${text}']|//mat-option//span[text() = ' $text ']"),
            page,
            elementToDropFocus
        )
    }

    @Step("User selects ~'{text}' from context menu '{this.name}'")
    fun selectByPartialText(text: String, page: BasePage) {
        selectBy(containsIgnoreCaseXpath("*", "text()", text), page)
    }

    @Step("User sends and select ~'{text}' from context menu '{this.name}'")
    fun sendAndSelect(value: String, fullValueName: String, page: BasePage) {
        this.sendKeys(value)
        selectByText(fullValueName, page)
    }

    @Step("User sends and select ~'{text}' from context menu '{this.name}'")
    fun sendKeys(keysToSend: String, page: BasePage) {
        page.e {
            this@AtmAdminSelect.findElement(inputValue).sendKeys(keysToSend)
            val option = page.wait {
                untilPresented<Button>(
                    By.xpath("//mat-option//span[text() = '${keysToSend}']|//mat-option//span[text() = ' $keysToSend ']"),
                    this@AtmAdminSelect, "Select option $keysToSend"
                )
            }
            click(option)
        }
    }
}