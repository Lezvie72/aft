package frontend.atm.validators

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import utils.Constants
import utils.helpers.Users

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Validators")
@Story("Validation check for \"amount\" fields.")
class ValidationCheckForAmountFields : BaseTest() {

    private val user1 = Users.ATM_USER_2FA_WITH_WALLET_MTEST01
    private val user2 = Users.ATM_USER_2FA_WITHOUT_WALLET_MTEST02
    private val testAmount = "99999.1234567891011123"

    @ResourceLock(Constants.ATM_USER_2FA_WITH_WALLET_MTEST01)
    @TmsLink("ATMCH-1937")
    @Test
    @DisplayName("In the Amount fields, you can only enter 8 characters after the decimal point.")
    fun validationCheckForAmountFieldForMain1WalletForIndustrialTokenForTransfer() {
        with(utils.helpers.openPage<AtmWalletPage>(driver) { submit(user1) }) {
            waitWalletsAreDisplayed()
            chooseWallet("Main 1")
            setSumTransferFieldIT(testAmount)
            checkTransferFieldEightDigitsDecimal()
            utils.helpers.openPage<AtmWalletPage>(driver)
            chooseWallet("Main 1")
            setSumRedeemFieldIT(testAmount)
            checkTransferFieldEightDigitsDecimal()
            utils.helpers.openPage<AtmWalletPage>(driver)
            chooseWallet("OTF 1")
            setSumTransferFieldIT(testAmount)
            checkTransferFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmStreamingPage>(driver)) {
            setSumUnitPriceField(testAmount)
        }
        with(AtmWalletPage(driver)) {
            checkTransferFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmRFQPage>(driver)) {
            setSumSelectAmountField(testAmount)
        }
        with(AtmWalletPage(driver)) {
            checkTransferFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmP2PPage>(driver)) {
            setSumSelectAmountFields(testAmount)
            checkTransferFieldEightDigitsDecimal()
        }
        with(utils.helpers.openPage<AtmMarketplacePage>(driver)) {
            choseCurrencyToken()
        }
        with(AtmWalletPage(driver)) {
            setSumTokenQuantityToBuyField(testAmount)
            checkTransferFieldEightDigitsDecimal()
        }
        with(AtmStreamingPage(driver)) {
            choseIndustrialToken()
        }
        with(utils.helpers.openPage<AtmWalletPage>(driver)) {
            setSumTokenQuantityToReceiveField(testAmount)
            checkTransferFieldEightDigitsDecimal()
        }
    }
}