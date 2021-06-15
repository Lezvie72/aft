package pages.htmlelements.blocks.atm.issuances

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button

@Name("Issuances Item")
//@FindBy(css = "atm-redeem-ind-request")
@FindBy(css = "atm-redeem-request-item")
class IssuanceRedemptionItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//div[contains(text(), 'Requested amount')]/ancestor::atm-amount-field//atm-amount")
    @Name("Requested amount")
    lateinit var requestedAmount: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Token quantity requested to redeem')]//ancestor::atm-redemption-deal-base//div//atm-amount")
    @Name("Token quantity requested to redeem")
    lateinit var tokenQuantityRequestedToRedeem: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Proceed')]/ancestor::button")
    @Name("Proceed button")
    private lateinit var proceed: Button

    @FindBy(xpath = ".//span[contains(text(), 'Details')]/ancestor::button")
    @Name("Details")
    private lateinit var details: Button

    val requestAmount
        get() = requestedAmount.amount

    val tokenQuantityRequestToRedeem
        get() = tokenQuantityRequestedToRedeem.amount

    val amountToSend
        get() = toSend.amount

    fun clickProceedButton() {
        e {
            click(proceed)
        }
    }

    fun clickProceedButtonForEtc() {
        e {
            click(details)
        }
    }

}