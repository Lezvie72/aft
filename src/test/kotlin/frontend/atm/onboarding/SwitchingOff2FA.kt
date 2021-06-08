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
import pages.atm.AtmProfilePage
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Switching off 2FA")
class SwitchingOff2FA : BaseTest() {

    @TmsLink("ATMCH-171")
    @Test
    @DisplayName("Disabling 2FA from 2FA App to None (not recommended)")
    fun disabling2FAFrom2FAAppToNone() {
        val user = Users.ATM_USER_KYC0_2FA_NONE
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            val userWith2FA = switchToGoogleAuth(user)
            try {
                Thread.sleep(10000)
                e {
                    click(none2fa)
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
            } catch (e: Exception) {
                throw e
            } finally {
                e {
                    sendKeys(atmOtpConfirmationInput, OAuth.generateCode(userWith2FA.oAuthSecret))
                    assert {
                        elementPresented(confirm)
                    }
                    click(atmOtpConfirmationConfirmButton)
                    assert {
                        elementContainingTextPresented("You have successfully completed 2nd factor authentication setting")
                    }
                }
            }
        }
    }

    @TmsLink("ATMCH-837")
    @Test
    @DisplayName("Field verification: Disabling 2FA from 2FA App to None (not recommended)")
    fun fieldVerificationDisabling2FAfrom2FAappToNone() {
        val user = Users.ATM_USER_2FA_OAUTH
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            e {
                click(none2fa)
                sendKeys(codeInput, "123456")
                click(submitCodeButton)
            }
            assert {
                elementContainingTextPresented("Wrong code")
            }
        }
    }

}