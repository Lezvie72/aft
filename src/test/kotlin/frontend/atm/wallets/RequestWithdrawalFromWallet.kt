package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.FIAT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmBankAccountsPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.Users.Companion.ATM_ADMIN
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Withdrawal")
class RequestWithdrawalFromWallet : BaseTest() {

    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-1279")
    @Test
    @DisplayName("Withdraw fiat. Interface. User have bank information")
    fun withdrawFiatInterfaceUserHaveBankInformation() {
        val user = Users.ATM_USER_2MAIN_WALLET

        val mainWallet = user.mainWallet

        val balanceWallet =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(FIAT, mainWallet.name)
                .toBigDecimal()
        val moreThanBalance = (balanceWallet + BigDecimal.ONE).toString()

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            chooseToken(FIAT)
            e {
                click(withdraw)
            }
            //region less then max
            e {
                deleteData(amountTransfer)
                sendKeys(
                    amountTransfer,
                    (balanceWallet - (balanceWallet * BigDecimal(0.02))).toString()//make amount to transfer less than balance
                )
            }
            assert {
                elementWithTextNotPresented(" Not enough USD to pay fee for this withdrawal ")
                elementWithTextNotPresented("redeem currency token")//должен быть линк
            }
            //endregion
            //region more than maximum
            e {
                deleteData(amountTransfer)
                sendKeys(amountTransfer, moreThanBalance)
            }
            assert {
                elementWithTextPresented(" Not enough USD to pay fee for this withdrawal ")
                elementWithTextPresented("redeem currency token")
            }
            //endregion
            //region zero
            e {
                deleteData(amountTransfer)
            }
            assert {
                elementWithTextPresented(" Enter Amount to withdraw ")
                elementPresented(disabledSubmitButton)
            }
            //endregion
            //region equal to balance
            e {
                deleteData(amountTransfer)
                sendKeys(amountTransfer, (balanceWallet).toString())
            }
            assert {
                elementWithTextNotPresented(" Not enough USD to pay fee for this withdrawal ")
                elementWithTextNotPresented("redeem currency token")//должен быть линк
            }
            //endregion
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-1278")
    @Test
    @DisplayName("Withdraw fiat. Main active wallet. User is controller and have 2FA")
    fun withdrawFiatMainActiveWalletUserIsControllerAndHave2FA() {
        val amount = "10"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET


        val wallet = user.mainWallet

        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        with(openPage<AtmAdminPaymentsPage>(driver) { submit(ATM_ADMIN) }) {
            addPayment(alias, amount)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            withdrawToken(wallet, "USD", "1", user)
            assert { elementWithTextPresentedIgnoreCase("Your withdrawal request registered successfully") }
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-1327")
    @Test
    @DisplayName("Withdraw fiat. User haven't bank information")
    fun withdrawFiatUserHaventBankInformation() {
        val amount = "10"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA

        val wallet = user.mainWallet

        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        with(openPage<AtmAdminPaymentsPage>(driver) { submit(ATM_ADMIN) }) {
            addPayment(alias, amount)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(FIAT)
                click(withdraw)
                click(addBankDetails)
            }
        }
        openPage<AtmBankAccountsPage>(driver).addBankAccount(
            "Russian BIC",
            "1223456",
            "Tesst",
            "Test",
            "1234567890123456",
            "USD",
            "Vavilova"
        )
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(FIAT)
                click(withdraw)
                sendKeys(amountTransfer, amount)
                select(currency, "USD")
                select(selectBankDetails, "Tesst")
                click(submitButton)
                click(submitButton)
            }
            signAndSubmitMessage(user, wallet.secretKey)
            assert { elementWithTextPresented(" Your withdrawal request registered successfully ") }
        }
        with(openPage<AtmBankAccountsPage>(driver) { submit(user) }) {
            try {
                step("Post test cleaning") {
                    chooseBankAccountDetails("Tesst")
                    deleteBankAccountDetails("Tesst")
                }
            } catch (e: Exception) {
                print("Card with '${"Tesst"}' not found")
                false
            }

        }
    }

}