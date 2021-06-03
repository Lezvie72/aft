package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.JavascriptExecutor
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Streaming"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Offer Acceptance Streaming")
class OfferAcceptanceStreaming : BaseTest() {

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-623")
    @Test
    @DisplayName("Steaming. Аccepting sell offer")
    fun streamingAcceptingSellOffer() {
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}") //1.97179569
        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.VT
        val amount = OtfAmounts.AMOUNT_10.amount

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
            e {
                click(overview)
            }
            assert {
                elementPresented(overviewBreadcrumbs)
                elementPresented(tradeHistory)
                elementPresented(myOffers)
            }
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$quoteAsset/$baseAsset",
                "$amount $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user1
            )
        }
        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            e {
                click(overview)
            }
            findAndOpenOfferInOverview(unitPrice)
            assert {
                elementWithTextPresentedIgnoreCase("Base asset amount")
                elementWithTextPresentedIgnoreCase("Price per unit")
                elementWithTextPresentedIgnoreCase("Expiration date")
                elementWithTextPresentedIgnoreCase("Counterparty")
                elementWithTextPresentedIgnoreCase("Asset pair")
                elementWithTextPresentedIgnoreCase("Total Amount")
                elementWithTextPresentedIgnoreCase("Fee option")
                elementWithTextPresentedIgnoreCase("Transaction fee")
                elementWithTextPresentedIgnoreCase("AMOUNT TO RECEIVE")
                elementWithTextPresentedIgnoreCase("AMOUNT TO SEND")
                elementPresented(confirmTradeButton)
                elementPresented(cancelAcceptOffer)
            }
            e {
                click(confirmTradeButton)
                signAndSubmitMessage(user, user.otfWallet.secretKey)
            }
        }
    }

//    @Disabled("ATMCH-4009")
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-614")
    @Test
    @DisplayName("Streaming. Accepting buy offer")
    fun streamingAcceptingBuyOffer() {
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}") //1.97179569
        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.VT
        val amount = OtfAmounts.AMOUNT_10.amount

        val taker = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val maker = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

    prerequisite {
        prerequisitesStreaming(
            baseAsset.toString(), quoteAsset.toString(), "1",
            "1", "1",
            "FIXED", "FIXED",
            true
        )
    }

        with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amount $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                maker
            )
            openPage<AtmProfilePage>(driver).logout()
        }

        val firstWallet = taker.otfWallet

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(taker) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val fee = with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
            e {
                click(overview)
            }
            findAndOpenOfferInOverview(unitPrice)
            e {
                val fee = wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                    offerFee.amount
                }
                click(confirmTradeButton)
                (this@OfferAcceptanceStreaming.driver as JavascriptExecutor).executeScript("document.body.style.zoom = '100%';")
                signAndSubmitMessage(taker, taker.otfWallet.secretKey)
                fee
            }
        }
        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(taker) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val baseExpected = baseBefore + BigDecimal.TEN * unitPrice - fee
        val quoteExpected = quoteBefore - BigDecimal.TEN

        assertThat(
            "Expected base balance: $baseExpected, was: $baseAfter",
            baseAfter,
            closeTo(baseExpected, BigDecimal("0.01"))
        )
        assertThat(
            "Expected quote balance: $quoteExpected, was: $quoteAfter",
            quoteAfter,
            closeTo(quoteExpected, BigDecimal("0.01"))
        )
    }

}