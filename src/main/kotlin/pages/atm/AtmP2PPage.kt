package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.CoinType.IT
import models.user.classes.DefaultUser
import models.user.interfaces.HasOtfWallet
import models.user.interfaces.SimpleWallet
import models.user.interfaces.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.p2p.P2PItem
import pages.htmlelements.elements.AtmAmount
import pages.htmlelements.elements.AtmRadio
import pages.htmlelements.elements.AtmSelectLazy
import pages.htmlelements.elements.AtmTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.Form
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.math.BigDecimal

@PageUrl("/trading/p2p")
class AtmP2PPage(driver: WebDriver) : AtmPage(driver) {

    enum class ExpireType {
        GOOD_TILL_CANCELLED, TEMPORARY;
    }

    @Name("Amount to receive")
    @FindBy(xpath = "//span[contains(text(),'AMOUNT TO RECEIVE')]/ancestor::atm-property-value//atm-amount")
    lateinit var amountToReceiveInIncomingForm: AtmAmount

    @Name("Amount to send in incoming form")
    @FindBy(xpath = "//span[contains(text(),'AMOUNT TO SEND')]/ancestor::atm-property-value//atm-amount")
    lateinit var amountToSendInIncomingForm: AtmAmount

    @Name("Amount to receive in accept form")
    @FindBy(xpath = "//div[contains(@class, 'ant-modal-body')]//span[contains(text(),'AMOUNT TO RECEIVE')]/ancestor::atm-property-value//atm-amount")
    lateinit var amountToReceiveInAcceptForm: AtmAmount

    @Name("Amount to send in accept form")
    @FindBy(xpath = "//div[contains(@class, 'ant-modal-body')]//span[contains(text(),'AMOUNT TO SEND')]/ancestor::atm-property-value//atm-amount")
    lateinit var amountToSendInAcceptForm: AtmAmount

    //<editor-fold desc="ELEMENTS">
    @Name("Atomyze Zzz Spinner waiting alert")
    @FindBy(xpath = "//i[@nztype='atomyzeZzzSpinner']")
    lateinit var waitSpinnerAlert: TextBlock

    @Name("View Incoming P2P")
    @FindBy(xpath = "//span[contains(text(), 'View Incoming')]")
    lateinit var viewIncomingP2P: Button

    @Name("Create P2P")
    @FindBy(xpath = "//span[contains(text(),'Create a Blocktrade')]")
    lateinit var createBlockTrade: Button

    @Name("Create P2P from My P2P")
    @FindBy(xpath = "//atm-p2p-outgoing//span[contains(text(),'CREATE BLOCKTRADE')]")
    lateinit var createFromMyBlockTrade: Button

    @Name("View my P2P")
    @FindBy(xpath = "//span[contains(text(),'View my Blocktrades')]")
    lateinit var viewMyP2P: Button

    @Name("Incoming P2P's")
    @FindBy(xpath = "//a[contains(text(),'INCOMING BLOCKTRADES')]")
    lateinit var incomingP2PS: Button

    @Name("My P2P's")
    @FindBy(xpath = "//a[contains(text(),'MY BLOCKTRADES')]")
    lateinit var myP2Ps: Button

    @Name("View history P2P")
    @FindBy(xpath = "//atm-p2p-home//span[contains(text(),'View history')]")
    lateinit var viewHistoryP2P: Button

    @Name("Deal history Blocktrade")
    @FindBy(xpath = "//a[contains(text(),'DEALS HISTORY')]")
    lateinit var dealHistoryP2P: Button

    @Name(" INCOMING BLOCKTRADES ")
    @FindBy(xpath = "//atm-p2p-incoming[1]")
    lateinit var incomingP2P: Button

    @Name("Incoming P2P")
    @FindBy(xpath = "//a[.=' INCOMING BLOCKTRADES ']//ancestor::div[@role='tab']")
    lateinit var incomingBT: Button

    @Name("Reject")
    @FindBy(xpath = "//atm-p2p-incoming-details//span[contains(text(),'Reject')]")
    lateinit var reject: Button

    @Name("Cancel p2p")
    @FindBy(xpath = "//nz-modal-container//span[contains(text(),'Cancel p2p offer')]")
    lateinit var cancelP2P: Button

    @Name("Cancel 2FA confirmation popup")
    @FindBy(xpath = "//div[@class='confirmation-form__footer']//span[contains(text(),'Cancel')]")
    lateinit var cancel2fa: Button

    @Name("Open P2P")
    @FindBy(xpath = "//atm-p2p-outgoing//atm-p2p-item[1]")
    lateinit var openP2P: Button

    @Name("Open incoming P2P")
    @FindBy(xpath = "//atm-p2p-item")
    lateinit var openIncomingP2P: Button

    @Name("Close BlockTrade card")
    @FindBy(xpath = "//span[contains(text(),'Close')]")
    lateinit var closeBlockTradeCard: Button

    @Name("Cancel open BlockTrade")
    @FindBy(xpath = "//span[contains(text(),'Cancel Blocktrade')]")
    lateinit var cancelOpenBlockTrade: Button

    @Name("Cancel P2P Order")
    @FindBy(xpath = ".//atm-p2p-outgoing//span[contains(text(),'Cancel')]")
    lateinit var cancelP2POrder: Button

    @Name("My P2P from create P2P page")
    @FindBy(xpath = "//div//a[@href='/trading/p2p/outgoing']")
    lateinit var myP2PFromCreate: Button

    @Name("First of row")
    @FindBy(xpath = "//iframe//nz-auto-option[1]/div")
    lateinit var firstOfWalletRow: Button

    @Name("Reset filters")
    @FindBy(xpath = "//button[contains(text(),'RESET')]")
    lateinit var resetFilters: Button

    @Name("To counterparty wallet")
//    @FindBy(xpath = "//input[@formcontrolname='recipient']")
    @FindBy(xpath = "//nz-form-item[1]//input")
    lateinit var toWallet: TextInput

    @Name("Error To counterparty wallet")
//    @FindBy(xpath = "//input[@formcontrolname='recipient']")
    @FindBy(xpath = ".//nz-form-item[1]//input//ancestor::div//div[contains(text(),'Field is required')]")
    lateinit var errorToWallet: TextInput

    @Name("To counterparty")
//    @FindBy(xpath = "//input[@formcontrolname='recipient']")
    @FindBy(xpath = "//nz-auto-option//div")
    lateinit var counterparty: Button

    @Name("Amount to send")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='baseAmount']//input")
    lateinit var amountToSend: Button

    @Name("Amount to receive")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='quoteAmount']//input")
    lateinit var amountToReceive: Button

    @Name("Today button")
    @FindBy(xpath = "//a[contains(text(), 'Today')]")
    lateinit var today: Button

    @Name("Token received")
    @FindBy(xpath = "//span[contains(text(), 'Token received')]//ancestor::nz-form-item//atm-custom-select")
    lateinit var tokenReceived: AtmSelectLazy

    @Name("Maturity date received")
    @FindBy(xpath = "//span[.='Maturity date received']//ancestor::nz-form-item//nz-select")
    lateinit var maturityDateReceived: AtmSelectLazy

    @Name("Offer maturity date")
    @FindBy(xpath = "//nz-form-item//span[.='Maturity date']//ancestor::atm-maturity-date//nz-select")
    lateinit var offerMaturityDate: AtmSelectLazy

    @Name("Date from")
    @FindBy(xpath = "//nz-date-picker[@formcontrolname='dateFrom'] | //span[text()='Date from']/ancestor::nz-form-control//input")
    lateinit var dateFrom: TextInput

    @Name("Data popup menu")
    @FindBy(css = "date-range-popup")
    lateinit var dateFormLabel: Form

    @Name("Asset to send")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='baseToken']//nz-select")
    lateinit var assetToSend: AtmSelectLazy

    @Name("Asset to receive")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='quoteToken']//nz-select")
    lateinit var assetToReceive: AtmSelectLazy

    @Name("Good till cancelled")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Good till cancelled')]")
    lateinit var goodTillCancelled: AtmRadio

    @Name("Limited time offer")
    @FindBy(xpath = "//atm-expires-control//span[contains(text(), 'Limited time offer')]")
    lateinit var limitedTimeOffer: AtmRadio

    @Name("Expires in")
    @FindBy(xpath = "//atm-expires-control//nz-input-number//input")
    lateinit var expiresIn: TextInput

    @Name("Create deal")
    @FindBy(xpath = "//button//span[contains(text(), ' CREATE DEAL ')]")
    lateinit var createDeal: Button

    @Name("Token name")
    @FindBy(xpath = "//atm-card-transaction-amounts//span[@class='token-name ng-star-inserted']")
    lateinit var tokenName: Button

    @Name("Cancel offer confirmation")
    @FindBy(xpath = "//div[contains(text(),'Cancel offer confirmation')]")
    lateinit var cancelOfferConfirmationDialog: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]")
    lateinit var cancel: Button

    @Name("CancelOfferSignatureDialog")
    @FindBy(xpath = "//button//span[contains(text(), ' Cancel')]")
    lateinit var cancelOfferSignatureDialog: Button

    @Name("Accept from details")
    @FindBy(xpath = "//atm-p2p-incoming-details//span[contains(text(),'Accept')]")
    lateinit var acceptFromDetails: Button

    @Name("P2P offer fee")
    @FindBy(xpath = "//atm-transaction-fee-control//atm-amount")
    lateinit var newOfferFee: AtmAmount

    @Name("Outgoing P2P offers")
    @FindBy(css = "atm-p2p-outgoing")
    lateinit var outgoingOffers: AtmTable<P2PItem>

    @Name("Incoming P2P offers")
    @FindBy(css = "atm-p2p-incoming")
    lateinit var incomingOffers: AtmTable<P2PItem>

    @Name("Trade history")
    @FindBy(css = "atm-p2p-history")
    lateinit var tradeHistory: AtmTable<P2PItem>

    @Name("P2P offer fee")
    @FindBy(xpath = "//*[text() = ' Transaction fee ']/ancestor::atm-property-value//atm-amount")
    lateinit var offerFee: AtmAmount

    @Name("From my wallet")
    @FindBy(xpath = "//*[contains(text(),'From my wallet')]//ancestor::atm-property-value//div[contains(@class, 'val')]")
    lateinit var fromWalletText: TextBlock

    @Name("Outgoing P2P with Open status")
    @FindBy(xpath = "//span[contains(text(),'ACTIVE')]")
    lateinit var openOutgoingP2P: Button

    @FindBy(xpath = ".//nz-modal-container//span[contains(text(),'Yes')]/ancestor::button")
    @Name("Yes button")
    lateinit var yesButton: Button
    //</editor-fold>

    //<editor-fold desc="ACTIONS">
    @Step("User create P2P")
    @Action("User create P2P")
    fun createP2P(
        walletID: String,
        toCounterparty: String,
        tokenToSend: CoinType,
        amountSend: String,
        tokenToReceive: CoinType,
        amountReceive: String,
        expiryType: ExpireType,
        user: HasOtfWallet,
        maturityDate: String = "",
        manualCompleted: Boolean = false
    ): BigDecimal =
        createP2P(
            walletID,
            toCounterparty,
            tokenToSend,
            amountSend,
            tokenToReceive,
            amountReceive,
            expiryType,
            user,
            user.otfWallet,
            maturityDate,
            manualCompleted
        )

    @Step("User create P2P")
    @Action("User create P2P")
    fun createP2P(
        walletID: String,
        toCounterparty: String,
        tokenToSend: CoinType,
        amountSend: String,
        tokenToReceive: CoinType,
        amountReceive: String,
        expiryType: ExpireType,
        user: User,
        wallet: SimpleWallet,
        maturityDate: String = "",
        manualCompleted: Boolean = false
    ): BigDecimal {
        return createP2PwithoutSign(
            walletID,
            toCounterparty,
            tokenToSend,
            amountSend,
            tokenToReceive,
            amountReceive,
            expiryType,
            maturityDate,
            manualCompleted
        ).also {
            signAndSubmitMessage(user, wallet.secretKey)
        }
    }

    @Step("User create P2P without signification")
    @Action("User create P2P without signification")
    fun createP2PwithoutSign(
        walletID: String,
        toCounterparty: String,
        tokenToSend: CoinType,
        amountSend: String,
        tokenToReceive: CoinType,
        amountReceive: String,
        expiryType: ExpireType,
        maturityDate: String = "",
        manualCompleted: Boolean = false
    ): BigDecimal {
        return e {
            click(createBlockTrade)
//            select(toWallet, toCounterparty)
            waitSpinnerAlertDisappeared()
            click(toWallet)
            sendKeys(toWallet, walletID)
            val button = wait {
                untilPresented<Button>(By.xpath("//nz-auto-option//div[contains(text(),'$toCounterparty')]"))
            }.to<Button>("toCounterparty")
            click(button)

            if (check { isElementContainsText(assetToReceive, tokenToSend.tokenSymbol) }) {
                assetToReceive.selectBeforeOther(tokenToSend.tokenSymbol, page)
            }

            select(assetToSend, tokenToSend.tokenSymbol)
            select(assetToReceive, tokenToReceive.tokenSymbol)

            // TODO: Я не до конца уверена, но можно попробовать delete из AtmInput
            //поле отвратительное, не чистится, и по умолчанию заполнено как 0.0000000
            //если не ставить вначале точку - то поле не очистится и первые цифры проигнорируются
            //теперь это работает так
            sendKeys(amountToSend, ".")
            sendKeys(amountToSend, amountSend)
            sendKeys(amountToReceive, ".")
            sendKeys(amountToReceive, amountReceive)
            if ((tokenToSend.tokenSymbol.startsWith(IT.tokenSymbol) or tokenToReceive.tokenSymbol.startsWith(IT.tokenSymbol))
                and maturityDate.isNotBlank()
            ) select(offerMaturityDate, maturityDate)
            when (expiryType) {
                ExpireType.GOOD_TILL_CANCELLED -> click(goodTillCancelled)
                ExpireType.TEMPORARY -> limitedTimeOffer()
            }
            if(check { isElementPresented(errorToWallet) }){
                click(toWallet)

                val button = wait {
                    untilPresented<Button>(By.xpath("//nz-auto-option//div[contains(text(),'$toCounterparty')]"))
                }.to<Button>("toCounterparty")
                click(button)
            }
            wait {
                until("") {
                    newOfferFee.text.isNotEmpty()
                }
            }
            Thread.sleep(2000)
            if (!manualCompleted) {
                click(createDeal)
            }
            newOfferFee.amount
        }
    }

    @Action("Fill field Expires in for the Limited Time Offer")
    fun limitedTimeOffer() {
        e {
            click(limitedTimeOffer)
            sendKeys(expiresIn, "1")
        }
    }

    @Step("User accept P2P offer")
    @Action("User accept P2P")
    fun acceptP2P(user: HasOtfWallet, amount: BigDecimal): BigDecimal {
        findIncomingP2P(amount)
        val fee = wait(15L) {
            until("Couldn't load fee") {
                offerFee.text.isNotEmpty()
            }
            offerFee.amount
        }

        wait(15L) {
            until("Couldn't load wallet") {
                fromWalletText.text != " No wallet "
            }
        }

        e {
            click(acceptFromDetails)
            alert { checkErrorAlert() }
            signAndSubmitMessage(user as DefaultUser, user.otfWallet.secretKey)
        }
        return fee
    }

    @Step("User find and open P2P offer")
    fun findIncomingP2P(amount: BigDecimal): AtmStreamingPage {
        e {
            click(viewIncomingP2P)
        }
        val myOffer = incomingOffers.find {
            it.amountToReceive == amount || it.amountToSend == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.open()
        return AtmStreamingPage(driver)
    }

    @Step("User find in trade history Blocktrade offer")
    fun findBlocktradeOfferInTradeHistory(amount: BigDecimal): AtmStreamingPage {
        e {
            click(viewHistoryP2P)
        }
        tradeHistory.find {
            it.receivedAmount == amount
        } ?: error("Can't find offer with unit price '$amount'")
        return AtmStreamingPage(driver)
    }

    @Step("User cancels P2P offer")
    fun cancelOffer(amount: BigDecimal, user: HasOtfWallet): AtmStreamingPage {
        e {
            click(viewIncomingP2P)
            click(myP2PFromCreate)
        }
        val myOffer = outgoingOffers.find {
            it.amountToReceive == amount
        } ?: error("Can't find offer with amount $amount")
        myOffer.cancelOffer()
        e {
            click(yesButton)
        }
        signAndSubmitMessage(user, user.otfWallet.secretKey)
        driver.navigate().refresh()
        val cancelledOffer = outgoingOffers.find {
            it.amountToReceive == amount
        }
        assertThat(
            "Offer with amount $amount should have been be cancelled",
            cancelledOffer,
            Matchers.nullValue()
        )
        return AtmStreamingPage(driver)
    }

    @Step("User find outgoing P2P offer")
    fun findOutgoingP2P(amount: BigDecimal): AtmStreamingPage {
        e {
            click(viewIncomingP2P)
            click(viewMyP2P)
        }
        val myOffer = outgoingOffers.find {
            it.amountToReceive == amount
        } ?: error("Can't find offer with unit price '$amount'")
        myOffer.open()
        return AtmStreamingPage(driver)
    }

    @Step("User reject P2P offer")
    fun rejectP2P(user: HasOtfWallet): AtmStreamingPage {
        e {
            click(reject)
            click(yesButton)

            signAndSubmitMessage(user, user.otfWallet.secretKey)
            alert { checkErrorAlert() }
            wait { until("Reject should be disappeared", 10L) { check { !isElementPresented(reject, 4L) } } }
        }
        return AtmStreamingPage(driver)
    }
    //</editor-fold>

    @Step("User choose counterparty in popup window")
    @Action("choose counterparty in popup window")
    fun chooseCounterpartyPopupList(input: WebElement, text: String) {
        e {
            click(input)
            sendKeys(input, text)
            wait {
                untilPresented<Button>(By.xpath("//nz-auto-option//div[contains(text(),'$text')]"))
            }.clickJS()
        }
    }

    @Step("Set filter today for buy offer type")
    fun setFilterBuyToday(
        receivedToken: CoinType, maturityDate: String = ""
    ) {
        e {
            click(resetFilters)
            select(tokenReceived, "$receivedToken")
            if (receivedToken.tokenSymbol.startsWith(IT.tokenSymbol)
                and maturityDate.isNotBlank()
            ) select(maturityDateReceived, maturityDate)
            wait {
                until("Filter form enabled after select token type ${receivedToken.tokenSymbol}") {
                    click(dateFrom)
                    untilPresented(dateFormLabel)
                    click(today)
                }
            }
        }
    }

    @Step("Overview. Find offer with amount to receive {amountReceive}")
    fun isOfferExist(amountReceive: BigDecimal, table: AtmTable<P2PItem>): Boolean {
        table.find { it.amountToSend == amountReceive || it.amountToReceive == amountReceive } ?: return false
        return true
    }

    @Step("Check fields Amount to send and Amount to receive")
    fun setSumSelectAmountFields(sum: String) {
        e {
            click(createBlockTrade)
            sendKeys(amountToSend, sum)
            sendKeys(amountToReceive, sum)
        }
    }

    @Step("Checking field for decimal count")
    fun checkTransferFieldEightDigitsDecimal() {
        check {
            Assert.assertTrue(
                "Entered amount is not displayed!",
                isElementPresented(By.xpath("//atm-amount-input[@formcontrolname=\"baseAmount\"]//span[contains(@class, 'decimal')]"))
            )
        }
        val numberOfDigitsAfterDecimalPoint: Int =
            findElement(By.xpath("//atm-amount-input[@formcontrolname=\"baseAmount\"]//span[contains(@class, 'decimal')]")).text.removePrefix(
                "."
            ).length
        check {
            Assert.assertTrue(
                "Number of digits after decimal point is not equal 8!",
                numberOfDigitsAfterDecimalPoint == 8
            )
        }

        check {
            Assert.assertTrue(
                "Entered amount is not displayed!",
                isElementPresented(By.xpath("//atm-amount-input[@formcontrolname=\"quoteAmount\"]//span[contains(@class, 'decimal')]"))
            )
        }
        val numberOfDigitsAfterDecimalPoint1: Int =
            findElement(By.xpath("//atm-amount-input[@formcontrolname=\"quoteAmount\"]//span[contains(@class, 'decimal')]")).text.removePrefix(
                "."
            ).length
        check {
            Assert.assertTrue(
                "Number of digits after decimal point is not equal 8!",
                numberOfDigitsAfterDecimalPoint1 == 8
            )
        }
    }

    @Step("Wait waiting spinner alert disappeared")
    fun waitSpinnerAlertDisappeared() {
        wait {
            until("Waiting alert should be not exist", 20L) {
                check {
                    !isElementPresented(waitSpinnerAlert, 1L)
                }
            }
        }
    }

    @Step("Set filter today")
    fun setFilterToday() {
        e {
            click(resetFilters)
            click(dateFrom)
            click(today)
        }
    }
}