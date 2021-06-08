package frontend.atm.otf.streaming

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.OtfAmounts
import models.user.classes.UserWithMainWalletAndOtf
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.WebDriver
import pages.atm.*
import pages.core.actions.ElementActions
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal
import java.util.*

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER),Tag(TagNames.Flow.FEE))
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Epic("Frontend")
@Feature("Streaming")
@Story("Placing of the new offer & Accept offer")
class PlacingOfTheNewOffer_And_AcceptingAnOffer : BaseTest() {
    companion object {
        private var unitPriceOffers = mutableMapOf<String, BigDecimal>()
    }

    data class BalanceState(
        var initBalanceBaseAsset: String = "",
        var afterBalanceBaseAsset: String = "",
        var initBalanceQuoteAsset: String = "",
        var afterBalanceQuoteAsset: String = "",
        var initBalanceBaseAssetHeld: String = "",
        var afterBalanceBaseAssetHeld: String = "",
        var initBalanceQuoteAssetHeld: String = "",
        var afterBalanceQuoteAssetHeld: String = "",
        var initBalanceThird: String = "",
        var afterBalanceThird: String = "",
        var initBalanceThirdHeld: String = "",
        var afterBalanceThirdHeld: String = ""
    )

    // preconditions
    private val baseAsset = CoinType.VT
    private val quoteAsset = CoinType.CC
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val amountBuy = OtfAmounts.AMOUNT_10.amount
    private val feeSize = "0.00000001"
    private val wallet = "OTF 1"
    private val feeCoinVT = CoinType.VT
    private val feeCoinCC = CoinType.CC
    private val feeCoinFT = CoinType.FT

    private val atmch5841 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinVT, feeCoinVT,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.FIXED,
        AtmAdminStreamingSettingsPage.feeModeState.FIXED
    )
    private val atmch5843 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinCC, feeCoinCC,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.FIXED,
        AtmAdminStreamingSettingsPage.feeModeState.FIXED
    )
    private val atmch5845 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinFT, feeCoinFT,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.FIXED,
        AtmAdminStreamingSettingsPage.feeModeState.FIXED
    )
    private val atmch5847 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinVT, feeCoinVT,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.VOLUME,
        AtmAdminStreamingSettingsPage.feeModeState.VOLUME
    )
    private val atmch5848 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinCC, feeCoinCC,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.VOLUME,
        AtmAdminStreamingSettingsPage.feeModeState.VOLUME
    )
    private val atmch5849 = AtmAdminStreamingSettingsPage.FeePreset(
        feeCoinFT, feeCoinFT,
        feeSize, feeSize, AtmAdminStreamingSettingsPage.feeModeState.VOLUME,
        AtmAdminStreamingSettingsPage.feeModeState.VOLUME
    )

    @Test
    @Order(10)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5841")
    @DisplayName("Offer placing - Checking the fee in base asset (fixed)")
    fun offerPlacingCheckingTheFeeInBaseAssetFixed() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5841)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )


                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() - feeSize.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { alert { checkErrorAlert() } }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() + feeSize.toDouble())).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $feeSize",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy + $feeSize",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5841"] = unitPriceOffer
    }

    @Test
    @Order(11)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5882")
    @DisplayName("Accept offer - Checking the fee in base asset (fixed)")
    fun acceptOfferCheckingTheFeeInBaseAssetFixed() {
        val preconditionsTestName = "ATMCH-5841"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!
        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() + feeSize.toDouble())).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)
        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() - feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() * unitPriceOffer.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy - $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(12)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5843")
    @DisplayName("Offer placing - Checking the fee in quote asset (fixed)")
    fun offerPlacingCheckingTheFeeInQuoteAssetFixed() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5843)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )


                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble()
                        ).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )


                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                        ).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5843"] = unitPriceOffer
    }

    @Test
    @Order(13)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5918")
    @DisplayName("Accept offer - Checking the fee in quote asset (fixed)")
    fun acceptOfferCheckingTheFeeInQuoteAssetFixed() {
        val preconditionsTestName = "ATMCH-5843"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!
        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble())
                            .toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer - $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            (amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble())
                        ).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)
        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(14)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5845")
    @DisplayName("Offer placing - Checking the fee in the third asset (fixed)")
    fun offerPlacingCheckingTheFeeInTheThirdAssetFixed() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5845)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )


                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5845"] = unitPriceOffer
    }

    @Test
    @Order(15)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5920")
    @DisplayName("Accept offer - Checking the fee in the third asset (fixed)")
    fun acceptOfferCheckingTheFeeInTheThirdAssetFixed() {
        val preconditionsTestName = "ATMCH-5845"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserTwo.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive $baseAsset should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserTwo.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(16)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5847")
    @DisplayName("Offer placing - Checking the fee in base asset (volume)")
    fun offerPlacingCheckingTheFeeInBaseAssetVolume() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Volume fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5847)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )


                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $amountBuy * $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                        ).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $amountBuy * $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            (amountBuy.toDouble() + amountBuy.toDouble() * feeSize.toDouble())
                        ).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy + $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble() + amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5847"] = unitPriceOffer
    }

    @Test
    @Order(17)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5945")
    @DisplayName("Accept offer - Checking the fee in base asset (volume)")
    fun acceptOfferCheckingTheFeeInBaseAssetVolume() {
        val preconditionsTestName = "ATMCH-5847"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Volume fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $amountBuy * $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() + amountBuy.toDouble() * feeSize.toDouble()))
                            .toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $amountBuy * $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() - amountBuy.toDouble() * feeSize.toDouble())
                            .toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() * unitPriceOffer.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy - $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(18)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5848")
    @DisplayName("Offer placing - Checking the fee in quote asset (volume)")
    fun offerPlacingCheckingTheFeeInQuoteAssetVolume() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Volume fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5848)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $amountBuy * $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() + amountBuy.toDouble() * feeSize.toDouble()
                        ).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer + $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() + amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $amountBuy * $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                        ).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5848"] = unitPriceOffer
    }


    @Test
    @Order(19)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5946")
    @DisplayName("Accept offer - Checking the fee in quote asset (volume)")
    fun acceptOfferCheckingTheFeeInQuoteAssetVolume() {
        val preconditionsTestName = "ATMCH-5848"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Volume fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $amountBuy * $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                        ).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer - $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $amountBuy * $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            (amountBuy.toDouble() * unitPriceOffer.toDouble() + amountBuy.toDouble() * feeSize.toDouble())
                        ).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(20)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5849")
    @DisplayName("Offer placing - Checking the fee in the third asset (volume)")
    fun offerPlacingCheckingTheFeeInTheThirdAssetVolume() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Volume fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                setupFee(atmch5849)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $amountBuy * $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThird.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $amountBuy * $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThird.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $amountBuy * $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5849"] = unitPriceOffer
    }

    @Test
    @Order(21)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5947")
    @DisplayName("Accept offer - Checking the fee in the third asset (volume)")
    fun acceptOfferCheckingTheFeeInTheThirdAssetVolume() {
        val preconditionsTestName = "ATMCH-5849"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Volume fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $amountBuy * $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceThird.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $amountBuy * $feeSize",
                    offerFee.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive $baseAsset should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $amountBuy * $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble() * feeSize.toDouble()).toDouble())
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by  $amountBuy * $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceThird.toDouble() - amountBuy.toDouble() * feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(22)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5864")
    @DisplayName("Offer placing - Checking the fee in base asset (default)")
    fun offerPlacingCheckingTheFeeInBaseAssetDefault() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setUpDefaultFeeOptionsInGlobalForm(feeCoinVT, feeSize, feeSize)
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() - feeSize.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() + feeSize.toDouble())).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $feeSize",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy + $feeSize",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5864"] = unitPriceOffer
    }

    @Test
    @Order(23)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5940")
    @DisplayName("Accept offer - Checking the fee in base asset (default)")
    fun acceptOfferCheckingTheFeeInBaseAssetDefault() {
        val preconditionsTestName = "ATMCH-5864"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                wait {
                    untilPresentedAnyWithText<TextBlock>("Error", "error")
                }

                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() + feeSize.toDouble())).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)
        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy + $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() - feeSize.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble() * unitPriceOffer.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy - $feeSize",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(24)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5865")
    @DisplayName("Offer placing - Checking the fee in quote asset (default)")
    fun offerPlacingCheckingTheFeeInQuoteAssetDefault() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setUpDefaultFeeOptionsInGlobalForm(feeCoinCC, feeSize, feeSize)
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble()
                        ).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                        ).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5865"] = unitPriceOffer
    }

    @Test
    @Order(25)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5943")
    @DisplayName("Accept offer - Checking the fee in quote asset (default)")
    fun acceptOfferCheckingTheFeeInQuoteAssetDefault() {
        val preconditionsTestName = "ATMCH-5865"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer - $feeSize",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble())
                            .toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, (amountBuy.toDouble())).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer - $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send should be $amountBuy * $unitPriceOffer + $feeSize",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(
                            Locale.US,
                            (amountBuy.toDouble() * unitPriceOffer.toDouble() + feeSize.toDouble())
                        ).toDouble()
                    )
                )
                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer + $feeSize",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble() - feeSize.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Test
    @Order(26)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5866")
    @DisplayName("Offer placing - Checking the fee in the third asset (default)")
    fun offerPlacingCheckingTheFeeInTheThirdAssetDefault() {
        var balanceStateUserOne = BalanceState()
        val feeType = "Fixed fee"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        // setting fee
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setUpDefaultFeeOptionsInGlobalForm(feeCoinFT, feeSize, feeSize)
                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
                click(edit)
                cleanFeeInEditForm()
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
            }
        }
        openPage<AtmAdminPage>(driver).logout()

        // BUY
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterBuyToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )

        // SELL
        // get init balance
        updateInitBalance(balanceStateUserOne, userOne)

        // create offer
        with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBuy $baseAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    userOne, manualCompleted = true
                )

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )

                click(placeOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                setFilterSellToday(this, this@with)
                findOfferBy(unitPriceOffer, myOffersList)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        // get after balance
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceBaseAsset.toDouble() - amountBuy.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserOne.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserOne.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $baseAsset is increased by $amountBuy",
            balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Held in orders value of $feeCoinFT is increased by $feeSize",
            balanceStateUserOne.afterBalanceThirdHeld.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserOne.initBalanceThirdHeld.toDouble() + feeSize.toDouble()
                ).toDouble()
            )
        )
        unitPriceOffers["ATMCH-5866"] = unitPriceOffer
    }

    @Test
    @Order(27)
    @ResourceLocks(
        ResourceLock(Constants.FEE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5944")
    @DisplayName("Accept offer - Checking the fee in the third asset (default)")
    fun acceptOfferCheckingTheFeeInTheThirdAssetDefault() {
        val preconditionsTestName = "ATMCH-5866"
        Assumptions.assumeTrue(
            unitPriceOffers.containsKey(preconditionsTestName),
            "Check completed preconditions by test $preconditionsTestName"
        )

        val unitPriceOffer: BigDecimal = unitPriceOffers[preconditionsTestName]!!

        var balanceStateUserOne = BalanceState()
        var balanceStateUserTwo = BalanceState()
        val feeType = "Fixed fee"

        // BUY ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterBuyToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive should be $amountBuy * $unitPriceOffer",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $baseAsset should be $amountBuy",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterBuyToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is decreased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() - amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserTwo.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() + amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is not changed",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $quoteAsset is decreased",
            balanceStateUserOne.initBalanceQuoteAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAssetHeld.toDouble()
            ).toDouble()
        )

        // SELL ACCEPT
        // get init balance userOne
        updateInitBalance(balanceStateUserOne, userOne)
        openPage<AtmProfilePage>(driver).logout()

        // get init balance userTwo
        updateInitBalance(balanceStateUserTwo, userTwo)

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                // check fee in offer
                click(overview)
                setFilterSellToday(this, this@with)
                val item = findOfferBy(unitPriceOffer, overviewOffersList)
                click(item)

                softAssert { elementContainsText(feeOptionType, feeType) }
                MatcherAssert.assertThat(
                    "Fee should be $feeSize",
                    offerFee.textBody,
                    Matchers.equalTo(feeSize)
                )
                MatcherAssert.assertThat(
                    "Amount to receive $baseAsset should be $amountBuy",
                    amountToReceive.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, amountBuy.toDouble()).toDouble())
                )
                MatcherAssert.assertThat(
                    "Amount to send $quoteAsset should be $amountBuy * $unitPriceOffer",
                    amountToSend.amount.toDouble(),
                    Matchers.equalTo(
                        "%.8f".format(Locale.US, amountBuy.toDouble() * unitPriceOffer.toDouble()).toDouble()
                    )
                )
                MatcherAssert.assertThat(
                    "Amount to send $feeCoinFT should be $feeSize",
                    amountToSendSecond.amount.toDouble(),
                    Matchers.equalTo("%.8f".format(Locale.US, feeSize.toDouble()).toDouble())
                )

                click(acceptOffer)
                alert { checkErrorAlert() }
                signAndSubmitMessage(userTwo, userTwo.otfWallet.secretKey)
                alert { checkErrorAlert() }

                // check offer (is disappeared)
                click(overview)
                setFilterSellToday(this, this@with)
                MatcherAssert.assertThat(
                    "Offer with unitPrice $unitPriceOffer should not exist",
                    !isOfferExist(unitPriceOffer, overviewOffersList)
                )
            }
        }

        // get after balance user two
        updateAfterBalance(balanceStateUserTwo, userTwo)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is increased by $amountBuy",
            balanceStateUserTwo.afterBalanceBaseAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceBaseAsset.toDouble() + amountBuy.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is decreased by $amountBuy * $unitPriceOffer",
            balanceStateUserTwo.afterBalanceQuoteAsset.toDouble(),
            Matchers.equalTo(
                "%.8f".format(
                    Locale.US,
                    balanceStateUserTwo.initBalanceQuoteAsset.toDouble() - amountBuy.toDouble() * unitPriceOffer.toDouble()
                ).toDouble()
            )
        )
        MatcherAssert.assertThat(
            "The Available value of $feeCoinFT is decreased by $feeSize",
            balanceStateUserTwo.afterBalanceThird.toDouble(),
            Matchers.equalTo(
                "%.8f".format(Locale.US, balanceStateUserTwo.initBalanceThird.toDouble() - feeSize.toDouble())
                    .toDouble()
            )
        )
        openPage<AtmProfilePage>(driver).logout()

        // get after balance user One
        updateAfterBalance(balanceStateUserOne, userOne)

        MatcherAssert.assertThat(
            "The Available value of $baseAsset is not changed",
            balanceStateUserOne.initBalanceBaseAsset.toDouble() == "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAsset.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Held in offers value of $baseAsset is decreased",
            balanceStateUserOne.initBalanceBaseAssetHeld.toDouble() > "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceBaseAssetHeld.toDouble()
            ).toDouble()
        )
        MatcherAssert.assertThat(
            "The Available value of $quoteAsset is increased",
            balanceStateUserOne.initBalanceQuoteAsset.toDouble() < "%.8f".format(
                Locale.US,
                balanceStateUserOne.afterBalanceQuoteAsset.toDouble()
            ).toDouble()
        )
    }

    @Step("Get init balance")
    private fun updateInitBalance(balanceStateUser: BalanceState, user: UserWithMainWalletAndOtf) {
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet was found"
                )
                balanceStateUser.initBalanceBaseAsset = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                balanceStateUser.initBalanceBaseAssetHeld = getHeldInOrdersFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                balanceStateUser.initBalanceQuoteAsset = getBalanceFromWalletForToken(quoteAsset, wallet)
                click(walletsHeader)
                balanceStateUser.initBalanceQuoteAssetHeld = getHeldInOrdersFromWalletForToken(quoteAsset, wallet)
                click(walletsHeader)
                balanceStateUser.initBalanceThird = getBalanceFromWalletForToken(feeCoinFT, wallet)
                click(walletsHeader)
                balanceStateUser.initBalanceThirdHeld = getHeldInOrdersFromWalletForToken(feeCoinFT, wallet)
            }
        }
    }

    @Step("Get after balance")
    private fun updateAfterBalance(balanceStateUser: BalanceState, user: UserWithMainWalletAndOtf) {
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(wallet),
                    "Wallet with label $wallet was found"
                )
                balanceStateUser.afterBalanceBaseAsset = getBalanceFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                balanceStateUser.afterBalanceBaseAssetHeld = getHeldInOrdersFromWalletForToken(baseAsset, wallet)
                click(walletsHeader)
                balanceStateUser.afterBalanceQuoteAsset = getBalanceFromWalletForToken(quoteAsset, wallet)
                click(walletsHeader)
                balanceStateUser.afterBalanceQuoteAssetHeld = getHeldInOrdersFromWalletForToken(quoteAsset, wallet)
                click(walletsHeader)
                balanceStateUser.afterBalanceThird = getBalanceFromWalletForToken(feeCoinFT, wallet)
                click(walletsHeader)
                balanceStateUser.afterBalanceThirdHeld = getHeldInOrdersFromWalletForToken(feeCoinFT, wallet)
            }
        }
    }

    @Step("Set filter buy and today")
    private fun setFilterBuyToday(
        elementActions: ElementActions<WebDriver>,
        atmStreamingPage: AtmStreamingPage
    ) {
        elementActions.click(atmStreamingPage.resetFilters)
        elementActions.click(atmStreamingPage.showBuyOnly)
        elementActions.select(atmStreamingPage.tradingPair, "$baseAsset/$quoteAsset")
        elementActions.click(atmStreamingPage.dateFrom)
        elementActions.click(atmStreamingPage.today)
    }

    @Step("Set filter sell and today")
    private fun setFilterSellToday(
        elementActions: ElementActions<WebDriver>,
        atmStreamingPage: AtmStreamingPage
    ) {
        elementActions.click(atmStreamingPage.resetFilters)
        elementActions.click(atmStreamingPage.showSellOnly)
        elementActions.select(atmStreamingPage.tradingPair, "$baseAsset/$quoteAsset")
        elementActions.click(atmStreamingPage.dateFrom)
        elementActions.click(atmStreamingPage.today)
    }
}
