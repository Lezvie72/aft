package pages.htmlelements.elements

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.BasePage
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TypifiedElement
import utils.helpers.containsIgnoreCaseXpath
import utils.helpers.to

class AtmSelect(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val menuSelector =
        By.xpath("//nz-option-container | //*[@class='ant-select-item-option-content'] | //*[contains(@class, 'mat-select-panel')]")

    private val selectedItemLocator = By.cssSelector("nz-select-item")

    fun selectBy(locator: By, page: BasePage) {

        page.nonCriticalWait {
            untilPresented<WebElement>(selectedItemLocator, this@AtmSelect)
        }

        page.e {
            val menu = until("Couldn't open modal selection list") {
                click(this@AtmSelect)
                page.wait {
                    untilPresented<WebElement>(menuSelector).to<TextBlock>("Menu")
                }
            }

            val option = page.wait {
                untilPresented<Button>(locator, menu)
            }

            click(option)
            page.nonCriticalWait {
                untilInvisibility(menu)
            }
            page.check {
                ifElementPresented(menu) {
                    click(this@AtmSelect)
                }
            }
        }
    }

    @Step("User selects '{text}' from context menu '{this.name}'")
    fun selectByText(text: String, page: BasePage) {
        selectBy(By.xpath(".//*[contains(text(), '$text')]"), page)
    }

    @Step("User selects ~'{text}' from context menu '{this.name}'")
    fun selectByPartialText(text: String, page: BasePage) {
        selectBy(containsIgnoreCaseXpath("*", "text()", text), page)
    }

}