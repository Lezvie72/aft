package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.*
import models.user.classes.DefaultUser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.time.LocalDate

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Fiat deposit")
class FiatDeposit : BaseTest() {

    private fun createAndRegisterUser(kycPassed: Boolean = false): DefaultUser {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, kycPassed)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        return user
    }

    @TmsLink("ATMCH-855")
    @Test
    @DisplayName("Admin panel. Transfer fiat to user")
    fun adminPanelTransferFiatToUser() {
        val date = LocalDate.now().toString()
        val amount = "10"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val userWallet = user.mainWallet

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(userWallet.name)
        }
        val balance = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalance(FIAT, userWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    click(addPaymentDialogButton)
                }
                assert {
                    elementPresented(currency)
                    elementPresented(amountPayment)
                    elementPresented(paymentId)
                    elementPresented(paymentDate)
                    elementPresented(clientAlias)
                    elementPresented(addPaymentsButton)
                }
                e {
                    select(currency, "USD")
                    sendKeys(paymentId, "213")
                    sendKeys(paymentDate, date)
                    sendKeys(clientAlias, alias)
                    sendKeys(amountPayment, amount)
                    click(addPaymentsButton)
                }
                wait(30L) {
                    until("Payment isn't gone") {
                        check {
                            isElementGone(paymentsDialog)
                        }
                    }
                }
            }
        }
        val balanceAfter = step("THEN User go to Wallet, get balance after send fiat and check them") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(
                FIAT, userWallet.name
            )
        }
        //TODO: how system makes rounding for USD equivalent (it's displayed without decimal part) so assertions can failed
        assertThat(
            balanceAfter,
            equalTo(balance + amount.toBigDecimal())
        )
    }
}