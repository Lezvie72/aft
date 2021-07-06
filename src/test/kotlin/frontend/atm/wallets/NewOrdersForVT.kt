package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmIssuancesPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmOrdersPage
import pages.atm.AtmOrdersPage.FilterByDealType
import pages.atm.AtmOrdersPage.FilterByStatus
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("New orders for VT.")
@TmsLink("ATMCH-4046")
class NewOrdersForVT : BaseTest() {

    private val baseAsset = CoinType.VT
    private val baseAssetIT = CoinType.IT
    private val quoteAsset = CoinType.CC
    private val amountBuy = OtfAmounts.AMOUNT_10.amount

    //    private val maturityDateInnerDate = baseAsset.date
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    )
    @TmsLink("ATMCH-5208")
    @Test
    @DisplayName("Order for VT token. Check the interface for order of BUY type in EXECUTED status")
    fun checkTheInterfaceForOrderOfBuyTypeInExecutedStatus() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(driver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.VT, amount.toString(), userOne, userOne.mainWallet)
        }

        with(openPage<AtmOrdersPage>(driver)) {
            e {
                chooseWallet(userOne.mainWallet.name)
                chooseToken(baseAsset)
                select(filterByDealType, FilterByDealType.URGENT.name)

                softAssert { elementContainingTextPresented(FilterByStatus.EXECUTED.name) }
                softAssert { elementContainingTextPresented("BUY") }

                orderList.find {
                    it.amountOrder == amount
                }?.let { click(it) }
                    ?: error("Can't find order with value '$amount'")

                softAssert { elementContainingTextPresented("BUY") } // as Type
                softAssert { elementContainingTextPresented("EXECUTED") } // as Status
                softAssert { elementContainingTextPresented("REQUEST ID") }
                softAssert { elementContainingTextPresented("AMOUNT") }
                softAssert { elementContainingTextPresented("SUBMISSION DATE") }
                softAssert { elementContainingTextPresented("TO PAY") }

                click(signatureDetails)

                softAssert { elementContainingTextPresented("MESSAGE") }
                softAssert { elementContainingTextPresented("MESSAGE HASH") }
                softAssert { elementContainingTextPresented("SIGNER") }
                softAssert { elementContainingTextPresented("ACCOUNT") }
                softAssert { elementContainingTextPresented("PUBLIC KEY") }
                softAssert { elementContainingTextPresented("SIGNATURE") }

                softAssert { elementContainingTextPresented("EXECUTED AMOUNT") }
                softAssert { elementContainingTextPresented("EXECUTION DATE") }
                softAssert { elementContainingTextPresented("EXERCISE PRICE") }
                softAssert { elementContainingTextPresented("PAID") }

                softAssert { elementContainingTextPresented("WALLET ID") }
                softAssert { elementContainingTextPresented("WALLET TYPE") }
                softAssert { elementContainingTextPresented("SIGNATURE TYPE") }

                softAssert { elementContainingTextPresented("CANCEL") }
                softAssert { elementContainingTextPresented("DOWNLOAD RECEIPT") }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    )
    @TmsLink("ATMCH-5219")
    @Test
    @DisplayName("Order for VT token. Check the interface for receive order in executed status.")
    fun checkTheInterfaceForReceiveOrderInExecutedStatus() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(driver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.VT, amount.toString(), userOne, userOne.mainWallet, "", true)
            openPage<AtmMarketplacePage>(driver).logout()

            openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }
                .changeStatusForOfferWithAmount(
                    baseAsset, amount,
                    AtmIssuancesPage.StatusType.APPROVE, itIssuer, itIssuer.mainWallet
                )
            openPage<AtmIssuancesPage>(driver).logout()
        }

        with(openPage<AtmOrdersPage>(driver) { submit(userOne) }) {
            e {
                chooseWallet(userOne.mainWallet.name)
                chooseToken(baseAsset)
                select(filterByDealType, FilterByDealType.DISTRIBUTIONAL.name)

                softAssert { elementContainingTextPresented(FilterByStatus.EXECUTED.name) }
                softAssert { elementContainingTextPresented("RECEIVE") }

                orderList.find {
                    it.amountOrder == amount
                }?.let { click(it) }
                    ?: error("Can't find order with value '$amount'")

                softAssert { elementContainingTextPresented("RECEIVE") } // as Type
                softAssert { elementContainingTextPresented("EXECUTED") } // as Status
                softAssert { elementContainingTextPresented("REQUEST ID") }
                softAssert { elementContainingTextPresented("AMOUNT") }
                softAssert { elementContainingTextPresented("SUBMISSION DATE") }

                click(signatureDetails)

                softAssert { elementContainingTextPresented("MESSAGE") }
                softAssert { elementContainingTextPresented("MESSAGE HASH") }
                softAssert { elementContainingTextPresented("SIGNER") }
                softAssert { elementContainingTextPresented("ACCOUNT") }
                softAssert { elementContainingTextPresented("PUBLIC KEY") }
                softAssert { elementContainingTextPresented("SIGNATURE") }

                softAssert { elementContainingTextPresented("EXECUTED AMOUNT") }
                softAssert { elementContainingTextPresented("EXECUTION DATE") }

                softAssert { elementContainingTextPresented("CHAT HISTORY") }
                softAssert { elementContainingTextPresented("There are no messages in this chat") }

                softAssert { elementContainingTextPresented("WALLET ID") }
                softAssert { elementContainingTextPresented("WALLET TYPE") }
                softAssert { elementContainingTextPresented("SIGNATURE TYPE") }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    )
    @TmsLink("ATMCH-5221")
    @Test
    @DisplayName("Order for VT token. Check the interface for receive order in declined status.")
    @Description("All messages and status should be visible and update without refreshing page. ATMCH-5319")
    fun checkTheInterfaceForReceiveOrderInDeclinedStatus() {
        // !! All message should be visible Without refreshing page -> ATMCH-5319 !!
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(driver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.VT, amount.toString(), userOne, userOne.mainWallet, "", true)
            openPage<AtmMarketplacePage>(driver).logout()

            openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }
                .changeStatusForOfferWithAmount(
                    baseAsset, amount,
                    AtmIssuancesPage.StatusType.DECLINE, itIssuer, itIssuer.mainWallet
                )
            openPage<AtmIssuancesPage>(driver).logout()
        }

        with(openPage<AtmOrdersPage>(driver) { submit(userOne) }) {
            e {
                chooseWallet(userOne.mainWallet.name)
                chooseToken(baseAsset)
                select(filterByDealType, FilterByDealType.DISTRIBUTIONAL.name)
                select(filterByStatus, FilterByStatus.DECLINED.name)

                softAssert { elementContainingTextPresented(FilterByStatus.DECLINED.name) }
                softAssert { elementContainingTextPresented("RECEIVE") }

                orderList.find {
                    it.amountOrder == amount
                }?.let { click(it) }
                    ?: error("Can't find order with value '$amount'")

                softAssert { elementContainingTextPresented("RECEIVE") } // as Type
                softAssert { elementContainingTextPresented("EXECUTED") } // as Status
                softAssert { elementContainingTextPresented("REQUEST ID") }
                softAssert { elementContainingTextPresented("AMOUNT") }
                softAssert { elementContainingTextPresented("SUBMISSION DATE") }

                click(signatureDetails)

                softAssert { elementContainingTextPresented("MESSAGE") }
                softAssert { elementContainingTextPresented("MESSAGE HASH") }
                softAssert { elementContainingTextPresented("SIGNER") }
                softAssert { elementContainingTextPresented("ACCOUNT") }
                softAssert { elementContainingTextPresented("PUBLIC KEY") }
                softAssert { elementContainingTextPresented("SIGNATURE") }

                softAssert { elementContainingTextPresented("EXECUTED AMOUNT") }
                softAssert { elementContainingTextPresented("EXECUTION DATE") }

                softAssert { elementContainingTextPresented("CHAT HISTORY") }
                softAssert { elementContainingTextPresented("There are no messages in this chat") }

                softAssert { elementContainingTextPresented("WALLET ID") }
                softAssert { elementContainingTextPresented("WALLET TYPE") }
                softAssert { elementContainingTextPresented("SIGNATURE TYPE") }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    )
    @TmsLink("ATMCH-5222")
    @Test
    @DisplayName("Order for VT token. Chat.")
    fun chat() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")
        val userDriver = createDriver()
        val issuerDriver = createDriver()
        val textFromIssuer = "Text from issuer"
        val textFromUser = "Text from user"

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(userDriver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.VT, amount.toString(), userOne, userOne.mainWallet, "", true)
        }

        val userWindow = openPage<AtmOrdersPage>(userDriver)
        with(userWindow) {
            e {
                chooseWallet(userOne.mainWallet.name)
                chooseToken(baseAsset)
                select(filterByDealType, FilterByDealType.DISTRIBUTIONAL.name)

                softAssert(userDriver) { elementContainingTextPresented(FilterByStatus.EXECUTED.name) }
                softAssert(userDriver) { elementContainingTextPresented("RECEIVE") }

                orderList.find {
                    it.amountOrder == amount
                }?.let { click(it) }
                    ?: error("Can't find order with value '$amount'")
                softAssert(userDriver) { elementContainingTextPresented("There are no messages in this chat") }
                softAssert(userDriver) { elementNotPresented(chatSendButton, 5L) }
            }
        }

        val issuerWindow = openPage<AtmIssuancesPage>(issuerDriver) { submit(itIssuer) }
        with(issuerWindow) {
            e {
                chooseToken(baseAsset)
                click(requestCurrentQueue)

                val myOffer = requestOffers.find {
                    it.totalRequestedAmount == amount
                } ?: error("Can't find offer with unit price '$amount'")
                myOffer.clickProceedButton()
                click(showChat)
                softAssert(issuerDriver) { elementPresented(chatSendButton) }
                sendKeys(chatInput, textFromIssuer)
                click(chatSendButton)
                Thread.sleep(10000)
                softAssert(issuerDriver) { elementContainingTextPresented(textFromIssuer) }
            }
        }

        with(userWindow) {
            e {
                softAssert(userDriver) { elementContainingTextPresented(textFromIssuer) }
                sendKeys(chatInput, textFromUser)
                click(chatSendButton)
                Thread.sleep(10000)
                softAssert(userDriver) { elementContainingTextPresented(textFromUser) }
                page.navigate().refresh()
                softAssert(userDriver) { elementContainingTextPresented(textFromUser) }
            }
        }

        with(issuerWindow) {
            e {
                assert {

                }
                page.navigate().refresh()
                click(showChat)
                softAssert(issuerDriver) { elementContainingTextPresented(textFromUser) }

                click(decline)
                signAndSubmitMessage(itIssuer, itIssuer.mainWallet.secretKey)
            }
        }

        with(userWindow) {
            e {
                softAssert(userDriver) { elementContainingTextPresented(FilterByStatus.DECLINED.name) }
                softAssert(userDriver) { elementNotPresented(chatSendButton) }
                softAssert(userDriver) { elementContainingTextPresented(textFromUser) }
                softAssert(userDriver) { elementContainingTextPresented(textFromIssuer) }
                page.navigate().refresh()
                Thread.sleep(10000)
                softAssert(userDriver) { elementContainingTextPresented(FilterByStatus.DECLINED.name) }
                softAssert(userDriver) { elementNotPresented(chatSendButton) }
                softAssert(userDriver) { elementContainingTextPresented(textFromUser) }
                softAssert(userDriver) { elementContainingTextPresented(textFromIssuer) }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02)
    )
    @TmsLink("ATMCH-5225")
    @Test
    @DisplayName("Order for VT token. Check the interface for receive order in submitted status.")
    fun checkTheInterfaceForOrderOfReceiveTypeInSubmittedStatus() {
        val amount = BigDecimal("10.${RandomStringUtils.randomNumeric(8)}")

        step("Precondition. Create order.") {
            openPage<AtmMarketplacePage>(driver) { submit(userOne) }
                .buyOrReceiveToken(CoinType.VT, amount.toString(), userOne, userOne.mainWallet, needReceive = true)
        }

        with(openPage<AtmOrdersPage>(driver)) {
            e {
                chooseWallet(userOne.mainWallet.name)
                chooseToken(baseAsset)
                select(filterByDealType, FilterByDealType.DISTRIBUTIONAL.name)

                softAssert { elementContainingTextPresented(FilterByStatus.SUBMITTED.name) }
                softAssert { elementContainingTextPresented("RECEIVE") }

                orderList.find {
                    it.amountOrder == amount
                }?.let { click(it) }
                    ?: error("Can't find order with value '$amount'")

                softAssert { elementContainingTextPresented("RECEIVE") } // as Type
                softAssert { elementContainingTextPresented("SUBMITTED") } // as Status
                softAssert { elementContainingTextPresented("REQUEST ID") }
                softAssert { elementContainingTextPresented("AMOUNT") }
                softAssert { elementContainingTextPresented("SUBMISSION DATE") }
                softAssert { elementContainingTextPresented("TO PAY") }

                click(signatureDetails)

                softAssert { elementContainingTextPresented("MESSAGE") }
                softAssert { elementContainingTextPresented("MESSAGE HASH") }
                softAssert { elementContainingTextPresented("SIGNER") }
                softAssert { elementContainingTextPresented("ACCOUNT") }
                softAssert { elementContainingTextPresented("PUBLIC KEY") }
                softAssert { elementContainingTextPresented("SIGNATURE") }

                softAssert { elementContainingTextPresented("QUEUED FOR PROCESSING") }

                softAssert { elementContainingTextPresented("WALLET ID") }
                softAssert { elementContainingTextPresented("WALLET TYPE") }
                softAssert { elementContainingTextPresented("SIGNATURE TYPE") }

                softAssert { elementPresented(chatSendButton) }
            }
        }
    }
}
