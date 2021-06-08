package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminStreamingSettingsPage
import pages.atm.AtmAdminStreamingSettingsPage.feeModeState.*
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.atm.AtmStreamingPage.ExpireType.*
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
        val baseInputValue = "CC"
        val quoteValue = "IT"
        val availableAmountValue = RandomStringUtils.random(2, false, true)
        val number = RandomStringUtils.random(2, false, true)
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
                baseInputSelect.sendAndSelect(baseInputValue, baseInputValue, this@with)
                quoteInputSelect.sendAndSelect(quoteValue, quoteValue, this@with)
                sendKeys(availableAmounts, availableAmountValue)
                feeAcceptAsset.delete()
                feeAcceptingAssetSelect.sendAndSelect(
                    baseInputValue,
                    baseInputValue,
                    this@with
                )

                feePlaceAsset.delete()
                feePlacingAssetSelect.sendAndSelect(
                    baseInputValue,
                    baseInputValue,
                    this@with
                )
                sendKeys(feePlaceAmount, number)
                sendKeys(feeAcceptAmount, number)
                select(feePlaceMode, "FIXED")
                select(feeAcceptMode, "FIXED")
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
                elementContainingTextPresented(baseInputValue)
            }
            deleteTradingPair(baseInputValue, quoteValue)
        }
    }

    @Disabled("Поле fee option отсутствует, не возможно сделать проверку в последнем шаге")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4117")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Edit trading pair.")
    fun streamingSettingsEditTradingPair() {
        val baseInputValue = "CC"
        val quoteValue = "IT_202011"
        val number = RandomStringUtils.random(2, false, true)
        val baseInputValue1 = RandomStringUtils.random(6, true, false)
        val quoteValue1 = RandomStringUtils.random(6, true, false)
        val availableAmountValue1 = RandomStringUtils.random(2, false, true)
        val number1 = RandomStringUtils.random(2, false, true)
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPair(
                baseInputValue, quoteValue, "",number, number,
                number, "FIXED", "FIXED", true
            )
            chooseTradingPair(baseInputValue, quoteValue)
            e {
                click(edit)
                sendKeys(baseInput, baseInputValue1)
                sendKeys(quoteInput, quoteValue1)
                sendKeys(availableAmounts, availableAmountValue1)
                sendKeys(feePlaceAsset, number1)
                sendKeys(feePlaceAmount, number1)
                sendKeys(feeAcceptAsset, number1)
                sendKeys(feeAcceptAmount, number1)
                click(confirmDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            chooseTradingPair(baseInputValue1, quoteValue1)
            e {
                click(edit)
            }
            assert {
                elementWithTextPresented(baseInputValue1)
                elementWithTextPresented(quoteValue1)
                elementWithTextPresented(availableAmountValue1)
                elementWithTextPresented(number1)
            }
        }

    }

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
                defaultAsset.delete()
                defaultAssetSelect.sendAndSelect("CC", "CC", this@with)
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
        val baseValue = "VT"
        val quoteValue = "FT"

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseValue, quoteValue, "","1",
                "1", "1",
                "FIXED", "FIXED",
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
            chooseTradingPair(baseValue, quoteValue)
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
                elementWithTextNotPresented("${baseValue}/${quoteValue}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseTradingPair(baseValue, quoteValue)
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
                select(selectAssetPair, "${baseValue}/${quoteValue}")
            }
            assert {
                elementContainingTextPresented("${baseValue}/${quoteValue}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteTradingPair(baseValue, quoteValue)
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4979")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Add trading pair")
    fun streamingAddTradingPair() {
        val baseValue = "FT"
        val quoteValue = "IT"
        val availableAmountValue = RandomStringUtils.random(2, false, true)
        val number = RandomStringUtils.random(2, false, true)

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
                chooseToken(baseInput, baseValue)
                chooseToken(quoteInput, quoteValue)
                sendKeys(availableAmounts, availableAmountValue)
                chooseToken(feePlaceAsset, baseValue)
                sendKeys(feePlaceAmount, number)
                chooseToken(feeAcceptAsset, quoteValue)
                sendKeys(feeAcceptAmount, number)
                select(feePlaceMode, "FIXED")
                select(feeAcceptMode, "FIXED")
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
                elementContainingTextPresented(baseValue)
            }
        }

        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF) }) {
            e {
                click(createOffer)
                select(selectAssetPair, "${baseValue}/${quoteValue}")
            }
            assert {
                elementContainingTextPresented("${baseValue}/${quoteValue}")
            }
        }
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteTradingPair(baseValue, quoteValue)
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-4096")
    @Test
    @DisplayName("Admin panel. OTF. Streaming settings. Change fee placing/accepting offer.")
    fun streamingSettingsChangeFeePlacingAcceptingOffer() {
        val unitPrice = BigDecimal("1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}") //1.97179569
        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.VT
        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        val defaultFeePlacingOfferMakerValue = "5.00000000"
        val defaultFeePlacingOfferTakerValue = "1.00000000"

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseAsset.tokenSymbol, quoteAsset.tokenSymbol,"",
                "1.000000000", "", "",
                MODE_UNDEFINED.state, MODE_UNDEFINED.state, true
            )

            chooseTradingPair( baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
            e {
                click(edit)
                select(feePlaceMode, MODE_UNDEFINED.state)

                select(feeAcceptMode, MODE_UNDEFINED.state)

                click(confirmDialog)
            }

            setUpDefaultFeeOptionsInGlobalForm(
                baseAsset,
                defaultFeePlacingOfferTakerValue,
                defaultFeePlacingOfferMakerValue
            )
        }

        val placingFee = step("${user1.email} create Streaming offer and placing fee in overview") {
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$baseAsset/$quoteAsset",
                "$amount $baseAsset",
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

            chooseTradingPair( baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
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
        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.FT

        step("Admin change value fee in trading pair not save action and check values") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
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
                val value1 = "1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}" //1.97179569

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
                    it[AtmAdminStreamingSettingsPage.BASE]?.text == baseAsset.tokenSymbol
                            && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == quoteAsset.tokenSymbol
                } ?: error("Row with Ticker symbol $baseAsset and $quoteAsset not found in table")

                val acceptFee1 = row1[AtmAdminStreamingSettingsPage.FEE_ACCEPT_OFFER]?.text
                val placeFee1 = row1[AtmAdminStreamingSettingsPage.FEE_PLACE_OFFER]?.text

                assertThat("Value $value1 equal $acceptFee1", value1, Matchers.not(Matchers.equalTo(acceptFee1)))
                assertThat("Value $value1 equal $placeFee1", value1, Matchers.not(Matchers.equalTo(placeFee1)))
            }
        }

        step("Admin change value fee in trading pair not save action and check values") {
            val value = "1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}"
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

                chooseTradingPair(baseAsset.tokenSymbol, quoteAsset.tokenSymbol)
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
                    it[AtmAdminStreamingSettingsPage.BASE]?.text == baseAsset.tokenSymbol
                            && it[AtmAdminStreamingSettingsPage.QUOTE]?.text == quoteAsset.tokenSymbol
                } ?: error("Row with Ticker symbol $baseAsset and $quoteAsset not found in table")

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

        val baseValue = "FT"
        val quoteValue = "IT"
        val fieldValue = "0.${RandomStringUtils.randomNumeric(3) + 1}"

        step("Admin add and delete pair $baseValue/$quoteValue in Streaming offer") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addTradingPairIfNotPresented(
                    baseValue,
                    quoteValue,"",
                    "1.00000000",
                    "1.00000000",
                    "1.00000000",
                    "FIXED",
                    "FIXED", true
                )
                chooseTradingPair(baseValue, quoteValue)
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


        step("${user1.email} check pair $baseValue/$quoteValue in Streaming offer") {
            with(openPage<AtmStreamingPage>(driver) { submit(user1) }) {
                e {
                    click(createOffer)
                    click(selectAssetPair)
                }
                assert {
                    elementContainingTextNotPresented("$baseValue/$quoteValue")
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-5303")
    @Test
    @DisplayName("Admin panel. Streaming settings. Accepting deleted trading pair")
    fun acceptingDeletedTradingPair() {
        val baseValue = "VT"
        val quoteValue = "CC"

        val unitPrice = BigDecimal("1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}") //1.97179569
        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseValue, quoteValue,"",
                "1.000000000", "", "",
                MODE_UNDEFINED.state, MODE_UNDEFINED.state, true
            )
        }

        step("${user1.email} create Streaming offer") {
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$baseValue/$quoteValue",
                "$amount $baseValue",
                unitPrice.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }

        AtmProfilePage(driver).logout()

        step("Admin delete Trading pair $baseValue/$quoteValue") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                deleteTradingPair(baseValue, quoteValue)
            }
        }

        step("${user.email} check offer from Overview") {
            val offerWithoutPair = with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(overview)
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
        step("Admin add Trading pair $baseValue/$quoteValue") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addTradingPair(
                    baseValue, quoteValue,"",
                    "1.000000000", "", "",
                    MODE_UNDEFINED.state, MODE_UNDEFINED.state, true
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET)
    )
    @TmsLink("ATMCH-5512")
    @Test
    @DisplayName("Admin panel. Streaming settings. Accepting disable trading pair")
    fun acceptingDisabledTradingPair() {
        val baseValue = "VT"
        val quoteValue = "CC"

        val unitPrice = BigDecimal("1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}") //1.97179569
        val amount = OtfAmounts.AMOUNT_1.amount

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTradingPairIfNotPresented(
                baseValue, quoteValue,"",
                "1.000000000", "", "",
                MODE_UNDEFINED.state, MODE_UNDEFINED.state, true
            )
        }

        step("${user1.email} create Streaming offer") {
            openPage<AtmStreamingPage>(driver) { submit(user1) }.createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "$baseValue/$quoteValue",
                "$amount $baseValue",
                unitPrice.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }

        AtmProfilePage(driver).logout()

        step("Admin set value available pair to false") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                chooseTradingPair(baseValue, quoteValue)
                e {
                    click(edit)
                    setCheckbox(pairAvailable, false)
                    click(confirmDialog)
                }
            }
        }

        step("${user.email} check offer from Overview") {
            val offerWithoutPair = with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
                e {
                    click(overview)
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

        step("Admin back value available pair to true") {
            with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                chooseTradingPair(baseValue, quoteValue)
                e {
                    click(edit)
                    setCheckbox(pairAvailable, true)
                    click(confirmDialog)
                }
            }
        }
    }
}