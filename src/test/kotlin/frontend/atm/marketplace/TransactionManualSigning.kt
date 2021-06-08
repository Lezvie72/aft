package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.FIAT
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Display wallet names")
class TransactionManualSigning : BaseTest() {


    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-426")
    @Test
    @DisplayName("Buying a Currency Token using a manual signature. Integer amount")
    fun buyingCurrencyTokenUsingManualSignatureIntegerAmount() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        val amount = "2"
        step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            val alias = openPage<AtmWalletPage>().getAlias()
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "100")
            }
        }
        val balanceBefore = openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, wallet.name)
        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver)) {
                buyToken(CC, wallet.publicKey, amount, user, wallet.secretKey)
                assert {
                    elementWithTextPresented(" Order completed successfully ")
                }
                e {
                    click(doneButton)
                }
                assert {
                    urlEndsWith("/orders")
                }
            }
        }
        val balanceAfter = openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, wallet.name)
        val expectedBalanceAfter = balanceBefore.toBigDecimal() + amount.toBigDecimal()
        assertThat(
            "Expected base balance: $expectedBalanceAfter, was: $balanceAfter",
            balanceAfter.toBigDecimal(),
            closeTo(expectedBalanceAfter, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-1934")
    @Test
    @DisplayName("Buying Currency Token (СС). Amount is a number with decimal places after comma")
    fun currencyTokenCCisNumber() {
        val amount = "1.987"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet

        step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            val alias = openPage<AtmWalletPage>().getAlias()
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "100")
            }
        }
        val balanceBefore = openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, wallet.name)
        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver)) {
                buyToken(CC, wallet.publicKey, amount, user, wallet.secretKey)
                assert {
                    elementWithTextPresented(" Order completed successfully ")
                }
            }
        }
        val balanceAfter = openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, wallet.name)
        val expectedBalanceAfter = balanceBefore.toBigDecimal() + amount.toBigDecimal()
        assertThat(
            "Expected base balance: $expectedBalanceAfter, was: $balanceAfter",
            balanceAfter.toBigDecimal(),
            closeTo(expectedBalanceAfter, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-1931")
    @Test
    @DisplayName("Buying Currency Token (CC). Wrong 2FA APP")
    fun buyingCurrencyTokenCCWrong2FAAPP() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet.publicKey
        step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            val alias = openPage<AtmWalletPage>().getAlias()
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "100")
            }
        }
        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver)) {
                e {
                    chooseToken(CC)
                    click(newOrderButton)
                    select(selectWallet, walletNum)
//                    val amount = Random.nextDouble(0.000001, 0.999999).toString()
//                    леджер любимый
                    val amount = "1"
                    sendKeys(tokenQuantity, amount)
                    click(submitButton)
                    val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                    sendKeys(privateKey, user.mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }
        }

    }

    @TmsLink("ATMCH-1932")
    @Test
    @DisplayName("Buying Currency Token (CC). Wrong signature")
    fun buyingCurrencyTokenCCWrongSignature() {
        val anotherUserPrivateKey = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET.otfWallet.secretKey
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet.publicKey

        step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            val alias = openPage<AtmWalletPage>().getAlias()
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "100")
            }
        }

        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver)) {
                e {
                    chooseToken(CC)
                    click(newOrderButton)
                    select(selectWallet, walletNum)
//                    val amount = Random.nextDouble(0.000001, 0.999999).toString()
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, "1")
                    click(submitButton)
                    signMessage(anotherUserPrivateKey)
                }
                assert {
                    elementWithTextPresentedIgnoreCase("Invalid key")
                }
            }
        }

    }

    @TmsLink("ATMCH-664")
    @Test
    @DisplayName("Buying Currency Token (СС)")
    fun buyingCurrencyToken() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet.publicKey
        step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            val alias = openPage<AtmWalletPage>().getAlias()
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "100")
            }
        }
        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                e {
                    chooseToken(CC)
                    click(newOrderButton)
                    select(selectWallet, walletNum)
//                    val amount = Random.nextDouble(0.000001, 0.999999).toString()
                    val amount = "1"
                    sendKeys(tokenQuantity, amount)
                    click(submitButton)
                    signAndSubmitMessage(user, user.mainWallet.secretKey)
                }
                assert {
                    elementWithTextPresented(" Order completed successfully ")
                }
            }
        }
        step("THEN User tap on DONE button and go to Market page") {
            with(AtmMarketplacePage(driver)) {
                e {
                    click(doneButton)
                }
                assert {
//                    urlEndsWith("/trading/market")
                    urlEndsWith("/orders")
                }

            }
        }
    }

    @TmsLink("ATMCH-666")
    @Test
    @DisplayName("Validation field of buying Currency Token (CC)")
    fun validationFieldOfBuyingCurrencyToken() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet

        val balance =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(FIAT, walletNum.name)
        val amount = (balance + BigDecimal.ONE).toString()

        with(openPage<AtmMarketplacePage>(driver)) {
            e {
                chooseToken(CC)
                click(newOrderButton)
                select(selectWallet, walletNum.publicKey)
                deleteData(tokenQuantity)
                sendKeys(tokenQuantity, "0")
                click(submitButton)
            }
            assert {
                elementWithTextPresented(" The field must be greater than 0 ")
            }
            e {
                deleteData(tokenQuantity)
                sendKeys(tokenQuantity, amount)
            }
            assert {
                elementContainingTextPresented("Amount to pay exceeds available balance")
            }
            e {
                deleteData(tokenQuantity)
                sendKeys(tokenQuantity, "1000001")
            }
            assert {
                elementContainingTextPresented("Entered token quantity is above max value required to proceed with this order")
            }
        }
    }


}
