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
        var expectedIDTBalanceFromOTFWalletAfterSendingDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletDouble: Double = 0.0
        var expectedIDTBalanceFromOTFWalletAfterSendingResult: Double = 0.0
        var amountToSendValueBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellResult: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterSellResult: Double = 0.0
        var amountToSendValueBeforeBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceBeforeBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterBuyResult: Double = 0.0
        var expectedIDTBalanceFromOTFWalletOfferDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferBeforeBuyDouble: Double = 0.0
        var oTFWalletCCTokenBalanceOfferAfterBuyResult: Double = 0.0
        val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
        val amount = "0.01"
        val mainWallet = user1.mainWallet
        val otfWallet = user1.otfWallet

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedIDTBalanceFromOTFWalletOfferDouble = heldInOffersUser.heldInOffers.toDouble()
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
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                Thread.sleep(5000)
                driver.navigate().refresh()
                expectedIDTBalanceFromOTFWalletAfterSendingDouble = balanceTokenUser.amount.toDouble()
                expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble = heldInOffersUser.heldInOffers.toDouble()
            }
            step("Check transfers success") {
                openPage<AtmWalletPage> {}
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
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
                    Matchers.equalTo(expectedIDTBalanceFromOTFWalletOfferDouble)
                )
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking before offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceBeforeSellDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferBeforeSellDouble = heldInOffersUser.heldInOffers.toDouble()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' offer is successfully created (start)") {
                Thread.sleep(5000)
                driver.navigate().refresh()
                checkSellOfferIsSuccessfullyCreatedStart(amount)
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering amount and fee") {
                Thread.sleep(5000)
                amountToSendValueBeforeSellDouble = amountToSendValue.amount.toDouble()
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
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking before offer buying") {
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
                amountToSendValueBeforeBuyDouble = amountToSendValue.amount.toDouble()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Buy' offer is successfully created (end)") {
                checkBuyOfferIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
                Thread.sleep(5000)
                driver.navigate().refresh()
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after offer buying") {
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
//                Thread.sleep(5000)
//                driver.navigate().refresh()
                oTFWalletCCTokenBalanceAfterBuyDouble = balanceTokenUser.amount.toDouble()
                oTFWalletCCTokenBalanceOfferAfterBuyDouble = heldInOffersUser.heldInOffers.toDouble()
                oTFWalletCCTokenBalanceAfterBuyResult = oTFWalletCCTokenBalanceAfterBuyDouble - oTFWalletCCTokenBalanceBeforeBuyDouble
                val oTFWalletCCTokenBalanceAfterBuyResultDouble: Double =
                    (oTFWalletCCTokenBalanceAfterBuyResult * 100.0).roundToLong() / 100.0
                MatcherAssert.assertThat(
                    oTFWalletCCTokenBalanceAfterBuyResultDouble,
                    Matchers.equalTo(amountToSendValueBeforeBuyDouble)
                )
                oTFWalletCCTokenBalanceOfferAfterBuyResult = oTFWalletCCTokenBalanceOfferBeforeBuyDouble - oTFWalletCCTokenBalanceOfferAfterBuyDouble
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
