package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Reviewing a list of wallets and their total balances")
class ReviewingListOfWalletsAndTheirTotalBalances : BaseTest() {

    @TmsLink("ATMCH-899")
    @Test
    @DisplayName("Wallets. User doesn't have wallets")
    fun userDoesntHaveWallets() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(wallets)
            }
            assert {
                urlEndsWith("/wallets")
            }
        }
    }

    @TmsLink("ATMCH-956")
    @Test
    @DisplayName("View the contents of the wallet-balances by assets (with the equivalent)")
    fun viewTheContentsOfTheWalletBalancesByAssets() {
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val walletMain = user.mainWallet
        val walletOTF = user.otfWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            assert {
                elementPresented(typeWallet)
                elementPresented(otfWalletTicker)
                elementPresented(mainWalletTicker)
            }
            e {
                click(typeWallet)
            }
            assert {
                elementWithTextPresentedIgnoreCase("MAIN")
                elementWithTextPresentedIgnoreCase("OTF")
                elementWithTextPresentedIgnoreCase("Wallet type")
                elementWithTextPresentedIgnoreCase("Balance")
                elementWithTextPresentedIgnoreCase("Active")
                elementContainingTextPresented(walletMain.name)
                elementContainingTextPresented(walletOTF.name)
            }
        }
    }


}