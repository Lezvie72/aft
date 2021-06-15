package utils

import org.openqa.selenium.By
import pages.BasePage
import pages.htmlelements.elements.AtmRadio
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.Radio
import java.math.BigDecimal

fun By.isDisplayed(page: BasePage): Boolean {
    return page.check {
        isElementPresented(this@isDisplayed)
    }
}

fun CheckBox.isChecked(): Boolean {
    return if (this.findElement(By.cssSelector("input")).getAttribute("aria-checked") == "true") {
        true
    } else this.findElement(By.cssSelector("span")).getAttribute("class") == "ant-checkbox ant-checkbox-checked"

}

fun AtmRadio.isChecked(): Boolean {
    val cls = this.getAttribute("class")
    return cls.contains("ant-radio-button ant-radio-button-checked")
}

infix fun BigDecimal.equalsTo(other: BigDecimal) = this.compareTo(other)==0