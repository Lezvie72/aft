package pages.htmlelements.blocks.atm.streaming

import models.user.interfaces.HasOtfWallet
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

@Name("Streaming item")
@FindBy(css = "atm-streaming-item")
class StreamingOfferItem : BaseBlock<AtmPage>() {
    enum class Direction {
        BUY, SELL
    }

    @FindBy(css = "atm-trade-direction")
    @Name("Direction")
    private lateinit var directionLocator: TextInput

    @FindBy(css = "nz-tag[nzcolor]")
    @Name("Status")
    private lateinit var statusLocator: TextInput

    @FindBy(xpath = ".//span[contains(text(), 'BASE ASSET/AMOUNT')]/ancestor::atm-property-value//atm-amount")
    @Name("To receive amount")
    private lateinit var baseLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Counterparty')]/ancestor::atm-property-value//atm-counterparty//atm-span")
    @Name("Show company")
    lateinit var Counterparty: Button

    @FindBy(xpath = ".//atm-span[contains(text(), ' Show ')]")
    @Name("Show active label")
    lateinit var show: Button

    @FindBy(
        xpath = ".//span[contains(text(), 'QUO" +
                "TE ASSET/AMOUNT')]/ancestor::atm-property-value//atm-amount"
    )
    @Name("To send amount")
    private lateinit var quoteLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'UNIT PRICE')]/ancestor::atm-property-value//atm-amount")
    @Name("Unit price")
    private lateinit var unitPriceLocator: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Counterparty')]/ancestor::atm-property-value//atm-span")
    @Name("Counterparty info")
    private lateinit var counterpartyValue: WebElement

    @FindBy(xpath = ".//span[contains(text(), 'CANCEL OFFER')]/ancestor::button")
    @Name("Cancel button")
    private lateinit var cancelButton: Button

    val baseAmount
        get() = baseLocator.amount

    val quoteAmount
        get() = quoteLocator.amount

    val isCancellable
        get() = check {
            isElementPresented(cancelButton)
        }
    val quoteCurrency
        get() = quoteLocator.currency

    val counterparty
        get() = counterpartyValue

    val baseCurrency
        get() = baseLocator.currency

    val unitPriceAmount
        get() = unitPriceLocator.amount

    val unitPriceCurrency
        get() = unitPriceLocator.currency

    fun clickCancelButton() {
        if (isCancellable) {
            e {
                click(cancelButton)
            }
        }
    }

    fun cancelOffer(user: HasOtfWallet) {
        clickCancelButton()
        page.signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    fun open() {
        e {
            click(unitPriceLocator)
        }
    }

    fun showCompany() {
        e {
            click(Counterparty)
        }
    }
}