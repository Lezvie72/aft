package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.FT
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.LimitType.MIN
import pages.atm.AtmIssuancesPage.OperationType.SELL
import utils.Constants
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("FT purchase for CC")
class FTPurchaseForCC : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-1975")
    @Test
    @DisplayName("Fractionalized Token. New order creating")
    fun fractionalizedTokenNewOrderCreating() {
        val amount = "0.01"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val mainWallet = user.mainWallet
        preset(user, "3000", mainWallet)
        val balanceBefore =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(FT, mainWallet.name)
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyToken(FT, mainWallet.publicKey, "1", user, mainWallet.secretKey)
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
        val balanceAfter = openPage<AtmWalletPage>().getBalanceFromWalletForToken(FT, mainWallet.name)
        val expectedBalanceAfter = balanceBefore.toBigDecimal() + amount.toBigDecimal()

        assertThat(
            "Expected base balance: $expectedBalanceAfter, was: $balanceAfter",
            balanceAfter.toBigDecimal(),
            closeTo(expectedBalanceAfter + BigDecimal.ONE, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-1976")
    @Test
    @DisplayName("Fractionalized Token. Validation during order creating")
    fun fractionalizedTokenValidationDuringOrderCreating() {
        val limitValueMin = BigDecimal("0.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}")
        val limitValueMax = BigDecimal("1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val walletAccept = user1.walletList[0]

        prerequisite { addCurrencyCoinToWallet(user, "1000", walletNum) }
        openPage<AtmProfilePage>(driver).logout()

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(FT, SELL, MIN, limitValueMin.toString(), user1, walletAccept)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(FT, SELL, MAX, limitValueMax.toString(), user1, walletAccept)
                openPage<AtmProfilePage>(driver).logout()
            }
        }
        step("User make oredr and check validation errors") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                e {
                    chooseToken(FT)
                    click(newOrderButton)
                    select(selectWallet, walletNum.publicKey.toString())
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, "#$%^&&*^&")
                    assert {
                        elementContainsValue(tokenQuantity, "0")
                    }
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, "TEST")
                    assert {
                        elementContainsValue(tokenQuantity, "0")
                    }
                    val min = amountLimit.minLimit
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, min)
                }
                assert {
                    elementWithTextPresented(" Entered token quantity is below min value required to proceed with this order ")
                }
                e {
                    val max = amountLimit.maxLimit
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, max)
                }
                assert {
                    elementWithTextPresented(" Entered token quantity is above max value required to proceed with this order ")
                    elementWithTextNotPresented("Submit")
                }
                e {
                    val moreThanBalance = (balanceUser.amount + BigDecimal.ONE).toString()
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, moreThanBalance)
                }
                assert {
                    elementWithTextPresented(" Amount to pay exceeds available balance ")
                    elementWithTextNotPresented("Submit")
                }
            }
            openPage<AtmProfilePage>(driver).logout()
        }
        step("User back limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(FT, SELL, MIN, "0.00000010", user1, walletAccept)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(FT, SELL, MAX, "1000.00000000", user1, walletAccept)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-1977")
    @Test
    @DisplayName("Fractionalized Token. Cancellation during order creating")
    fun fractionalizedTokenCancellationDuringOrderCreating() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val walletNum = user.mainWallet

        val balanceBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
                FT,
                walletNum.name
            )
        }
        step("User make new order for FT and cancel this order during creation") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                e {
                    chooseToken(FT)
                    click(newOrderButton)
                    click(cancel)
                }
                assert {
                    elementPresented(newOrderButton)
                }
                e {
                    click(newOrderButton)
                    select(selectWallet, walletNum.publicKey)
                    sendKeys(tokenQuantity, "1")
                    click(submitButton)
                    click(cancelSubmitPrivateKeyButton)
                }
                assert { elementPresented(selectWallet) }
                e {
                    click(submitButton)
                    assert { elementWithTextPresentedIgnoreCase("Manual signature") }
                    click(cancelSubmitPrivateKeyButton)
                    assert { elementPresented(selectWallet) }
                    click(submitButton)
                    sendKeys(
                        privateKey,
                        walletNum.secretKey
                    )
                    click(confirmPrivateKeyButton)
                    click(atmOtpCancel)
                }
                assert { elementPresented(selectWallet) }
            }
            val balanceAfter = step("User get balance from wallet after operation") {
                openPage<AtmWalletPage>().getBalanceFromWalletForToken(
                    FT,
                    walletNum.name
                )
            }
            assertThat(
                "Expected base balance: $balanceAfter, was: $balanceBefore",
                balanceAfter.toBigDecimal(),
                closeTo(balanceBefore.toBigDecimal(), BigDecimal("0.01"))
            )
        }
    }

    @TmsLink("ATMCH-1979")
    @Test
    @DisplayName("Fractionalized Token. Wrong signature filling during order creating")
    fun fractionalizedTokenWrongSignatureFillingDuringOrderCreating() {
        val invalidKey = RandomStringUtils.random(6, true, true)
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        preset(user, "1000", wallet)
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            e {
                chooseToken(FT)
                click(newOrderButton)
                select(selectWallet, wallet.publicKey.toString())
                deleteData(tokenQuantity)
                sendKeys(tokenQuantity, "1")
                click(privateKey)
                click(submitButton)
                sendKeys(privateKey, invalidKey)
            }
            assert { elementWithTextPresented(" Invalid key ") }
            e {
                deleteData(privateKey)
                sendKeys(privateKey, user.mainWallet.secretKey)
                click(confirmPrivateKeyButton)
            }
            assert { elementPresented(atmOtpConfirmationInput) }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-1980")
    @Test
    @DisplayName("Fractionalized Token. Wrong 2FA code filling during order creating")
    fun fractionalizedTokenWrong2FACodeFillingDuringOrderCreating() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        preset(user, "1000", wallet)
        with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
            e {
                chooseToken(FT)
                click(newOrderButton)
                select(selectWallet, wallet.publicKey.toString())
                deleteData(tokenQuantity)
                sendKeys(tokenQuantity, "1")
                click(submitButton)
                Thread.sleep(2_000)
                click(privateKey)
                sendKeys(privateKey, user.mainWallet.secretKey)
                click(confirmPrivateKeyButton)

                val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                sendKeys(atmOtpConfirmationInput, code)
                click(atmOtpConfirmationConfirmButton)
            }
            assert {
                elementWithTextPresented(" Wrong code ")
            }
        }
    }

    private fun preset(user: DefaultUser, amount: String, wallet: SimpleWallet) {
        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, amount)
        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyTokenNew(CC, amount, user, wallet)
    }
}
