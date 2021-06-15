package pages.htmlelements.blocks.atm.rfq

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button

@Name("RFQ Outgoing Offer Item")
@FindBy(css = "atm-rfq-item-outgoing-offers")
class RFQOutgoingOfferItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//div[contains(text(), 'OFFERS')]//ancestor::atm-rfq-item-outgoing-offers//atm-amount")
    @Name("To send amount")
    private lateinit var offer: AtmAmount

    @Name("Open chat")
    @FindBy(xpath = ".//span[contains(text(),'OPEN CHAT')]")
    lateinit var openChatButton: Button

    val offerAmount
        get() = offer.amount

    fun openChat() {
        e {
            click(openChatButton)
        }
    }
}