package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Validation check for \"amount\" fields.")
class ValidationCheckForAmountFields : BaseTest() {
    private val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST06
    private val testAmount = "99999.1234567891011123"

    private val mainWalletObject = user1.mainWallet
    private val otfWalletObject = user1.otfWallet

    @ResourceLock(Constants.ATM_USER_2FA_WITH_WALLET_MTEST06)
    @TmsLink("ATMCH-1937")
    @Test
    @DisplayName("In the Amount fields, you can only enter 8 characters after the decimal point.")
    fun validationCheckForAmountFields() {
        with(utils.helpers.openPage<AtmAdminGeneralSettingsPage>(driver) { submit(user1) }) {
            pageIsDisplayed()
            checkingTogglesStatusAndSwitchingToCorrect()
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver){ submit(user1) }) {
            waitWalletsAreDisplayed()
            chooseWallet(mainWalletObject.name)
            setSumTransferFieldIT(testAmount)
            checkInputFieldEightDigitsDecimal()
            utils.helpers.openPage<AtmWalletPage>(driver)
            chooseWallet(mainWalletObject.name)
            setSumRedeemFieldIT(testAmount)
            checkInputFieldEightDigitsDecimal()
            utils.helpers.openPage<AtmWalletPage>(driver)
            chooseWallet(otfWalletObject.name)
            setSumTransferFieldIT(testAmount)
            checkInputFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            setSumUnitPriceField(testAmount)
        }
        with(AtmWalletPage(driver)) {
            checkInputFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmRFQPage>(driver)) {
            setSumSelectAmountField(testAmount)
        }
        with(AtmWalletPage(driver)) {
            checkInputFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmP2PPage>(driver)) {
            setSumSelectAmountFields(testAmount)
            checkTransferFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmMarketplacePage>(driver)) {
            chooseToken(CoinType.CC)
        }
        with(AtmWalletPage(driver)) {
            setSumTokenQuantityToBuyField(testAmount)
            checkInputFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmMarketplacePage>(driver)) {
            chooseToken(CoinType.IT)
        }
        with(AtmWalletPage(driver)) {
            setSumTokenQuantityToReceiveField(testAmount)
            checkInputFieldEightDigitsDecimal()
        }
    }
}