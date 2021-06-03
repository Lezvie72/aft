package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.VT
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmWalletPage
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Review of the contents of one wallet")
class ReviewOfTheContentsOfOneWallet : BaseTest() {

    @Disabled("ATMCH-948")
    @TmsLink("ATMCH-610")
    @Test
    @DisplayName("View the contents of one wallet - balances by assets. Empty equivalent (Main. Block wallet)")
    fun viewTheContentsOfOneWalletBalancesByAssetsEmptyEquivalentMainBlockWallet() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_WITH_BLOCK_WALLET) }) {
            e {
                click(mainWallet)
            }
            assert {
                elementPresented(refreshWalletData)
                elementPresented(blockWallet)
                elementPresented(assign)

            }
        }
    }

    @TmsLink("ATMCH-609")
    @Test
    @DisplayName("View the contents of one wallet - balances by assets. Empty equivalent (OTF. active wallet)")
    fun viewContentActiveOtfWallet() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(otfWalletTicker)
            }
            assert {
                elementPresented(walletName)
                elementPresented(walletStatus)
                elementPresented(walletId)
                elementPresented(walletBalance)
                elementPresented(otfWalletType)
                elementPresented(walletAuthorizationType)
                elementPresented(walletStorage)
                elementPresented(assign)
                elementPresented(showZeroBalance)
                elementPresented(yourEmployeeRole)
                elementPresented(yourWalletRole)
            }
            setDisplayZeroBalance(true)
            assert {
                elementPresented(tokenTiker)
                elementPresented(underlyingAsset)
                elementPresented(availableBalance)
                elementPresented(walletInsideBalance)
            }
        }
    }

    @TmsLink("ATMCH-1154")
    @Test
    @DisplayName("View the contents of one wallet - balances by assets. Empty equivalent (Main. active wallet)")
    fun viewTheContentsOfOneWalletBalancesByAssetsEmptyEquivalent() {
        //any user without VT on Main wallet
        val user = Users.ATM_USER_MAIN_OTF_MOVE

        val secondWallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(secondWallet.name)
            assert {
                elementContainingTextNotPresented(VT.tokenName)
            }
            setDisplayZeroBalance(true)
            assert {
                elementIsDisplayed("Wallet status")
                elementWithTextPresented(secondWallet.name)
                elementIsDisplayed("Show zero balance button")
                elementIsDisplayed("Assign button")
                elementIsDisplayed("Create wallet button")
                elementWithTextPresented(" WALLET ID ")
                elementWithTextPresented(" WALLET TYPE ")
                elementWithTextPresented(" SIGNATURE TYPE ")
                elementWithTextPresented(" STORAGE ")
                elementWithTextPresented(" BALANCE ")
                elementWithTextPresented(" AVAILABLE ")
                //TODO: held in orders is hidden with https://sdexnt.atlassian.net/browse/ATMCH-4633
//                elementWithTextPresented(" HELD IN ORDERS ")
                elementWithTextPresented(" UNDERLYING ASSET ")
                elementWithTextPresented(" YOUR EMPLOYEE ROLE ")
                elementWithTextPresented(" YOUR WALLET ROLE ")
                elementIsDisplayed("token's tiker")
                elementWithTextPresented(VT.tokenName)
            }
        }
    }
}