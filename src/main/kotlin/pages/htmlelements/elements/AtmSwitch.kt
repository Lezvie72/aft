package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement

class AtmSwitch(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val switchNameLocator = By.cssSelector("label")
    private val switchButtonLocator = By.cssSelector("nz-switch > button")

    val switchName: String
        get() = this.findElement(switchNameLocator).text

    val switchState: Boolean
        get() = getLocatorState()

    fun switch(): Boolean {
        this.findElement(switchButtonLocator).click()
        return switchState
    }

    private fun getLocatorState(): Boolean {
        return this.findElement(switchButtonLocator).getAttribute("class").contains("ant-switch-checked")
    }

}