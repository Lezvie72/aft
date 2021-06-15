package pages.htmlelements.blocks.atm.rfq


import models.user.interfaces.HasOtfWallet
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button


@Name("RFQ Outgoing Item")
@FindBy(css = "atm-rfq-item-outgoing")
class RFQOutgoingItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//span[contains(text(), 'BASE ASSET/AMOUNT')]/ancestor::atm-property-value//atm-amount")
    @Name("To receive amount")
    private lateinit var baseLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'QUOTE ASSET/AMOUNT')]/ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    private lateinit var quoteLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/following-sibling::atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @Name("Create an offer")
    @FindBy(xpath = "//button//span[contains(text(), 'CREATE AN OFFER')]")
    lateinit var createAnOffer: Button

    @Name("View offers")
    @FindBy(xpath = ".//span[contains(text(),'VIEW OFFERS')]")
    lateinit var viewOffers: Button

    @FindBy(xpath = ".//span[contains(text(), 'CANCEL REQUEST')]/ancestor::button")
    @Name("Cancel button")
    private lateinit var cancelButtonRFQ: Button

    @FindBy(xpath = ".//div[contains(text(), 'OFFERS')]//ancestor::atm-rfq-item-outgoing-offer//atm-amount")
    @Name("To send amount")
    private lateinit var offer: AtmAmount

    val offerAmount
        get() = offer.amount

    val baseAmount
        get() = baseLocator.amount

    val quoteAmount
        get() = quoteLocator.amount

    val amountToSend
        get() = toSend.amount

    val isCancellable
        get() = check {
            isElementPresented(cancelButtonRFQ)
        }

    fun clickCancelButtonRfq() {
        e {
            click(cancelButtonRFQ)
        }
    }

    fun viewOffer() {
        e {
            click(viewOffers)
        }
    }

    fun cancelRfqOffer(user: HasOtfWallet) {
        clickCancelButtonRfq()
        page.signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    fun open() {
        e {
            click(createAnOffer)
        }
    }
}