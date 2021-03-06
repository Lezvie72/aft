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
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
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
@Story("Offer Placement Streaming")
class OfferPlacementStreaming : BaseTest() {

    private val industrialUserOne2FA = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val userOne = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
    private val userTwo = Users.ATM_USER_2FA_OTF_OPERATION
    private val userThree = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
    private val wallet = "OTF 1"
    private val amountCount = OtfAmounts.AMOUNT_10.amount
    private val firstWallet = userTwo.otfWallet
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val invalid2FaKey = "123456"
    private val invalidPrivateKey = "12345678bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"


    @ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF)
    @TmsLink("ATMCH-721")
    @Test
    @DisplayName("Streaming. Place offer. Cancel placing transfer")
    fun streamingPlaceOfferCancelPlacingTransfer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569

        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                click(createOffer)
                click(iWantToBuyAsset)
                select(selectAssetPair, "$quoteAsset/$baseAsset")
                selectAmount("$amountCount $quoteAsset")
                clear(unitPrice)
                sendKeys(unitPrice, unitPriceAmount.toString())
                click(cancelPlaceOffer)
                click(myOffer)
            }
            val cancelledOffer = myOffersList.find {
                it.unitPriceAmount == unitPriceAmount
            }
            assertThat(
                "Offer with amount $unitPriceAmount should have been be cancelled",
                cancelledOffer,
                nullValue()
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-713")
    @Test
    @DisplayName("Streaming. Place offer. Buy, temporary offer")
    fun streamingPlaceOfferBuyTemporaryOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(userTwo) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            val fee = createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY, userTwo
            )
            with(AtmStreamingPage(driver)) {
                findAndOpenOfferInOfferList(unitPriceAmount)
                wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                }
            }

            val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(userTwo) }) {
                val base = getBalance(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, firstWallet.name)
                base to quote
            }

            //fee расчитывается в baseAsset, при покупке baseBalance расчитывается сразу
            //Ожидаем снижение баланса на (amount * price + fee)
            val baseExpected = baseBefore - (BigDecimal.TEN * unitPriceAmount + fee)
            assertThat(
                "Expected base balance: $baseExpected, was: $baseAfter",
                baseAfter,
                closeTo(baseExpected, BigDecimal("0.01"))
            )
            assertThat(
                "Expected quote balance: $quoteBefore, was: $quoteAfter",
                quoteAfter,
                closeTo(quoteBefore, BigDecimal("0.01"))
            )

        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-712")
    @Test
    @DisplayName("Streaming. Place offer. Sell, good till cancel")
    fun streamingPlaceOfferSellGoodTillCancel() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(userThree) }) {
            createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, userThree
            )
        }

        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val quoteExpected = quoteBefore - (BigDecimal.TEN)

        assertThat(
            "Expected base balance: $baseBefore, was: $baseAfter",
            baseAfter,
            closeTo(baseBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected quote balance: $quoteExpected, was: $quoteAfter",
            quoteAfter,
            closeTo(quoteExpected, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF)
    @TmsLink("ATMCH-714")
    @Test
    @DisplayName("Streaming. Place offer. Sell, temporary offer")
    fun streamingPlaceOfferSellTemporaryOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY, user
            )
        }
        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }
        //fee в baseAsset, base asset не расчитывается до принятия оффера
        //Ожидаем base без изменений, quote снижен на amount
        val quoteExpected = quoteBefore - BigDecimal.TEN

        assertThat(
            "Expected base balance: $baseBefore, was: $baseAfter",
            baseAfter,
            closeTo(baseBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected quote balance: $quoteExpected, was: $quoteAfter",
            quoteAfter,
            closeTo(quoteExpected, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-719")
    @Test
    @DisplayName("Streaming. Place offer. Invalid signature")
    fun streamingPlaceOfferInvalidSignature() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val anotherUser = Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET
        val user = Users.ATM_USER_2FA_OTF_OPERATION

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            e {
                click(createOffer)
                click(iWantToBuyAsset)
                select(selectAssetPair, "$quoteAsset/$baseAsset")
                selectAmount(amountCount)
                clear(unitPrice)
                sendKeys(unitPrice, unitPriceAmount.toString())
                limitedTimeOffer()
                click(placeOffer)
                click(privateKey)
                sendKeys(privateKey, user.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                enterConfirmationCode(anotherUser.oAuthSecret)
                assert {
                    elementWithTextPresentedIgnoreCase("Wrong code")
                }
            }
        }

        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        assertThat(
            "Expected base balance: $baseAfter, was: $baseAfter",
            baseAfter,
            closeTo(baseBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected quote balance: $quoteBefore, was: $quoteAfter",
            quoteAfter,
            closeTo(quoteBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF)
    @TmsLink("ATMCH-613")
    @Test
    @DisplayName("Streaming. Placing buy offer")
    fun streamingPlacingBuyOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY,
                user
            )
        }
        with(AtmStreamingPage(driver)) {
            findAndOpenOfferInOfferList(unitPriceAmount)
            assert {
                elementWithTextPresentedIgnoreCase("Base asset amount")
                elementWithTextPresentedIgnoreCase("Total Amount")
                elementWithTextPresentedIgnoreCase("Asset pair")
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF)
    @TmsLink("ATMCH-624")
    @Test
    @DisplayName("Streaming. Placing sell offer")
    fun streamingPlacingSellOffer() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569
        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.VT
        val user = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
        val amount = OtfAmounts.AMOUNT_10.amount

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            e {
                click(overview)
            }
            assert {
                elementPresented(overviewBreadcrumbs)
                elementPresented(tradeHistory)
                elementPresented(myOffers)
            }
            openPage<AtmStreamingPage>(driver) { submit(user) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user
            )
            findAndOpenOfferInOfferList(unitPrice)
            assert {
                elementWithTextPresentedIgnoreCase("BASE ASSET/AMOUNT")
                elementWithTextPresentedIgnoreCase("QUOTE ASSET/AMOUNT")
                elementWithTextPresentedIgnoreCase("PRICE PER UNIT")
                elementWithTextPresentedIgnoreCase("Expiration")
                elementWithTextPresentedIgnoreCase("Counterparty")
                elementPresented(cancelOfferButton)
            }
        }
    }

    @TmsLink("ATMCH-711")
    @Test
    @DisplayName("Streaming. Place offer. Interface")
    fun streamingPlaceOfferInterface() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            e {
                click(createOffer)
                clear(unitPrice)
                sendKeys(unitPrice, unitPriceAmount.toString())
            }
            assert {
                elementPresented(iWantToBuyAsset)
                elementPresented(iWantToSellAsset)
                elementPresented(selectAssetPair)
                elementPresented(amount)
                elementPresented(unitPrice)
                elementContainingTextPresented("Transaction fee")
                elementWithTextPresentedIgnoreCase("AMOUNT TO RECEIVE")
                elementWithTextPresentedIgnoreCase("AMOUNT TO SEND")
            }
            e {
                click(limitedTimeOffer)
            }
            assert {
                elementPresented(selectTime)
                elementPresented(addTime)
            }

        }
    }

    @ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF)
    @TmsLink("ATMCH-647")
    @Test
    @DisplayName("Streaming. Place offer. Buy, good till cancel")
    fun streamingPlaceOfferBuyGoodTillCancel() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }
        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            val fee = createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "$quoteAsset/$baseAsset",
                "$amountCount $quoteAsset",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, user
            )

            val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                val base = getBalance(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, firstWallet.name)
                base to quote
            }

            //fee расчитывается в baseAsset, при покупке baseBalance расчитывается сразу
            //Ожидаем снижение баланса на (amount * price + fee)
            val baseExpected = baseBefore - (BigDecimal.TEN * unitPriceAmount + fee)
            assertThat(
                "Expected base balance: $baseExpected, was: $baseAfter",
                baseAfter,
                closeTo(baseExpected, BigDecimal("0.01"))
            )
            assertThat(
                "Expected quote balance: $quoteBefore, was: $quoteAfter",
                quoteAfter,
                closeTo(quoteBefore, BigDecimal("0.01"))
            )

        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF)
    @TmsLink("ATMCH-608")
    @Test
    @DisplayName("Streaming. Place offer. Invalid 2FA code")
    fun streamingPlaceOfferInvalid2FACode() {
        var initBalance = ""
        var afterBalance = ""
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

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
            softAssert {
                elementWithTextPresentedIgnoreCase("Create an offer")
            }

            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountCount $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne2FA, manualCompleted = true
                )
                softAssert { elementWithTextPresentedIgnoreCase("New offer") }

                click(goodTillCancelled)

                softAssert { elementWithTextPresentedIgnoreCase("Amount to receive") }
                softAssert { elementWithTextPresentedIgnoreCase("Amount to send") }
                softAssert { elementWithTextPresentedIgnoreCase("Fee option") }
                softAssert { elementWithTextPresentedIgnoreCase("Transaction fee") }

                click(placeOffer)
                sendKeys(privateKey, industrialUserOne2FA.otfWallet.secretKey)
                click(confirmPrivateKeyButton)
                wait {
                    until("confirmation window presented") {
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
    }
}