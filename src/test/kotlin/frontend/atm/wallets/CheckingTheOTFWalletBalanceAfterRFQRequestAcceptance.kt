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
@Story("Wallet/Checking the OTF wallet balance after RFQ request acceptance")

class CheckingTheOTFWalletBalanceAfterRFQRequestAcceptance : BaseTest() {

    @ResourceLock(Constants.ATM_USER_2FA_WITH_WALLET_MTEST03)
    @TmsLink("ATMCH-2514")
    @Test
    @DisplayName("Checking the OTF wallet balance after RFQ request acceptance")

    fun checkingTheOTFWalletBalanceAfterRFQRequestAcceptance() {

        val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
        val user3 = Users.ATM_USER_2FA_WITH_WALLET_MTEST03
        val amount = "1"
        val heldInOffers = "0.00000000"
        val mainWallet = user1.mainWallet
        val otfWallet = user1.otfWallet
        var expectedCCBalanceFromOTFWalletDouble: Double = 0.0
        var expectedVTBalanceFromOTFWalletDouble: Double = 0.0
        var availableBalanceValueBeforeSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceAfterSellDoubleRFQ: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellDouble: Double = 0.0
        var oTFWalletCCTokenBalanceRFQAfterSellResult: Double = 0.0
        var expectedCCBalanceFromOTFWalletOffer: Double = 0.0
        var expectedVTBalanceFromOTFWalletOffer: Double = 0.0
        var amountToSendValueDouble: Double = 0.0
        var expectedCCBalanceFromOTFWalletDoubleAfter: Double = 0.0
        var expectedCCBalanceFromOTFWalletOfferAfter: Double = 0.0
        var expectedVTBalanceFromOTFWalletDoubleAfter: Double = 0.0
        var expectedVTBalanceFromOTFWalletOfferAfter: Double = 0.0
        var expectedVTBalanceFromOTFWalletDoubleAfterResultUser3: Double = 0.0
        var expectedVTBalanceFromOTFWalletOfferAfterResultUser3: Double = 0.0
        var amountToSendValueDoubleUser1: Double = 0.0
        var expectedCCBalanceFromOTFWalletDoubleAfterUser1: Double = 0.0
        var expectedCCBalanceFromOTFWalletOfferAfterUser1: Double = 0.0
        var expectedVTBalanceFromOTFWalletDoubleAfterUser1: Double = 0.0
        var expectedVTBalanceFromOTFWalletOfferAfterUser1: Double = 0.0
        var expectedVTBalanceFromOTFWalletDoubleAfterResultUser1: Double = 0.0
        var expectedCCBalanceFromOTFWalletOfferAfterResultUser1: Double = 0.0
        var expectedCCBalanceFromOTFWalletDoubleUser3: Double = 0.0
        var expectedCCBalanceFromOTFWalletOfferUser3: Double = 0.0
        var expectedVTBalanceFromOTFWalletDoubleUser3: Double = 0.0
        var expectedVTBalanceFromOTFWalletOfferUser3: Double = 0.0

        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            step("The OTF wallet balance remembering") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedCCBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
                driver.navigate().back()
                chooseToken(CoinType.VT)
                expectedVTBalanceFromOTFWalletDouble = balanceTokenUser.amount.toDouble()
                expectedVTBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toDouble()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' RFQ is successfully created (start)") {
                checkSellRFQIsSuccessfullyCreatedStart(amount)
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Sell' RFQ is successfully created (end)") {
                checkSellRFQIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
            }
        }
        utils.helpers.openPage<AtmWalletPage>(driver).logout()
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user3) }) {
            step("The OTF wallet balance remembering for User3") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedCCBalanceFromOTFWalletDoubleUser3 = balanceTokenUser.amount.toDouble()
                expectedCCBalanceFromOTFWalletOfferUser3 = heldInOffersUser.heldInOffers.toDouble()
                driver.navigate().back()
                chooseToken(CoinType.VT)
                expectedVTBalanceFromOTFWalletDoubleUser3 = balanceTokenUser.amount.toDouble()
                expectedVTBalanceFromOTFWalletOfferUser3 = heldInOffersUser.heldInOffers.toDouble()
            }
            with(AtmStreamingPage(driver)) {
                step("Confirm RFQ with values remembering (start)") {
                    confirmRFQWithValuesRememberingStart(amount)
                }
            }
            with(AtmWalletPage(driver)) {
                step("Values remembering amount to send value") {
                    Thread.sleep(5000)
                    amountToSendValueDouble = amountToSendValue.amount.toDouble()
                }
            }
            with(AtmStreamingPage(driver)) {
                step("Confirm RFQ with values remembering (end)") {
                    confirmRFQWithValuesRememberingEnd(user3.otfWallet.secretKey, user3.oAuthSecret)
                }
            }
            with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                step("The OTF wallet balance checking after RFQ selling (user3)") {
                    waitWalletsAreDisplayed()
                    chooseWallet(otfWallet.name)
                    chooseToken(CoinType.CC)
                    expectedCCBalanceFromOTFWalletDoubleAfter = balanceTokenUser.amount.toDouble()
                    expectedCCBalanceFromOTFWalletOfferAfter = heldInOffersUser.heldInOffers.toDouble()
                    driver.navigate().back()
                    chooseToken(CoinType.VT)
                    expectedVTBalanceFromOTFWalletDoubleAfter = balanceTokenUser.amount.toDouble()
                    expectedVTBalanceFromOTFWalletOfferAfter = heldInOffersUser.heldInOffers.toDouble()
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletDoubleAfter,
                        Matchers.equalTo(expectedVTBalanceFromOTFWalletDoubleUser3)
                    )
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletOfferAfter,
                        Matchers.equalTo(expectedVTBalanceFromOTFWalletOfferUser3)
                    )
                    expectedVTBalanceFromOTFWalletDoubleAfterResultUser3 =
                        expectedCCBalanceFromOTFWalletDoubleUser3 - expectedCCBalanceFromOTFWalletDoubleAfter
                    val amountDouble = amount.toDouble()
                    val expectedVTBalanceFromOTFWalletDoubleAfterResultMath: Double =
                        (expectedVTBalanceFromOTFWalletDoubleAfterResultUser3 * 100.0).roundToLong() / 100.0
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletDoubleAfterResultMath,
                        Matchers.equalTo(amountDouble)
                    )
                    expectedVTBalanceFromOTFWalletOfferAfterResultUser3 =
                        expectedCCBalanceFromOTFWalletOfferAfter - expectedCCBalanceFromOTFWalletOfferUser3
                    val expectedVTBalanceFromOTFWalletOfferAfterResultMath: Double =
                        (expectedVTBalanceFromOTFWalletOfferAfterResultUser3 * 100.0).roundToLong() / 100.0
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletOfferAfterResultMath,
                        Matchers.equalTo(amountDouble)
                    )
                }
            }

            utils.helpers.openPage<AtmWalletPage>(driver).logout()
            with(utils.helpers.openPage<AtmStreamingPage>(driver) { submit(user1) }) {
                step("Check 'Sell' RFQ is successfully accepted (start)") {
                    checkSellRFQIsSuccessfullyAcceptedStart()
                }
            }
            with(AtmWalletPage(driver)) {
                step("Values remembering amount to send value (user1)") {
                    Thread.sleep(5000)
                    amountToSendValueDoubleUser1 = amountToSendValue.amount.toDouble()
                }
            }
            with(AtmStreamingPage(driver)) {
                step("Check 'Sell' RFQ is successfully accepted (end)") {
                    checkSellRFQIsSuccessfullyAcceptedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
                }
            }
            with(utils.helpers.openPage<AtmWalletPage>(driver)) {
                step("The OTF wallet balance checking after RFQ selling (user1)") {
                    waitWalletsAreDisplayed()
                    chooseWallet(otfWallet.name)
                    chooseToken(CoinType.CC)
                    expectedCCBalanceFromOTFWalletDoubleAfterUser1 = balanceTokenUser.amount.toDouble()
                    expectedCCBalanceFromOTFWalletOfferAfterUser1 = heldInOffersUser.heldInOffers.toDouble()
                    driver.navigate().back()
                    chooseToken(CoinType.VT)
                    expectedVTBalanceFromOTFWalletDoubleAfterUser1 = balanceTokenUser.amount.toDouble()
                    expectedVTBalanceFromOTFWalletOfferAfterUser1 = heldInOffersUser.heldInOffers.toDouble()
                    MatcherAssert.assertThat(
                        expectedCCBalanceFromOTFWalletOfferAfterUser1,
                        Matchers.equalTo(expectedCCBalanceFromOTFWalletOffer)
                    )
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletOfferAfterUser1,
                        Matchers.equalTo(expectedVTBalanceFromOTFWalletOffer)
                    )
                    expectedVTBalanceFromOTFWalletDoubleAfterResultUser1 =
                        expectedVTBalanceFromOTFWalletDouble - expectedVTBalanceFromOTFWalletDoubleAfterUser1
                    val amountDouble = amount.toDouble()
                    val expectedVTBalanceFromOTFWalletDoubleAfterResultMathUser1: Double =
                        (expectedVTBalanceFromOTFWalletDoubleAfterResultUser1 * 100.0).roundToLong() / 100.0
                    MatcherAssert.assertThat(
                        expectedVTBalanceFromOTFWalletDoubleAfterResultMathUser1,
                        Matchers.equalTo(amountToSendValueDoubleUser1)
                    )
                    expectedCCBalanceFromOTFWalletOfferAfterResultUser1 =
                        expectedCCBalanceFromOTFWalletDoubleAfterUser1 - expectedCCBalanceFromOTFWalletDouble
                    val expectedCCBalanceFromOTFWalletOfferAfterResultMathUser1: Double =
                        (expectedCCBalanceFromOTFWalletOfferAfterResultUser1 * 100.0).roundToLong() / 100.0
                    MatcherAssert.assertThat(
                        expectedCCBalanceFromOTFWalletOfferAfterResultMathUser1,
                        Matchers.equalTo(amountDouble)
                    )
                }
            }
        }
    }
}