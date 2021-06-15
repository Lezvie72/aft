package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmLoginPage
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Entering authentication data")
class EnteringAuthenticationData : BaseTest() {

    @TmsLink("ATMCH-145")
    @Test
    @DisplayName("Sign in with NONE 2FA setting with an registered profile")
    fun signInNone2FASetRegProfile() {
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, Users.ATM_USER_KYC0.email)
                sendKeys(atmUserPassword, Constants.DEFAULT_PASSWORD)
                val since = LocalDateTime.now(ZoneOffset.UTC)
                click(signInButton)
                verifyDeviceIfNeeded(Users.ATM_USER_KYC0, since)
            }
            assert { urlEndsWith("/profile/info") }
        }
    }

    @TmsLink("ATMCH-696")
    @Test
    @DisplayName("Fields validation: Sing in with NONE 2FA setting with an registered profile")
    fun validationSignInNone2FASetRegProfile() {
        with(openPage<AtmLoginPage>(driver)) {
            e {

                click(forgotPassword)
            }
            assert {
                urlEndsWith("/forgot-password")
            }
            e {
                click(signInLinkButton)
            }
            assert {
                urlEndsWith("/login")
            }
            driver.navigate().forward()
            assert {
                urlEndsWith("/login")
            }
            e {
                sendKeys(atmUserEmail, "aft")
            }
            assert {
                elementWithTextPresented(" Invalid Email format ")
            }
            e {
                sendKeys(atmUserEmail, "aft.uat.sdex@gm")
            }
            assert {
                elementWithTextPresented(" Invalid Email format ")
            }
            e {
                sendKeys(atmUserEmail, "aft.uat.sdex@gmail")
            }
            assert {
                elementWithTextPresented(" Invalid Email format ")
            }
            e {
                sendKeys(atmUserEmail, "aft.uat.sdex@gmailklklklklklklklklkl.")
            }
            assert {
                elementWithTextPresented(" Invalid Email format ")
            }
            e { sendKeys(atmUserPassword, "21yui2y1iuy") }
            assert { elementDisabled(signInButton) }
        }
    }

    @Disabled("https://sdexnt.atlassian.net/browse/ATM-894")
    @TmsLink("ATMCH-703")
    @Test
    @DisplayName("Protection against Entering incorrect passwords on Sign in page")
    fun protectAgainstEnterIncorrectPass() {
        val email = Users.ATM_USER_FOR_RECOVERY.email
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, email)
                sendKeys(atmUserPassword, "12")
                click(signInButton)
                sendKeys(atmUserPassword, "121212")
                click(signInButton)
                sendKeys(atmUserPassword, "122222")
                click(signInButton)
                sendKeys(atmUserPassword, "1222222")
                click(signInButton)
                sendKeys(atmUserPassword, "12323232")
                click(signInButton)
                sendKeys(atmUserPassword, "12232323")
                click(signInButton)
                sendKeys(atmUserPassword, "1223232")
                click(signInButton)
                sendKeys(atmUserPassword, "1244444")
                click(signInButton)
                sendKeys(atmUserPassword, "1244324")
                click(signInButton)
                sendKeys(atmUserPassword, "1240944")
                click(signInButton)
                click(capcha)
                sendKeys(atmUserPassword, "12")
                click(signInButton)
                sendKeys(atmUserPassword, "121212")
                click(signInButton)
                sendKeys(atmUserPassword, "122222")
                click(signInButton)
                sendKeys(atmUserPassword, "1222222")
                click(signInButton)
                sendKeys(atmUserPassword, "12323232")
                click(signInButton)
                sendKeys(atmUserPassword, "12232323")
                click(signInButton)
                sendKeys(atmUserPassword, "1223232")
                click(signInButton)
                sendKeys(atmUserPassword, "1244444")
                click(signInButton)
                sendKeys(atmUserPassword, "1244324")
                click(signInButton)
                sendKeys(atmUserPassword, "1240944")
                click(signInButton)
            }
            val since = LocalDateTime.now(ZoneOffset.UTC)
            val href = GmailApi.getHrefForNewUserATM(email, since)
            driver.navigate().to(href)
            e {
                assert { urlEndsWith("/forgot-password") }
            }
        }
    }

    @Disabled("https://sdexnt.atlassian.net/browse/ATM-894")
    @TmsLink("ATMCH-716")
    @Test
    @DisplayName("Temporary blocking entering incorrect OTP code on Sign in page  with current 2FA setting 2FA App")
    fun tempBlockAfterIncorrectOTPoAuth() {
        val user = Users.ATM_USER_2FA_OAUTH_BLOCK
        val page = step("GIVEN user with 2FA attempts to log in") {
            openPage<AtmLoginPage>(driver)
        }

        val since = LocalDateTime.now(ZoneOffset.UTC)
        with(page) {
            step("WHEN user attempts to enter incorrect OTP 10 times") {
                repeat(5) { index ->
                    step("${index + 1} of 5") {
                        loginWithout2FA(user.email, user.password)
                        val secret = user.oAuthSecret
                        val code = if (index > 0) {
                            "123456"
                        } else {
                            if (OAuth.generateCode(secret) == "123456") "123457" else "123456"
                        }

                        e {
                            sendKeys(atmOtpConfirmationInput, code)
                            click(atmOtpConfirmationConfirmButton)
                        }
                        assert {
                            elementContainingTextPresented("Wrong code")
                        }
                        val secondCode = if (index > 0) {
                            "123456"
                        } else {
                            if (OAuth.generateCode(secret) == "123456") "123457" else "123456"
                        }
                        e {
                            sendKeys(atmOtpConfirmationInput, secondCode)
                            click(atmOtpConfirmationConfirmButton)
                        }
                        assert {
                            elementContainingTextPresented("Wrong code")
                        }
                        e {
                            click(atmOtpCancel)
                        }
                    }
                }
            }
        }

        with(openPage<AtmLoginPage>(driver)) {
            step("THEN user is unable to login") {
                loginWithout2FA(user.email, user.password)
                assert {
                    elementContainingTextPresented("You have exceeded the allowed number of 2FA verification attempts. Your account is blocked for 15 minutes. Please try again later") //TODO: Check error message
                }
            }
            step("AND user gets notification via email") {
                GmailApi.getEmailBody(user.email, "", since)
//                assertThat(email, matchesPattern("")) //TODO: Create pattern for email
            }
        }


    }


}