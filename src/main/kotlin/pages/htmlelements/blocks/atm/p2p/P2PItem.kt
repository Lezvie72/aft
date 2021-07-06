package pages.htmlelements.blocks.atm.p2p


import models.user.interfaces.HasOtfWallet
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button


@Name("P2P Item")
@FindBy(css = "atm-p2p-item")
class P2PItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//span[contains(text(), 'TO RECEIVE')]/ancestor::atm-property-value//atm-amount")
    @Name("To receive amount")
    lateinit var toReceive: AtmAmount

    @Name("Show button")
    @FindBy(xpath = ".//atm-span[contains(text(), 'Show')]")
    lateinit var showCounterparty: Button

    @Name("Counterparty")
    @FindBy(xpath = ".//span[contains(text(), 'Counterparty')]/ancestor::atm-property-value//atm-counterparty")
    lateinit var counterpartyValue: Button

    @FindBy(xpath = ".//span[contains(text(), ' AMOUNT RECEIVED ')]/ancestor::atm-property-value//atm-amount")
    @Name("Amount received")
    lateinit var amountReceived: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'AMOUNT SENT')]/ancestor::atm-property-value//atm-amount")
    @Name("Amount received")
    lateinit var amountSend: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Cancel')]/ancestor::button")
    @Name("Cancel button")
    private lateinit var cancelButton: Button

    val amountToReceive
        get() = toReceive.amount

    val receivedAmount
        get() = amountReceived.amount

    val amountToSend
        get() = toSend.amount

    val amountToSendHistory
        get() = amountSend.amount

    val isCancellable
        get() = check {
            isElementPresented(cancelButton)
        }

    val currencyToReceive
        get() = toReceive.currency

    val currencyToSend
        get() = toSend.currency

    fun clickCancelButton() {
        e {
            click(cancelButton)
        }
    }

    fun cancelOffer(user: HasOtfWallet) {
        clickCancelButton()
        page.signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    fun cancelOffer() {
        clickCancelButton()
    }

    fun open() {
        e {
            click(toReceive)
        }
    }
}