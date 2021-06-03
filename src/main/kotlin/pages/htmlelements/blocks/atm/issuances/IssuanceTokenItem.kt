package pages.htmlelements.blocks.atm.issuances

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

@Name("Issuances Item")
@FindBy(css = "atm-issuer-token")
class IssuanceTokenItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//div[contains(text(), 'Total requested')]/ancestor::atm-amount-field//atm-amount")
    @Name("Total requested")
    lateinit var totalRequested: AtmAmount

    @FindBy(xpath = ".//atm-issuer-token//div[contains(@class, 'issuer-token__symbol')]")
    @Name("Token")
    lateinit var token: TextInput

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