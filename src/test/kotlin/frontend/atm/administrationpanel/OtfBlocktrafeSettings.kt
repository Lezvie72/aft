package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmP2PPage.ExpireType
import pages.atm.AtmP2PPage.ExpireType.*
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.isChecked
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("OTF. Blocktrade Settings")
class OtfBlocktrafeSettings : BaseTest() {


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
                click(confirmDialog)
            }
            assertThat(
                "Expected error text: $errorText",
                tokenInput.errorText == errorText
            )

        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4167")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade/P2P settings. Add token.")
    fun blocktradeSettingsAddTokenRate() {
        val token = "FT"
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTokenIfNotPresented(
                token,
                true,
                "10",
                token,
                "FIXED",
                token,
                "10",
                "FIXED"
            )
        }

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteToken(token)
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
                click(cancelDialog)
            }
            addNewToken(
                token,
                true,
                "10",
                token,
                "FIXED",
                token,
                "10",
                "FIXED"
            )
            assert {
                elementPresented(fiatToken)
            }
            with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
                e {
                    click(createBlockTrade)
                    select(assetToSend, token)
                }
                assert {
                    elementContainingTextPresented(token)
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
            addTokenIfNotPresented(
                "FIAT",
                true,
                "10",
                "FIAT",
                "FIXED",
                "FIAT",
                "10",
                "FIXED"
            )
        }

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            deleteToken("FIAT")
            e {
                click(add)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                click(assetToSend)
            }
            assert {
                elementContainingTextNotPresented("FIAT")
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addNewToken(
                "FIAT",
                true,
                "10",
                "FIAT",
                "FIXED",
                "FIAT",
                "10",
                "FIXED"
            )
            assert {
                elementContainingTextPresented("FIAT")
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4125")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade/P2P settings. Edit token.")
    fun blocktradeSettingsEditToken() {
        val token = "FT"
        val number = RandomStringUtils.random(2, false, true)
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addTokenIfNotPresented(
                token,
                true,
                "10",
                token,
                "FIXED",
                token,
                "10",
                "FIXED"
            )
        }

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseToken(token)
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
                sendKeys(feePlacingAsset, token)
                select(feePlacingMode, "VOLUME")
                sendKeys(feeAcceptingAsset, token)
                sendKeys(feeAcceptingAmount, number)
                select(feeAcceptingMode, "VOLUME")
                click(confirmDialog)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                select(assetToSend, token)
            }
            assert {
                elementContainingTextPresented(token)
            }
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4098")
    @Test
    @DisplayName("Admin panel. OTF. Blocktrade settings. Change default fee placing/accepting offer.")
    fun blocktradeSettingsChangeFee() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val defaultAssetValueNew = "CC"

        val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(7)+1}"
        val defaultFeePlacingOfferInputTakerValueNew ="0.${RandomStringUtils.randomNumeric(7)+1}"
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

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
            e{
                defaultAsset.delete()
                sendKeys(defaultAsset, defaultAssetValueNew)
                chooseToken(defaultAsset, defaultAssetValueNew)
                if (check { isElementPresented(save) }){ click(save) }
                defaultFeePlacingOfferMaker.delete()
                sendKeys(defaultFeePlacingOfferMaker, defaultFeePlacingOfferInputMakerValueNew)
                assert {
                    elementPresented(save)
                }
                click(save)
                defaultFeeAcceptingOfferTaker.delete()
                sendKeys(defaultFeeAcceptingOfferTaker, defaultFeePlacingOfferInputTakerValueNew)
                assert {
                    elementPresented(save)
                }
                click(save)
                deleteToken(defaultAssetValueNew)
                addNewDefaultToken(defaultAssetValueNew)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e{
                click(createBlockTrade)
                chooseCounterpartyPopupList(toWallet, "autotest")
                click(amountToSend)
                select(assetToReceive, defaultAssetValueNew)
                select(assetToSend, defaultAssetValueNew)
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
            createP2P(walletID, "autotest",
                CoinType.CC, amount.toString(),
                CoinType.CC, amount.toString(), TEMPORARY, user1)
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
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5930")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Default values")
    fun blocktradeDefaultValues() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val defaultAssetValueNew = "CC"

        val defaultFeePlacingOfferInputMakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"
        val defaultFeePlacingOfferInputTakerValueNew = "0.${RandomStringUtils.randomNumeric(7) + 1}"

        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addNewDefaultTokenIfNotPresented(defaultAssetValueNew)

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
                defaultAsset.delete()
                sendKeys(defaultAsset, defaultAssetValueNew)
                chooseToken(defaultAsset, defaultAssetValueNew)
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
                Matchers.hasToString(defaultAssetValueNew)
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
                deleteToken(defaultAssetValueNew)
                addNewDefaultToken(defaultAssetValueNew)
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                chooseCounterpartyPopupList(toWallet, "autotest")
                click(amountToSend)
                select(assetToReceive, defaultAssetValueNew)
                select(assetToSend, defaultAssetValueNew)
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
                CoinType.CC, amount.toString(),
                CoinType.CC, amount.toString(),
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
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5388")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Edit token")
    fun blocktradeEditToken() {
        val defaultAssetValueNew = "CC"
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
                deleteToken(defaultAssetValueNew)
                addNewDefaultToken(defaultAssetValueNew)
                chooseToken(defaultAssetValueNew)
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
                chooseToken(defaultAssetValueNew)
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
                elementContainingTextNotPresented(defaultAssetValueNew)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                chooseToken(defaultAssetValueNew)
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
                chooseToken(defaultAssetValueNew)
                click(edit)
                assertThat(true, Matchers.equalTo(available.isChecked()))
            }
        }
        with(openPage<AtmP2PPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(createBlockTrade)
                select(assetToSend, defaultAssetValueNew)
            }
            assert {
                elementContainingTextPresented(defaultAssetValueNew)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-5389")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Delete trading pair")
    fun blocktradeDeleteTradingPair() {
        val defaultAssetValue = "CC"

        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {

            addNewDefaultTokenIfNotPresented(defaultAssetValue)

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
                chooseToken(defaultAssetValue)
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
                elementContainingTextNotPresented(defaultAssetValue)
            }
        }
        with(openPage<AtmAdminBlocktradeSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                addNewDefaultToken(defaultAssetValue)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-4982")
    @Test
    @DisplayName("Administration panel. OTF management. Blocktrade. Add token")
    fun blocktradeAddToken() {
        val defaultAssetValue = "CC"

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
                deleteToken(defaultAssetValue)
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
                defaultAssetValue, true, "1", defaultAssetValue,
                "FIXED", defaultAssetValue, "2", "FIXED"
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
                chooseToken(defaultAssetValue)
                click(edit)
            }
            assertThat(
                "Default asset saved",
                defaultAsset.value,
                Matchers.hasToString(defaultAssetValue)
            )
            assertThat(
                "Fee accepting asset saved",
                feeAcceptingAsset.value,
                Matchers.hasToString(defaultAssetValue)
            )
            assertThat(
                "Fee placing asset saved",
                feePlacingAsset.value,
                Matchers.hasToString(defaultAssetValue)
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
                select(assetToSend, defaultAssetValue)
                select(assetToReceive, defaultAssetValue)
            }
            assert {
                elementContainingTextPresented(defaultAssetValue)
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
                "FIAT",
                false,
                "10",
                "FIAT",
                "FIXED",
                "FIAT",
                "10",
                "FIXED"
            )
            e {
                chooseToken("FIAT")
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
                elementContainingTextNotPresented("FIAT")
            }
        }
    }


}