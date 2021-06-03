package frontend.smoke

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.FIAT
import models.user.classes.DefaultUser
import models.user.classes.DefaultUserWith2FA
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.Constants
import utils.gmail.GmailApi
import utils.helpers.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tag("Smoke")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Execution(ExecutionMode.SAME_THREAD)
@Epic("Frontend")
@Feature("ATM Smoke")
class SmokeTests : BaseTest() {

    @TmsLink("ATMCH-277")
    @Order(0)
    @Test
    @DisplayName("Main flow")
    fun mainFlow() {
        val user = DefaultUser(
            email = generateEmail().toLowerCase(),
            password = Constants.DEFAULT_PASSWORD,
            project = 1
        )

        step("Registration") {
            step("Send invitation") {
                with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                    e {
                        sendInvitation(user.email, true)
                    }
                }
            }

            step("Navigate from email") {
                openPage<AtmHomePage>(driver)
                val href = GmailApi.getHrefForNewUserATM(user.email)
                driver.navigate().to(href)
            }

            step("Register user") {
                with(AtmLoginPage(driver)) {
                    e {
                        fillRegForm()
                        assert { urlEndsWith("/profile/info") }
                    }
                }
            }

            openPage<AtmProfilePage>().logout()
        }
        step("Authorization") {
            with(openPage<AtmLoginPage>(driver)) {
                e {
                    loginWithout2FA(user.email)
                    assert { urlEndsWith("/profile/info") }
                }
            }
            openPage<AtmProfilePage>().logout()
        }
        step("Password recovery") {
            step("Password recovery. Request") {
                with(openPage<AtmLoginPage>(driver)) {
                    passwordRecovery(user.email)
                    e {
                        assert { urlEndsWith("/forgot-password") }
                    }
                }
            }

            step("Password recovery. Navigate from email") {
                val href = GmailApi.getHrefPassRecoveryUserATM(user.email)
                driver.navigate().to(href)
            }

            step("Password recovery. Enter new password") {
                with(AtmLoginPage(driver)) {
                    enterNewPassword(Constants.DEFAULT_NEW_PASSWORD)
                    user.password = Constants.DEFAULT_NEW_PASSWORD
                    assert { urlEndsWith("/login") }
                }
            }

            step("Password recovery. Login with new password") {
                Thread.sleep(2_000)
                with(AtmLoginPage(driver)) {
                    e {
                        submit(user)
                        assert { urlEndsWith("/profile/info") }
                    }
                }
            }
        }
        step("2FA App") {
            val userWith2FA = with(openPage<AtmProfilePage>(driver) { submit(user) }) {
                step("Enable 2FA. Check incorrect OTP code from e-mail") {
                    e {
                        val since = LocalDateTime.now(ZoneOffset.UTC)
                        click(googleAuthButton)
                        GmailApi.getVerificationCode(user.email, since)
                        sendKeys(codeInput, "123456")
                        click(submitCodeButton)
                        assert {
                            elementContainingTextPresented("Wrong code")
                        }
                    }
                }

                step("Enable 2FA. Re-send OTP and enter it") {
                    Thread.sleep(60_000)
                    assert {
                        elementContainingTextPresented("The confirmation code is expired")
                    }
                    val since = LocalDateTime.now(ZoneOffset.UTC)
                    e {
                        click(resendOtpCode)
                    }
                    val code = GmailApi.getVerificationCode(user.email, since)
                    e {
                        sendKeys(codeInput, code)
                        click(submitCodeButton)
                    }
                }

                step("Enable 2FA. Confirm secret") {
                    val secret = secretKey.getAttribute("innerHTML")
                    e {
                        click(googleConfirmationCheckbox)
                        sendKeys(appKey, OAuth.generateCode(secret))
                        click(continueButton)
                    }
                    step("Check if code wasn't accepted and repeat") {
                        if (check { isElementWithTextPresented(" Wrong code ") }) {
                            e {
                                sendKeys(appKey, OAuth.generateCode(secret))
                                click(continueButton)
                            }
                        }
                    }
                    DefaultUserWith2FA(email = user.email, oAuthSecret = secret).apply {
                        password = user.password
                    }
                }
            }

            step("Enable 2FA. Login and logout with 2FA") {
                with(AtmProfilePage(driver).logout()) {
                    submit(userWith2FA)
                    assert { urlEndsWith("/profile/info") }
                }
            }
        }
        step("Wallets creation") {
            val label = RandomStringUtils.randomAlphabetic(5)
            val (privateKey, publicKey) = with(openPage<AtmKeysPage>(driver) { submit(user) }) {
                e {
                    generatePublicAndPrivateKey()
                }
            }
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    click(registerWalletButton)
                }
                assert {
                    urlEndsWith("/wallets/new")
                }
                e {
                    click(mainWalletRadioButton)
                    sendKeys(this@with.publicKey, publicKey)
                    sendKeys(labelWallet, label)
                    click(signMessage)
                }
                assert { elementPresented(privateKeyInput) }

                e {
                    click(privateKeyInput)
                    sendKeys(privateKeyInput, privateKey)
                    click(confirmSignature)
                    clickUntilElementIsPresented(nextButton, "Wallets",1, pollingEveryInSeconds = 5)
                }

                Assertions.assertTrue(isWalletWithLabelPresented(label), "Wallet with label $label wasn't found")
            }
        }
    }

    @TmsLink("ATMCH-277")
    @Order(50)
    @Test
    @DisplayName("FIAT deposit")
    fun fiatDeposit() {
        val date = LocalDate.now().toString()
        val amount = "10"
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        }

        val balance = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalance(FIAT,wallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    click(addPaymentDialogButton)
                }
                e {
                    select(currency, "USD")
                    sendKeys(paymentId, "213")
                    sendKeys(paymentDate, date)
                    sendKeys(clientAlias, alias)
                    sendKeys(amountPayment, amount)
                    click(addPaymentsButton)
                }
            }
        }
        val balanceAfter = step("THEN User go to Wallet, get balance after send fiat and check them") {

            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                Thread.sleep(10000)
                getBalance(FIAT,wallet.name)
            }
        }

        MatcherAssert.assertThat(
            balance + amount.toBigDecimal(),
            Matchers.equalTo(balanceAfter)
        )
    }

    @TmsLink("ATMCH-277")
    @Order(60)
    @Test
    @DisplayName("CC buying")
    fun buyCurrencyCoin() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val wallet = user.mainWallet
        step("WHEN User create Currency Coin order") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                e {
                    chooseToken(CC)
                    click(newOrderButton)
                    select(selectWallet, wallet.publicKey)
                    val amount = "2"
                    sendKeys(tokenQuantity, amount)
                    click(submitButton)
                }
                signAndSubmitMessage(user.oAuthSecret, wallet.secretKey)
                assert {
                    elementWithTextPresented(" Order completed successfully ")
                }
            }
        }
    }
}