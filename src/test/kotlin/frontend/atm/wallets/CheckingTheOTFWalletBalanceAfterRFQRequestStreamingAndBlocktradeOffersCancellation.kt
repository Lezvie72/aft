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
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedCCBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
            }

        }
    }
}