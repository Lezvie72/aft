package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement

class AtmInput(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val inputValue = By.ByXPath(".//input")
    private val error = By.ByXPath(".//mat-error[@style='display: block;']")

    private val inputElement: WebElement
        get() = this.findElement(inputValue)

    val value: String
        get() = inputElement.getAttribute("value")

    val errorText: String
        get() = this.findElement(error).text

    override fun clear() {
        inputElement.clear()
    }

    fun delete() {
        for (i in 0..value.length)
            inputElement.sendKeys(Keys.BACK_SPACE)
    }

    override fun sendKeys(vararg keysToSend: CharSequence?) {
        this.findElement(inputValue).sendKeys(*keysToSend)
    }

    override fun getText(): String {
        return value
    }

}