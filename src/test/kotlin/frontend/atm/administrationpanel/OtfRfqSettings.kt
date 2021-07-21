package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.*
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import pages.atm.AtmAdminRfqSettingsPage
import pages.atm.AtmAdminRfqSettingsPage.FeeModeState
import pages.atm.AtmAdminRfqSettingsPage.FeeModeState.MODE_UNDEFINED
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.FIXED
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.isChecked
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.OTCSETTINGS))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("OTF. RFQ Settings")
class OtfRfqSettings : BaseTest() {

    private val tokenETC = ETC
    private val quoteToken = VT
    private val baseToken = CC
    private val fiatToken = FIAT

    private val amount = BigDecimal("3.0000${org.apache.commons.lang.RandomStringUtils.randomNumeric(4)}")
    private val dealAmount = BigDecimal("1.0000${org.apache.commons.lang.RandomStringUtils.randomNumeric(4)}")

    private val defaultPlaceFee = BigDecimal("2.00000000")
    private val defaultAcceptFee = BigDecimal("1.00000000")

    private val user1 = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4086")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Validation.")
    fun rfqSettingsValidation() {
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
                click(firstRow)
            }
            assert {
                elementPresented(edit)
                elementPresented(delete)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4097")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Change fee placing/accepting offer.")
    fun rfqSettingsChangeFeeAcceptingOffer() {

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(baseToken, baseToken, "1", "1", MODE_UNDEFINED)
        }

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                defaultFeePlacingOfferMaker.delete()
                sendKeys(defaultFeePlacingOfferMaker, defaultPlaceFee.toString())
                click(save)

                defaultFeeAcceptingOfferTaker.delete()
                sendKeys(defaultFeeAcceptingOfferTaker, defaultAcceptFee.toString())
                click(save)
            }
        }

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseToken, quoteToken, amount, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()

        val feeDeal = with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()

        val feeAccept = with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)
        }

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(baseToken, baseToken, "1", "1", FeeModeState.FIXED)
        }

        assertThat(
            "TRANSACTION FEE value equals the Default fee placing offer",
            feeDeal,
            Matchers.closeTo(defaultPlaceFee, BigDecimal("0.01"))
        )

        assertThat(
            "TRANSACTION FEE value equals the Default fee accepting offer",
            feeAccept,
            Matchers.closeTo(defaultAcceptFee, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4180")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Add incorrect token rate.")
    fun rfqSettingsAddIncorrectTokenRate() {
        val errorText = "Token is not found"
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
            }
            assert {
                elementDisabled(confirmDialog)
            }
            wait {
                until("pop up window is presented", 15) {
                    check {
                        isElementPresented(By.xpath(".//mat-option//span[@class='mat-option-text']"))
                    }
                }
            }
            assertThat(
                "Expected error text: $errorText",
                popUpWindow.getAttribute("innerHTML") == errorText
            )
        }
    }

    @Issue("ATMCH-6103")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4164")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Add token rate.")
    fun rfqSettingsAddTokenRate() {
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
                chooseToken(tokenInput, fiatToken.tokenSymbol)
                click(confirmDialog)
            }
            assert {
                elementWithTextPresented("Impossible to use fiat token \"${fiatToken.tokenSymbol}\" in secondary market")
            }
            e {
                click(clearTokenInput)
                chooseToken(tokenInput, tokenETC.tokenSymbol)
                chooseToken(feePlacingAsset, fiatToken.tokenSymbol)
                chooseToken(feeAcceptingAsset, fiatToken.tokenSymbol)
                click(confirmDialog)
            }
            assert {
                elementWithTextPresented("Impossible to use fiat token \"${fiatToken.tokenSymbol}\" in secondary market")
            }
            e {
                click(cancelDialog)
                click(add)
                chooseToken(tokenInput, tokenETC.tokenSymbol)
                setCheckbox(availableBase, true)
                setCheckbox(availableQuote, true)
                sendKeys(feePlacingAmount, "10")
                chooseToken(feePlacingAsset, tokenETC.tokenSymbol)
                select(feePlacingMode, FIXED.state)
                chooseToken(feeAcceptingAsset, tokenETC.tokenSymbol)
                sendKeys(feeAcceptingAmount, "10")
                select(feeAcceptingMode, FIXED.state)
                click(confirmDialog)
            }
            assert {
                elementContainingTextPresented(tokenETC.tokenSymbol)
            }
            deleteToken(tokenETC.tokenSymbol)
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4124")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Edit token rate.")
    fun rfqSettingsEditTokenRate() {

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(baseToken, baseToken, "3", "1.5", FeeModeState.FIXED)
        }

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(quoteToken, baseToken, "2", "2.7", FeeModeState.FIXED)
        }


        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseToken, quoteToken, amount, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()

        val feeDeal = with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()

        val feeAccept = with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)
        }

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(baseToken, baseToken, "1", "1", FeeModeState.FIXED)
        }

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            changeFeeSettingsForToken(quoteToken, baseToken, "1", "1", FeeModeState.FIXED)
        }

        assertThat(
            "FEE deal value equals fee placing amount",
            feeDeal,
            Matchers.closeTo(BigDecimal("3"), BigDecimal("0.01"))
        )

        assertThat(
            "FEE accept value equals fee accept amount",
            feeAccept,
            Matchers.closeTo(BigDecimal("1.5"), BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4166")
    @Test
    @DisplayName("Admin panel. OTF. RFQ settings. Delete token rate.")
    fun rfqSettingsDeleteTokenRate() {

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(add)
                tokenInputSelect.sendAndSelect(tokenETC.tokenSymbol, tokenETC.tokenSymbol, this@with)

                Thread.sleep(2000)
                click(confirmDialog)
                wait {
                    until("dialog add token is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
                deleteToken(tokenETC.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4981")
    @Test
    @DisplayName("Administration panel. OTF management. RFQ. Add token rate")
    fun rfqAddTokenRate() {

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

            val row = rfqSettingsTable.find {
                it[AtmAdminRfqSettingsPage.TOKEN]?.text == baseToken.tokenSymbol
            }?.get(AtmAdminRfqSettingsPage.TOKEN)
            if (row != null) {
                deleteToken(baseToken.tokenSymbol)
            }

            e {
                click(add)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(tokenInput)
                elementPresented(availableBase)
                elementPresented(availableQuote)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(feeAcceptingAsset)
                elementPresented(feeAcceptingAmount)
                elementPresented(feeAcceptingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                click(cancelDialog)
                addToken(
                    baseToken.tokenSymbol, true, true,
                    "1", FIXED.state, "2", FIXED.state
                )
                driver.navigate().refresh()
                wait {
                    until("wait until data presented", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assertThat(
                "Default asset saved",
                defaultAsset.value,
                Matchers.hasToString(baseToken.tokenSymbol)
            )
            assertThat(
                "Fee accepting asset saved",
                feeAcceptingAsset.value,
                Matchers.hasToString(baseToken.tokenSymbol)
            )
            assertThat(
                "Fee placing asset saved",
                feePlacingAsset.value,
                Matchers.hasToString(baseToken.tokenSymbol)
            )
            assertThat(
                "Fee placing amount saved",
                feePlacingAmount.value,
                Matchers.hasToString("1")
            )
            assertThat(
                "Fee accepting amount saved",
                feeAcceptingAmount.value,
                Matchers.hasToString("2")
            )
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_SECOND) }) {
            e {
                click(createRequest)
                select(assetToSend, baseToken.tokenSymbol)
                select(assetToReceive, baseToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5385")
    @Test
    @DisplayName("Administration panel. OTF management. RFQ. Edit token rate")
    fun rfqEditTokenRate() {

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
            val row = rfqSettingsTable.find {
                it[AtmAdminRfqSettingsPage.TOKEN]?.text == baseToken.tokenSymbol
            }?.get(AtmAdminRfqSettingsPage.TOKEN)
            if (row != null) {
                deleteToken(baseToken.tokenSymbol)
            }
            e {
                addToken(
                    baseToken.tokenSymbol, true, true,
                    "1", FIXED.state, "2", FIXED.state
                )
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assert {
                elementContainingTextPresented("Token")
                elementPresented(tokenInput)
                elementPresented(availableBase)
                elementPresented(availableQuote)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(feeAcceptingAsset)
                elementPresented(feeAcceptingAmount)
                elementPresented(feeAcceptingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                setCheckbox(availableBase, false)
                setCheckbox(availableQuote, false)
                click(confirmDialog)
                wait {
                    until("dialog add token is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
                driver.navigate().refresh()
                wait {
                    until("wait until data presented", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assertThat(false, Matchers.equalTo(availableBase.isChecked()))
            assertThat(false, Matchers.equalTo(availableQuote.isChecked()))
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_SECOND) }) {
            e {
                click(createRequest)
                click(assetToSend)
            }
            assert { elementContainingTextNotPresented(baseToken.tokenSymbol) }
            e {
                click(assetToReceive)
            }
            assert { elementContainingTextNotPresented(baseToken.tokenSymbol) }
        }
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseToken(baseToken.tokenSymbol)
                click(edit)
                setCheckbox(availableBase, true)
                setCheckbox(availableQuote, true)
                click(confirmDialog)
                wait {
                    until("dialog add token is gone", 15) {
                        check {
                            isElementGone(confirmDialog)
                        }
                    }
                }
                driver.navigate().refresh()
                wait {
                    until("wait until data presented", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assertThat(true, Matchers.equalTo(availableBase.isChecked()))
            assertThat(true, Matchers.equalTo(availableQuote.isChecked()))
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_SECOND) }) {
            e {
                click(createRequest)
                select(assetToSend, baseToken.tokenSymbol)
                select(assetToReceive, baseToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5386")
    @Test
    @DisplayName("Administration panel. OTF management. RFQ. Delete token rate")
    fun rfqDeleteTokenRate() {

        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTokenIfNotPresented(quoteToken)
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
                deleteToken(quoteToken.tokenSymbol)
                driver.navigate().refresh()
                wait {
                    until("wait until data presented", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }
            }
            val row = rfqSettingsTable.find {
                it[AtmAdminRfqSettingsPage.TOKEN]?.text == quoteToken.tokenSymbol
            }?.get(AtmAdminRfqSettingsPage.TOKEN)
            Assert.assertTrue("Row not equal null", row == null)
        }
        with(openPage<AtmRFQPage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_SECOND) }) {
            e {
                click(createRequest)
                click(assetToSend)
            }
            assert { elementContainingTextNotPresented(quoteToken.tokenSymbol) }
            e {
                click(assetToReceive)
            }
            assert { elementContainingTextNotPresented(quoteToken.tokenSymbol) }
        }
        with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addToken(quoteToken.tokenSymbol, true, true)
        }
    }

    @Issue("ATMCH-6103")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5387")
    @Test
    @DisplayName("Administration panel. OTF management. RFQ. Default values")
    fun rfqDefaultValues() {

        val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"
        val defaultFeePlacingOfferInputTakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"
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
                click(clearButton)
                sendKeys(defaultAsset, fiatToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented("Token is not found")
            }
            e {
                click(clearButton)
                sendKeys(defaultAsset, baseToken.tokenSymbol)
                chooseToken(defaultAsset, baseToken.tokenSymbol)
                if (check { isElementPresented(save) }) {
                    click(save)
                }
                defaultFeePlacingOfferMaker.delete()
                sendKeys(defaultFeePlacingOfferMaker, defaultFeePlacingOfferInputMakerValueNew)
                click(save)
                defaultFeeAcceptingOfferTaker.delete()
                sendKeys(defaultFeeAcceptingOfferTaker, defaultFeePlacingOfferInputTakerValueNew)
                click(save)
                driver.navigate().refresh()
                wait {
                    until("wait for loading page after refresh", 15) {
                        check {
                            isElementPresented(defaultAsset)
                        }
                    }
                }

            }
            assertThat(
                "Default asset saved",
                defaultAsset.value,
                Matchers.hasToString(baseToken.tokenSymbol)
            )
            assertThat(
                "Default fee placing offer (Maker) saved",
                defaultFeePlacingOfferMaker.value,
                Matchers.hasToString(defaultFeePlacingOfferInputMakerValueNew)
            )
            assertThat(
                "Default fee placing offer (Taker) saved",
                defaultFeeAcceptingOfferTaker.value,
                Matchers.hasToString(defaultFeePlacingOfferInputTakerValueNew)
            )

            val row = rfqSettingsTable.find {
                it[AtmAdminRfqSettingsPage.TOKEN]?.text == baseToken.tokenSymbol
            }?.get(AtmAdminRfqSettingsPage.TOKEN)
            if (row != null) {
                deleteToken(baseToken.tokenSymbol)
            }
            addToken(baseToken.tokenSymbol, true, true)

        }
    }

}