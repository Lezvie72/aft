package frontend.atm.wallets

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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmIssuancesPage
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.LimitType.MIN
import pages.atm.AtmIssuancesPage.OperationType.REDEMPTION
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Redemption (Buyback) CC with single authorization")
class RedemptionCCWithSingleAuthorization : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_2FA_MAIN_WALLET)
    @TmsLink("ATMCH-2210")
    @Test
    @DisplayName("Redemption CC")
    fun redemptionCC() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val wallet = user.mainWallet

        prerequisite {
            addCurrencyCoinToWallet(user, "10", wallet)
        }
        val balanceBefore =
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
                FIAT,
                wallet.name
            )
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            redeemToken(
                CC,
                wallet,
                "1",
                "",
                user
            )
            assert {
                elementWithTextPresented(" The token has been redeemed successfully ")
                elementPresented(doneButton)
            }
        }
        val balanceAfter = openPage<AtmWalletPage>().getBalanceFromWalletForToken(FIAT, wallet.name)
        assertThat(
            "Expected base balance: $balanceAfter, was: $balanceBefore",
            balanceAfter.toBigDecimal(),
            closeTo(balanceBefore.toBigDecimal() + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }

    @TmsLink("ATMCH-2217")
    @Test
    @DisplayName("Redemption CC. Wrong 2FA code filling")
    fun redemptionCCWrong2FACodeFilling() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val wallet = user.walletList[0]

        prerequisite {
            addCurrencyCoinToWallet(user, "10", wallet)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(CC)
                click(redemption)
                select(selectWallet, wallet.publicKey)
                sendKeys(tokenQuantity, "1")
                click(submitButton)
                click(privateKey)
                sendKeys(privateKey, wallet.secretKey)
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

    @TmsLink("ATMCH-2216")
    @Test
    @DisplayName("Redemption CC. Wrong signature filling ")
    fun redemptionCCWrongSignatureFilling() {

        val user = Users.ATM_USER_2MAIN_WALLET
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val wallet = user.walletList[0]
        val wallet1 = user1.mainWallet
        prerequisite {
            addCurrencyCoinToWallet(user, "10", wallet)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(CC)
                click(redemption)
                select(selectWallet, wallet.publicKey)
                sendKeys(tokenQuantity, "1")
                click(submitButton)
                e {
                    sendKeys(
                        privateKey,
                        wallet1.secretKey
                    )
                    click(confirmPrivateKeyButton)
                }
                assert { elementWithTextPresented(" Invalid key ") }
                e {
                    sendKeys(privateKey, wallet.secretKey)
                    click(confirmPrivateKeyButton)
                }
                assert { elementPresented(atmOtpConfirmationInput) }
            }
        }
    }

    @TmsLink("ATMCH-2214")
    @Test
    @DisplayName("Redemption CC. Validation checking")
    fun redemptionCCValidationChecking() {
        val limitValueMin = BigDecimal("0.${org.apache.commons.lang.RandomStringUtils.randomNumeric(7)}1")
        val limitValueMax = BigDecimal("1.${org.apache.commons.lang.RandomStringUtils.randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS

        val wallet = user.mainWallet
        val walletAccept = user1.walletList[0]

        prerequisite { addCurrencyCoinToWallet(user, "10", wallet) }
        openPage<AtmProfilePage>(driver).logout()

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(CC, REDEMPTION, MIN, limitValueMin.toString(), user1, walletAccept)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(CC, REDEMPTION, MAX, limitValueMax.toString(), user1, walletAccept)
                openPage<AtmProfilePage>(driver).logout()
            }
        }
        step("User make order and check validation errors") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(wallet.name)
                    chooseToken(CC)
                    click(redemption)
                    select(selectWallet, wallet.publicKey)
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
                }
                e {
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, "0.000000001")
                    assert {
                        elementContainsValue(tokenQuantity, "0")
                    }
                }
                e {
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
                }
                e {
                    val moreThanBalance = (balanceUser.amount + BigDecimal.ONE).toString()
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, moreThanBalance)
                }
                assert {
                    elementWithTextPresented(" Amount to redeem exceeds available balance ")
                    elementDisabled(submitForCheckDisabling)
                }
            }
            openPage<AtmProfilePage>(driver).logout()
        }
        step("User back limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(
                    CC,
                    REDEMPTION,
                    MIN, "0.00000010", user1, walletAccept
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    CC,
                    REDEMPTION,
                    MAX, "1000.00000000", user1, walletAccept
                )
            }
        }
    }
}