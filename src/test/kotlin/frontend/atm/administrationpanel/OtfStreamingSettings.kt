package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.*
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminStreamingSettingsPage
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.FIXED
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.MODE_UNDEFINED
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.atm.AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED
import pages.atm.AtmStreamingPage.OperationType.SELL
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("OTF. Streaming Settings")
class OtfStreamingSettings : BaseTest() {

    private val baseToken = CC
    private val quoteToken = VT
    private val tokenFT = FT
    private val tokenIT = IT
    private val fiatToken = FIAT


    private val maturityDate = "202011"

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4177")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Add incorrect trading pair.")
    fun streamingSettingsAddIncorrectTokenRate() {

        val errorText = "Field is required"

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Add trading pair")
                elementPresented(baseInput)
                elementPresented(quoteInput)
                elementPresented(pairAvailable)
                elementPresented(feePlaceAsset)
                elementPresented(feePlaceAmount)
                elementPresented(feePlaceMode)
                elementPresented(feeAcceptAmount)
                elementPresented(feeAcceptAsset)
                elementPresented(feeAcceptMode)
                elementPresented(availableAmounts)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                sendKeys(baseInput, "qwerty")
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                baseInput.errorText == errorText
            )
            e {
                sendKeys(quoteInput, "qwerty")
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                quoteInput.errorText == errorText
            )

        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4142")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Add trading pair.")
    fun streamingSettingsAddTradingPair() {

        val availableAmountValue = RandomStringUtils.random(2, false, true)
        val number = RandomStringUtils.random(2, false, true)

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val row = streamingSettingsTable.find {
                it[AtmAdminStreamingSettingsPage.BASE]?.text == baseToken.tokenSymbol
                        && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == tokenIT.tokenSymbol + "_${maturityDate}"
            }
            if (row != null) {
                deleteTradingPair(baseToken.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")

            }
        }

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Add trading pair")
                elementPresented(baseInput)
                elementPresented(quoteInput)
                elementPresented(pairAvailable)
                elementPresented(feePlaceAsset)
                elementPresented(feePlaceAmount)
                elementPresented(feePlaceMode)
                elementPresented(feeAcceptAmount)
                elementPresented(feeAcceptAsset)
                elementPresented(feeAcceptMode)
                elementPresented(availableAmounts)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                baseInputSelect.sendAndSelect(baseToken.tokenSymbol, baseToken.tokenSymbol, this@with)
                quoteInputSelect.sendAndSelect(tokenIT.tokenSymbol, tokenIT.tokenSymbol, this@with)

                select(maturityDateQuoteValue, maturityDate)
                sendKeys(availableAmounts, availableAmountValue)

                feeAcceptAsset.delete()
                feeAcceptingAssetSelect.sendAndSelect(
                    baseToken.tokenSymbol,
                    baseToken.tokenSymbol,
                    this@with
                )

                feePlaceAsset.delete()
                feePlacingAssetSelect.sendAndSelect(
                    baseToken.tokenSymbol,
                    baseToken.tokenSymbol,
                    this@with
                )
                sendKeys(feePlaceAmount, number)
                select(feePlaceMode, FIXED.state)

                sendKeys(feeAcceptAmount, number)
                select(feeAcceptMode, FIXED.state)

                click(confirmDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
            deleteTradingPair(baseToken.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4117")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Edit trading pair.")
    fun streamingSettingsEditTradingPair() {

        val unitPriceAmount = BigDecimal("1.${randomNumeric(8)}") //1.97179569
        val amount = OtfAmounts.AMOUNT_1.amount

        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA

        val feeValue = BigDecimal("1.${randomNumeric(8)}")

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseToken.tokenSymbol, quoteToken.tokenSymbol, "",
                "1.000000000", "", "",
                FIXED.state, FIXED.state, true
            )
            chooseTradingPair(baseToken.tokenSymbol, quoteToken.tokenSymbol)
            e {
                click(edit)
                sendKeys(feePlaceAmount, feeValue.toString())
                select(feePlaceMode, FIXED.state)
                sendKeys(feeAcceptAmount, feeValue.toString())
                select(feeAcceptMode, FIXED.state)
                click(confirmDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
        }

        val fee = with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
            e {
                click(createOffer)
                click(iWantToBuyAsset)

                select(selectAssetPair, "$baseToken/$quoteToken")
                selectAmount(amount)
                clear(unitPrice)
                sendKeys(unitPrice, unitPriceAmount.toString())
                click(goodTillCancelled)

                wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                }
                offerFee.amount
            }
        }

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseTradingPair(baseToken.tokenSymbol, quoteToken.tokenSymbol)
            e {
                click(edit)

                sendKeys(feePlaceAmount, "1")

                sendKeys(feeAcceptAmount, "1")
                click(confirmDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }

        }
        assertThat(
            "Offer with amount $amount should have been be cancelled",
            fee,
            Matchers.closeTo(feeValue, BigDecimal("0.01"))
        )
    }


    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4084")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Validation.")
    fun streamingSettingsValidation() {
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Streaming settings")
                elementPresented(defaultAsset)
                elementPresented(defaultFeePlacingOfferInputMaker)
                elementPresented(defaultFeePlacingOfferInputTaker)
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
                elementContainingTextPresented("Base")
                elementContainingTextPresented("Quote")
                elementContainingTextPresented("Pair available")
                elementContainingTextPresented("Fee Place offer")
                elementContainingTextPresented("Fee Accept offer")
                elementContainingTextPresented("Available amounts")
                elementPresented(streamingSettingsTable)
            }
            e {
                click(firstRow)
            }
            assert {
                elementPresented(edit)
                elementPresented(delete)
            }
        }
    }

    @Issue("ATMCH-6103")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5384")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Default values")
    fun streamingDefaultValues() {
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(1) + 1}"
            val defaultFeePlacingOfferInputTakerValueNew = "0.${RandomStringUtils.randomNumeric(1) + 1}"

            assert {
                elementContainingTextPresented("Streaming settings")
                elementPresented(defaultAsset)
                elementPresented(defaultFeePlacingOfferInputMaker)
                elementPresented(defaultFeePlacingOfferInputTaker)
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
                elementContainingTextPresented("Base")
                elementContainingTextPresented("Quote")
                elementContainingTextPresented("Pair available")
                elementContainingTextPresented("Fee Place offer")
                elementContainingTextPresented("Fee Accept offer")
                elementContainingTextPresented("Available amounts")
                elementPresented(streamingSettingsTable)
            }

            val defaultAssetValue = defaultAsset.value
            val defaultFeePlacingOfferInputMakerValue = defaultFeePlacingOfferInputMaker.value
            val defaultFeePlacingOfferInputTakerValue = defaultFeePlacingOfferInputTaker.value

            e {
                click(clearButton)
                sendKeys(defaultAsset, fiatToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented("Token is not found")
            }

            e {
                defaultAsset.delete()
                defaultAssetSelect.sendAndSelect(baseToken.tokenSymbol, baseToken.tokenSymbol, this@with)
//                click(defaultAssetSave)
                defaultFeePlacingOfferInputMaker.delete()
                sendKeys(defaultFeePlacingOfferInputMaker, defaultFeePlacingOfferInputMakerValueNew)
                click(defaultFeePlacingOfferInputMakerSave)
                defaultFeePlacingOfferInputTaker.delete()
                sendKeys(defaultFeePlacingOfferInputTaker, defaultFeePlacingOfferInputTakerValueNew)
                click(defaultFeePlacingOfferInputTakerSave)
            }
            assertThat("Default asset saved", defaultAsset.value, Matchers.hasToString("CC"))
            assertThat(
                "Default fee placing offer (Maker) saved",
                defaultFeePlacingOfferInputMaker.value,
                Matchers.hasToString(defaultFeePlacingOfferInputMakerValueNew)
            )
            assertThat(
                "Default fee placing offer (Taker) saved",
                defaultFeePlacingOfferInputTaker.value,
                Matchers.hasToString(defaultFeePlacingOfferInputTakerValueNew)
            )
            e {
                defaultAsset.delete()
                defaultAssetSelect.sendAndSelect(defaultAssetValue, defaultAssetValue, this@with)
//                click(defaultAssetSave)
                defaultFeePlacingOfferInputMaker.delete()
                sendKeys(defaultFeePlacingOfferInputMaker, defaultFeePlacingOfferInputMakerValue)
                click(defaultFeePlacingOfferInputMakerSave)
                defaultFeePlacingOfferInputTaker.delete()
                sendKeys(defaultFeePlacingOfferInputTaker, defaultFeePlacingOfferInputTakerValue)
                click(defaultFeePlacingOfferInputTakerSave)
            }

        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5382")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Edit trading pair ")
    fun streamingEditTradingPair() {

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                quoteToken.tokenSymbol, tokenFT.tokenSymbol, "", "1",
                "1", "1",
                FIXED.state, FIXED.state,
                true
            )
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Streaming settings")
                elementPresented(defaultAsset)
                elementPresented(defaultFeePlacingOfferInputMaker)
                elementPresented(defaultFeePlacingOfferInputTaker)
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
                elementContainingTextPresented("Base")
                elementContainingTextPresented("Quote")
                elementContainingTextPresented("Pair available")
                elementContainingTextPresented("Fee Place offer")
                elementContainingTextPresented("Fee Accept offer")
                elementContainingTextPresented("Available amounts")
                elementPresented(streamingSettingsTable)
            }
            chooseTradingPair(quoteToken.tokenSymbol, tokenFT.tokenSymbol)
            e {
                click(edit)
            }
            assert {
                elementPresented(baseInput)
                elementPresented(quoteInput)
                elementPresented(pairAvailable)
                elementPresented(feePlaceAsset)
                elementPresented(feePlaceAmount)
                elementPresented(feePlaceMode)
                elementPresented(feeAcceptAsset)
                elementPresented(feeAcceptAmount)
                elementPresented(feeAcceptMode)
                elementPresented(availableAmounts)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                setCheckbox(pairAvailable, false)
                click(confirmDialog)
                wait {
                    until("dialog edit trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
        }
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF) }) {
            e {
                click(createOffer)
                click(selectAssetPair)
            }
            assert {
                elementWithTextNotPresented("${quoteToken}/${tokenFT}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseTradingPair(quoteToken.tokenSymbol, tokenFT.tokenSymbol)
            e {
                click(edit)
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
                wait {
                    until("dialog edit trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
        }
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF) }) {
            e {
                click(createOffer)
                select(selectAssetPair, "${quoteToken.tokenSymbol}/${tokenFT.tokenSymbol}")
            }
            assert {
                elementContainingTextPresented("${quoteToken.tokenSymbol}/${tokenFT.tokenSymbol}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteTradingPair(quoteToken.tokenSymbol, tokenFT.tokenSymbol)
        }
    }

    @Issue("ATMCH-6103")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4979")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Add trading pair")
    fun streamingAddTradingPair() {

        val availableAmountValue = randomNumeric(2)
        val number = RandomStringUtils.random(2, false, true)

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val row = streamingSettingsTable.find {
                it[AtmAdminStreamingSettingsPage.BASE]?.text == tokenFT.tokenSymbol
                        && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == tokenIT.tokenSymbol + "_${maturityDate}"
            }
            if (row != null) {
                deleteTradingPair(tokenFT.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Add trading pair")
                elementPresented(baseInput)
                elementPresented(quoteInput)
                elementPresented(pairAvailable)
                elementPresented(feePlaceAsset)
                elementPresented(feePlaceAmount)
                elementPresented(feePlaceMode)
                elementPresented(feeAcceptAmount)
                elementPresented(feeAcceptAsset)
                elementPresented(feeAcceptMode)
                elementPresented(availableAmounts)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                chooseToken(baseInput, fiatToken.tokenSymbol)
                chooseToken(quoteInput, baseToken.tokenSymbol)
                click(confirmDialog)
            }
            assert {
                elementWithTextPresented("Impossible to use fiat token \"${fiatToken.tokenSymbol}\" in secondary market")
            }

            e {
                click(clearBaseInput)
                click(clearQuoteInput)
                chooseToken(baseInput, baseToken.tokenSymbol)
                chooseToken(quoteInput, fiatToken.tokenSymbol)
                click(confirmDialog)
            }
            assert {
                elementWithTextPresented("Impossible to use fiat token \"${fiatToken.tokenSymbol}\" in secondary market")
            }
            e {
                click(clearBaseInput)
                chooseToken(baseInput, tokenFT.tokenSymbol)
                click(clearQuoteInput)
                chooseToken(quoteInput, baseToken.tokenSymbol)
                chooseToken(feePlaceAsset, fiatToken.tokenSymbol)
                chooseToken(feeAcceptAsset, fiatToken.tokenSymbol)
                click(confirmDialog)
            }
            assert {
                elementWithTextPresented("Impossible to use fiat token \"${fiatToken.tokenSymbol}\" in secondary market")
            }

            e {
                click(clearBaseInput)
                chooseToken(baseInput, tokenFT.tokenSymbol)
                click(clearQuoteInput)
                chooseToken(quoteInput, tokenIT.tokenSymbol)
                select(maturityDateQuoteValue, maturityDate)

                click(clearFeeAcceptAsset)
                click(clearFeePlaceAsset)
                sendKeys(availableAmounts, availableAmountValue)
                chooseToken(feePlaceAsset, tokenFT.tokenSymbol)
                sendKeys(feePlaceAmount, number)
                select(feePlaceMode, FIXED.state)

                chooseToken(feeAcceptAsset, tokenIT.tokenSymbol)
                select(maturityDateAcceptAsset, maturityDate)

                sendKeys(feeAcceptAmount, number)
                select(feeAcceptMode, FIXED.state)
                setCheckbox(pairAvailable, true)
                click(confirmDialog)

                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }

            assert {
                elementContainingTextPresented(tokenFT.tokenSymbol)
            }
        }

        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF) }) {
            e {
                click(createOffer)
                select(selectAssetPair, "${tokenFT.tokenSymbol}/${tokenIT.tokenSymbol}")
            }
            assert {
                elementContainingTextPresented("${tokenFT}/${tokenIT.tokenSymbol}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteTradingPair(tokenFT.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4096")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Change fee placing/accepting offer.")
    fun streamingSettingsChangeFeePlacingAcceptingOffer() {
        val unitPrice = BigDecimal("1.${randomNumeric(8)}") //1.97179569

        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        val defaultFeePlacingOfferMakerValue = "5.00000000"
        val defaultFeePlacingOfferTakerValue = "1.00000000"

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseToken.tokenSymbol, quoteToken.tokenSymbol, "",
                "1.000000000", "", "",
                FIXED.state, FIXED.state, true
            )

            chooseTradingPair(baseToken.tokenSymbol, quoteToken.tokenSymbol)
            e {
                click(edit)
                select(feePlaceMode, MODE_UNDEFINED.state)
                Thread.sleep(2000)
                select(feeAcceptMode, MODE_UNDEFINED.state)

                click(confirmDialog)
            }

            setUpDefaultFeeOptionsInGlobalForm(
                baseToken,
                defaultFeePlacingOfferTakerValue,
                defaultFeePlacingOfferMakerValue
            )
        }

        val placingFee = step("${user1.email} create Streaming offer and placing fee in overview") {
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                SELL,
                "${baseToken.tokenSymbol}/${quoteToken.tokenSymbol}",
                "$amount ${baseToken.tokenSymbol}",
                unitPrice.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }
        AtmProfilePage(driver).logout()
        val feeAccepting = step("${user.email} check Streaming offer and accepting fee in overview ") {
            with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(overview)
                }
                findAndOpenOfferInOverview(unitPrice)
                val feeAccepting = wait(15L) {
                    until("Couldn't load fee") {
                        offerFee.text.isNotEmpty()
                    }
                    offerFee.amount
                }
                feeAccepting
            }
        }

        assertThat(
            "Value $defaultFeePlacingOfferTakerValue not equal $placingFee",
            defaultFeePlacingOfferTakerValue,
            Matchers.equalTo(placingFee.toString())
        )

        assertThat(
            "Value $defaultFeePlacingOfferMakerValue not equal $feeAccepting",
            defaultFeePlacingOfferMakerValue,
            Matchers.equalTo(feeAccepting.toString())
        )

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            chooseTradingPair(baseToken.tokenSymbol, quoteToken.tokenSymbol)
            e {
                click(edit)
                select(feePlaceMode, FIXED.state)

                select(feeAcceptMode, FIXED.state)

                click(confirmDialog)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4130")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Cancel edit trading pair.")
    fun streamingSettingsCancelEditTradingPair() {

        step("Admin change value fee in trading pair not save action and check values") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

                chooseTradingPair(baseToken.tokenSymbol, tokenFT.tokenSymbol)
                e {
                    click(edit)
                }
                softAssert {
                    elementContainingTextPresented("Edit trading pair")
                    elementPresented(baseInput)
                    elementPresented(quoteInput)
                    elementPresented(pairAvailable)
                    elementPresented(feePlaceAsset)
                    elementPresented(feePlaceAmount)
                    elementPresented(feePlaceMode)
                    elementPresented(feeAcceptAmount)
                    elementPresented(feeAcceptAsset)
                    elementPresented(feeAcceptMode)
                    elementPresented(availableAmounts)
                    elementPresented(confirmDialog)
                    elementPresented(cancelDialog)
                }
                val value1 = "1.${randomNumeric(8)}" //1.97179569

                e {
                    deleteData(feePlaceAmount)
                    sendKeys(feePlaceAmount, value1)
                    select(feePlaceMode, FIXED.state)

                    deleteData(feeAcceptAmount)
                    sendKeys(feeAcceptAmount, value1)
                    select(feeAcceptMode, FIXED.state)

                    click(cancelDialog)
                }
                val row1 = streamingSettingsTable.find {
                    it[AtmAdminStreamingSettingsPage.BASE]?.text == baseToken.tokenSymbol
                            && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == tokenFT.tokenSymbol
                } ?: error("Row with Ticker symbol $baseToken and $tokenFT not found in table")

                val acceptFee1 = row1[AtmAdminStreamingSettingsPage.FEE_ACCEPT_OFFER]?.text
                val placeFee1 = row1[AtmAdminStreamingSettingsPage.FEE_PLACE_OFFER]?.text

                assertThat("Value $value1 equal $acceptFee1", value1, Matchers.not(Matchers.equalTo(acceptFee1)))
                assertThat("Value $value1 equal $placeFee1", value1, Matchers.not(Matchers.equalTo(placeFee1)))
            }
        }

        step("Admin change value fee in trading pair not save action and check values") {
            val value = "1.${randomNumeric(8)}"
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

                chooseTradingPair(baseToken.tokenSymbol, tokenFT.tokenSymbol)
                e {
                    click(edit)

                    deleteData(feePlaceAmount)
                    sendKeys(feePlaceAmount, value)
                    select(feePlaceMode, FIXED.state)

                    deleteData(feeAcceptAmount)
                    sendKeys(feeAcceptAmount, value)
                    select(feeAcceptMode, FIXED.state)

                    click(defaultAsset)
                }

                val row = streamingSettingsTable.find {
                    it[AtmAdminStreamingSettingsPage.BASE]?.text == baseToken.tokenSymbol
                            && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == tokenFT.tokenSymbol
                } ?: error("Row with Ticker symbol $baseToken and $tokenFT not found in table")

                val acceptFee = row[AtmAdminStreamingSettingsPage.FEE_ACCEPT_OFFER]?.text
                val placeFee = row[AtmAdminStreamingSettingsPage.FEE_PLACE_OFFER]?.text

                assertThat("Value $value equal $acceptFee", value, Matchers.not(Matchers.equalTo(acceptFee)))
                assertThat("Value $value equal $placeFee", value, Matchers.not(Matchers.equalTo(placeFee)))

            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4144")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Delete trading pair")
    fun streamingDeleteTradingPair() {
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        val fieldValue = "0.${RandomStringUtils.randomNumeric(3) + 1}"

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val row = streamingSettingsTable.find {
                it[AtmAdminStreamingSettingsPage.BASE]?.text == tokenFT.tokenSymbol
                        && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == tokenIT.tokenSymbol + "_${maturityDate}"
            }
            if (row != null) {
                deleteTradingPair(tokenFT.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")
            }
        }

        step("Admin add and delete pair ${tokenFT.tokenSymbol}/${tokenIT.tokenSymbol} in Streaming offer") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addTradingPairIfNotPresented(
                    tokenFT.tokenSymbol,
                    tokenIT.tokenSymbol, maturityDate,
                    "1.00000000",
                    "1.00000000",
                    "1.00000000",
                    FIXED.state,
                    FIXED.state, true
                )
                chooseTradingPair(tokenFT.tokenSymbol, tokenIT.tokenSymbol + "_${maturityDate}")
                e {
                    click(delete)
                    click(yes)
                    wait {
                        until("dialog delete trading pair is gone", 15) {
                            check {
                                isElementGone(yes)
                            }
                        }
                    }
                }
                assert { elementWithTextNotPresented(fieldValue) }
            }
        }


        step("${user1.email} check pair $tokenFT/$tokenIT in Streaming offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
                e {
                    click(createOffer)
                    click(selectAssetPair)
                }
                assert {
                    elementContainingTextNotPresented("$tokenFT/$tokenIT")
                }
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5303")
    @Test
    @DisplayName("Admin panel. Streaming settings. Accepting deleted trading pair")
    fun acceptingDeletedTradingPair() {

        val unitPrice = BigDecimal("1.${randomNumeric(8)}") //1.97179569
        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                quoteToken.tokenSymbol, baseToken.tokenSymbol, "",
                "1.000000000", "", "",
                FIXED.state, FIXED.state, true
            )
        }

        step("Admin delete Trading pair $quoteToken/$baseToken") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                deleteTradingPair(quoteToken.tokenSymbol, baseToken.tokenSymbol)
            }
        }

        step("${user.email} check offer from Overview") {
            val offerWithoutPair = with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(overview)
                    click(showSellOnly)
                }
                val offerWithoutPair = overviewOffersList.find {
                    it.unitPriceAmount == unitPrice
                }
                offerWithoutPair
            }
            assertThat(
                "Offer with amount $amount shouldn't exists",
                offerWithoutPair,
                Matchers.nullValue()
            )
        }
        step("Admin add Trading pair $quoteToken/$baseToken") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addTradingPair(
                    quoteToken.tokenSymbol, baseToken.tokenSymbol, "",
                    "1.000000000", "", "",
                    MODE_UNDEFINED.state, MODE_UNDEFINED.state, true
                )
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5512")
    @Test
    @DisplayName("Admin panel. Streaming settings. Accepting disable trading pair")
    fun acceptingDisabledTradingPair() {

        val unitPrice = BigDecimal("1.${randomNumeric(8)}") //1.97179569
        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                quoteToken.tokenSymbol, baseToken.tokenSymbol, "",
                "1.000000000", "", "",
                FIXED.state, FIXED.state, true
            )
        }

        step("${user1.email} create Streaming offer") {
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                SELL,
                "${quoteToken.tokenSymbol}/${baseToken.tokenSymbol}",
                "$amount ${quoteToken.tokenSymbol}",
                unitPrice.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }

        step("${user.email} check offer from Overview") {
            with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(overview)
                    click(showSellOnly)
                }
                val offerWithoutPair = overviewOffersList.find {
                    it.quoteAmount == unitPrice
                }

                assertThat(
                    "Offer with amount $amount shouldn't exists",
                    offerWithoutPair,
                    Matchers.nullValue()
                )
            }

        }

        step("Admin back value available pair to true") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                chooseTradingPair(quoteToken.tokenSymbol, baseToken.tokenSymbol)
                e {
                    click(edit)
                    setCheckbox(pairAvailable, true)
                    click(confirmDialog)
                }
            }
        }
    }
}