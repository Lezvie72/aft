package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement

class AtmAdminRadio(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    override fun isEnabled(): Boolean {
        return this.findElement(By.cssSelector("input")).isEnabled
    }

    override fun isSelected(): Boolean {
        return this.findElement(By.xpath(".//mat-radio-button[@class='mat-radio-button mat-accent mat-radio-checked']")).isDisplayed
    }


}


