package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import pages.atm.*
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Onboarding")
class SmokeOnboardingE2E : BaseTest() {

    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("1-4: Registration with KYC")
    fun registrationWithKYC() {

        val user = newUser()

        step("Set invite by administrator") {
            with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                sendInvitation(user.email, true)
            }
        }

        step("User follows link from email") {
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefForNewUserATM(user.email)
            driver.navigate().to(href)
        }

        with(AtmLoginPage(driver)) {

            step("User go to 'New account page'") {
                assert {
                    urlMatches(".*\\/register\\?invite=.*")
                }
            }

            step("Check create user tab") {
                assert {
                    elementPresented(atmUserEmailForRegistration)
                    elementPresented(atmUserPassword)
                    elementPresented(atmUserConfirmPassword)
                    elementPresented(capcha)
                }
            }

            step("User fills in register form") {
                e {
                    sendKeys(atmUserEmailForRegistration, user.email)
                    sendKeys(atmUserPassword, user.password)
                    sendKeys(atmUserConfirmPassword, user.password)
                    click(capcha)
                }
            }

            step("Check that 'REGISTER' button becomes active") {
                assert {
                    elementPresented(registerNewAtmUser)
                }
                wait {
                    until("Button 'Register' should be enabled") {
                        registerNewAtmUser.getAttribute("disabled") == null
                    }
                }
            }

            step("User presses register button") {
                e {
                    click(registerNewAtmUser)
                }
            }

            step("User is redirected to profile page") {
                assert {
                    urlEndsWith("/profile/info")
                }
            }

            step("Button 'START KYC' isn't displayed") {
                with(AtmProfilePage(driver)) {
                    assert {
                        elementNotPresented(startKYC)
                    }
                }
            }

            step("User logs out") {
                AtmProfilePage(driver).logout()
            }

        }
    }

    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("5-11: Keys generation")
    fun keysGeneration() {

        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            step("Go to Key generator page") {
                e {
                    click(keySignatureGenerator)
                }
            }
        }

        with(AtmKeysPage(driver)) {

            step("Check Key generator page") {
                assert {
                    urlEndsWith("/profile/keys")
                    elementWithTextPresented("Key generator")
                    elementWithTextPresented("Keys")
                    elementWithTextPresented("Enter your mnemonic phrase (12 words) or generate a new one to generate keys for your wallet")
                    elementPresented(publicKeyInput)
                    elementPresented(privateKeyInput)
                    elementPresented(publicKeyPaste)
                    elementPresented(privateKeyPaste)
                    elementPresented(generatePhraseButton)
                    elementPresented(mnemmonicPhraseInput)
                    elementPresented(passphraseInput)
                    elementPresented(repeatePassphraseInput)
                }
            }

            step("Insert the short mnemonic phrase") {
                e {
                    sendKeys(mnemmonicPhraseInput, "apple")
                }
            }

            step("Check error messages for short mnemonic page") {
                assert {
                    elementWithTextPresented(" Insufficient entropy in mnemonic, please generate new one ")
                    elementWithTextPresented(" Should be exactly 12 words in mnemonic phrase ")
                }
            }

            step("Click the Generate Phrase button") {
                e {
                    click(generatePhraseButton)
                }
            }

            step("Check the Generate Keys button is enabled") {
                assert {
                    //TODO: should check the button is enabled, not presented
                    elementPresented(generateKeysButton)
                }
            }

            step("Click the Generate Keys button") {
                e {
                    click(generateKeysButton)
                }
            }

            step("Check the tab") {
                assert {
                    elementWithTextPresented("Save your mnemonic phrase")
                    elementWithTextPresented(" Please save your mnemonic phrase on paper to be able to recover your keys based on the phrase. ")
                    elementPresented(mnemonicPhraseGenField)
                    elementPresented(clickToClipboardButton)
                    elementPresented(mnemonicPhraseCheckbox)
                    elementPresented(cancelButtonFromDialog)
                    elementPresented(continueButton)
                }
            }

            step("Finish keys generation") {
                e {
                    setCheckbox(mnemonicPhraseCheckbox, true)
                    click(continueButton)
                    checkFieldIsNotEmpty(publicKeyInput)
                    checkFieldIsNotEmpty(privateKeyInput)
                }
            }

        }
    }

    @Disabled("no SMS 2FA in project")
    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("12-17: Change from none to 2FA sms")
    fun changeFromNoneTo2FaSMS() {

    }

    @Disabled("no SMS 2FA in project")
    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("18-19: Log in by 2FA sms")
    fun loginBy2FASMS() {

    }

    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("20-22: Registration without KYC")
    fun registrationWithoutKYC() {

        val user = newUser()

        step("User get invitation") {
            with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                sendInvitation(user.email, false)
            }
        }

        step("User go to link from email") {
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefForNewUserATM(user.email)
            driver.navigate().to(href)
        }

        with(AtmLoginPage(driver)) {

            step("Check the register form") {
                assert {
                    elementPresented(atmUserEmailForRegistration)
                    elementPresented(atmUserPassword)
                    elementPresented(atmUserConfirmPassword)
                    elementPresented(capcha)
                }
            }

            step("Fill in the register form") {
                e {
                    sendKeys(atmUserEmailForRegistration, user.email)
                    sendKeys(atmUserPassword, user.password)
                    sendKeys(atmUserConfirmPassword, user.password)
                    click(capcha)
                }
            }

            step("User presses register button") {
                assert {
                    elementPresented(registerNewAtmUser)
                }

                e {
                    wait {
                        until("Button 'Register' should be enabled") {
                            registerNewAtmUser.getAttribute("disabled") == null
                        }
                    }
                    click(registerNewAtmUser)
                }
            }

            step("Check the KYC is not passed") {
                with(AtmProfilePage(driver)) {
                    assert {
                        elementPresented(startKYC)
                    }
                }
            }
        }
    }

    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("23-34: Change from none to 2FA APP")
    fun changeFromNoneTo2FAAPP() {

        val user = Users.ATM_USER_ADD_2FA_APP

        with(openPage<AtmProfilePage>(driver) { submit(user) }) {

            step("User clicks 2FA button and uses wrong code") {
                e {
                    click(googleAuthButton)
                    sendKeys(codeInput, "123456")
                    click(submitCodeButton)
                }

                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }

            step("User waits 60 seconds and click resend code button and use the code") {
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
            }

            with(AtmKeysPage(driver)) {

                step("Check the 2FA APP activate form") {
                    assert {
                        elementPresented(cancelButton)
                        elementPresented(disabledContinueButton)
                        elementPresented(codeSaveConfirmCheckbox)
                        elementPresented(appCodeInput)
                        elementPresented(googleOauthLink)
                        elementPresented(redHatFreeOtpLink)
                    }

                }

                step("User uses wrong code") {
                    e {
                        sendKeys(appCodeInput, "12121")
                        setCheckbox(codeSaveConfirmCheckbox, true)
                        click(continueButton)
                    }
                }

                step("Check wrong code message") {
                    assert {
                        elementContainingTextPresented("Wrong code")
                    }
                }

                val secretCode = getAuthSecretCode()

                step("Finish 2FA to APP with code ${secretCode}") {
                    e {

                        sendKeys(appCodeInput, OAuth.generateCode(secretCode))
                        click(continueButton)

                        if (check {
                                e {
                                    isElementWithTextPresented("Wrong code", 2L)
                                }
                            }) {
                            sendKeys(appCodeInput, OAuth.generateCode(secretCode))
                            click(continueButton)
                        }
                    }
                }

                step("Check success changing 2FA message") {
                    assert {
                        elementContainingTextPresented("You have successfully completed 2nd factor authentication setting")
                    }
                }

                val changed2FAEmailBody = GmailApi.getBodyChange2FAStatus(user.email)

                step("Check email body with 2FA changing message") {
                    assert {
                        assertThat(changed2FAEmailBody, containsString("You have successfully changed 2nd factor authentication setting."))
                    }
                }

                step("Log out") {
                    AtmProfilePage(driver).logout()
                }

                step("User login with 2FA APP") {
                    with(openPage<AtmLoginPage>(driver)) {

                        e {
                            sendKeys(atmUserEmail, user.email)
                            sendKeys(atmUserPassword, user.password)
                            val since = LocalDateTime.now(ZoneOffset.UTC)
                            click(signInButton)

                            submitConfirmationCode(secretCode)
                        }

                        assert {
                            urlEndsWith("/profile/info")
                        }
                    }
                }

                step("Set 2FA auth as NONE") {

                    e {
                        click(none2fa)
                        submitConfirmationCode(secretCode)
                    }

                    assert {
                        elementContainingTextPresented("You have successfully completed 2nd factor authentication setting")
                    }
                }
            }
        }
    }

    @TmsLink("ATMCH-5264")
    @Test
    @DisplayName("35-40:Forgot the password")
    fun forgotThePassword() {

        val user = Users.ATM_USER_FOR_RECOVERY
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"

        with(openPage<AtmLoginPage>(driver)) {

            step("User clicks forgot password button") {
                e {
                    click(forgotPassword)
                }
            }

            val since = LocalDateTime.now(ZoneOffset.UTC)

            step("User sets the email and click recovery button") {
                with(AtmForgotPassPage(driver)) {

                    e {
                        sendKeys(emailRecovery, user.email)
                        click(sendInstructionsButton)
                    }

                    assert {
                        elementWithTextPresented(" E-mail to reset your password has been sent. You may attempt again in 5 minutes ")
                    }
                }
            }

            step("Go to recovery password link from thw email") {
                openPage<AtmHomePage>(driver)
                val href = GmailApi.getHrefPassRecoveryUserATM(user.email, since)
                driver.navigate().to(href)
            }

            step("Check recovery password page") {
                assert {
                    elementWithTextPresented("Enter new password")
                    elementPresented(atmUserPasswordForRestore)
                    elementPresented(atmUserConfirmRestorePassword)
                    elementPresented(confirm)
                }
            }

            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)

            step("User changes the password") {
                enterNewPassword(password)
            }

            step("Check change password mesage from the email") {
                val hrefRec = GmailApi.getBodyPassChangedUserATM(user.email, sinceRec)
                assertThat(hrefRec, containsString("Your password has been reset"))

            }

            step("User logs in with new password") {
                openPage<AtmLoginPage>(driver)

                e {
                    sendKeys(atmUserEmail, user.email)
                    sendKeys(atmUserPassword, password)
                    click(signInButton)
                }

                assert { urlEndsWith("/profile/info") }
            }
        }
    }

}