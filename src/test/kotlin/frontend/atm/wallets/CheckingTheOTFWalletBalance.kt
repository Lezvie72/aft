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
        var expectedIDTBalanceFromOTFWalletOffer: String
        var expectedIDTBalanceFromOTFWalletAfterSending: String = ""
        var expectedIDTBalanceFromOTFWalletOfferAfterSending: String
        var expectedIDTBalanceFromOTFWalletAfterSendingDouble: Double
        var expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble: String
        var expectedIDTBalanceFromOTFWalletDouble: Double
        var expectedIDTBalanceFromOTFWalletAfterSendingResult: Double
        var amountToReceiveValueBeforeSell: String = ""
        var amountToSendValueBeforeSell: String = ""
        var amountToSendValueBeforeSellDouble: Double
        var transactionFeeValueBeforeSell: String = ""
        var oTFWalletCCTokenBalanceBeforeSell: String = ""
        var oTFWalletCCTokenBalanceOfferBeforeSell: String = ""
        var oTFWalletCCTokenBalanceBeforeSellDouble: Double
        var oTFWalletCCTokenBalanceAfterSell: String = ""
        var oTFWalletCCTokenBalanceAfterSellDouble: Double
        var oTFWalletCCTokenBalanceOfferAfterSell: String = ""
        var oTFWalletCCTokenBalanceAfterSellResult: Double
        var oTFWalletCCTokenBalanceOfferAfterSellDouble: Double
        var oTFWalletCCTokenBalanceOfferBeforeSellDouble: Double
        var oTFWalletCCTokenBalanceOfferAfterSellResult: Double
        var amountToReceiveValueBeforeBuy: String = ""
        var amountToSendValueBeforeBuy: String = ""
        var amountToSendValueBeforeBuyDouble: Double
        var transactionFeeValueBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceOfferBeforeBuy: String = ""
        var oTFWalletCCTokenBalanceBeforeBuyDouble: Double
        var oTFWalletCCTokenBalanceAfterBuyDouble: Double
        var oTFWalletCCTokenBalanceAfterBuyResult: Double
        var oTFWalletCCTokenBalanceOfferAfterBuyDouble: Double
        var oTFWalletCCTokenBalanceOfferBeforeBuyDouble: Double
        var oTFWalletCCTokenBalanceOfferAfterBuyResult: Double
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
                expectedIDTBalanceFromOTFWallet = balanceTokenUser.amount.toString()
                expectedIDTBalanceFromOTFWalletOffer = heldInOffersUser.heldInOffers.toString()
            }
            step("Go into CC token in Main 1 wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet("Main 1")
                chooseToken(CoinType.CC)
                sendingFundsToAnotherWallet(amount, mainWallet.secretKey, user1.oAuthSecret)
//                e {
//                    step("Sending funds to another wallet") {
//                        click(move)
//                        sendKeys(amountTransfer, amount)
//                        click(submitButton)
//                        sendKeys(privateKey, mainWallet.secretKey)
//                        click(confirmPrivateKeyButton)
//                        submitConfirmationCode(user1.oAuthSecret)
//                        click(doneButton)
//                    }
//                }
            }
            step("Check balance in OTF wallet") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletAfterSending = balanceTokenUser.amount.toString()
                expectedIDTBalanceFromOTFWalletOfferAfterSending = heldInOffersUser.heldInOffers.toString()
            }
            step("Check transfers success") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                expectedIDTBalanceFromOTFWalletAfterSendingDouble =
                    expectedIDTBalanceFromOTFWalletAfterSending.toDouble()
                expectedIDTBalanceFromOTFWalletOfferAfterSendingDouble = heldInOffersUser.heldInOffers.toString()
                expectedIDTBalanceFromOTFWalletDouble = expectedIDTBalanceFromOTFWallet.toDouble()
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
//        }
//        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking before offer selling") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceBeforeSell = balanceTokenUser.amount.toString()
                oTFWalletCCTokenBalanceOfferBeforeSell = heldInOffersUser.heldInOffers.toString()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Sell' offer is successfully created (start)") {
                checkSellOfferIsSuccessfullyCreatedStart(amount)
//                e {
//                    click(createOffer)
//                    click(iWantToSellAsset)
//                    wait {
//                        until("Active element loaded", 15) {
//                            check {
//                                isElementPresented(assetPairSelector)
//                            }
//                        }
//                    }
//                    sendKeys(unitPrice, amount)
//                    sendKeys(expiresIn, "1")
//                }
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering amount and fee") {
                Thread.sleep(5000)
                amountToReceiveValueBeforeSell = amountToReceiveValue.amount.toString()
                amountToSendValueBeforeSell = amountToSendValue.amount.toString()
                transactionFeeValueBeforeSell = transactionFeeValue.amount.toString()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Sell' offer is successfully created (end)") {
                checkSellOfferIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
//                e {
//                    click(placeOffer)
//                    sendKeys(privateKey, user1.otfWallet.secretKey)
//                    click(confirmPrivateKeyButton)
//                    submitConfirmationCode(user1.oAuthSecret)
//                }
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceAfterSell = balanceTokenUser.amount.toString()
                oTFWalletCCTokenBalanceOfferAfterSell = heldInOffersUser.heldInOffers.toString()
                oTFWalletCCTokenBalanceAfterSellDouble = oTFWalletCCTokenBalanceAfterSell.toDouble()
                oTFWalletCCTokenBalanceBeforeSellDouble = oTFWalletCCTokenBalanceBeforeSell.toDouble()
                amountToSendValueBeforeSellDouble = amountToSendValueBeforeSell.toDouble()
                oTFWalletCCTokenBalanceOfferAfterSellDouble = oTFWalletCCTokenBalanceOfferAfterSell.toDouble()
                oTFWalletCCTokenBalanceOfferBeforeSellDouble = oTFWalletCCTokenBalanceOfferBeforeSell.toDouble()
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
//        }
//        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking before offer buying") {
                openPage<AtmWalletPage> {}
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceBeforeBuy = balanceTokenUser.amount.toString()
                oTFWalletCCTokenBalanceOfferBeforeBuy = heldInOffersUser.heldInOffers.toString()
            }
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            step("Check 'Buy' offer is successfully created (start)") {
                checkBuyOfferIsSuccessfullyCreatedStart(amount)
//                e {
//                    click(createOffer)
//                    click(iWantToBuyAsset)
//                    wait {
//                        until("Active element loaded", 15) {
//                            check {
//                                isElementPresented(assetPairSelector)
//                            }
//                        }
//                    }
//                    sendKeys(unitPrice, amount)
//                    sendKeys(expiresIn, "1")
//                }
            }
        }
        with(AtmWalletPage(driver)) {
            step("Values remembering amount and fee") {
                Thread.sleep(5000)
                amountToReceiveValueBeforeBuy = amountToReceiveValue.amount.toString()
                amountToSendValueBeforeBuy = amountToSendValue.amount.toString()
                transactionFeeValueBeforeBuy = transactionFeeValue.amount.toString()
            }
        }
        with(AtmStreamingPage(driver)) {
            step("Check 'Buy' offer is successfully created (end)") {
                checkBuyOfferIsSuccessfullyCreatedEnd(user1.otfWallet.secretKey, user1.oAuthSecret)
//                e {
//                    click(placeOffer)
//                    sendKeys(privateKey, user1.otfWallet.secretKey)
//                    click(confirmPrivateKeyButton)
//                    submitConfirmationCode(user1.oAuthSecret)
//                }
            }
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            step("The OTF wallet balance checking after offer selling") {
                waitWalletsAreDisplayed()
                chooseWallet(otfWallet.name)
                chooseToken(CoinType.CC)
                oTFWalletCCTokenBalanceAfterBuy = balanceTokenUser.amount.toString()
                oTFWalletCCTokenBalanceOfferAfterBuy = heldInOffersUser.heldInOffers.toString()
                oTFWalletCCTokenBalanceAfterBuyDouble = oTFWalletCCTokenBalanceAfterBuy.toDouble()
                oTFWalletCCTokenBalanceBeforeBuyDouble = oTFWalletCCTokenBalanceBeforeBuy.toDouble()
                amountToSendValueBeforeBuyDouble = amountToSendValueBeforeBuy.toDouble()
                oTFWalletCCTokenBalanceOfferAfterBuyDouble = oTFWalletCCTokenBalanceOfferAfterBuy.toDouble()
                oTFWalletCCTokenBalanceOfferBeforeBuyDouble = oTFWalletCCTokenBalanceOfferBeforeBuy.toDouble()
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

