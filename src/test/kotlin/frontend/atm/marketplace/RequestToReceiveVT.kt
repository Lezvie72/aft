package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Requset to receive VT")
class RequestToReceiveVT : BaseTest() {

    @TmsLink("ATMCH-688")
    @Test
    @DisplayName("VT data. Interface")
    fun vtDataInterface() {
        with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
            assert {
                elementContainingTextPresented("VALIDATION TOKEN")
                elementContainingTextPresented("Token type")
                elementContainingTextPresented("Validation Token")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Buy")

            }
            chooseToken(VT)
            assert {
                elementContainingTextPresented("Issuer description")
//                elementContainingTextPresented("Deal types")
                elementContainingTextPresented("Total supply")
//                elementContainingTextPresented("Transfer")
//                elementContainingTextPresented("On sale")
                elementContainingTextPresented("In circulation")
                elementContainingTextPresented("TRANSFER FEE")
                elementContainingTextPresented("Charged in")
                elementContainingTextPresented("Fee rate (%)")
                elementContainingTextPresented("Floor")
                elementContainingTextPresented("Cap")
                elementContainingTextPresented("ATTACHMENTS")
//                elementContainingTextPresented("Document #1")
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-863")
    @Test
    @DisplayName("Buying VT")
    fun buyingVT() {
        val amount = "1"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet

        val balanceBefore =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(VT, wallet.name)
        preset(user, "100", wallet)
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyToken(VT, wallet.publicKey, "1", user, wallet.secretKey)
            assert {
                elementWithTextPresented(" Order completed successfully ")
            }
        }
        with(AtmMarketplacePage(driver)) {
            e {
                click(doneButton)
            }
            assert {
                urlMatches(".*/orders$")
            }

        }
        val balanceAfter = openPage<AtmWalletPage>().getBalanceFromWalletForToken(VT, wallet.name)
        val expectedBalanceAfter = balanceBefore.toBigDecimal() + amount.toBigDecimal()
        assertThat(
            "Expected base balance: $expectedBalanceAfter, was: $balanceAfter",
            balanceAfter.toBigDecimal(),
            closeTo(expectedBalanceAfter, BigDecimal("0.01"))
        )

    }

    private fun preset(user: DefaultUser, amount: String, wallet: SimpleWallet) {
        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, amount)
        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyTokenNew(CC, amount, user, wallet)
    }

    @Disabled("Пользователь для токенов не имеет прав")
    @TmsLink("ATMCH-2329")
    @Test
    @DisplayName("VT. Receive order in status DECLINED. ")
    fun vtRecieveOrderInStatusDeclined() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val acceptTokensUser = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

        val tokenSymbolName = VT
        val amount = "1.00000000"

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(tokenSymbolName)
            e {
                click(newOrderButton)
                click(receiveButton)
                select(selectWallet, user.mainWallet.publicKey)
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(user.oAuthSecret, user.mainWallet.secretKey)

            assert {
                elementContainingTextPresented("Done")
            }
        }


        val oldBalance =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(tokenSymbolName, user.mainWallet.name)
        val oldId = openPage<AtmOrdersPage>(driver) { submit(user) }
            .findSubmittedOrder(tokenSymbolName, user.mainWallet, user.email, BigDecimal(amount)).requestedId.text

        AtmProfilePage(driver).logout()

        openPage<AtmIssuancesPage>(driver) { submit(acceptTokensUser) }.findOffersForTokenById(
            tokenSymbolName,
            oldId,
            AtmIssuancesPage.StatusType.DECLINE,
            acceptTokensUser,
            acceptTokensUser.mainWallet
        )

        AtmProfilePage(driver).logout()

        val newBalance =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(tokenSymbolName, user.mainWallet.name)

        assertThat(
            "Old balance equal to new one",
            oldBalance,
            Matchers.equalTo(newBalance)
        )

        with(openPage<AtmOrdersPage>(driver) { submit(user) }) {
            val order = findDeclinedOrder(tokenSymbolName, user.mainWallet, user.email, BigDecimal(amount))

            assertThat(
                "Ids equal",
                oldId,
                Matchers.equalTo(order.requestedId.text)
            )

            assert {
                elementPresented(order.totalRequested)
                elementPresented(order.submittedDate)
                // В данный момент отсутствует параметр validThru
                elementPresented(order.validThroughDate)
                elementPresented(order.issuer)
                elementPresented(order.requestedId)
                elementPresented(order.requestor)
                elementPresented(order.signature)
            }
        }


    }

    @Disabled("Пользователь для токенов не имеет прав")
    @TmsLink("ATMCH-3234")
    @Test
    @DisplayName("VT receiving. Signing during order creating")
    fun vtRecievingSigningDuringOrderCreating() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val acceptTokensUser = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

        val tokenSymbolName = VT
        val amount = "1.00000000"

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(tokenSymbolName)
            e {
                click(newOrderButton)
                click(receiveButton)
                select(selectWallet, user.mainWallet.publicKey)
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(user.oAuthSecret, user.mainWallet.secretKey)

            assert {
                elementContainingTextPresented("Done")
            }
        }

        val oldBalance =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(tokenSymbolName, user.mainWallet.name)
        val id = openPage<AtmOrdersPage>(driver) { submit(user) }
            .findSubmittedOrder(tokenSymbolName, user.mainWallet, user.email, BigDecimal(amount)).requestedId.text

        AtmProfilePage(driver).logout()

        openPage<AtmIssuancesPage>(driver) { submit(acceptTokensUser) }.findOffersForTokenById(
            tokenSymbolName,
            id,
            AtmIssuancesPage.StatusType.APPROVE,
            acceptTokensUser,
            acceptTokensUser.mainWallet
        )

        AtmProfilePage(driver).logout()

        val newBalance =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(tokenSymbolName, user.mainWallet.name)

        assertThat(
            "Old balance equal to new one",
            oldBalance,
            Matchers.equalTo(newBalance - BigDecimal(amount))
        )
    }
}
