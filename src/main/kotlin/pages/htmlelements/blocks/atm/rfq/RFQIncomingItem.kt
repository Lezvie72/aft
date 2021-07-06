package pages.htmlelements.blocks.atm.rfq


import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock


@Name("RFQ Incoming Item")
@FindBy(css = "atm-rfq-item-incoming")
class RFQIncomingItem : BaseBlock<AtmPage>() {

    @Name("Label sell")
    @FindBy(xpath = ".//nz-tag[contains(text(), 'SELL')]")
    lateinit var labelSell: TextBlock

    @Name("Label buy")
    @FindBy(xpath = ".//nz-tag[contains(text(), 'BUY')]")
    lateinit var labelBuy: TextBlock

    @Name("Expiration")
    @FindBy(xpath = ".//span[contains(text(), 'Expiration')]/ancestor::atm-property-value//atm-span")
    lateinit var expirationDate: TextBlock

    @Name("Maturity date")
    @FindBy(xpath = ".//span[contains(text(), 'Maturity date')]/ancestor::atm-property-value//span[contains(@class, 'date-property')]")
    lateinit var maturityDate: TextBlock

    @Name("Show button")
    @FindBy(xpath = ".//atm-span[contains(text(), 'Show')]")
    lateinit var showCounterparty: Button

    @Name("Counterparty")
    @FindBy(xpath = ".//span[contains(text(), 'Counterparty')]/ancestor::atm-property-value//atm-counterparty")
    lateinit var counterpartyValue: Button

    @FindBy(xpath = ".//span[contains(text(), 'BASE ASSET/AMOUNT') or contains(text(),'BASE ASSET')]/ancestor::atm-property-value//atm-amount")
    @Name("To receive amount")
    lateinit var baseLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'QUOTE ASSET/AMOUNT') or contains(text(),'QUOTE ASSET')]/ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    lateinit var quoteLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/following-sibling::atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @Name("Create an offer")
    @FindBy(xpath = ".//a[contains(text(), 'CREATE AN OFFER')]")
    lateinit var createAnOffer: Button

    @FindBy(xpath = ".//span[contains(text(), ' CANCEL REQUEST ')]/ancestor::button")
    @Name("Cancel button")
    private lateinit var cancelButtonRFQ: Button

    @Name("View offers")
    @FindBy(xpath = "//span[contains(text(),'VIEW OFFERS')]")
    lateinit var viewOffers: Button

    @Name("Go to chat")
    @FindBy(xpath = "//a//span[contains(text(),'GO TO CHAT')]")
    lateinit var  goToChat: Button

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

    fun viewOffer(){
        e{
            click(viewOffers)
        }
    }

    fun clickCancelButtonRfq() {
        e {
            click(cancelButtonRFQ)
        }
    }

    fun cancelRfqOffer(user: DefaultUser, wallet: SimpleWallet) {
        clickCancelButtonRfq()
        page.signAndSubmitMessage(user, wallet.secretKey)
    }

    fun open() {
        e {
            click(createAnOffer)
        }
    }
    fun goToChat() {
        e {
            click(goToChat)
        }
    }
}