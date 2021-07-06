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


@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.STREAMING.NUMBER))
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


    @ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    @TmsLink("ATMCH-721")
    @Test
    @DisplayName("Streaming. Place offer. Cancel placing transfer")
    fun streamingPlaceOfferCancelPlacingTransfer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val (baseBefore, _) = with(openPage<AtmWalletPage>(driver) { submit(userOne) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                click(createOffer)
                click(iWantToBuyAsset)
                select(selectAssetPair, "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}")
                selectAmount("$amount ${quoteAsset.tokenSymbol}")
                clear(unitPrice)
                sendKeys(unitPrice, unitPriceAmount.toString())
                click(cancelPlaceOffer)
                click(myOffer)
            }

            assertThat(
                "Offer with unit price $unitPriceAmount should be not exist",
                !isOfferExist(unitPriceAmount, myOffersList)
            )
        }

        val (baseAfter, _) = with(openPage<AtmWalletPage>(driver)) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        assertThat(
            "Expected ${baseAsset.tokenSymbol} balance: before $baseBefore, now: $baseAfter",
            baseAfter,
            closeTo(baseBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF),
        ResourceLock(Constants.ATM_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-713")
    @Test
    @DisplayName("Streaming. Place offer. Buy, temporary offer")
    fun streamingPlaceOfferBuyTemporaryOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val (baseBeforeHeld, _) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            softAssert { elementContainingTextPresented("CREATE AN OFFER") }

            val fee = createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amount ${quoteAsset.tokenSymbol}",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY, user
            )

            assertThat(
                "Offer with unit price $unitPrice should be exist",
                isOfferExist(unitPriceAmount, myOffersList)
            )

            val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                val base = getBalance(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, firstWallet.name)
                base to quote
            }

            val (baseAfterHeld, _) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
                val base = getHeldInOrders(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getHeldInOrders(quoteAsset, firstWallet.name)
                base to quote
            }

            //fee расчитывается в baseAsset, при покупке baseBalance расчитывается сразу
            //Ожидаем снижение баланса на (amount * price + fee)
            val baseExpected = baseBefore - (BigDecimal.TEN * unitPriceAmount + fee)
            val baseExpectedHeld = baseBeforeHeld + (BigDecimal.TEN) + fee

            assertThat(
                "Expected ${baseAsset.tokenSymbol} balance: $baseExpected, was: $baseAfter",
                baseAfter,
                closeTo(baseExpected, BigDecimal("0.01"))
            )
            assertThat(
                "Expected ${quoteAsset.tokenSymbol} balance: $quoteBefore, was: $quoteAfter",
                quoteAfter,
                closeTo(quoteBefore, BigDecimal("0.01"))
            )
            assertThat(
                "Expected ${baseAsset.tokenSymbol} balance for held: $baseExpectedHeld, was: $baseAfterHeld",
                baseAfterHeld,
                closeTo(baseExpectedHeld, BigDecimal("0.01"))
            )
        }
    }

    @ResourceLock(Constants.ATM_USER_2FA_OTF_OPERATION_SECOND)
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

        val (_, quoteBeforeHeld) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }


        with(openPage<AtmStreamingPage>(driver) { submit(userThree) }) {
            softAssert { elementContainingTextPresented("CREATE AN OFFER") }

            createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amount ${quoteAsset.tokenSymbol}",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, userThree
            )
            assertThat(
                "Offer with unit price $unitPrice should be exist",
                isOfferExist(unitPriceAmount, myOffersList)
            )
        }

        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val (_, quoteAfterHeld) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }

        val quoteExpected = quoteBefore - (BigDecimal.TEN)
        val quoteExpectedHeld = quoteBeforeHeld + (BigDecimal.TEN)

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
        assertThat(
            "Expected quote balance for held: $quoteExpectedHeld, was: $quoteAfterHeld",
            quoteAfterHeld,
            closeTo(quoteExpectedHeld, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET)
    @TmsLink("ATMCH-714")
    @Test
    @DisplayName("Streaming. Place offer. Sell, temporary offer")
    fun streamingPlaceOfferSellTemporaryOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val (_, quoteBeforeHeld) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }


        with(openPage<AtmStreamingPage>(driver) { submit(userThree) }) {
            softAssert { elementContainingTextPresented("CREATE AN OFFER") }

            createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amount ${quoteAsset.tokenSymbol}",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.TEMPORARY, userThree
            )
            assertThat(
                "Offer with unit price $unitPrice should be exist",
                isOfferExist(unitPriceAmount, myOffersList)
            )
        }

        val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val (_, quoteAfterHeld) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }

        val quoteExpected = quoteBefore - (BigDecimal.TEN)
        val quoteExpectedHeld = quoteBeforeHeld + (BigDecimal.TEN)

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
        assertThat(
            "Expected quote balance for held: $quoteExpectedHeld, was: $quoteAfterHeld",
            quoteAfterHeld,
            closeTo(quoteExpectedHeld, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ATM_USER_2FA_OTF_OPERATION)
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
                click(goodTillCancelled)
                wait {
                    until("Manual signature should be appeared", 15L) {
                        click(placeOffer)
                        check {
                            isElementPresented(manualSignatureLabel, 4L)
                        }
                    }
                }
                clickUntilElementIsPresented(placeOffer, "Manual signature", 15, 5)
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

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET)
    @TmsLink("ATMCH-613")
    @Test
    @DisplayName("Streaming. Placing buy offer")
    fun streamingPlacingBuyOffer() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesStreaming(
                baseAsset, quoteAsset, "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountCount ${quoteAsset.tokenSymbol}",
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

    @ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    @TmsLink("ATMCH-624")
    @Test
    @DisplayName("Streaming. Placing sell offer")
    fun streamingPlacingSellOffer() {
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}") //1.97179569
        val user = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesStreaming(
                baseAsset, quoteAsset, "1",
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
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountCount ${quoteAsset.tokenSymbol}",
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

    @ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    @TmsLink("ATMCH-647")
    @Test
    @DisplayName("Streaming. Place offer. Buy, good till cancel")
    fun streamingPlaceOfferBuyGoodTillCancel() {
        val unitPriceAmount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val (baseBefore, quoteBefore) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val (baseBeforeHeld, _) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
            val base = getHeldInOrders(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getHeldInOrders(quoteAsset, firstWallet.name)
            base to quote
        }


        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            softAssert { elementContainingTextPresented("CREATE AN OFFER") }

            val fee = createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountCount ${quoteAsset.tokenSymbol}",
                unitPriceAmount.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, user
            )
            assertThat(
                "Offer with unit price $unitPrice should be exist",
                isOfferExist(unitPriceAmount, myOffersList)
            )

            val (baseAfter, quoteAfter) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                val base = getBalance(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getBalance(quoteAsset, firstWallet.name)
                base to quote
            }

            val (baseAfterHeld, _) = with(openPage<AtmWalletPage>(driver) { submit(userThree) }) {
                val base = getHeldInOrders(baseAsset, firstWallet.name)
                openPage<AtmWalletPage>(driver)
                val quote = getHeldInOrders(quoteAsset, firstWallet.name)
                base to quote
            }

            //fee расчитывается в baseAsset, при покупке baseBalance расчитывается сразу
            //Ожидаем снижение баланса на (amount * price + fee)
            val baseExpected = baseBefore - (BigDecimal.TEN * unitPriceAmount + fee)
            val baseExpectedHeld = baseBeforeHeld + (BigDecimal.TEN) + fee

            assertThat(
                "Expected ${baseAsset.tokenSymbol} balance: $baseExpected, was: $baseAfter",
                baseAfter,
                closeTo(baseExpected, BigDecimal("0.01"))
            )
            assertThat(
                "Expected ${quoteAsset.tokenSymbol} balance: $quoteBefore, was: $quoteAfter",
                quoteAfter,
                closeTo(quoteBefore, BigDecimal("0.01"))
            )
            assertThat(
                "Expected ${baseAsset.tokenSymbol} balance for held: $baseExpectedHeld, was: $baseAfterHeld",
                baseAfterHeld,
                closeTo(baseExpectedHeld, BigDecimal("0.01"))
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET)
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
                    "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                    "$amountCount ${quoteAsset.tokenSymbol}",
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