package pages.atm

import io.qameta.allure.Allure
import models.user.classes.DefaultUser
import models.user.classes.DefaultUserWith2FA
import models.user.interfaces.auth.HasTwoFA
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.gmail.GmailApi
import utils.helpers.OAuth
import java.time.LocalDateTime
import java.time.ZoneOffset

@PageUrl("/profile/info")
class AtmProfilePage(driver: WebDriver) : AtmPage(driver) {

    @Name("Google auth")
    @FindBy(xpath = "//span[contains(text(), '2FA APP')]")
    lateinit var googleAuthButton: Button

    @Name("None (Not reccomended)")
    @FindBy(xpath = "//button//span[contains(text(),' None ')]")
    lateinit var none2fa: Button

    @Name("Confirm OTP")
    @FindBy(css = "button[type='submit']")
    lateinit var confirm: Button

    @Name("Code input")
    @FindBy(css = "input[formcontrolname='otpCode']")
    lateinit var codeInput: TextInput

    @Name("Submit code")
    @FindBy(css = "button[type='submit']")
    lateinit var submitCodeButton: Button

    @Name("Resend OTP")
    @FindBy(xpath = "//button[contains(text(), 'Re-send code')]")
    lateinit var resendOtpCode: Button

    @Name("Secret key")
    @FindBy(xpath = "//span[@class='mobile-app__span mobile-app__span--value']")
    lateinit var secretKey: TextInput

    @Name("Confirm that you have written down or save secret code")
    @FindBy(xpath = "//span[@class='ant-checkbox-inner']")
    lateinit var confirmSecretSaved: CheckBox

    @Name("App key")
    @FindBy(css = "input[formcontrolname='appCode']")
    lateinit var appKey: TextInput

    @Name("Google confirmation")
    @FindBy(css = "label[formcontrolname='codeSavedConfirm']")
    lateinit var googleConfirmationCheckbox: CheckBox

    @Name("Continue button")
    @FindBy(xpath = "//span[contains(text(), 'Continue')]")
    lateinit var continueButton: CheckBox

    @Name("Start")
    @FindBy(xpath = "//span[contains(text(), 'START')]")
    lateinit var start: Button

    @Name("Re-send button")
    @FindBy(xpath = "//atm-countdown-otp//a[contains(text(), 'Re-send code')]")
    lateinit var reSendButton: Button

    @Name("Start KYC")
    @FindBy(xpath = "//span[contains(text(), 'Start KYC')]")
    lateinit var startKYC: Button

    @Name("Confirm start KYC")
    @FindBy(xpath = "//span[contains(text(), 'START')]")
    lateinit var confirmStartKYC: Button

    @Name("Cancel start KYC")
    @FindBy(xpath = "//span[contains(text(), 'Cancel')]")
    lateinit var cancelStartKYC: Button

    @Name("Key & Signature management")
    @FindBy(css = "atm-profile a[href='/profile/keys']")
    lateinit var keySignatureGenerator: Button

    @Name("Employees")
    @FindBy(css = "atm-profile a[href='/profile/employees']")
    lateinit var employees: Button

    @Name("Bank accounts")
    @FindBy(css = "atm-profile a[href='/profile/bank-accounts']")
    lateinit var bankAccount: Button

    @Name("Change password")
    @FindBy(css = "atm-profile a[href='/profile/change-password']")
    lateinit var change_password: Button

    @Name("Wallets")
    @FindBy(xpath = "//atm-header//a[contains(text(), 'Wallets')]")
    lateinit var wallets: Button

    @Name("Trading")
    @FindBy(css = "a[href='/trading']")
    lateinit var trading: Button

    @Name("Orders")
    @FindBy(css = "a[href='/orders']")
    lateinit var orders: Button

    @Name("Explorer")
    @FindBy(css = "a[href='/explorer']")
    lateinit var explorer: Button

    @Name("Validator")
    @FindBy(css = "a[href='/validator']")
    lateinit var validator: Button

    @Name("Issuances")
    @FindBy(css = "a[href='/issuances']")
    lateinit var issuances: Button

    @Name("Marketplace")
    @FindBy(xpath = "//a[contains(text(), 'Marketplace')]")
    lateinit var marketPlace: Button

    @Name("Devices")
    @FindBy(xpath = "//a[contains(text(),'Devices')]")
    lateinit var devices: Button

    @Name("Change password")
    @FindBy(xpath = "//a[contains(text(),'Change password')]")
    lateinit var changePassword: Button

    @Name("Company name")
    @FindBy(xpath = "//atm-property-value//span[contains(text(),'LEGAL ENTITY NAME')]//ancestor::div[3]//atm-span")
    lateinit var companyName: TextBlock

    @Name("Participant role")
    @FindBy(xpath = "//atm-property-value//span[contains(text(),'PARTICIPANT ROLE')]//ancestor::div[3]//atm-span")
    lateinit var participantRole: TextBlock

    // TODO: Проверить работу
    fun switchToGoogleAuth(user: DefaultUser): HasTwoFA {
        val since = LocalDateTime.now(ZoneOffset.UTC)
        e {
            click(googleAuthButton)
        }
        val code = GmailApi.getVerificationCode(user.email, since)
        e {
            sendKeys(codeInput, code)
            click(submitCodeButton)
        }
        val secret = secretKey.getAttribute("innerHTML")
        Allure.addAttachment("secret", secret)
        println(secret)
        e {
            click(googleConfirmationCheckbox)
            sendKeys(appKey, OAuth.generateCode(secret))
            click(continueButton)
        }
        nonCriticalWait {
            until("") {
                check {
                    isElementContainingTextPresented("You have successfully completed 2nd factor authentication setting")
                }
            }
        }
        return DefaultUserWith2FA(
            email = user.email,
            oAuthSecret = secret
        )
    }

    fun getCompanyName(): String {
        return companyName.text
    }

    fun getParticipantRole(): String {
        return participantRole.text
    }


}