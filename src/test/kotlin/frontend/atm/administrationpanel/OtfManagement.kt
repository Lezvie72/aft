package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmAdminStreamingSettingsPage.feeModeState.FIXED
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.isChecked

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("OTF management")
class OtfManagement : BaseTest() {

    private val token1 = CoinType.ETC1
    private val token2 = CoinType.GF46ILN046B
    private val baseToken = CoinType.CC
    private val quoteToken = CoinType.VT
    private val tokenFT = CoinType.FT
    private val tokenIT = CoinType.IT

    private val maturityDate = "202011"

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5393")
    @Test
    @DisplayName("Administration panel. OTF management. Fields validation and mandatory")
    fun otfManagementFieldsValidation() {
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            val errorText = "This field can only use numbers"
            val errorText1 = "The field must be positive"
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
                sendKeys(defaultFeePlacingOfferInputMaker, "-12345")
                pressEnter(defaultFeePlacingOfferInputMaker)
            }
            assertThat(
                "Expected error text: $errorText",
                defaultFeePlacingOfferInputMaker.errorText == errorText
            )
            e {
                sendKeys(defaultFeePlacingOfferInputTaker, "-12345")
                pressEnter(defaultFeePlacingOfferInputTaker)
            }
            assertThat(
                "Expected error text: $errorText",
                defaultFeePlacingOfferInputTaker.errorText == errorText
            )
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
                chooseToken(baseInput, tokenFT.tokenSymbol)
                chooseToken(quoteInput, quoteToken.tokenSymbol)
                sendKeys(availableAmounts, "-123;qwe")
                chooseToken(feePlaceAsset, tokenFT.tokenSymbol)
                sendKeys(feePlaceAmount, "-123;qwe")
                chooseToken(feeAcceptAsset, quoteToken.tokenSymbol)
                sendKeys(feeAcceptAmount, "-123;qwe")
                select(feePlaceMode, FIXED.state)
                select(feeAcceptMode, FIXED.state)
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                feePlaceAmount.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText",
                feeAcceptAmount.errorText == errorText1
            )
            e {
                click(firstRow)
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
                sendKeys(availableAmounts, "-123;qwe")
                sendKeys(feePlaceAmount, "-123;qwe")
                sendKeys(feeAcceptAmount, "-123;qwe")
                setCheckbox(pairAvailable, true)
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                feePlaceAmount.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText",
                feeAcceptAmount.errorText == errorText1
            )
        }
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val errorText = "Field is required"
            assert {
                elementContainingTextPresented("Rfq settings")
                elementWithTextPresented("Default Asset")
                elementWithTextPresented("Default fee placing offer (Maker)")
                elementWithTextPresented("Default fee accepting offer (Taker)")
                elementPresented(rfqSettingsTable)
                elementContainingTextPresented("Token")
                elementContainingTextPresented("Available base")
                elementContainingTextPresented("Available quote")
                elementContainingTextPresented("Fee for placing offer")
                elementContainingTextPresented("Fee for accepting offer")
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
            }
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(availableBase)
                elementPresented(availableQuote)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                sendKeys(tokenInput, "qwerty")
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                tokenInput.errorText == errorText
            )
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val errorText = "Field is required"
            assert {
                elementContainingTextPresented("Blocktrade settings")
                elementWithTextPresented("Default Asset")
                elementWithTextPresented("Default fee placing offer (Maker)")
                elementWithTextPresented("Default fee accepting offer (Taker)")
                elementPresented(blocktradeSettingsTable)
                elementContainingTextPresented("Token")
                elementContainingTextPresented("Available")
                elementContainingTextPresented("Fee for placing offer")
                elementContainingTextPresented("Fee for accepting offer")
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
            }
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(available)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                sendKeys(tokenInput, "qwerty")
                click(confirmDialog)
            }
            MatcherAssert.assertThat(
                "Expected error text: $errorText",
                tokenInput.errorText == errorText
            )
        }
    }

    @TmsLink("ATMCH-5394")
    @Test
    @DisplayName("Administration panel. OTF management. Cancelation scenarios")
    fun otfManagementCancelation() {

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
                chooseToken(baseInput, token1.tokenSymbol)
                chooseToken(quoteInput, token2.tokenSymbol)
                sendKeys(availableAmounts, "1")
                chooseToken(feePlaceAsset, token1.tokenSymbol)
                sendKeys(feePlaceAmount, "1")
                chooseToken(feeAcceptAsset, token2.tokenSymbol)
                sendKeys(feeAcceptAmount, "1")
                select(feePlaceMode, FIXED.state)
                select(feeAcceptMode, FIXED.state)
                setCheckbox(pairAvailable, true)
                click(cancelDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            assert { elementContainingTextNotPresented(token2.tokenSymbol) }
            e {
                click(firstRow)
                click(edit)
            }
            assert {
                elementPresented(baseInput)
                elementPresented(quoteInput)
                elementPresented(pairAvailable)
                elementPresented(feePlaceAsset)
                elementPresented(feePlaceAmount)
                elementPresented(feeAcceptAmount)
                elementPresented(feeAcceptAsset)
                elementPresented(availableAmounts)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                sendKeys(availableAmounts, "1")
                sendKeys(feePlaceAmount, "1")
                sendKeys(feeAcceptAmount, "1")
                click(cancelDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            assert { elementContainingTextNotPresented(token2.tokenSymbol) }
            e {
                click(firstRow)
                click(edit)
                click(cancelDialog)
                click(delete)
                click(no)

                driver.navigate().refresh()
                wait {
                    until("wait for loading page after refresh", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
                assert {
                    elementContainingTextNotPresented(token2.tokenSymbol)
                }
            }
        }
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Rfq settings")
                elementPresented(defaultAsset)
                elementPresented(defaultFeePlacingOfferMaker)
                elementPresented(defaultFeeAcceptingOfferTaker)
                elementPresented(rfqSettingsTable)
                elementContainingTextPresented("Token")
                elementContainingTextPresented("Available base")
                elementContainingTextPresented("Available quote")
                elementContainingTextPresented("Fee for placing offer")
                elementContainingTextPresented("Fee for accepting offer")
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
            }
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(availableBase)
                elementPresented(availableQuote)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                chooseToken(tokenInput, token1.tokenSymbol)
                setCheckbox(availableBase, true)
                setCheckbox(availableQuote, true)
                sendKeys(feePlacingAmount, "10")
                chooseToken(feePlacingAsset, token1.tokenSymbol)
                select(feePlacingMode, FIXED.state)
                chooseToken(feeAcceptingAsset, token2.tokenSymbol)
                sendKeys(feeAcceptingAmount, "10")
                select(feeAcceptingMode, FIXED.state)
                click(cancelDialog)
            }
            assert {
                elementContainingTextNotPresented(token1.tokenSymbol)
            }
            e {
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(availableBase)
                elementPresented(availableQuote)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                setCheckbox(availableBase, true)
                setCheckbox(availableQuote, true)
                sendKeys(feePlacingAmount, "10")

                click(feePlaceClearButton)
                chooseToken(feePlacingAsset, token1.tokenSymbol)
                select(feePlacingMode, FIXED.state)

                click(feeAcceptClearButton)
                chooseToken(feeAcceptingAsset, token2.tokenSymbol)
                sendKeys(feeAcceptingAmount, "10")
                select(feeAcceptingMode, FIXED.state)

                click(cancelDialog)
            }
            assert {
                elementContainingTextNotPresented(token2.tokenSymbol)
            }
            e {
                chooseToken(baseToken.tokenSymbol)
                click(delete)
                click(no)

                driver.navigate().refresh()
                wait {
                    until("wait until data presented", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
                assert {
                    elementContainingTextNotPresented(token2.tokenSymbol)
                    elementContainingTextNotPresented(token1.tokenSymbol)
                }
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Blocktrade settings")
                elementPresented(defaultAsset)
                elementPresented(defaultFeePlacingOfferMaker)
                elementPresented(defaultFeeAcceptingOfferTaker)
                elementPresented(blocktradeSettingsTable)
                elementContainingTextPresented("Token")
                elementContainingTextPresented("Available")
                elementContainingTextPresented("Fee for placing offer")
                elementContainingTextPresented("Fee for accepting offer")
                elementPresented(add)
                elementPresented(editDisabled)
                elementPresented(deleteDisabled)
            }
            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                chooseToken(tokenInput, token2.tokenSymbol)
                setCheckbox(available, true)
                sendKeys(feePlacingAmount, "1")
                chooseToken(feePlacingAsset, token2.tokenSymbol)
                select(feePlacingMode, FIXED.state)
                chooseToken(feeAcceptingAsset, token1.tokenSymbol)
                sendKeys(feeAcceptingAmount, "2")
                select(feeAcceptingMode, FIXED.state)
                click(cancelDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            assert {
                elementContainingTextNotPresented(token2.tokenSymbol)
            }
            e {
                click(firstRow)
                click(edit)
            }
            assert {
                elementContainingTextPresented("token")
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                click(tokenClearButton)
                chooseToken(tokenInput, token2.tokenSymbol)
                setCheckbox(available, true)
                sendKeys(feePlacingAmount, "1")
                chooseToken(feePlacingAsset, token2.tokenSymbol)
                select(feePlacingMode, FIXED.state)
                chooseToken(feeAcceptingAsset, token1.tokenSymbol)
                sendKeys(feeAcceptingAmount, "2")
                select(feeAcceptingMode, FIXED.state)
                click(cancelDialog)
                wait {
                    until("dialog add trading pair is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
            }
            assert {
                elementContainingTextNotPresented(token2.tokenSymbol)
            }
            e {
                chooseToken(baseToken.tokenSymbol)
                click(delete)
                click(no)

                driver.navigate().refresh()
                assert {
                    elementContainingTextNotPresented(token2.tokenSymbol)
                }
            }
        }
    }

    @Issue("https://sdexnt.atlassian.net/browse/ATMCH-6611")
    @TmsLink("ATMCH-4978")
    @Test
    @DisplayName("Administration panel. OTF management. General settings")
    fun otfManagementGeneralSettings() {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("RFQ trading section settings")
                elementContainingTextPresented("Streaming trading section settings")
                elementContainingTextPresented("Blocktrade trading section settings")
                elementPresented(rfqToggle)
                elementPresented(streamingToggle)
                elementPresented(blocktradeToggle)
                elementPresented(rfqLink)
                elementPresented(streamingLink)
                elementPresented(blocktradeLink)
            }
            e {
                setCheckbox(rfqToggle, false)
                driver.navigate().refresh()
                wait {
                    until("page refreshed", 15) {
                        check {
                            isElementPresented(rfqToggle)
                        }
                    }
                }
            }
            assertThat(false, Matchers.equalTo(rfqToggle.isChecked()))
        }
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            assert {
                elementContainingTextNotPresented("RFQ")
            }
        }
        with(openPage<AtmAdminGeneralSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setCheckbox(rfqToggle, true)
                driver.navigate().refresh()
                wait {
                    until("page refreshed", 15) {
                        check {
                            isElementPresented(rfqToggle)
                        }
                    }
                }
            }
            assertThat(true, Matchers.equalTo(rfqToggle.isChecked()))
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            assert {
                elementPresented(createRequest)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5383")
    @Test
    @DisplayName("Administration panel. OTF management. Streaming. Delete trading pair")
    fun streamingDeleteTradingPair() {
        with(openPage<AtmAdminStreamingSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            val fieldValue = "0.${RandomStringUtils.randomNumeric(3) + 1}"
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
            addTradingPair(
                baseToken.tokenSymbol,
                tokenIT.tokenSymbol,
                maturityDate,
                fieldValue,

                fieldValue,

                fieldValue,
                FIXED.state,
                FIXED.state, true
            )
            chooseTradingPair(baseToken.tokenSymbol, tokenIT.tokenSymbol+"_${maturityDate}")
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

    @Disabled("Переключатели кликаются через раз")
    @TmsLink("ATMCH-4112")
    @Test
    @DisplayName("[OTF manage] Disable and Enable trading sections")
    fun disableAndEnableTradingSections() {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setCheckbox(rfqToggle, false)
                setCheckbox(blocktradeToggle, false)
                setCheckbox(streamingToggle, false)
            }
        }
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            assert {
                elementContainingTextNotPresented("RFQ")
                elementContainingTextNotPresented("Streaming")
                elementContainingTextNotPresented("Blocktrade")
            }
        }
        with(openPage<AtmAdminGeneralSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                setCheckbox(rfqToggle, true)
                setCheckbox(blocktradeToggle, true)
                setCheckbox(streamingToggle, true)
            }
        }
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            assert {
                elementPresented(createOffer)
                elementPresented(overview)
                elementPresented(tradeHistory)
            }
            e {
                click(tradeHistory)
            }
            assert {
                elementPresented(overview)
                elementPresented(myOffers)
                elementPresented(tradeHistory)
            }
            e {
                click(myOffers)
            }
            assert {
                assert { elementPresented(placeOffer) }
            }
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            assert {
                elementPresented(createRequest)
                elementPresented(historyOffers)
                elementPresented(myRequest)
            }
            e {
                click(historyOffers)
            }
            assert {
                elementPresented(myRequest)
                elementPresented(tradeHistory)
            }
            e {
                click(myRequest)
            }
            assert { elementPresented(createRequest) }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(createBlockTrade)
                elementPresented(viewMyP2P)
                elementPresented(viewHistoryP2P)
            }
            e {
                click(viewIncomingP2P)
            }
            assert {
                elementPresented(createBlockTrade)
            }
            e {
                click(incomingBT)
            }
            assert {
                elementPresented(createBlockTrade)
            }
        }
    }
}