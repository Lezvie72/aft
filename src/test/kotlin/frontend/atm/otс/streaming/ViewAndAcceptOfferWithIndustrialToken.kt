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
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag("OTC"), Tag("Streaming"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("View and accept offer with Industrial token")
class ViewAndAcceptOfferWithIndustrialToken : BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val industrialUserOne2FA = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountCount = OtfAmounts.AMOUNT_10.amount
    private val maturityDateInnerDate = "22 September 2020"
    private val invalid2FaKey = "123456"
    private val invalidPrivateKey = "12345678bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"
    private val wallet = "OTF 1"

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3764")
    @Test
    @DisplayName("Accept sell offer. Wrong 2FA")
    fun acceptSellOfferWrong2Fa() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""


        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                click(acceptOffer)
                alert { checkErrorAlert() }
                wait { untilPresented(confirmTradeLabel) }
                sendKeys(privateKey, industrialUserOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                assert {
                    elementEnabled(confirmationLabel)
                }
                // not valid secret key
                enterConfirmationCode(invalid2FaKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("Error message about invalid or wrong code should have but did not appear") {
                        untilPresented(invalidOrWrongCode)
                    }
                }
                assertThat(
                    "Confirm button is disabled",
                    confirmButtonInDialogWindow.getAttribute("disabled").contains("true")
                )
                click(cancelOffer)
                isOfferExist(unitPrice, myOffersList)
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalance should be equal $afterBalance",
            initBalance.equals(afterBalance)
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3765")
    @Test
    @DisplayName("Accept buy offer. Invalid secret key")
    fun acceptBuyOfferInvalidSecretKey() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserTwo) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                click(acceptOffer)
                alert { checkErrorAlert() }
                wait { untilPresented(confirmTradeLabel) }
                sendKeys(privateKey, invalidPrivateKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("Error message about invalid or wrong code should have but did not appear") {
                        untilPresented(invalidOrWrongCode)
                    }
                }
                assertThat(
                    "Confirm button is disabled",
                    confirmButtonInDialogWindow.getAttribute("disabled").contains("true")
                )
                click(cancelOffer)
                assertThat(
                    "Offer with unit price $unitPrice should be exist",
                    isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalance should be equal $afterBalance",
            initBalance.equals(afterBalance)
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3766")
    @Test
    @DisplayName("Accept buy offer. Wrong 2FA")
    fun acceptBuyOfferWrong2Fa() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""


        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(baseAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                click(acceptOffer)
                alert { checkErrorAlert() }
                sendKeys(privateKey, industrialUserOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                assert {
                    elementEnabled(confirmationLabel)
                }
                // not valid secret key
                enterConfirmationCode(invalid2FaKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("Error message about invalid or wrong code should have but did not appear") {
                        untilPresented(invalidOrWrongCode)
                    }
                }
                assertThat(
                    "Confirm button is disabled",
                    confirmButtonInDialogWindow.getAttribute("disabled").contains("true")
                )
                click(cancelOffer)
                assertThat(
                    "Offer with unit price $unitPrice should be exist",
                    isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalance = getBalanceFromWalletForToken(baseAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalance should be equal $afterBalance",
            initBalance.equals(afterBalance)
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3704")
    @Test
    @DisplayName("View offers in Overview. User is IT client")
    fun viewOffersInOverviewUserIsItClient() {
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPriceSell.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserTwo) }) {
            e {
                click(overview)
                wait {
                    untilPresented(overviewOffersList)
                }
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                select(tradingPair, "$quoteAsset/$baseAsset")
                softAssert { elementPresented(buyLabel) }
                softAssert { elementPresented(sellLabel) }
                softAssert { elementPresented(baseAssetAmountLabel) }
                softAssert { elementPresented(quoteAssetAmountLabel) }
                softAssert { elementPresented(unitPriceLabel) }
                softAssert { elementPresented(maturityDateLabel) }
                softAssert { elementPresented(baseMaturityDate) }
                softAssert { elementPresented(counterpartyLabel) }

                findAndOpenOfferInOverview(unitPriceBuy)
                wait { untilPresented(offerDetailsLabel) }

                softAssert { elementContainingTextPresented("COUNTERPARTY") }
                softAssert { elementContainingTextPresented("EXPIRATION DATE") }
                softAssert { elementContainingTextPresented("ASSET PAIR") }
                softAssert { elementContainingTextPresented("BASE ASSET AMOUNT") }
                softAssert { elementContainingTextPresented("MATURITY DATE") }
                softAssert { elementContainingTextPresented("PRICE PER UNIT") }
                softAssert { elementContainingTextPresented("TOTAL AMOUNT") }
                softAssert { elementContainingTextPresented("FEE OPTION") }
                softAssert { elementContainingTextPresented("TRANSACTION FEE") }
                softAssert { elementContainingTextPresented("DIRECTION") }
                softAssert { elementContainingTextPresented("AMOUNT TO RECEIVE") }
                softAssert { elementContainingTextPresented("AMOUNT TO SEND") }
                softAssert { elementContainingTextPresented("MATURITY DATE") }
                softAssert { elementContainingTextPresented("CANCEL") }
                softAssert { elementContainingTextPresented("ACCEPT OFFER") }
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3705")
    @Test
    @DisplayName("Accept buy offer. User without 2FA")
    fun acceptBuyOfferUserWithout2Fa() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserTwo) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                acceptOffer(industrialUserTwo)
            }
        }

        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet wasn't found"
                )
                afterBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        assertThat(
            "Balance should be $initBalance - $amountCount",
            afterBalance.toFloat(),
            Matchers.equalTo(initBalance.toFloat() - amountCount.toFloat())
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3709")
    @Test
    @DisplayName("View offers in Overview. User isn't IT client")
    fun viewOffersInOverviewUserIsntItClient() {
        with(openPage<AtmStreamingPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                click(overview)
                wait {
                    untilPresented(overviewOffersList)
                }
                assertThat(
                    "Non industrial user should not see ${quoteAsset.tokenSymbol} in pair",
                    !selectAssetPair.getHeadersAsString(page).contains(quoteAsset.tokenSymbol)
                )
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_OTF), ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3711")
    @Test
    @DisplayName("Accept buy offer. User with 2FA")
    fun acceptBuyOfferUserWith2Fa() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""


        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                acceptOffer(industrialUserOne2FA)
                assertThat(
                    "Offer with unit price $unitPrice should not be exist",
                    !isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalance should be decreased by $amountCount",
            afterBalance.toFloat(), Matchers.equalTo(initBalance.toFloat() - amountCount.toFloat())
        )
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-3712")
    @Test
    @DisplayName("Streaming IT. Accept sell offer. User with 2FA")
    fun streamingItAcceptSellOfferUserWith2Fa() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalanceBase = ""
        var afterBalanceBase = ""
        var initBalanceQuote = ""
        var afterBalanceQuote = ""
        var feeSizeAccept = ""


        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalanceBase = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                initBalanceQuote = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                feeSizeAccept = offerFee.amount.toString()
                acceptOffer(industrialUserOne2FA)
                assertThat(
                    "Offer with unit price $unitPrice should not be exist",
                    !isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalanceBase = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                afterBalanceQuote = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalanceBase should be decreased by $amountCount * $unitPrice + $feeSizeAccept",
            afterBalanceBase.toFloat(),
            Matchers.equalTo(initBalanceBase.toFloat() - amountCount.toFloat() * unitPrice.toFloat() - feeSizeAccept.toFloat())
        )
        assertThat(
            "Init balance $quoteAsset = $initBalanceQuote should be increased by $amountCount",
            afterBalanceQuote.toFloat(), Matchers.equalTo(initBalanceQuote.toFloat() + amountCount.toFloat())
        )
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-3713")
    @Test
    @DisplayName("Streaming IT. Accept sell offer. User without 2FA")
    fun streamingItAcceptSellOfferUserWithout2Fa() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalanceBase = ""
        var afterBalanceBase = ""
        var initBalanceQuote = ""
        var afterBalanceQuote = ""
        var feeSizeAccept = ""


        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserTwo) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalanceBase = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                initBalanceQuote = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    untilPresented(offerDetailsLabel)
                }
                feeSizeAccept = offerFee.amount.toString()
                acceptOffer(industrialUserTwo)
                assertThat(
                    "Offer with unit price $unitPrice should not be exist",
                    !isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalanceBase = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                afterBalanceQuote = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalanceBase should be decreased by $amountCount * $unitPrice + $feeSizeAccept",
            afterBalanceBase.toFloat(),
            Matchers.equalTo(initBalanceBase.toFloat() - amountCount.toFloat() * unitPrice.toFloat() - feeSizeAccept.toFloat())
        )
        assertThat(
            "Init balance $quoteAsset = $initBalanceQuote should be increased by $amountCount",
            afterBalanceQuote.toFloat(), Matchers.equalTo(initBalanceQuote.toFloat() + amountCount.toFloat())
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-3716")
    @Test
    @DisplayName("Streaming IT. Accept sell offer. Invalid secret key")
    fun acceptSellOfferInvalidSecretKey() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserTwo) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                initBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                click(overview)
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPrice)
                wait {
                    until("accept offer button should be be enabled", 20L) {
                        acceptOffer.isEnabled
                    }
                }
                click(acceptOffer)
                alert { checkErrorAlert() }
                wait { untilPresented(confirmTradeLabel) }
                sendKeys(privateKey, invalidPrivateKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("Error message about invalid or wrong code should have but did not appear") {
                        untilPresented(invalidOrWrongCode)
                    }
                }
                assertThat(
                    "Confirm button is disabled",
                    confirmButtonInDialogWindow.getAttribute("disabled").contains("true")
                )
                click(cancelOffer)
                assertThat(
                    "Offer with unit price $unitPrice should be exist",
                    isOfferExist(unitPrice, overviewOffersList)
                )
            }
        }
        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet not found"
                )
                afterBalance = getBalanceFromWalletForToken(quoteAsset, wallet)
            }
        }
        assertThat(
            "Init balance $baseAsset = $initBalance should be equal $afterBalance",
            initBalance.equals(afterBalance)
        )
    }
}