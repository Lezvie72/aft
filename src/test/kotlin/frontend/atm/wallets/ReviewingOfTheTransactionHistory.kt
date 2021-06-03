package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import pages.atm.AtmWalletPage.Companion.ASSERT_TEXT_BALANCES
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Reviewing of the transaction history")
class ReviewingOfTheTransactionHistory : BaseTest() {

    @TmsLink("ATMCH-642")
    @Test
    @DisplayName("Check all wallets (user haven't wallets)")
    fun checkAllWalletsUserHaventWallets() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_OAUTH_NOWALLETS) }) {
            assert {
                elementPresented(registerWalletButton)
                elementContainingTextPresented("You don't have a wallet. Please register a wallet")
            }
        }
    }

    @TmsLink("ATMCH-632")
    @Test
    @DisplayName("Check all wallets (user have wallets)")
    fun checkAllWallets() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(wallets)
            }
            assert {
                urlEndsWith("/wallets")
            }
        }
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            assert {
                elementPresented(registerWalletButton)
                elementPresented(otfWalletTicker)
                elementPresented(mainWalletTicker)
                elementContainingTextPresented("Type wallet")
            }
            e {
                click(typeWallet)
            }
            assert {
                elementContainingTextPresented("All")
                elementContainingTextPresented("MAIN")
                elementContainingTextPresented("OTF")
                elementContainingTextPresented("ISSUER")
            }
        }
    }

    @TmsLink("ATMCH-633")
    @Test
    @DisplayName("Check transaction history for active wallet")
    fun checkTransactionHistoryForActiveWallet() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val wallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            e {
                chooseToken(CC)
            }
            assert {
                elementPresented(walletName)
                elementPresented(walletStatus)
                elementPresented(showZeroBalance)
                elementWithTextPresented(" WALLET ID ")
                elementWithTextPresented(" WALLET TYPE ")
                elementWithTextPresented(" Underlying asset ")
                //TODO: rewrite to locator?
                elementWithTextPresented(ASSERT_TEXT_BALANCES)
                elementWithTextPresented(" Available ")
                //TODO: held in orders is hidden with https://sdexnt.atlassian.net/browse/ATMCH-4633
                //elementWithTextPresented(ASSERT_TEXT_HELD_IN_ORDERS)
                elementPresented(transfer)
                //Transactions section
//                elementWithTextPresented(" Type ")
                elementWithTextPresented(" Creation date ")
                elementWithTextPresented(" Time utc ")
//                elementWithTextPresented(" Items ")
                elementWithTextPresented(" Transaction amount ")
                elementWithTextPresented(" TX id ")


            }
        }
    }


}