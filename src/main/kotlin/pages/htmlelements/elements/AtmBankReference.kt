package pages.htmlelements.elements

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import ru.yandex.qatools.htmlelements.element.TypifiedElement

class AtmBankReference(wrappedElement: WebElement) : TypifiedElement(wrappedElement) {

    private val recipientName = By.xpath("//div[contains(@class,'fiat-requisites__val')][2]")
    private val recipientAddress = By.xpath("//div[contains(@class,'fiat-requisites__val')][3]")
    private val bankName = By.xpath("//div[contains(@class,'fiat-requisites__val')][4]")
    private val bankAddress = By.xpath("//div[contains(@class,'fiat-requisites__val')][5]")
    private val bankIdCode = By.xpath("//div[contains(@class,'fiat-requisites__val')][6]")
    private val accountNumber = By.xpath("//div[contains(@class,'fiat-requisites__val')][7]")

    val recName: String
        get() = this.findElement(recipientName).text

    val recAddress: String
        get() = this.findElement(recipientAddress).text

    val nameOfBank: String
        get() = this.findElement(bankName).text

    val addressOfBank: String
        get() = this.findElement(bankAddress).text

    val bankCode: String
        get() = this.findElement(bankIdCode).text

    val accNum: String
        get() = this.findElement(accountNumber).text

}
