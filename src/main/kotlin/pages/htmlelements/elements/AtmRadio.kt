package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement

class AtmRadio(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    override fun isEnabled(): Boolean {
        return this.findElement(By.cssSelector("input")).isEnabled
    }

    override fun isSelected(): Boolean {
        return this.findElement(By.xpath("//span[@class='ant-radio ant-radio-checked']")).isDisplayed
    }



}


