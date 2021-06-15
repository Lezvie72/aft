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
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Offer Acceptance Streaming")
class OfferAcceptanceStreaming : BaseTest() {
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountCount = OtfAmounts.AMOUNT_10.amount
    private val userOne = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val userTwo = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val firstWallet = this.userOne.otfWallet

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-623")
    @Test
    @DisplayName("Steaming. Аccepting sell offer")
    fun streamingAcceptingSellOffer() {
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(this@OfferAcceptanceStreaming.userTwo) }) {
            e {
                click(overview)
            }
            assert {
                elementPresented(overviewBreadcrumbs)
                elementPresented(tradeHistory)
                elementPresented(myOffers)
            }
            openPage<AtmStreamingPage>(driver) { submit(this@OfferAcceptanceStreaming.userTwo) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                this@OfferAcceptanceStreaming.userTwo
            )
        }
        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmStreamingPage>(driver) { submit(this@OfferAcceptanceStreaming.userOne) }) {
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
                elementWithTextPresentedIgnoreCase("amountCount TO RECEIVE")
                elementWithTextPresentedIgnoreCase("amountCount TO SEND")
                elementPresented(confirmTradeButton)
                elementPresented(cancelAcceptOffer)
            }
            e {
                click(confirmTradeButton)
                signAndSubmitMessage(
                    this@OfferAcceptanceStreaming.userOne,
                    this@OfferAcceptanceStreaming.userOne.otfWallet.secretKey
                )
            }
        }
    }

//    @Disabled("ATMCH-4009")
    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-614")
    @Test
    @DisplayName("Streaming. Accepting buy offer")
    fun streamingAcceptingBuyOffer() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalanceBase = ""
        var afterBalanceBase = ""
        var initBalanceQuote = ""
        var afterBalanceQuote = ""
        var feeSizeAccept = ""


        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userTwo
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(userOne) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(firstWallet.name),
                    "Wallet with label $firstWallet not found"
                )
                initBalanceBase = getBalanceFromWalletForToken(baseAsset, firstWallet.name)
                click(walletsHeader)
                initBalanceQuote = getBalanceFromWalletForToken(quoteAsset, firstWallet.name)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }

                softAssert { elementContainingTextPresented("COUNTERPARTY") }
                softAssert { elementContainingTextPresented("EXPIRATION DATE") }
                softAssert { elementContainingTextPresented("ASSET PAIR") }
                softAssert { elementContainingTextPresented("BASE ASSET AMOUNT") }
                softAssert { elementContainingTextPresented("PRICE PER UNIT") }
                softAssert { elementContainingTextPresented("TOTAL AMOUNT") }
                softAssert { elementContainingTextPresented("FEE OPTION") }
                softAssert { elementContainingTextPresented("TRANSACTION FEE") }
                softAssert { elementContainingTextPresented("DIRECTION") }
                softAssert { elementContainingTextPresented("AMOUNT TO RECEIVE") }
                softAssert { elementContainingTextPresented("AMOUNT TO SEND") }
                softAssert { elementContainingTextPresented("CANCEL") }
                softAssert { elementContainingTextPresented("ACCEPT OFFER") }

                feeSizeAccept = offerFee.amount.toString()
                acceptOffer(userOne)
                assertThat(
                    "Offer with unit price $unitPrice should not be exist",
                    !isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(firstWallet.name),
                    "Wallet with label ${firstWallet.name} not found"
                )
                afterBalanceBase = getBalanceFromWalletForToken(baseAsset, firstWallet.name)
                click(walletsHeader)
                afterBalanceQuote = getBalanceFromWalletForToken(quoteAsset, firstWallet.name)
            }
        }

        assertThat(
            "Init balance $baseAsset = $initBalanceBase should be increased by $amountCount",
            afterBalanceBase.toFloat(),
            Matchers.equalTo(initBalanceBase.toFloat() + amountCount.toFloat() * unitPrice.toFloat() - feeSizeAccept.toFloat())
        )
        assertThat(
            "Init balance $quoteAsset = $initBalanceQuote should be decreased by $amountCount",
            afterBalanceQuote.toFloat(), Matchers.equalTo(initBalanceQuote.toFloat() - amountCount.toFloat())
        )
    }

}