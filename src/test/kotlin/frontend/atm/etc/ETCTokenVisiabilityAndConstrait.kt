package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Epic("Frontend")
@Feature("ETC")
@Story("ETC Token Visibility And Constraint")
class ETCTokenVisibilityAndConstraint : BaseTest() {


    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    @TmsLink("ATMCH-4272")
    @Test
    @DisplayName("ETC. Check client's ETC wallet")
    fun etcCheckClientEtcWallet() {
        val user = Users.ATM_USER_FOR_ETC_TOKENS_THIRD
        val mainWallet = user.walletList[0]
        step("User go to wallet, check button Redemption for ETC token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    setDisplayZeroBalance(true)
                    chooseToken(ETC)
                }
                assert { elementPresented(redemption) }
            }
        }
        step("User go to wallet, check balance") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                setDisplayZeroBalance(true)
                assert {
                    elementContainingTextPresented(ETC.tokenName)
                }
                chooseToken(ETC)
                assert {
                    elementPresented(balanceTokenUser)
                    elementPresented(heldInOrders)
                }
            }
        }
    }

    @TmsLink("ATMCH-4267")
    @Test
    @DisplayName("ETC. Check Marketplace for ETC token")
    fun etcCheckMarketplaceForETCToken() {
        val user = Users.ATM_USER_FOR_ETC_TOKENS
        val mainWallet = user.walletList[0]
        step("User go to wallet, check button Redemption for ETC token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                assert { elementContainingTextPresented(ETC.tokenName) }
                e {
                    chooseToken(ETC)
                }
                assert { elementNotPresented(newOrderButton) }
            }
        }
    }

    @TmsLink("ATMCH-4269")
    @Test
    @DisplayName("ETC. Check issuer's wallet")
    fun etcCheckIssuerWallet() {
        val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val mainWallet = user.walletList[0]
        val mainWallet1 = user.walletList[1]
        step("User go to wallet, check ETC token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                assert {
                    elementContainingTextPresented(ETC.tokenName)
                }
                chooseToken(ETC)
                assert {
                    elementPresented(transferEtc)
                    elementNotPresented(redemption)
                }
            }
        }
    }

}