package pages.htmlelements.blocks.atm.orders

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.helpers.to

@Name("Orders Item")
@FindBy(css = "atm-order-request.request")
class OrdersItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//atm-request-status")
    @Name("Status")
    lateinit var status: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'REQUESTED AMOUNT')]/ancestor::div[contains(@class, 'property')]//atm-amount")
    @Name("Requested amount")
    lateinit var totalRequested: AtmAmount

    @FindBy(xpath = ".//atm-property-value[contains(@class, 'request__item')]//atm-amount")
    @Name("Amount")
    lateinit var amountInOrder: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TYPE')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Type")
    lateinit var type: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'SUBMITTED')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Submitted date")
    lateinit var submittedDate: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'VALID THRU')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Valid Thru")
    lateinit var validThroughDate: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'ISSUER')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Issuer")
    lateinit var issuer: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'REQUEST ID')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Request ID")
    lateinit var requestedId: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'REQUESTOR')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Requester")
    lateinit var requestor: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'SIGNATURE')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__wrapper-val')]")
    @Name("Signature")
    lateinit var signature: TextBlock

    @FindBy(xpath = ".//div[contains(@class, 'orders-list__request-actions')]//span[contains(text(), ' Details ')]")
    @Name("Details button")
    private lateinit var details: Button

    @Step("check the message")
    fun checkStatus(status: String) {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-order-request-status//nz-tag[contains(@class,'request__status')][contains(text(),'${status.toUpperCase()}')]"))
        }.to<Button>("Card '$status'")
        assert { elementPresented(cardWithStatus) }
        MatcherAssert.assertThat("Order", cardWithStatus.text, Matchers.`is`(status.toUpperCase()))
    }

    //    @FindBy(xpath = ".//span[contains(text(), 'TOTAL REQUESTED')]/ancestor::div[3]//atm-amount")
//    @Name("Total requested")
//    lateinit var totalRequested: AtmAmount

    private val requestedStatus = By.xpath(".//atm-request-status//nz-tag[contains(@class, 'request__status')]")

    val totalRequestedAmount
        get() = totalRequested.amount

    val amountOrder
        get() = amountInOrder.amount

    val requestAmount
        get() = totalRequested.amount

    val statusText: String
        get() = status.text.toLowerCase()

    fun clickDetailsButton() {
        e {
            click(details)
        }
    }

}