package pages.atm

import io.qameta.allure.Allure
import io.qameta.allure.Step
import junit.framework.Assert.assertEquals
import junit.framework.Assert
import models.user.classes.DefaultUser
import models.user.classes.DefaultUserWith2FA
import models.user.interfaces.auth.HasTwoFA
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
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

    @Name("Add node")
    @FindBy(css = "a[href='/validator/add']")
    lateinit var addNode: Button

    @Name("Arrow button")
    @FindBy(xpath = "//*[contains(@class,'ant-select-arrow')]")
    lateinit var arrowBtn: TextBlock

    @Name("Endorser type")
    @FindBy(xpath = "//nz-option-item[contains(@title, 'ENDORSER')]")
    lateinit var endorserType: TextBlock

    @Name("Submit button")
    @FindBy(xpath = "//span[contains(text(), 'Submit')]")
    lateinit var submitBtn: Button

    @Name("New node request")
    @FindBy(xpath = "//h2[contains(text(), 'New node request')]")
    lateinit var newNodeRequest: TextBlock

    @Name("No tokens added yet")
    @FindBy(xpath = "//p[contains(text(), 'No tokens added yet')]")
    lateinit var noTokensAddedYet: TextBlock

    @Name("Any token added")
    @FindBy(xpath = "//atm-separated-card[contains(@class,'card content-block ng-star-inserted')]")
    lateinit var anyTokenAdded: TextBlock

    @Name("Employee role")
    @FindBy(xpath = "//atm-property-value//span[contains(text(),'EMPLOYEE ROLE')]//ancestor::div[3]//atm-span")
    lateinit var employeeRole: TextBlock

    @Name("No bank accounts added yet")
    @FindBy(xpath = "//h2[contains(text(), 'No bank accounts added yet')]")
    lateinit var noBankAccountAddedYet: TextBlock

    @Name("ADD EMPLOYEE button")
    @FindBy(xpath = "//span[contains(text(), 'ADD EMPLOYEE')]")
    lateinit var addEmployeeBtn: Button

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

    @Step("User Issuer checks available sections on the platform")
    fun issuerChecksAvailableSectionsOnThePlatform() {
        val settings = mapOf(
            "Trading" to (trading),
            "Orders" to (orders),
            "Wallets" to (wallets),
            "Validator" to (validator),
            "Explorer" to (explorer),
            "Issuances" to (issuances)
        )
        for ((key, value) in settings) {
            when (key) {
                "Validator" -> assert { elementNotPresentedWithCustomTimeout(value as WebElement, 1) }
                else -> check { isElementPresented(value as WebElement) }
            }
        }
    }

    @Step("User Issuer goes to issuance page and checks that no any tokens")
    fun goesToIssuerGoesToIssuancePageAndChecksThatPageIsOpen() {
        e{
            click(issuances)
        }
        assert {
            elementPresented(noTokensAddedYet)
        }
    }

    @Step("User Issuer goes to issuance page and see linked tokens")
    fun goesToIssuerGoesToIssuancePageAndSeeLinkedTokens() {
        e{
            click(issuances)
        }
        assert {
            elementPresented(anyTokenAdded)
        }
    }
    fun checksInformationSection(): String {
        return participantRole.text
    }

    @Step("User checks his company participant role")
    fun checksInformationSection(usersParticipantRole: String) {
        assert {
            elementPresented(participantRole)
            assertEquals(participantRole.text, usersParticipantRole)
        }
    }
    @Step("User Validator checks available sections on the platform")
    fun checksAvailableSectionsOnThePlatform() {
        val settings = mapOf(
            "Trading" to (trading),
            "Orders" to (orders),
            "Wallets" to (wallets),
            "Validator" to (validator),
            "Explorer" to (explorer),
            "Issuances" to (issuances)
        )
        for ((key, value) in settings) {
            when (key) {
                "Issuances" -> assert { elementNotPresentedWithCustomTimeout(value as WebElement, 1) }
                else -> check { isElementPresented(value as WebElement) }
            }
        }
    }

    @Step("User Validator goes to validators page and adds node")
    fun goesToValidatorsPageAndAddsNode() {
        e{
            click(validator)
            click(addNode)
            click(arrowBtn)
            click(endorserType)
            click(submitBtn)
        }
        assert{
            elementPresented(newNodeRequest)
        }
    }

    @Step("User Generic Participant Manager checks available sections on the platform")
    fun genericParticipantUserChecksAvailableSectionsOnThePlatform() {
        val settings = mapOf(
            "Trading" to (trading),
            "Orders" to (orders),
            "Wallets" to (wallets),
            "Explorer" to (explorer)
        )
        for ((key, value) in settings) {
            when (key) {
                "Validator" -> assert { elementNotPresentedWithCustomTimeout(value as WebElement, 1) }
                "Issuances" -> assert { elementNotPresentedWithCustomTimeout(value as WebElement, 1) }
                else -> check { isElementPresented(value as WebElement) }
            }
        }
    }

    @Step("User Generic Participant Manager checks available sections him")
    fun checksAvailableSectionsForGenericParticipantManager() {
        assert { elementNotPresentedWithCustomTimeout(employees, 1) }
        e{
            click(bankAccount)
        }
        assert {
            elementPresented(noBankAccountAddedYet)
        }
    }

    @Step("User checks his employee role")
    fun checksUserInformationSection(usersEmployeeRole: String) {
        assert {
            elementPresented(employeeRole)
            assertEquals(employeeRole.text, usersEmployeeRole)
        }
    }

    @Step("User Generic Participant Admin checks available sections him")
    fun checksAvailableSectionsForGenericParticipantAdmin() {
        e{ click(bankAccount) }
        assert { elementPresented(noBankAccountAddedYet) }
        driver.navigate().back()
        e{ click(employees) }
        assert { elementPresented(addEmployeeBtn) }
    }

}