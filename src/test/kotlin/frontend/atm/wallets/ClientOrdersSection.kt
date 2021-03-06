package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import pages.atm.AtmOrdersPage
import pages.atm.AtmWalletPage
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Client orders section (buy and buyback)")
class ClientOrdersSection : BaseTest() {

    @TmsLink("ATMCH-2279")
    @Test
    @DisplayName("VT. Buy order in status EXECUTED.")
    fun vtBuyOrderInStatusExecuted() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.mainWallet
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        prerequisite {
            addCurrencyCoinToWallet(user, "10", mainWallet)
        }
        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyTokenNew(VT, amount.toString(), user, mainWallet)

        with(openPage<AtmOrdersPage>(driver) { submit(user) }) {
            findOrderAndCheckStatus(mainWallet, VT, amount, "executed")
            assert {
                elementContainingTextPresented(" BUY")
            }
        }
    }

    @TmsLink("ATMCH-2281")
    @Test
    @DisplayName("СС. Buyback order with status Executed")
    fun ccBuybackOrderWithStatusExecuted() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.mainWallet
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        prerequisite {
            addCurrencyCoinToWallet(user, "10", mainWallet)
        }
        openPage<AtmWalletPage>(driver) { submit(user) }.redeemToken(
            CC,
            mainWallet,
            amount.toString(),
            "",
            user
        )

        with(openPage<AtmOrdersPage>(driver) { submit(user) }) {
            findOrderAndCheckStatus(mainWallet, CC, amount, "executed")
            assert {
                elementContainingTextPresented(" BUY")
            }
        }
    }

    @TmsLink("ATMCH-2282")
    @Test
    @DisplayName(" СС. Buy order in status executed.")
    fun ccBuyOrderInStatusExecuted() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.mainWallet
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        prerequisite {
            addCurrencyCoinToWallet(user, amount.toString(), mainWallet)
        }

        with(openPage<AtmOrdersPage>(driver) { submit(user) }) {
            findOrderAndCheckStatus(mainWallet, CC, amount, "executed")
            assert {
                elementContainingTextPresented(" BUY")
            }
        }
    }

    @TmsLink("ATMCH-2283")
    @Test
    @DisplayName("Orders. Select wallet")
    fun ordersSelectWallet() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.mainWallet

        prerequisite {
            addCurrencyCoinToWallet(user, "1", mainWallet)
        }

        with(openPage<AtmOrdersPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            assert {
//                elementWithTextPresented(" PENDING ")
                elementPresented(orderTokenItem)
            }
            chooseToken(CC)
            assert {
                elementPresented(currencyCoinButton)
                elementWithTextPresented(" PENDING ")
            }
        }
    }


}