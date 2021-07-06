package pages.htmlelements.blocks.atm.issuances

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Issuances Item")
@FindBy(css = "atm-dist-request-item")
class IssuanceRequestItem : BaseBlock<AtmPage>() {

    // Ошибка в xpath. Если вдруг что-то не работает, то я чинила >.>
    @FindBy(xpath = ".//*[contains(text(), 'Requested') or contains(text(), 'Total requested') or contains(text(), 'Requested amount')]/ancestor::div[contains(@class,'property')]//atm-amount")
    @Name("Total requested")
    lateinit var totalRequested: AtmAmount

    // TODO: css Статуса не является уникальным и скорее всего его переделают >.>
//    @FindBy(xpath = ".//atm-request-status")
//    @Name("Status")
//    lateinit var status: TextBlock

    @FindBy(xpath = ".//div[contains(text(), 'Request ID')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__value')]")
    @Name("Request ID")
    lateinit var requestedId: TextBlock

    @FindBy(xpath = ".//div[contains(text(), 'Requestor')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__value')]")
    lateinit var requestor: TextBlock

    @FindBy(xpath = ".//div[contains(text(), 'Submitted')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__value')]")
    lateinit var submittedDate: TextBlock

    @FindBy(xpath = ".//div[contains(text(), 'Valid thru')]/ancestor::div[contains(@class, 'property')]//div[contains(@class, 'property__value')]")
    lateinit var validThroughDate: TextBlock

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Proceed')]/ancestor::button")
    @Name("Proceed button")
    private lateinit var proceed: Button

    val totalRequestedAmount
        get() = totalRequested.amount

    val amountToSend
        get() = toSend.amount

    fun clickProceedButton() {
        e {
            click(proceed)
        }
    }

}