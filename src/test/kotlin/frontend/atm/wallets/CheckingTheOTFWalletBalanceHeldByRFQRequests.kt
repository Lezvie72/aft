package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmStreamingPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.step
import kotlin.math.roundToLong

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Wallet/Checking the OTF wallet balance")

class CheckingTheOTFWalletBalanceHeldByRFQRequests : BaseTest() {
    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-2510")
    @Test
    @DisplayName("Checking the OTF wallet balance (held by RFQ requests)")

    fun checkingTheOTFWalletBalanceByRFQRequests() {
        var expectedIDTBalanceFromOTFWalletAfterSendingDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletAfterSendingResult: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellResultRFQ: Double = 0.0
        val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
        val user2 = Users.ATM_USER_2FA_WITHOUT_WALLET_MTEST02
        val user3 = Users.ATM_USER_2FA_WITH_WALLET_MTEST03
        val amountVal = "1"
        val heldInOffers: Double = 0.0
        val mainWallet = user1.mainWallet
        val otfWallet = user1.otfWallet
        var expectedССBalanceFromOTFWalletDouble: Double = 0.0
        var availableBalanceValueBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellDoubleRFQ: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellResult: Double = 0.0
        var expectedCCBalanceFromOTFWalletOffer: Double = 0.0

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedССBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
            }
            step("Go into CC token in Main 1 wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet("Main 1")
                chooseToken(CoinType.CC)
                sendingFundsToAnotherWallet(amountVal, mainWallet.secretKey, user1.oAuthSecret)
            }
            step("Check balance in OTF wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletAfterSendingDouble = balanceTokenUser.amount.toDouble()
            }
            step("Check transfers success") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble = heldInOffersUser.heldInOffers.toDouble()
                expectedIDTBalanceFromOTFWalletAfterSendingResult =
                    expectedIDTBalanceFromOTFWalletAfterSendingDouble - expectedССBalanceFromOTFWalletDouble
                val expectedIDTBalanceFromOTFWalletAfterSendingResultDouble: Double =
                    (expectedIDTBalanceFromOTFWalletAfterSendingResult * 100.0).roundToLong() / 100.0
                val amountDouble = amountVal.toDouble()
                MatcherAssert.assertThat(
                    expectedIDTBalanceFromOTFWalletAfterSendingResultDouble,
                    Matchers.equalTo(amountDouble)
                )
                MatcherAssert.assertThat(
                    expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble,
                    Matchers.equalTo(heldInOffers)
                )
            }
            step("The OTF wallet balance checking before RFQ") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedССBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
            }
        }

        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' RFQ is successfully created (start)") {
                checkSellRFQIsSuccessfullyCreatedStart(amountVal)
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering available balance") {
                Thread.sleep(5000)
                availableBalanceValueBeforeSellDouble = availableBalanceValue.amount.toDouble()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Sell' RFQ is successfully created (end)") {
                checkSellRFQIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
            }
        }
        utils.helpers.openPage<AtmWalletPage>(driver).logout()
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user3) }) {
            step("Confirm RFQ") {
                waitWalletsAreDisplayed()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Confirm RFQ") {
                confirmRFQ(amountVal, user3.otfWallet.secretKey, user3.oAuthSecret)
            }
        }
        utils.helpers.openPage<AtmWalletPage>(driver).logout()
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("Check 'Sell' RFQ is successfully accepted (start)") {
                waitWalletsAreDisplayed()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' RFQ is successfully accepted (start)") {
                checkSellRFQIsSuccessfullyAcceptedStart(user1.otfWallet.secretKey, user1.oAuthSecret)
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after RFQ offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceAfterSellDoubleRFQ = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceRFQAfterSellDouble = heldInOffersUser.heldInOffers.toDouble()
                oTFWalletCCTokenBalanceAfterSellResultRFQ = expectedССBalanceFromOTFWalletDouble - oTFWalletCCTokenBalanceAfterSellDoubleRFQ
                val amountDouble = amountVal.toDouble()
                val oTFWalletCCTokenBalanceAfterSellResultDoubleRFQ: Double =
                    (oTFWalletCCTokenBalanceAfterSellResultRFQ * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceAfterSellResultDoubleRFQ,
                    Matchers.equalTo(amountDouble)
                )
                oTFWalletCCTokenBalanceRFQAfterSellResult =
                    oTFWalletCCTokenBalanceRFQAfterSellDouble - expectedCCBalanceFromOTFWalletOffer
                val oTFWalletCCTokenBalanceRFQAfterSellResultDouble: Double =
                    (oTFWalletCCTokenBalanceRFQAfterSellResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceRFQAfterSellResultDouble,
                    Matchers.equalTo(amountDouble)
                )
            }
        }
    }
}