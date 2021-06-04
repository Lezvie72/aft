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
class CheckingTheOTFWalletBalance : BaseTest() {

    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-2501")
    @Test
    @DisplayName("Checking the OTF wallet balance (held by Streaming offers)")
    fun checkingTheOTFWalletBalance() {
        var expectedIDTBalanceFromOTFWallet: String = ""
        var expectedIDTBalanceFromOTFWalletOffer: String = ""
        var expectedIDTBalanceFromOTFWalletAfterSending: String = ""
        var expectedIDTBalanceFromOTFWalletOfferAfterSending: String = ""
        var expectedIDTBalanceFromOTFWalletAfterSendingDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble: String = ""
        var expectedIDTBalanceFromOTFWalletDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletAfterSendingResult: Double = 0.0
        var amountToReceiveValueBeforeSell: String = ""
        var amountToSendValueBeforeSell: String = ""
        var amountToSendValueBeforeSellDouble: Double = 0.0
        var transactionFeeValueBeforeSell: String = ""
        var oTFWalletCCTokenBalanceBeforeSell: String = ""
        var oTFWalletCCTokenBalanceOfferBeforeSell: String = ""
        var oTFWalletCCTokenBalanceBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSell: String = ""
        var oTFWalletCCTokenBalanceAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterSell: String = ""
        var oTFWalletCCTokenBalanceAfterSellResult: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterSellResult: Double = 0.0
        var amountToReceiveValueBeforeBuy: String = ""
        var amountToSendValueBeforeBuy: String = ""
        var amountToSendValueBeforeBuyDouble: Double = 0.0
        var transactionFeeValueBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceOfferBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceBeforeBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterBuyResult: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferBeforeBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterBuyResult: Double = 0.0
        var oTFWalletCCTokenBalanceAfterBuy: String = ""
        var oTFWalletCCTokenBalanceOfferAfterBuy: String = ""
        val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
        val user2 = Users.ATM_USER_2FA_WITHOUT_WALLET_MTEST02
        val amount = "0.01"
        val heldInOffers = "0E-8"
        val mainWallet = user1.mainWallet
        val otfWallet = user1.otfWallet

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
//                expectedIDTBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toString()
                expectedIDTBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
            }
            step("Go into CC token in Main 1 wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet("Main 1")
                chooseToken(CoinType.CC)
                sendingFundsToAnotherWallet(amount, mainWallet.secretKey, user1.oAuthSecret)
            }
            step("Check balance in OTF wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
//                expectedIDTBalanceFromOTFWalletOfferAfterSending = heldInOffersUser.heldInOffers.toString()
                expectedIDTBalanceFromOTFWalletAfterSendingDouble = balanceTokenUser.amount.toDouble()
            }
            step("Check transfers success") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble = heldInOffersUser.heldInOffers.toString()
                expectedIDTBalanceFromOTFWalletAfterSendingResult =
                    expectedIDTBalanceFromOTFWalletAfterSendingDouble - expectedIDTBalanceFromOTFWalletDouble
                val expectedIDTBalanceFromOTFWalletAfterSendingResultDouble: Double =
                    (expectedIDTBalanceFromOTFWalletAfterSendingResult * 100.0).roundToLong() / 100.0
                val amountDouble = amount.toDouble()
                MatcherAssert.assertThat(
                    expectedIDTBalanceFromOTFWalletAfterSendingResultDouble,
                    Matchers.equalTo(amountDouble)
                )
                MatcherAssert.assertThat(
                    expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble,
                    Matchers.equalTo(heldInOffers)
                )
            }
            step("The OTF wallet balance checking before offer selling") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceBeforeSellDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferBeforeSellDouble = heldInOffersUser.heldInOffers.toDouble()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' offer is successfully created (start)") {
                checkSellOfferIsSuccessfullyCreatedStart(amount)
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering amount and fee") {
                Thread.sleep(5000)
//                amountToReceiveValueBeforeSell = amountToReceiveValue.amount.toString()
                amountToSendValueBeforeSellDouble = amountToSendValue.amount.toDouble()
//                transactionFeeValueBeforeSell = transactionFeeValue.amount.toString()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Sell' offer is successfully created (end)") {
                checkSellOfferIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceAfterSellDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferAfterSellDouble = heldInOffersUser.heldInOffers.toDouble()
                oTFWalletCCTokenBalanceAfterSellResult = oTFWalletCCTokenBalanceBeforeSellDouble - oTFWalletCCTokenBalanceAfterSellDouble
                val oTFWalletCCTokenBalanceAfterSellResultDouble: Double =
                    (oTFWalletCCTokenBalanceAfterSellResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceAfterSellResultDouble,
                    Matchers.equalTo(amountToSendValueBeforeSellDouble)
                )
                oTFWalletCCTokenBalanceOfferAfterSellResult = oTFWalletCCTokenBalanceOfferAfterSellDouble - oTFWalletCCTokenBalanceOfferBeforeSellDouble
                val oTFWalletCCTokenBalanceOfferAfterSellResultDouble: Double =
                    (oTFWalletCCTokenBalanceOfferAfterSellResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceOfferAfterSellResultDouble,
                    Matchers.equalTo(amountToSendValueBeforeSellDouble)
                )
            }
            step("The OTF wallet balance checking before offer buying") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceBeforeBuyDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferBeforeBuyDouble = heldInOffersUser.heldInOffers.toDouble()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Buy' offer is successfully created (start)") {
                checkBuyOfferIsSuccessfullyCreatedStart(amount)
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering amount and fee") {
                Thread.sleep(5000)
//                amountToReceiveValueBeforeBuy = amountToReceiveValue.amount.toString()
                amountToSendValueBeforeBuyDouble = amountToSendValue.amount.toDouble()
//                transactionFeeValueBeforeBuy = transactionFeeValue.amount.toString()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Buy' offer is successfully created (end)") {
                checkBuyOfferIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceAfterBuyDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferAfterBuyDouble = heldInOffersUser.heldInOffers.toDouble()
                oTFWalletCCTokenBalanceAfterBuyResult = oTFWalletCCTokenBalanceBeforeBuyDouble - oTFWalletCCTokenBalanceAfterBuyDouble
                val oTFWalletCCTokenBalanceAfterBuyResultDouble: Double =
                    (oTFWalletCCTokenBalanceAfterBuyResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceAfterBuyResultDouble,
                    Matchers.equalTo(amountToSendValueBeforeBuyDouble)
                )
                oTFWalletCCTokenBalanceOfferAfterBuyResult = oTFWalletCCTokenBalanceOfferAfterBuyDouble - oTFWalletCCTokenBalanceOfferBeforeBuyDouble
                val oTFWalletCCTokenBalanceOfferAfterBuyResultDouble: Double =
                    (oTFWalletCCTokenBalanceOfferAfterBuyResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceOfferAfterBuyResultDouble,
                    Matchers.equalTo(amountToSendValueBeforeBuyDouble)
                )
            }
        }
    }
}

