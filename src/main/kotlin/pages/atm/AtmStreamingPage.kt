package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.user.interfaces.HasOtfWallet
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.streaming.StreamingOfferItem
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.attachScreenshot
import java.math.BigDecimal

@PageUrl("/trading/streaming")
class AtmStreamingPage(driver: WebDriver) : AtmPage(driver) {
    private val acceptOfferAlert = "//*[contains(text(), 'Accept offer')]"

    enum class ExpireType {
        GOOD_TILL_CANCELLED, TEMPORARY;
    }

    enum class OperationType {
        BUY, SELL;
    }

    //div[contains(text(),'Invalid key')] | //div[contains(text(),'Wrong code')]
    // labels
    @Name("Invalid or wrong code")
    @FindBy(xpath = "//div[contains(text(),'Invalid key')] | //div[contains(text(),'Wrong code')]")
    lateinit var invalidOrWrongCode: TextBlock

    @Name("New offer label")
    @FindBy(xpath = "//h2[contains(text(),'New offer')]")
    lateinit var newOfferLabel: TextBlock

    @Name("Fee option type")
    @FindBy(xpath = "//span[contains(text(),'Fee option')]/ancestor::atm-property-value//atm-span")
    lateinit var feeOptionType: TextBlock

    @Name("Buy label")
    @FindBy(xpath = "//nz-tag[contains(text(),' BUY ')]")
    lateinit var buyLabel: TextBlock

    @Name("Sell label")
    @FindBy(xpath = "//nz-tag[contains(text(),' SELL ')]")
    lateinit var sellLabel: TextBlock

    @Name("BASE ASSET/AMOUNT label")
    @FindBy(xpath = "//span[contains(text(),'BASE ASSET/AMOUNT')]")
    lateinit var baseAssetAmountLabel: TextBlock

    @Name("QUOTE ASSET/AMOUNT label")
    @FindBy(xpath = "//span[contains(text(),'QUOTE ASSET/AMOUNT')]")
    lateinit var quoteAssetAmountLabel: TextBlock

    @Name("Unit price label")
    @FindBy(xpath = "//span[contains(text(),' UNIT PRICE ')]")
    lateinit var unitPriceLabel: TextBlock

    @Name("Maturity date label")
    @FindBy(xpath = "//span[contains(text(),' Maturity date ')]")
    lateinit var maturityDateLabel: TextBlock

    @Name("COUNTERPARTY label")
    @FindBy(xpath = "//span[contains(text(),' Counterparty ')]")
    lateinit var counterpartyLabel: TextBlock

    @Name("EXPIRATION label")
    @FindBy(xpath = "//span[contains(text(),' Expiration ')]")
    lateinit var experationLabel: TextBlock

    //<editor-fold desc="ELEMENTS">
    @Name("Today button")
    @FindBy(xpath = "//a[contains(text(), 'Today')]")
    lateinit var today: Button

    @Name("Overview button")
    @FindBy(xpath = "//span[contains(text(),'Overview')]")
    lateinit var overview: Button

    @Name("Overview button")
    @FindBy(xpath = "//a[contains(text(),'OVERVIEW')]")
    lateinit var overviewBreadcrumbs: Button

    @Name("My offers")
    @FindBy(xpath = "//a[contains(text(),'MY OFFERS')]")
    lateinit var myOffers: Button

    @Name("Trade history button")
    @FindBy(xpath = "//a[contains(text(),'TRADE HISTORY')]")
    lateinit var tradeHistory: Button

    @Name("Cancel button")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]/ancestor::button")
    lateinit var cancelOffer: Button

    @Name("Cancel button from accept")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]/ancestor::button")
    lateinit var cancelAcceptOffer: Button

    @Name("Cancel button from place")
    @FindBy(xpath = "//a//span[contains(text(),'CANCEL')]")
    lateinit var cancelPlaceOffer: Button

    @Name("Cancel button in manual signature")
    @FindBy(xpath = "//atm-message-sign-form//span[contains(text(),'Cancel')]")
    lateinit var cancelInManualSignature: Button

    @Name("Cancel offer button")
    @FindBy(xpath = "//span[contains(text(),'CANCEL OFFER')]")
    lateinit var cancelOfferButton: Button

    @Name("Create offer")
    @FindBy(xpath = "//a[@href='/trading/streaming/outgoing/create']")
    lateinit var createOffer: Button

    @Name("I want to buy asset")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='direction']//span[contains(text(), ' I want to buy asset ')]")
    lateinit var iWantToBuyAsset: AtmRadio

    @Name("I want to sell asset")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='direction']//span[contains(text(), ' I want to sell asset ')]")
    lateinit var iWantToSellAsset: AtmRadio

    @Name("Base amount")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='baseAmount']")
    lateinit var amount: AtmRadio

    @Name("Base maturity date")
    @FindBy(xpath = "//nz-form-item//span[.='Base maturity date:']//ancestor::nz-form-item//nz-select")
    lateinit var baseMaturityDate: AtmSelectLazy

    @Name("Offer maturity date")
    @FindBy(xpath = "//nz-form-item//span[.='Maturity date']//ancestor::nz-form-item//nz-select")
    lateinit var offerMaturityDate: AtmSelectLazy

    @Name("Offer maturity date in open card")
    @FindBy(xpath = "//atm-transaction-amounts//span[contains(text(),'Maturity date')]//ancestor::atm-property-value//span[@class='date-property__date ng-star-inserted']")
    lateinit var offerMaturityDateInOpenCard: TextBlock

    @Name("Select asset pair")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='pair']")
    lateinit var selectAssetPair: AtmSelectLazy

    @Name("Select amount")
    @FindBy(xpath = "//nz-select[@formcontrolname='baseAmount']")
    lateinit var selectAmount: AtmSelect

    @Name("Select fee")
    @FindBy(xpath = "//label[contains(text(),'Select fee option')]//ancestor::nz-form-item//nz-select")
    lateinit var selectFee: AtmSelect

    @Name("Unit price")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='price']//input")
    lateinit var unitPrice: TextInput

    @Name("Select time")
    @FindBy(css = "atm-expires-control nz-select")
    lateinit var selectTime: AtmSelect

    @Name("Add time")
    @FindBy(css = "atm-expires-control nz-input-number input")
    lateinit var addTime: TextInput

    @Name("Place offer")
    @FindBy(xpath = "//span[contains(text(),' PLACE OFFER ')]")
    lateinit var placeOffer: Button

    @Name("My offer")
    @FindBy(xpath = "//a[contains(text(),' MY OFFERS ')]")
    lateinit var myOffer: Button

    @Name("Streaming card")
    @FindBy(xpath = "//atm-streaming-item[1]")
    lateinit var streamingCard: Button

    @Name("Good till cancelled")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Good till cancelled')]")
    lateinit var goodTillCancelled: AtmRadio

    @Name("Limited time offer")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Limited time offer')]")
    lateinit var limitedTimeOffer: AtmRadio

    @Name("My offers")
    @FindBy(css = "atm-streaming-my-offers")
    lateinit var myOffersList: AtmTable<StreamingOfferItem>

    @Name("Overview offers")
    @FindBy(css = "atm-streaming-overview")
    lateinit var overviewOffersList: AtmTable<StreamingOfferItem>

    @Name("Trade history")
    @FindBy(css = "atm-streaming-trades-history")
    lateinit var tradeHistoryList: AtmTable<StreamingOfferItem>

    @Name("Confirm trade")
    @FindBy(xpath = "//span[contains(text(),'ACCEPT OFFER')]//ancestor::button")
    lateinit var confirmTradeButton: Button

    @Name("Confirm button in dialog window")
    @FindBy(xpath = "//span[contains(text(),'Confirm')]//ancestor::button")
    lateinit var confirmButtonInDialogWindow: Button

    @Name("Streaming offer fee")
    @FindBy(xpath = "//*[text() = ' Transaction fee ']/ancestor::atm-property-value//atm-amount")
    lateinit var offerFee: AtmAmount

    @Name("Streaming offer amount to receive")
    @FindBy(xpath = "//*[text() = ' AMOUNT TO RECEIVE ']/ancestor::atm-property-value//atm-amount")
    lateinit var amountToReceive: AtmAmount

    @Name("Streaming offer amount to send")
    @FindBy(xpath = "//*[text() = ' AMOUNT TO SEND ']/ancestor::atm-property-value//atm-amount")
    //atm-amount//span[starts-with(@class,'token-amount')]
    lateinit var amountToSend: AtmAmount

    @Name("Streaming offer amount to send")
    @FindBy(xpath = "//*[text() = ' AMOUNT TO SEND ']/ancestor::atm-property-value//atm-amount[2]")
    //atm-amount//span[starts-with(@class,'token-amount')]
    lateinit var amountToSendSecond: AtmAmount

    //<editor-fold "FILTERS">
    @Name("Show all deal direction")
    @FindBy(xpath = "//span[contains(text(),'Show all deal directions')]")
    lateinit var showAllDialDirection: AtmRadio

    @Name("Show sell only")
    @FindBy(xpath = "//span[contains(text(),'Show sell only')]")
    lateinit var showSellOnly: AtmRadio

    @Name("Show buy only")
    @FindBy(xpath = "//span[contains(text(),'Show buy only')]")
    lateinit var showBuyOnly: AtmRadio

    @Name("Trading pair")
    @FindBy(xpath = "//span[contains(text(), 'Trading pair')]//ancestor::nz-form-item//atm-custom-select")
    lateinit var tradingPair: AtmSelectLazy

    @Name("Sort by")
    @FindBy(xpath = "//span[contains(text(), 'Sort by')]//ancestor::nz-form-item//atm-custom-select")
    lateinit var sortBy: AtmSelectLazy

    @Name("Date from")
    @FindBy(xpath = "//nz-date-picker[@formcontrolname='dateFrom'] | //span[text()='Date from']/ancestor::nz-form-control//input")
    lateinit var dateFrom: TextInput

    @Name("Date to")
    @FindBy(xpath = "//atm-custom-date-picker[@formcontrolname='dateTo']")
    lateinit var dateTo: TextInput

    @Name("Show expires and cancel switcher")
    @FindBy(xpath = "//nz-switch//button")
    lateinit var showExpiresAndCancel: Button
    //nz-switch//button

    @Name("Trading")
    @FindBy(xpath = "//header//nav/a[@href='/trading']")
    lateinit var tradingHeader: Button

    @Name("Accept offer")
    @FindBy(xpath = "//button//span[contains(text(), 'ACCEPT OFFER')]")
    lateinit var acceptOffer: Button

    @Name("Reset filters")
    @FindBy(xpath = "//button[contains(text(),'RESET')]")
    lateinit var resetFilters: Button

    @Name("Participant sells")
    @FindBy(xpath = "//atm-trade-direction-participant//nz-tag[contains(@class,'red')][contains(text(),'PARTICIPANT SELLS')]")
    lateinit var participantSells: Button

    @Name("Participant buys")
    @FindBy(xpath = "//atm-trade-direction-participant//nz-tag[contains(@class,'green')][contains(text(),'PARTICIPANT BUYS')]")
    lateinit var participantBuys: Button
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="ACTIONS">
    @Step("User create Streaming")
    @Action("User create Streaming")
    fun createStreaming(
        operationType: OperationType,
        assetPair: String,
        amount: String,
        amountUnitPrice: String,
        expiryType: ExpireType,
        user: HasOtfWallet,
        maturityDate: String = "",
        manualCompleted: Boolean = false
    ): BigDecimal {
        return e {
            click(createOffer)
            when (operationType) {
                OperationType.BUY -> click(iWantToBuyAsset)
                OperationType.SELL -> click(iWantToSellAsset)
            }
            select(selectAssetPair, assetPair)
            if (assetPair.startsWith("IT") and maturityDate.isNotBlank()) select(offerMaturityDate, maturityDate)
            selectAmount(amount)
            clear(unitPrice)
            sendKeys(unitPrice, amountUnitPrice)
            when (expiryType) {
                ExpireType.GOOD_TILL_CANCELLED -> click(goodTillCancelled)
                ExpireType.TEMPORARY -> limitedTimeOffer()
            }
            val fee = wait(15L) {
                until("Couldn't load fee") {
                    offerFee.text.isNotEmpty()
                }
                offerFee.amount
            }
            if (!manualCompleted) {
                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(user, user.otfWallet.secretKey)
                alert { checkErrorAlert() }
            }
            attachScreenshot("Created offer by user ${user.email}", driver)
            fee
        }
    }

    @Action("Fill field Expires in for the Limited Time Offer")
    fun limitedTimeOffer() {
        e {
            click(limitedTimeOffer)
            sendKeys(addTime, "1")
        }
    }

    @Step("User cancel Streaming")
    @Action("User cancel Streaming")
    fun cancelStreaming(
        user: HasOtfWallet
    ) {
        e {
            click(cancelOfferButton)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
    }

    @Step("My offers. Open offer with unit price {unitPrice}")
    fun findAndOpenOfferInOfferList(unitPrice: BigDecimal): AtmStreamingPage {
        e {
            click(myOffer)
        }
        val myOffer = myOffersList.find {
            it.unitPriceAmount == unitPrice
        } ?: error("Can't find offer with unit price '$unitPrice'")
        myOffer.open()
        return AtmStreamingPage(driver)
    }

    @Step("Cancel offer with unit price {unitPrice}")
    fun cancelOffer(unitPrice: BigDecimal, user: HasOtfWallet): AtmStreamingPage {
        e {
            click(myOffer)
        }
        val myOffer = myOffersList.find {
            it.unitPriceAmount == unitPrice
        } ?: error("Can't find offer with amount $unitPrice")

        myOffer.cancelOffer(user)
        driver.navigate().refresh()
        val cancelledOffer = myOffersList.find {
            it.unitPriceAmount == unitPrice
        }
        MatcherAssert.assertThat(
            "Offer with amount $unitPrice should have been be cancelled",
            cancelledOffer,
            Matchers.nullValue()
        )
        return AtmStreamingPage(driver)
    }

    @Step("Select amount for {quoteAsset}")
    fun selectAmount(value: String): AtmStreamingPage {
        wait {
            untilPresented<Button>(
                By.xpath("//nz-radio-group[@formcontrolname='baseAmount']//span[contains(text(), '$value')]"),
                "Select base amount by $value"
            )
        }.click()
        return AtmStreamingPage(driver)
    }

    @Step("Overview. Open offer with unit price {unitPrice}")
    fun findAndOpenOfferInOverview(unitPrice: BigDecimal): AtmStreamingPage {
        val myOffer = overviewOffersList.find {
            it.unitPriceAmount == unitPrice
        } ?: error("Can't find offer with unit price '$unitPrice'")
        myOffer.open()
        return AtmStreamingPage(driver)
    }

    @Step("Overview. Open offer with unit price {unitPrice}")
    fun findOfferInHistory(unitPrice: BigDecimal): StreamingOfferItem {
        return tradeHistoryList.find {
            it.unitPriceAmount == unitPrice
        } ?: error("Can't find offer with unit price '$unitPrice'")
    }

    //</editor-fold>
    @Step("Overview. Find offer with unit price {unitPrice}")
    fun findOfferBy(unitPrice: BigDecimal, table: AtmTable<StreamingOfferItem>): StreamingOfferItem {
        var item = table.find {
            it.unitPriceAmount == unitPrice
        } ?: error("Can't find offer with unit price '$unitPrice'")
        return item
    }

    @Step("Overview. Find offer with unit price {unitPrice}")
    fun isOfferExist(unitPrice: BigDecimal, table: AtmTable<StreamingOfferItem>): Boolean {
        table.find { it.unitPriceAmount == unitPrice } ?: return false
        return true
    }

    @Step("Overview. Accept open ofer")
    fun acceptOffer(user: HasOtfWallet) {
        e {
            wait {
                until("Accept offer button should be enabled") {
                    acceptOffer.isEnabled
                }
            }
            click(acceptOffer)
            alert { checkErrorAlert() }
            signAndSubmitMessage(user, user.otfWallet.secretKey)
            alert { checkErrorAlert() }
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
            select(tradingPair, "$baseAsset/$quoteAsset")
            if (baseAsset.tokenSymbol.startsWith("IT")
                and maturityDate.isNotBlank()
            ) select(baseMaturityDate, maturityDate)
            click(dateFrom)
            click(today)
        }
    }

    @Step("Set filter today")
    fun setFilterToday(
        baseAsset: CoinType,
        quoteAsset: CoinType, maturityDate: String = ""
    ) {
        e {
            click(resetFilters)
            select(tradingPair, "$baseAsset/$quoteAsset")
            if (baseAsset.tokenSymbol.startsWith("IT")
                and maturityDate.isNotBlank()
            ) select(baseMaturityDate, maturityDate)
            click(dateFrom)
            click(today)
        }
    }
}