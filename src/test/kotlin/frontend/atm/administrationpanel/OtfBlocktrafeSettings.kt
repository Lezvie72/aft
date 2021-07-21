package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.*
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
import org.openqa.selenium.By
import pages.atm.AtmAdminBlocktradeSettingsPage
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.*
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType.TEMPORARY
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
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
@Story("OTF. Blocktrade Settings")
class OtfBlocktrafeSettings : BaseTest() {

    private val baseToken = CC
    private val quoteToken = VT
    private val tokenFT = FT
    private val tokenFiat = FIAT

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4087")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade settings. Validation.")
    fun blocktradeSettingsValidation() {
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
                click(firstRow)
            }
            assert {
                elementPresented(edit)
                elementPresented(delete)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4181")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade/P2P settings. Add incorrect token.")
    fun blocktradeSettingsAddIncorectTokenRate() {
        val errorText = "Token is not found"

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
    @TmsLink("ATMCH-4167")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade/P2P settings. Add token.")
    fun blocktradeSettingsAddTokenRate() {

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val row = blocktradeSettingsTable.find {
                it[AtmAdminBlocktradeSettingsPage.TOKEN]?.text == tokenFT.tokenSymbol
            }?.get(AtmAdminBlocktradeSettingsPage.TOKEN)
            if (row != null) {
                deleteToken(tokenFT.tokenSymbol)
            }
            e {
                click(add)
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
                chooseToken(tokenInput, tokenFiat.tokenSymbol)
                click(confirmDialog)
            }
            assert { elementWithTextPresented("Impossible to use fiat token \"${tokenFiat.tokenSymbol}\" in secondary market") }
            e {
                click(tokenClearButton)
                chooseToken(tokenInput, baseToken.tokenSymbol)
                chooseToken(feePlacingAsset, tokenFiat.tokenSymbol)
                chooseToken(feeAcceptingAsset, tokenFiat.tokenSymbol)
                click(confirmDialog)
            }
            assert { elementWithTextPresented("Impossible to use fiat token \"${tokenFiat.tokenSymbol}\" in secondary market") }
            e {
                click(cancelDialog)
            }
            val row1 = blocktradeSettingsTable.find {
                it[AtmAdminBlocktradeSettingsPage.TOKEN]?.text == tokenFT.tokenSymbol
            }?.get(AtmAdminBlocktradeSettingsPage.TOKEN)
            if (row1 == null) {
                addNewToken(
                    tokenFT.tokenSymbol,
                    true,
                    "10",
                    tokenFT.tokenSymbol,
                    FIXED.state,
                    tokenFT.tokenSymbol,
                    "10",
                    FIXED.state
                )
            }
            assert {
                elementContainingTextPresented(tokenFT.tokenSymbol)
            }
            with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
                e {
                    click(createBlockTrade)
                    select(assetToSend, tokenFT.tokenSymbol)
                }
                assert {
                    elementContainingTextPresented(tokenFT.tokenSymbol)
                }
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4168")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade settings. Delete token.")
    fun blocktradeSettingsDeleteToken() {

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val row = blocktradeSettingsTable.find {
                it[AtmAdminBlocktradeSettingsPage.TOKEN]?.text == baseToken.tokenSymbol
            }?.get(AtmAdminBlocktradeSettingsPage.TOKEN)
            if (row != null) {
                deleteToken(baseToken.tokenSymbol)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                click(assetToSend)
            }
            assert {
                elementContainingTextNotPresented(baseToken.tokenSymbol)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addNewToken(
                baseToken.tokenSymbol,
                true,
                "10",
                baseToken.tokenSymbol,
                FIXED.state,
                baseToken.tokenSymbol,
                "10",
                FIXED.state
            )
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4125")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade/P2P settings. Edit token.")
    fun blocktradeSettingsEditToken() {

        val number = RandomStringUtils.random(2, false, true)
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTokenIfNotPresented(
                tokenFT.tokenSymbol,
                true,
                "10",
                tokenFT.tokenSymbol,
                FIXED.state,
                tokenFT.tokenSymbol,
                "10",
                FIXED.state
            )
        }

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(tokenFT.tokenSymbol)
            assert {
                elementPresented(edit)
                elementPresented(delete)
            }
            e {
                click(edit)
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
                setCheckbox(available, true)
                sendKeys(feePlacingAmount, number)
                sendKeys(feePlacingAsset, tokenFT.tokenSymbol)
                select(feePlacingMode, VOLUME.state)
                sendKeys(feeAcceptingAsset, tokenFT.tokenSymbol)
                sendKeys(feeAcceptingAmount, number)
                select(feeAcceptingMode, VOLUME.state)
                click(confirmDialog)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                select(assetToSend, tokenFT.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(tokenFT.tokenSymbol)
            }
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4098")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade settings. Change default fee placing/accepting offer.")
    fun blocktradeSettingsChangeFee() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH

        val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"
        val defaultFeePlacingOfferInputTakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(baseToken.tokenSymbol)
            e {
                click(edit)
            }
            e {
                setCheckbox(available, true)
                select(feePlacingMode, MODE_UNDEFINED.state)
                Thread.sleep(2000)
                select(feeAcceptingMode, MODE_UNDEFINED.state)
                click(confirmDialog)
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
                click(clearButton)
                sendKeys(defaultAsset, baseToken.tokenSymbol)
                chooseToken(defaultAsset, baseToken.tokenSymbol)
                if (check { isElementPresented(assetSave) }) {
                    click(assetSave)
                }
                defaultFeePlacingOfferMaker.delete()
                sendKeys(defaultFeePlacingOfferMaker, defaultFeePlacingOfferInputMakerValueNew)
                assert {
                    elementPresented(placeSave)
                }
                click(placeSave)
                defaultFeeAcceptingOfferTaker.delete()
                sendKeys(defaultFeeAcceptingOfferTaker, defaultFeePlacingOfferInputTakerValueNew)
                assert {
                    elementPresented(acceptSave)
                }
                click(acceptSave)
                val row = blocktradeSettingsTable.find {
                    it[AtmAdminBlocktradeSettingsPage.TOKEN]?.text == baseToken.tokenSymbol
                }?.get(AtmAdminBlocktradeSettingsPage.TOKEN)
                if (row == null) {
                    addNewDefaultToken(baseToken.tokenSymbol)
                }
            }
        }
        val fee = with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID, companyName,
                baseToken, amount.toString(),
                quoteToken, amount.toString(), TEMPORARY, user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                findIncomingP2P(amount)
            }
            assertThat(
                "TRANSACTION FEE value equals the Default fee placing offer (maker)",
                fee,
                Matchers.hasToString(defaultFeePlacingOfferInputMakerValueNew)
            )

            assertThat(
                "TRANSACTION FEE value equals the Default fee placing offer (maker)",
                newOfferFee.amount,
                Matchers.hasToString(defaultFeePlacingOfferInputTakerValueNew)
            )
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(baseToken.tokenSymbol)
            e {
                click(edit)
            }
            e {
                setCheckbox(available, true)
                select(feePlacingMode, FIXED.state)
                Thread.sleep(2000)
                select(feeAcceptingMode, FIXED.state)
                click(confirmDialog)
            }
        }
    }

    @Issue("ATMCH-6103")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5930")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Default values")
    fun blocktradeDefaultValues() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"
        val defaultFeePlacingOfferInputTakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(baseToken.tokenSymbol)
            e {
                click(edit)
            }
            e {
                setCheckbox(available, true)
                select(feePlacingMode, MODE_UNDEFINED.state)
                Thread.sleep(2000)
                select(feeAcceptingMode, MODE_UNDEFINED.state)
                click(confirmDialog)
            }
        }

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addNewDefaultTokenIfNotPresented(baseToken.tokenSymbol)

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
                click(clearButton)
                sendKeys(defaultAsset, tokenFiat.tokenSymbol)
            }
            assert {
                elementContainingTextPresented("Token is not found")
            }
            e {
                click(clearButton)
                sendKeys(defaultAsset, baseToken.tokenSymbol)
                chooseToken(defaultAsset, baseToken.tokenSymbol)
                if (check { isElementPresented(assetSave) }) {
                    click(assetSave)
                }
                defaultFeePlacingOfferMaker.delete()
                sendKeys(defaultFeePlacingOfferMaker, defaultFeePlacingOfferInputMakerValueNew)
                click(placeSave)
                defaultFeeAcceptingOfferTaker.delete()
                sendKeys(defaultFeeAcceptingOfferTaker, defaultFeePlacingOfferInputTakerValueNew)
                click(acceptSave)
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
            e {
                deleteToken(baseToken.tokenSymbol)
                addNewDefaultToken(baseToken.tokenSymbol)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            e {
                click(createBlockTrade)
                waitSpinnerAlertDisappeared()
                sendKeys(toWallet, walletID)
                wait {
                    untilPresented<Button>(By.xpath("//nz-auto-option//div[contains(text(),'$companyName')]"))
                }.clickJS()
                click(amountToSend)
                select(assetToReceive, quoteToken.tokenSymbol)
                select(assetToSend, baseToken.tokenSymbol)
                sendKeys(amountToSend, amount.toString())
                sendKeys(amountToReceive, amount.toString())
            }
            assertThat(
                "TRANSACTION FEE value equals the Default fee placing offer (maker)",
                newOfferFee.amount,
                Matchers.hasToString(defaultFeePlacingOfferInputMakerValueNew)
            )
        }
        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                quoteToken, amount.toString(),
                baseToken, amount.toString(),
                TEMPORARY, user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                findIncomingP2P(amount)
            }
            assertThat(
                "TRANSACTION FEE value equals the Default fee placing offer (maker)",
                newOfferFee.amount,
                Matchers.hasToString(defaultFeePlacingOfferInputTakerValueNew)
            )
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(baseToken.tokenSymbol)
            e {
                click(edit)
            }
            e {
                setCheckbox(available, true)
                select(feePlacingMode, FIXED.state)
                Thread.sleep(2000)
                select(feeAcceptingMode, FIXED.state)
                click(confirmDialog)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5388")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Edit token")
    fun blocktradeEditToken() {

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
                deleteToken(baseToken.tokenSymbol)
                addNewDefaultToken(baseToken.tokenSymbol)
                chooseToken(baseToken.tokenSymbol)
                click(edit)
            }
            assert {
                elementContainingTextPresented("Edit token")
                elementPresented(tokenInput)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(feeAcceptingAmount)
                elementPresented(feeAcceptingAsset)
                elementPresented(feeAcceptingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
            e {
                setCheckbox(available, false)
                click(confirmDialog)
                wait {
                    until("wait when popup will close", 15) {
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
                assertThat(false, Matchers.equalTo(available.isChecked()))
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                click(assetToSend)
            }
            assert {
                elementContainingTextNotPresented(baseToken.tokenSymbol)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseToken(baseToken.tokenSymbol)
                click(edit)
                setCheckbox(available, true)
                click(confirmDialog)
                wait {
                    until("wait when popup will close", 15) {
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
                assertThat(true, Matchers.equalTo(available.isChecked()))
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                select(assetToSend, baseToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5389")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Delete trading pair")
    fun blocktradeDeleteTradingPair() {

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            addNewDefaultTokenIfNotPresented(baseToken.tokenSymbol)

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
                chooseToken(baseToken.tokenSymbol)
                click(delete)
                click(yes)
            }
            wait {
                until("dialog delete token is gone", 15) {
                    check {
                        isElementGone(yes)
                    }
                }
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                click(assetToSend)
            }
            assert {
                elementContainingTextNotPresented(baseToken.tokenSymbol)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                addNewDefaultToken(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4982")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Add token")
    fun blocktradeAddToken() {

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
                deleteToken(baseToken.tokenSymbol)
                click(add)
            }
            assert {
                elementContainingTextPresented("Add token")
                elementPresented(tokenInput)
                elementPresented(feePlacingAmount)
                elementPresented(feePlacingAsset)
                elementPresented(feePlacingMode)
                elementPresented(feeAcceptingAmount)
                elementPresented(feeAcceptingAsset)
                elementPresented(feeAcceptingMode)
                elementPresented(confirmDialog)
                elementPresented(cancelDialog)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addNewToken(
                baseToken.tokenSymbol, true,
                "1", baseToken.tokenSymbol,
                FIXED.state, baseToken.tokenSymbol,
                "2", FIXED.state
            )
            driver.navigate().refresh()
            wait {
                until("wait until data presented", 15) {
                    check {
                        isElementPresented(defaultAsset)
                    }
                }
            }
            e {
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
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                select(assetToSend, quoteToken.tokenSymbol)
                select(assetToReceive, baseToken.tokenSymbol)
            }
            assert {
                elementContainingTextPresented(baseToken.tokenSymbol)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5523")
    @Test
    @DisplayName("Admin panel. Blocktrade settings. Token unavailable")
    fun blocktradeTokenUnavailable() {

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTokenIfNotPresented(
                baseToken.tokenSymbol,
                false,
                "10",
                baseToken.tokenSymbol,
                FIXED.state,
                baseToken.tokenSymbol,
                "10",
                FIXED.state
            )
            e {
                chooseToken(baseToken.tokenSymbol)
                click(edit)
                setCheckbox(available, false)
                click(confirmDialog)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                click(assetToSend)
            }
            assert {
                elementContainingTextNotPresented(baseToken.tokenSymbol)
            }
        }
    }


}