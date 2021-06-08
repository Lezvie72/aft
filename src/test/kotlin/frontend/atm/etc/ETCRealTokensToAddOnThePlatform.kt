package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("ETC entity in the platform")
class ETCRealTokensToAddOnThePlatform : BaseTest() {

    //4507
    @TmsLink("ATMCH-4757")
    @Test
    @DisplayName("ETC. Displaying in wallets")
    fun etcDisplayingInWallets() {
        val user = Users.ATM_USER_FOR_ETC_TOKENS
        val mainWallet = user.mainWallet

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(ETC1.tokenName)
                    elementContainingTextPresented(ETC2.tokenName)
                    elementContainingTextPresented(ETC3.tokenName)
                    elementContainingTextPresented(ETC4.tokenName)
                    elementContainingTextPresented(ETC5.tokenName)
                    elementContainingTextPresented(ETC6.tokenName)
                }
            }
        }
        step("Admin go to token page and check token status") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                getTokenStatus(ETC1, "available")
                getTokenStatus(ETC2, "available")
                getTokenStatus(ETC3, "available")
                getTokenStatus(ETC4, "available")
                getTokenStatus(ETC5, "available")
                getTokenStatus(ETC6, "available")
            }
        }

    }

    @TmsLink("ATMCH-4657")
    @Test
    @DisplayName("ETC. Displaying the ETC token from the Issuer")
    fun etcDisplayingTheETCTokenFromTheIssuer() {
        val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val mainWallet = user.mainWallet

        step("User go to wallet, check token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    setDisplayZeroBalance(true)
                }
                assert {
                    elementContainingTextPresented(ETC1.tokenName)
                    elementContainingTextPresented(ETC2.tokenName)
                    elementContainingTextPresented(ETC3.tokenName)
                    elementContainingTextPresented(ETC4.tokenName)
                    elementContainingTextPresented(ETC5.tokenName)
                    elementContainingTextPresented(ETC6.tokenName)
                }
            }
        }
        step("Admin go to token page and check token status") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                getTokenStatus(ETC1, "available")
                getTokenStatus(ETC2, "available")
                getTokenStatus(ETC3, "available")
                getTokenStatus(ETC4, "available")
                getTokenStatus(ETC5, "available")
                getTokenStatus(ETC6, "available")
            }
        }

    }

    @TmsLink("ATMCH-4644")
    @Test
    @DisplayName("ETC. Displaying the ETС token on the marketplace")
    fun etcDisplayingTheETCTokenOnTheMarketplace() {
        val user = Users.ATM_USER_FOR_ETC_TOKENS

        step("User go to wallet, check token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                assert {
                    elementContainingTextPresented(ETC1.tokenName)
                    elementContainingTextPresented(ETC2.tokenName)
                    elementContainingTextPresented(ETC3.tokenName)
                    elementContainingTextPresented(ETC4.tokenName)
                    elementContainingTextPresented(ETC5.tokenName)
                    elementContainingTextPresented(ETC6.tokenName)
                }
            }
        }
        step("Admin go to token page and check token status") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                getTokenStatus(ETC1, "available")
                getTokenStatus(ETC2, "available")
                getTokenStatus(ETC3, "available")
                getTokenStatus(ETC4, "available")
                getTokenStatus(ETC5, "available")
                getTokenStatus(ETC6, "available")
            }
        }

    }
}

