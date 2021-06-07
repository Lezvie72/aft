package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmStreamingPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.step

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Wallet/Checking the OTF wallet balance")
class CheckingTheOTFWalletBalanceAfterRFQRequestStreamingAndBlocktradeOffersCancellation : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_2FA_WITH_WALLET_MTEST01),
        ResourceLock(Constants.ATM_USER_2FA_WITHOUT_WALLET_MTEST02),
        ResourceLock(Constants.ATM_USER_2FA_WITH_WALLET_MTEST03)
    )
    @TmsLink("ATMCH-2512")
    @Test
    @DisplayName("Checking the OTF wallet balance after RFQ request, Streaming and Blocktrade offers cancellation")
    fun checkingTheOTFWalletBalanceAfterRFQRequestStreamingAndBlocktradeOffersCancellation() {
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
        var expectedCCBalanceFromOTFWalletDouble: Double = 0.0
        var availableBalanceValueBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellDoubleRFQ: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellResult: Double = 0.0
        var expectedCCBalanceFromOTFWalletOffer: Double = 0.0
        var expectedIDTBalanceFromOTFWallet: String = ""
        var expectedIDTBalanceFromOTFWalletOffer: String = ""
        var expectedIDTBalanceFromOTFWalletAfterSending: String = ""
        var expectedIDTBalanceFromOTFWalletOfferAfterSending: String = ""
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
        val amount = "0.01"
        with(utils.helpers.openPage<AtmStreamingPage>(driver) { submit(user1) } ) {
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
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedCCBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
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
                amountToSendValueBeforeSellDouble = amountToSendValue.amount.toDouble()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Sell' offer is successfully created (end)") {
                checkSellOfferIsSuccessfullyCreatedEnd(user3.otfWallet.secretKey, user3.oAuthSecret)
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Confirm RFQ") {
                confirmRFQ(amountVal, user3.otfWallet.secretKey, user3.oAuthSecret)
            }
        }

    }
}