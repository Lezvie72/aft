package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmLoginPage
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Confirming authorization by 2FA")
class ConfirmingAuthorizationBy2FA : BaseTest() {

    @TmsLink("ATMCH-710")
    @Test
    @DisplayName("Field validation : Sign in with current 2FA setting 2FA App")
    fun signInWithOauthFieldValidation() {
        val user = Users.ATM_USER_2FA_OAUTH
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, user.email)
                sendKeys(atmUserPassword, user.password)
                val since = LocalDateTime.now(ZoneOffset.UTC)
                click(signInButton)
                verifyDeviceIfNeeded(user, since)
            }
            val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
            e {
                sendKeys(atmOtpConfirmationInput, code)
                click(atmOtpConfirmationConfirmButton)
                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }

        }
    }

    @TmsLink("ATMCH-136")
    @Test
    @DisplayName("Sign in with current 2FA setting 2FA App")
    fun signInWithCurrent2FAsetting2FAapp() {
        val user = Users.ATM_USER_2FA_OAUTH
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, user.email)
                sendKeys(atmUserPassword, user.password)
                val since = LocalDateTime.now(ZoneOffset.UTC)
                click(signInButton)
                verifyDeviceIfNeeded(user, since)
                assert {
                    elementContainingTextPresented("Enter confirmation code given by Authenticator app")
                }
                assert {
                    elementContainingTextPresented("Enter confirmation code")
                }
                assert {
                    elementPresented(atmOtpCancel)
                }
            }
            val code = OAuth.generateCode(user.oAuthSecret)
            e {
                sendKeys(atmOtpConfirmationInput, code)
                assert {
                    elementPresented(confirm)
                }
                click(atmOtpConfirmationConfirmButton)
                assert { urlEndsWith("/profile/info") }
            }
        }
    }

}