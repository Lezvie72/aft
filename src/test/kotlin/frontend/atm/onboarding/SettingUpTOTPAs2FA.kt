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
import pages.atm.AtmKeysPage
import pages.atm.AtmProfilePage
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Setting Up TOTP As 2FA")
class SettingUpTOTPAs2FA : BaseTest() {

    @TmsLink("ATMCH-708")
    @Test
    @DisplayName("Field Validation: Change 2FA settings from None (not recommended) to 2FA APP")
    fun fieldValidationChange2FASettingFromNoneTo2FAAPP() {
        val user = Users.ATM_USER_KYC0_2FA_NONE
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            e {
                click(googleAuthButton)
                sendKeys(codeInput, "123456")
                click(submitCodeButton)
            }
            assert {
                elementContainingTextPresented("Wrong code")
            }
            Thread.sleep(60_000)
            val since = LocalDateTime.now(ZoneOffset.UTC)
            e {
                click(resendOtpCode)
            }
            val code = GmailApi.getVerificationCode(user.email, since)
            e {
                sendKeys(codeInput, code)
                click(submitCodeButton)
            }
            with(AtmKeysPage(driver)) {
                assert {
                    elementPresented(cancelButton)
                    elementPresented(continueButton)
                    elementPresented(codeSaveConfirmCheckbox)
                    elementPresented(appCodeInput)
                    elementPresented(googleOauthLink)
                    elementPresented(redHatFreeOtpLink)
                }
                e {
                    sendKeys(appCodeInput, "12121")
                    setCheckbox(codeSaveConfirmCheckbox, true)
                    click(continueButton)
                }
                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }

        }


    }


}