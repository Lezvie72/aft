package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Transaction of the transfer with a fee")
class TransactionTransferWithFee : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-706")
    @Test
    @DisplayName("Transfer with fee. Wrong wallet")
    fun transferWithFeeWrongWallet() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList.get(0)
        val wrongWallet = "MIIEpQIBAAKCAQEA3Tz2mr7SZiAMfQyuvBjM9Oi..Z1BjP5CE/Wm/Rr500P"

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(firstMainWallet.name)
                chooseToken(CC)
                click(transfer)
                select(
                    fromWallet,
                    firstMainWallet.publicKey
                )
                sendKeys(
                    toWallet,
                    wrongWallet
                )
                sendKeys(amountTransfer, "1")
                sendKeys(transferNote, "note")
                assert {
                    elementContainingTextPresented("No wallet with this address")
                    elementDisabled(submitForCheckDisabling)
                }
            }

        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET),
        ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    )
    @TmsLink("ATMCH-1962")
    @Test
    @DisplayName("Transfer with fee. Transfer other users")
    fun transferWithFeeTransferOtherUsers() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val firstMainWallet = user.mainWallet
        val secondMainWallet = user2.mainWallet

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1.34", "1.432")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "2", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalanceFromWalletForToken(CC, secondMainWallet.name)
                .toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val (_, fee2) = openPage<AtmWalletPage>(driver) { submit(user) }.transferFromWalletToWallet(
            CC,
            firstMainWallet,
            secondMainWallet,
            "1",
            "",
            "",
            user

        )
        //TODO fee = 0E-8 притом что fee2 возвращает нормальное число
//        assertThat(
//            fee,
//            closeTo(fee2, BigDecimal("0.1"))
//        )
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalanceFromWalletForToken(CC, secondMainWallet.name)
                .toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore - BigDecimal.ONE - fee2, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-701")
    @Test
    @DisplayName("Transfer with fee. Insufficient funds")
    fun transferWithFeeInsufficientFunds() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]


        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        val amountToTransfer = balanceWalletFromBefore + BigDecimal.ONE
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(firstMainWallet.name)
                chooseToken(CC)
                click(transfer)
                select(
                    fromWallet,
                    firstMainWallet.publicKey
                )
                sendKeys(
                    toWallet,
                    secondMainWallet.publicKey
                )
                sendKeys(amountTransfer, amountToTransfer.toString())
                sendKeys(transferNote, "note")
            }
            assert {
                elementWithTextPresented(" Not enough CC to transfer ")
            }
        }
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-702")
    @Test
    @DisplayName("Transfer with fee. Wrong 2FA code")
    fun transferWithFeeWrong2FACode() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val anotherOauthKey = Users.ATM_USER_2FA_OTF_OPERATION

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(firstMainWallet.name)
                chooseToken(CC)
                click(transfer)
                select(fromWallet, firstMainWallet.publicKey)
                sendKeys(toWallet, secondMainWallet.publicKey)
                sendKeys(amountTransfer, "1")
                sendKeys(transferNote, "note")
                click(submit)
                e {
                    signMessage(firstMainWallet.secretKey)
                    submitConfirmationCode(anotherOauthKey.oAuthSecret)
                }
                assert { elementContainingTextPresented("Wrong code") }
            }
        }
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-704")
    @Test
    @DisplayName("Transfer with fee. Cancel transfer")
    fun transferWithFeeCancelTransfer() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1.54", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(firstMainWallet.name)
                chooseToken(CC)
                click(transfer)
                select(
                    fromWallet,
                    firstMainWallet.publicKey
                )
                sendKeys(
                    toWallet,
                    secondMainWallet.publicKey
                )
                sendKeys(amountTransfer, "1")
                sendKeys(transferNote, "note")
                click(cancel)
            }
            //TODO после починскитранзакций дописать проверку
        }
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-705")
    @Test
    @DisplayName("Transfer with fee. Wrong signature")
    fun transferWithFeeWrongSignature() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]
        val wrongSignature = "MIIEpQIBAAKCAQEA3Tz2mr7SZiAMfQyuvBjM9Oi..Z1BjP5CE/Wm/Rr500P"

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(firstMainWallet.name)
                chooseToken(CC)
                click(transfer)
                select(
                    fromWallet,
                    firstMainWallet.publicKey
                )
                sendKeys(
                    toWallet,
                    secondMainWallet.publicKey
                )
                sendKeys(amountTransfer, "1")
                sendKeys(transferNote, "note")
                click(submit)
                sendKeys(privateKey, wrongSignature)
                assert { elementContainingTextPresented(" Invalid key ") }
            }

        }
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-634")
    @Test
    @DisplayName("Transfer with fee")
    fun transferWithFee() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1.54", "1.52")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        val (_, fee2) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            transferFromWalletToWallet(CC, firstMainWallet, secondMainWallet, "1", "", "", user)
        }

//        assertThat(
//            fee.toBigDecimal(),
//            closeTo(fee2, BigDecimal("0.1"))
//        )
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore - BigDecimal.ONE - fee2, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-1963")
    @Test
    @DisplayName("Transfer with fee. Transfer with comment")
    fun transferWithFeeTransferWithComment() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "100", "1", "1.42")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        val (_, fee2) = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            transferFromWalletToWallet(CC, firstMainWallet, secondMainWallet, "1", "test", "", user)
        }
//        assertThat(
//            fee,
//            closeTo(fee2, BigDecimal("0.1"))
//        )
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore - BigDecimal.ONE - fee2, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_2MAIN_WALLET)
    @TmsLink("ATMCH-1341")
    @Test
    @DisplayName("Transfer without fee")
    fun transferWithoutFee() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET

        val firstMainWallet = user.walletList[0]
        val secondMainWallet = user.walletList[1]

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken("CC", "CC", "0", "0", "0")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyTokenNew(CC, "1", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            transferFromWalletToWallet(CC, firstMainWallet, secondMainWallet, "1", "test", "", user)
        }
//        assertThat(
//            fee,
//            closeTo(fee2, BigDecimal("0.1"))
//        )
        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, secondMainWallet.name).toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceWalletFromBefore - BigDecimal.ONE, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )

    }


}