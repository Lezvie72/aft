package pages.htmlelements.blocks.atm.rfq


import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button


@Name("RFQ History Item")
@FindBy(css = "atm-rfq-item-history")
class RFQHistoryOfferItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//span[contains(text(), 'BASE ASSET/AMOUNT')]/ancestor::atm-property-value//atm-amount")
    @Name("To receive amount")
    private lateinit var baseLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'AMOUNT PAID')]//ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    private lateinit var amountPaid: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), ' AMOUNT RECEIVED ')]//ancestor::atm-property-value//atm-amount")
    @Name("To send amount")
    private lateinit var amountRecieve: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'TO SEND')]/following-sibling::atm-amount")
    @Name("To send amount")
    lateinit var toSend: AtmAmount

    @Name("Create an offer")
    @FindBy(xpath = "//button//span[contains(text(), ' CREATE AN OFFER ')]")
    lateinit var createAnOffer: Button

    @FindBy(xpath = ".//span[contains(text(), ' CANCEL REQUEST ')]/ancestor::button")
    @Name("Cancel button")
    private lateinit var cancelButtonRFQ: Button

    val baseAmount
        get() = baseLocator.amount

    val paidAmount
        get() = amountPaid.amount

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

    fun cancelRfqOffer(user: DefaultUser, wallet: SimpleWallet) {
        clickCancelButtonRfq()
        page.signAndSubmitMessage(user, wallet.secretKey)
    }

    fun open() {
        e {
            click(amountPaid)
        }
    }

}