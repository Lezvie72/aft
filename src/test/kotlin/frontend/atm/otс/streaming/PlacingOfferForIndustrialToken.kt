package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.*
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
@Story("Placing offer for industrial token.")
class PlacingOfferForIndustrialToken : BaseTest() {

    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val industrialUserOne2FA = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountSell = OtfAmounts.AMOUNT_10.amount
    private val amountBuy = OtfAmounts.AMOUNT_10.amount
    private val unitPriceOffer = BigDecimal("1")
    private val maturityDate = "22 September 2020"
    private val wallet = "OTF 1"
    private val invalid2FaKey = "123456"
    private val invalidPrivateKey = "12345678bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3695")
    @Test
    @DisplayName("IT client create offer for buy.")
    fun itClientCreateOfferForBuy() {
        var initBalance = ""
        var afterBalance = ""
        var fee = BigDecimal("0")

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserTwo) }) {
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
                fee = createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserTwo,
                    maturityDate
                )
            }
        }

        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet wasn't found"
                )
                afterBalance = getBalanceFromWalletForToken(baseAsset, wallet)
            }
        }

        assertThat(
            "Balance should be $initBalance - $amountBuy - $fee",
            afterBalance.toFloat(),
            Matchers.equalTo(initBalance.toFloat() - amountBuy.toFloat() - fee.toFloat())
        )
    }


    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3683")
    @Test
    @DisplayName("IT client create offer for sell.")
    fun itClientCreateOfferForSell() {
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne) }) {
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
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountSell $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDate
                )
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
            "Balance should be $initBalance - $amountSell",
            afterBalance.toFloat(),
            Matchers.equalTo(initBalance.toFloat() - amountSell.toFloat())
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3770")
    @Test
    @DisplayName("IT client create offer for buy. Wrong 2FA")
    fun iTclientCreateOfferForBuyWrong2Fa() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne2FA, maturityDate, manualCompleted = true
                )
                click(goodTillCancelled)
                click(placeOffer)
                sendKeys(privateKey, industrialUserOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("confirmation window presetned") {
                        untilPresented(confirmationLabel)
                    }
                }
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
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3771")
    @Test
    @DisplayName("IT client create offer for sell. Wrong 2FA")
    fun iTclientCreateOfferForSellWrong2Fa() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne2FA) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne2FA, maturityDate, manualCompleted = true
                )
                click(goodTillCancelled)
                click(placeOffer)
                wait { untilPresented(manualSignatureLabel) }
                sendKeys(privateKey, industrialUserOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("confirmation window presetned") {
                        untilPresented(confirmationLabel)
                    }
                }
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
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3776")
    @Test
    @DisplayName("IT client create offer for sell. Wrong key")
    fun iTclientCreateOfferForSellWrongKey() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne) }) {
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
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDate, manualCompleted = true
                )
                click(goodTillCancelled)
                click(placeOffer)
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
            "Init balance $quoteAsset = $initBalance should be equal $afterBalance",
            initBalance.equals(afterBalance)
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3777")
    @Test
    @DisplayName("IT client create offer for buy. Wrong key")
    fun iTclientCreateOfferForBuyWrongKey() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance = ""

        with(openPage<AtmWalletPage>(driver) { submit(industrialUserOne) }) {
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
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDate, manualCompleted = true
                )
                click(goodTillCancelled)
                click(placeOffer)
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

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3767")
    @Test
    @DisplayName("IT client create offer for buy. User has 2FA")
    fun itClientCreateOfferForBuyUserHas2Fa() {
        var initBalance = ""
        var afterBalance = ""
        var fee = BigDecimal("0")

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
                fee = createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne2FA,
                    maturityDate
                )
            }
        }

        with(openPage<AtmWalletPage>(driver)) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet wasn't found"
                )
                afterBalance = getBalanceFromWalletForToken(baseAsset, wallet)
            }
        }

        assertThat(
            "Balance should be $initBalance - $amountBuy - $fee",
            afterBalance.toFloat(),
            Matchers.equalTo(initBalance.toFloat() - amountBuy.toFloat() - fee.toFloat())
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3768")
    @Test
    @DisplayName("IT client create offer for sell. User has 2FA")
    fun itClientCreateOfferForSellUserHas2Fa() {
        var initBalance = ""
        var afterBalance = ""
        var fee = BigDecimal("0")

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
                fee = createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne2FA,
                    maturityDate
                )
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

        // correct if fee not in QuoteAsset
        assertThat(
            "Balance should be $initBalance - $amountBuy",
            afterBalance.toFloat(),
            Matchers.equalTo(initBalance.toFloat() - amountBuy.toFloat())
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3691")
    @Test
    @DisplayName("Check create IT offer if user isn't IT client")
    fun checkCreateItOfferIfUserIsntItClient() {
        with(openPage<AtmStreamingPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                click(createOffer)
                wait {
                    untilPresented(newOfferLabel)
                }
                assertThat(
                    "New offer should not exist ${quoteAsset.tokenSymbol} in pair",
                    !selectAssetPair.getHeadersAsString(page).contains(quoteAsset.tokenSymbol)
                )
            }
        }
    }
}