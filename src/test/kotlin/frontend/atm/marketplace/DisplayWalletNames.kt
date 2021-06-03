package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import pages.atm.AtmWalletPage.OperationType.REDEMPTION
import pages.atm.AtmWalletPage.OperationType.TRANSFER
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Display wallet names")
class DisplayWalletNames : BaseTest() {

    @TmsLink("ATMCH-2086")
    @Test
    @DisplayName("Display wallet names")
    fun displayWalletNames() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            checkWalletName(CC, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmMarketplacePage>(driver)) {
            checkWalletName(VT, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmMarketplacePage>(driver)) {
            checkWalletName(FT, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmMarketplacePage>(driver)) {
            checkWalletName(IT, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmWalletPage>(driver)) {
            chooseWallet(wallet.name)
            checkWalletName(REDEMPTION, CC, wallet.name, wallet.publicKey)
            checkWalletName(TRANSFER, CC, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmWalletPage>(driver)) {
            chooseWallet(wallet.name)
            e {
                click(showZeroBalance)
            }
            checkWalletName(REDEMPTION, IT, wallet.name, wallet.publicKey)
            checkWalletName(TRANSFER, IT, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmWalletPage>(driver)) {
            chooseWallet(wallet.name)
//            checkWalletName(REDEMPTION, VT, wallet.name, wallet.publicKey)
            checkWalletName(TRANSFER, VT, wallet.name, wallet.publicKey)
        }
        with(openPage<AtmWalletPage>(driver)) {
            chooseWallet(wallet.name)
            checkWalletName(REDEMPTION, FT, wallet.name, wallet.publicKey)
            checkWalletName(TRANSFER, FT, wallet.name, wallet.publicKey)
        }
    }

}
