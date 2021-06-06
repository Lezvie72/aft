package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.user.interfaces.HasOtfWallet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmRFQPage.OperationType.BUY
import pages.atm.AtmRFQPage.OperationType.SELL
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.rfq.*
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.math.BigDecimal

@PageUrl("/trading/rfq")
class AtmRFQPage(driver: WebDriver) : AtmPage(driver) {

    enum class OperationType {
        BUY, SELL;
    }

    @Name("Create request")
    @FindBy(xpath = "//a[@href='/trading/rfq/requests/create']")
    lateinit var createRequest: Button

    @Name("View request")
    @FindBy(xpath = "//a[@href='/trading/rfq/incoming']")
    lateinit var viewRequest: Button

    @Name("My request")
    @FindBy(xpath = "//a[@href='/trading/rfq/requests']")
    lateinit var myRequest: Button

    @Name("I want to buy asset")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='direction']//span[contains(text(), 'I want to buy asset')]")
    lateinit var iWantToBuyAsset: AtmRadio

    @Name("I want to sell asset")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='direction']//span[contains(text(), 'I want to sell asset')]")
    lateinit var iWantToSellAsset: AtmRadio

    @Name("Amount to send")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='baseAmount']//input")
    lateinit var amountToSend: Button

    @Name("Total offer amount")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='quoteAmount']//input")
    lateinit var totalOfferAmount: TextInput

    @Name("Total offer amount")
    @FindBy(xpath = "//nz-switch[@formcontrolname='hasOffers']")
    lateinit var switch: Button

    @Name("Asset to send")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='baseToken']//nz-select")
    lateinit var assetToSend: AtmSelectLazy

    @Name("Asset to receive")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='quoteToken']//nz-select")
    lateinit var assetToReceive: AtmSelectLazy

    @Name("Create request from form")
    @FindBy(xpath = "//button//span[contains(text(), 'CREATE REQUEST')]")
    lateinit var createRequestFromForm: Button

    @Name("Create an offer")
    @FindBy(xpath = "//button//span[contains(text(), 'CREATE AN OFFER')]")
    lateinit var createAnOffer: Button

    @Name("Cancel request button")
    @FindBy(xpath = ".//span[contains(text(),'CANCEL REQUEST')]")
    lateinit var cancelRequestButton: Button

    @Name("Open chat")
    @FindBy(xpath = "//span[contains(text(),'OPEN CHAT')]")
    lateinit var openChatButton: Button

    @Name("Create deal")
    @FindBy(xpath = ".//atm-rfq-offer-form//button//span[contains(text(),'MAKE OFFER')]")
    lateinit var makeOffer: Button

    @Name("View offers")
    @FindBy(xpath = "//span[contains(text(),'VIEW OFFERS')]")
    lateinit var viewOffers: Button

    @Name("Trade history")
    @FindBy(xpath = "//a[contains(text(),'TRADE HISTORY')]")
    lateinit var tradeHistory: Button

    @Name("Open RFQ")
    @FindBy(xpath = "//atm-rfq-item-outgoing[1]")
    lateinit var openRFQ: Button

    @Name("Accept offer")
    @FindBy(xpath = "//span[contains(text(),'ACCEPT OFFER')]")
    lateinit var acceptOffer: Button

    @Name("Save changes")
    @FindBy(xpath = "//span[contains(text(),'SAVE CHANGES')]/ancestor::button")
    lateinit var  saveChanges: Button

    @Name("Change offer")
    @FindBy(xpath = "//span[contains(text(),'CHANGE OFFER')]/ancestor::button")
    lateinit var  changeOffer: Button

    @Name("Go to chat")
    @FindBy(xpath = "//a//span[contains(text(),'GO TO CHAT')]")
    lateinit var  goToChat: Button

    @Name("Reset filters")
    @FindBy(xpath = "//button[contains(text(),'RESET')]")
    lateinit var resetFilters: Button

    @Name("Today button")
    @FindBy(xpath = "//a[contains(text(), 'Today')]")
    lateinit var today: Button

    @Name("Show buy only")
    @FindBy(xpath = "//span[contains(text(),'Show buy only')]")
    lateinit var showBuyOnly: AtmRadio

    @Name("Base asset")
    @FindBy(xpath = "//span[contains(text(), 'Base asset')]//ancestor::nz-form-item//atm-custom-select")
    lateinit var baseAssetButton: AtmSelectLazy

    @Name("Quote asset")
    @FindBy(xpath = "//span[contains(text(), 'Quote asset')]//ancestor::nz-form-item//atm-custom-select")
    lateinit var quoteAssetButton: AtmSelectLazy

    @Name("Base maturity date")
    @FindBy(xpath = "//nz-form-item//span[.='Base maturity date:']//ancestor::nz-form-item//nz-select")
    lateinit var baseMaturityDate: AtmSelectLazy

    @Name("Outgoing RFQ offers")
    @FindBy(css = "atm-rfq-outgoing")
    lateinit var outgoingOffers: AtmTable<RFQOutgoingItem>

    @Name("Incoming RFQ offers")
    @FindBy(css = "atm-rfq-incoming")
    lateinit var incomingOffers: AtmTable<RFQIncomingItem>

    @Name("Incoming RFQ offers with deal")
    @FindBy(css = "atm-rfq-incoming")
    lateinit var incomingOffersWithDeal: AtmTable<RFQIncomingOfferItem>

    @Name("Outgoing RFQ deal offers")
    @FindBy(css = "atm-rfq-item-outgoing")
    lateinit var outgoingDealOffers: AtmTable<RFQOutgoingOfferItem>

//    @Name("Outgoing RFQ deal offers")
//    @FindBy(css = "atm-rfq-item-outgoing")
//    lateinit var outgoingDealOfferss: AtmTable<RFQOutgoingMultipleOfferItem>

    @Name("History RFQ offers")
    @FindBy(css = "atm-rfq-history")
    lateinit var historyOffers: AtmTable<RFQHistoryOfferItem>

    @Name("Trade history")
    @FindBy(css = "atm-p2p-history")
    lateinit var dealHistory: AtmTable<RFQOutgoingItem>

    @Name("RFQ offer fee")
    @FindBy(xpath = "//*[text() = ' Transaction fee ']/ancestor::atm-property-value//atm-amount")
    lateinit var offerFee: AtmAmount

    @Name("Good till cancelled")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Good till cancelled')]")
    lateinit var goodTillCancelled: AtmRadio

    @Name("Limited time offer")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Limited time offer')]")
    lateinit var limitedTimeOffer: AtmRadio

    @Name("Expires in")
    @FindBy(xpath = "//atm-expires-control[@formcontrolname='expires']//input")
    lateinit var expiresIn: TextInput

    @Name("Date from")
    @FindBy(xpath = "//nz-date-picker[@formcontrolname='dateFrom'] | //span[text()='Date from']/ancestor::nz-form-control//input")
    lateinit var dateFrom: TextInput

    @Name("Expiry deal time")
    @FindBy(xpath = "//atm-expires-control[@formcontrolname='expires']//nz-input-number//input")
    lateinit var expiryDealTime: AtmSelect

    @Name("Show requests with offers")
    @FindBy(xpath = "//nz-switch[@formcontrolname='hasOffers']//button")
    lateinit var requestsWithOffers: Button

    @Name("Chat input")
    @FindBy(xpath = "//atm-chat//textarea[@formcontrolname='message']")
    lateinit var chatInput: TextInput

    @Name("Chat send button")
    @FindBy(xpath = "//atm-chat//button")
    lateinit var chatSendButton: Button

    @Action("Fill field Expires in for the Limited Time Offer")
    fun limitedTimeOffer() {
        e {
            click(limitedTimeOffer)
            sendKeys(expiresIn, "1")
        }
    }

    @Step("User create RFQ")
    @Action("User create RFQ")
    fun createRFQ(
        type: OperationType,
        assetSend: CoinType,
        assetReceive: CoinType,
        amount: BigDecimal,
        time: String,
        user: HasOtfWallet
    ) {
        e {
            click(createRequest)
            when (type) {
                BUY -> click(iWantToBuyAsset)
                SELL -> click(iWantToSellAsset)
            }
            select(assetToSend, assetSend.tokenSymbol)
            select(assetToReceive, assetReceive.tokenSymbol)
            sendKeys(amountToSend, amount.toString())
            deleteData(expiresIn)
            sendKeys(expiresIn, time)
            click(createRequestFromForm)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    @Step("User cancel RFQ")
    @Action("User cancel RFQ")
    fun cancelRFQ(
        user: HasOtfWallet
    ) {
        e {
            click(cancelRequestButton)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    @Step("User create RFQ deal")
    @Action("User create RFQ deal")
    fun createDeal(
        amount: BigDecimal,
        dealAmount: BigDecimal,
        time: String,
        user: HasOtfWallet
    ): BigDecimal {
        e {
            click(viewRequest)
        }
        val myOffer = incomingOffers.find {
            it.baseAmount == amount
        } ?: error("Can't find offer with base amount '$amount'")
        myOffer.open()
        e {
            sendKeys(totalOfferAmount, dealAmount.toString())
        }
        val fee = wait(15L) {
            until("Couldn't load fee") {
                offerFee.text.isNotEmpty()
            }
            offerFee.amount
        }
        e {
            sendKeys(expiryDealTime, time)
            Thread.sleep(2000)
            click(makeOffer)
//            val makeOffer = findElement(By.xpath(".//button//span[contains(text(),'MAKE OFFER')]"))
//            click(makeOffer)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
        return fee
    }

    @Step("User cancel RFQ offer")
    @Action("User cancel RFQ offer")
    fun cancelRfqOffer(amount: BigDecimal, user: HasOtfWallet): AtmRFQPage {
        e {
            click(myRequest)
        }
        val myOffer = outgoingOffers.find {
            it.baseAmount == amount
        } ?: error("Can't find offer with base amount '$amount'")
        myOffer.cancelRfqOffer(user)
        driver.navigate().refresh()
        val cancelledOffer = outgoingOffers.find {
            it.baseAmount == amount
        }
        assertThat(
            "Offer with amount $amount should have been be cancelled",
            cancelledOffer,
            Matchers.nullValue()
        )
        return AtmRFQPage(driver)
    }

    @Step("User outgoing RFQ offer")
    @Action("User outgoing RFQ offer")
    fun findOutgoingRFQ(amount: BigDecimal): AtmRFQPage {
//        e {
//            click(myRequest)
//        }
//        setDisplayRequestsWithOffers(true)
        val myOffer = outgoingOffers.find {
            it.baseAmount == amount
        } ?: error("Can't find offer with base amount '$amount'")
        myOffer.viewOffer()
        return AtmRFQPage(driver)
    }

    @Step("User incoming RFQ offer")
    @Action("User incoming RFQ offer")
    fun findIncomingRFQ(amount: BigDecimal): AtmRFQPage {
        e {
            click(viewRequest)
        }
        val myOffer = incomingOffers.find {
            it.baseAmount == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.open()
        return AtmRFQPage(driver)
    }

    @Step("User find RFQ deal offer")
    @Action("User find RFQ deal offer")
    fun findDealOffers(amount: BigDecimal, dealAmount: BigDecimal): AtmRFQPage {
        e {
            click(myRequest)
        }
        setDisplayRequestsWithOffers(true)
        val myOffer = outgoingOffers.find {
            it.baseAmount == amount
        } ?: error("Can't find offer with base amount '$amount'")
        myOffer.viewOffer()
        val myOffer1 = outgoingDealOffers.find {
            it.offerAmount == dealAmount
        } ?: error("Can't find offer with base amount'$dealAmount'")
        assertThat(
            "Offer with amount $dealAmount should be presented",
            myOffer1,
            Matchers.notNullValue()
        )
        return AtmRFQPage(driver)
    }

    @Step("User accept RFQ offer")
    @Action("User accept RFQ offer")
    fun acceptOffer(amount: BigDecimal, dealAmount: BigDecimal, user: HasOtfWallet): BigDecimal {
        e {
            click(myRequest)
        }
//        setDisplayRequestsWithOffers(true)
        findOutgoingRFQ(amount)
        val myOfferDeal = outgoingDealOffers.find {
            it.offerAmount == dealAmount
        } ?: error("Can't find offer with offer amount'$dealAmount'")
        myOfferDeal.openChat()
        val fee = wait(15L) {
            until("Couldn't load fee") {
                offerFee.text.isNotEmpty()
            }
            offerFee.amount
        }
        e {
            click(acceptOffer)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
        return fee
    }

    @Step("User find RFQ offer in history")
    @Action("User find RFQ offer in history")
    fun findOfferInHistory(dealAmount: BigDecimal): AtmRFQPage {
        e {
            click(viewRequest)
            click(tradeHistory)
        }
        val myOffer = historyOffers.find {
            it.paidAmount == dealAmount
        } ?: error("Can't find offer with paid amount '$dealAmount'")
        myOffer.open()
        return AtmRFQPage(driver)
    }

    @Step("Wait requests with offers")
    @Action("Wait requests with offers")
    fun setDisplayRequestsWithOffers(state: Boolean) {
        fun currentState() = requestsWithOffers.getAttribute("class").contains("ant-switch-checked")
        if (currentState() != state) {
            e {
                until("Couldn't set Show zero balance to $state") {
                    click(requestsWithOffers)
                }
                currentState() == state
            }
        }
    }

    @Step("Set filter today for buy offer type")
    fun setFilterBuyToday(
        baseAsset: CoinType,
        quoteAsset: CoinType, maturityDate: String = ""
    ) {
        e {
            click(resetFilters)
            click(showBuyOnly)
            select(baseAssetButton, baseAsset.tokenSymbol)
            select(quoteAssetButton, quoteAsset.tokenSymbol)
            if ((baseAsset.tokenSymbol.startsWith("IT") or quoteAsset.tokenSymbol.startsWith("IT"))
                and maturityDate.isNotBlank()
            ) select(baseMaturityDate, maturityDate)
            click(dateFrom)
            click(today)
        }
    }

    @Step("Overview. Find offer with amount {amount}")
    fun isOfferExist(amount: BigDecimal, table: AtmTable<RFQOutgoingItem>): Boolean {
        table.find { it.baseAmount == amount } ?: return false
        return true
    }

    @Step("check the message from chat")
    fun checkTheMessageFromChat(message: String) {
        val textMessage = wait {
            untilPresented<WebElement>(By.xpath(".//atm-chat-message//atm-span[contains(text(),'${message}')]"))
        }.to<Button>("Card '$message'")
        assert { elementPresented(textMessage) }
        assertThat("Order", textMessage.text, Matchers.`is`(message))
    }
}