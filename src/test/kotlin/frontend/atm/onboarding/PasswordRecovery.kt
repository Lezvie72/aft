package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmForgotPassPage
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import pages.core.actions.AssertActions
import utils.Constants
import utils.Environment
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.generateEmail
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Password recovery")
class PasswordRecovery : BaseTest() {

    @TmsLink("ATMCH-147")
    @Test
    @DisplayName("Password recovery")
    fun passwordRecovery() {
        val user = Users.ATM_USER_FOR_RECOVERY
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"
        with(openPage<AtmLoginPage>(driver)) {
            e {
                click(forgotPassword)
            }
            val since = LocalDateTime.now(ZoneOffset.UTC)
            with(AtmForgotPassPage(driver)) {
                e {
                    sendKeys(emailRecovery, user.email)
                    click(sendInstructionsButton)
                }
//                assert { elementWithTextPresented(" E-mail to reset your password has been sent. If you didn't receive it, please, check your spam-box, or attempt again in 4 minutes ") }
            }
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefPassRecoveryUserATM(user.email, since)
            driver.navigate().to(href)
            assert {
                elementWithTextPresented("Enter new password")
                elementPresented(atmUserPasswordForRestore)
                elementPresented(atmUserConfirmRestorePassword)
                elementPresented(confirm)
            }
            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
            enterNewPassword(password)
            val hrefRec = GmailApi.getBodyPassChangedUserATM(user.email, sinceRec)
            MatcherAssert.assertThat(hrefRec, Matchers.containsString("Your password has been reset"))

            openPage<AtmLoginPage>(driver)
            e {
                sendKeys(atmUserEmail, user.email)
                sendKeys(atmUserPassword, password)
                click(signInButton)
            }
            assert { urlEndsWith("/profile/info") }
        }

    }

    @TmsLink("ATMCH-835")
    @Test
    @DisplayName("Field verification: Password recovery")
    fun fieldVerificationPasswordRecovery() {
        val user = Users.ATM_USER_KYC0_2FA_NONE
        with(openPage<AtmLoginPage>(driver)) {
            e {
                click(forgotPassword)
                assert { urlEndsWith("/forgot-password") }
                assert {
                    validateField(
                        atmUserEmail,
                        AssertActions.EMAIL_VALIDATION,
                        "Invalid Email format"
                    )
                }
                sendKeys(atmUserEmail, "telegraph@skolopendra.com")
                click(confirm)
                assert { elementContainingTextPresented("E-mail is incorrect, or such user does not exist") }
                sendKeys(atmUserEmail, user.email)
            }
            val since = LocalDateTime.now(ZoneOffset.UTC)
            e {
                click(confirm)
                assert { elementContainingTextPresented("E-mail to reset your password has been sent. You may attempt again in 5 minutes") }
                openPage<AtmLoginPage>(driver)
                Thread.sleep(61_000)
                click(forgotPassword)
                sendKeys(atmUserEmail, user.email)
                click(confirm)
                assert { elementContainingTextPresented("E-mail to reset your password has been sent. If you didn't receive it, please, check your spam-box, or attempt again in 4 minutes") }
                val href = GmailApi.getHrefPassRecoveryUserATM(user.email, since)
                driver.navigate().to(href)
                assert {
                    validateField(
                        atmUserPasswordForRestore,
                        AssertActions.PASSWORD_VALIDATION,
                        "Use stronger password"
                    )
                }
                sendKeys(atmUserPasswordForRestore, "12345")
                sendKeys(atmUserConfirmRestorePassword, "123")
                assert { elementContainingTextPresented("Passwords do not match") }
            }
        }
    }

}