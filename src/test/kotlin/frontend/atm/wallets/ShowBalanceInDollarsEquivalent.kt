package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmAdminTokensPage.EquivalentType.*
import pages.atm.AtmIssuancesPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Show balance in dollars equivalent")
class ShowBalanceInDollarsEquivalent : BaseTest() {

    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-2248")
    @Test
    @DisplayName(" USD equivalent, fractional rate")
    fun usdEquivalentFractionalRate() {
        val value = "1.24"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val wallet = user.mainWallet

        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            editUsdEquivalent("VT", FIXED, value,"")
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            setDisplayZeroBalance(true)
            val balanceFromWallet = balanceUser.amount
            val usdBalanceVT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(VT,wallet.name)
            val usdBalanceCC = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(CC,wallet.name)
            val usdBalanceFT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(FT,wallet.name)
            val usdBalanceIT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(IT,wallet.name)
            val balanceVT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(VT,wallet.name)
            val usdBalanceFiat = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(FIAT,wallet.name)
            //TODO: how system makes rounding for USD equivalent (it's displayed without decimal part) so assertions can failed
            assertThat(
                "Expected base balance: $usdBalanceVT, was: $balanceVT",
                usdBalanceVT,
                closeTo(balanceVT * value.toBigDecimal(), BigDecimal("0.01"))
            )
            assertThat(
                "Expected base balance: $balanceFromWallet, was: sum of balance tokens in usd equivalent",
                balanceFromWallet,
                closeTo(usdBalanceCC + usdBalanceFT + usdBalanceFiat + usdBalanceVT + usdBalanceIT, BigDecimal("0.01"))
            )
        }
    }

    @TmsLink("ATMCH-2247")
    @Test
    @DisplayName("USD equivalent, integer rate")
    fun usdEquivalentIntegerRate() {
        val value = "1.00"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val wallet = user.mainWallet

        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            editUsdEquivalent("VT",FIXED, value,"")
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            setDisplayZeroBalance(true)
            val balanceFromWallet = balanceUser.amount
            val usdBalanceVT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(VT,wallet.name)
            val usdBalanceCC = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(CC,wallet.name)
            val usdBalanceFT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(FT,wallet.name)
            val usdBalanceIT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(IT,wallet.name)
            val balanceVT = openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(VT,wallet.name)
            val usdBalanceFiat = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForUSD(FIAT,wallet.name)
            assertThat(
                "Expected base balance: $usdBalanceVT, was: $balanceVT",
                usdBalanceVT,
                closeTo(balanceVT * value.toBigDecimal(), BigDecimal("0.01"))
            )
            assertThat(
                "Expected base balance: $balanceFromWallet, was: sum of balance tokens in usd equivalent",
                balanceFromWallet,
                closeTo(usdBalanceCC + usdBalanceFT + usdBalanceFiat + usdBalanceVT + usdBalanceIT, BigDecimal("0.01"))
            )
        }
    }
}