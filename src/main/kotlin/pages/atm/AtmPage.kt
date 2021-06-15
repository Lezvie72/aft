package pages.atm

import io.qameta.allure.Step
import io.qameta.allure.model.Status
import models.user.interfaces.User
import models.user.interfaces.auth.HasTwoFA
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedConditions.*
import pages.BasePage
import pages.core.annotations.Action
import pages.core.annotations.PageDomain
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.Environment
import utils.helpers.*

@PageDomain(
    baseUrlProperty = "front.atm.url",
    baseUrlLoginProperty = "front.atm.login",
    baseUrlPasswordProperty = "front.atm.password",
    authProvider = AtmLoginPage::class
)
open class AtmPage(driver: WebDriver) : BasePage(driver) {

    @Name("Login")
    @FindBy(xpath = "//a[contains(text(),'Login')]")
    lateinit var atmLoginButton: Button

    @Name("Logout")
    @FindBy(xpath = "//span[contains(text(), 'Logout')]/ancestor::button")
    lateinit var atmLogoutButton: Button

    @Name("OTP confirmation")
    @FindBy(css = "atm-confirm-otp input[formcontrolname='otpCode']")
    lateinit var atmOtpConfirmationInput: TextInput

    @Name("Label of OFFER DETAILS window")
    @FindBy(xpath = "//h2[contains(text(), 'OFFER DETAILS')]")
    lateinit var offerDetailsLabel: TextBlock

    @Name("Label of confirmation window Confirmation")
    @FindBy(xpath = "//div[contains(text(), 'Confirmation')]")
    lateinit var confirmationLabel: TextBlock

    @Name("Label of confirmation window Manual signature")
    @FindBy(xpath = "//div[contains(text(), 'Manual signature')]")
    lateinit var manualSignatureLabel: TextBlock

    @Name("Label of confirmation window Confirm trade")
    @FindBy(xpath = "//div[contains(text(), 'Confirm trade')]")
    lateinit var confirmTradeLabel: TextBlock

    @Name("Label of cancel offer confirmation window")
    @FindBy(xpath = "//div[contains(text(), 'Cancel offer confirmation')]")
    lateinit var cancelOfferLabel: TextBlock

    @Name("Private key")
//    @FindBy(xpath = "//nz-form-control//atm-hide-value-input[@formcontrolname='secretKey']//input")
    @FindBy(xpath = ".//atm-hide-value-input[@formcontrolname='secretKey']//textarea")
    lateinit var privateKey: TextInput

    @Name("Confirm private key")
    @FindBy(xpath = "//button[2]//span[contains(text(), 'Confirm')]")
    lateinit var confirmPrivateKeyButton: Button

    @Name("Cancel submit private key")
    @FindBy(xpath = "//button//span[contains(text(), 'Cancel')]")
    lateinit var cancelSubmitPrivateKeyButton: Button

    @Name("Cancel enter verification code")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var atmOtpCancel: Button

    @Name("OTP confirmation")
    @FindBy(xpath = "//atm-confirm-otp//button//span[contains(text(), 'Confirm')]")
    lateinit var atmOtpConfirmationConfirmButton: Button

    @Name("Profile")
    @FindBy(xpath = "//a[@href='/profile']")
    lateinit var profile: Button

    @Step("Click button Login")
    fun login(): AtmLoginPage {
        e {
            click(atmLoginButton)
        }
        return AtmLoginPage(driver)
    }

    @Step("Logout")
    @Action("Logout")
    fun logout(): AtmLoginPage {
        e {
            click(profile)
            click(atmLogoutButton)
        }
        driver.logout(Environment.atm_front_base_url)
        return AtmLoginPage(driver)
    }

    @Step("Sign message with manual key")
    fun signMessage(manualSecretKey: String?): Boolean {
        val secretKey = manualSecretKey ?: error("Couldn't find manual secret key doesn't have manual private key")

        return check {
            privateKey.ifPresented(30L) {
                e {
                    click(privateKey)
                    sendKeys(privateKey, secretKey)
                    click(confirmPrivateKeyButton)
                }
                nonCriticalWait(15L) {
                    until("") {
                        val locator =
                            By.xpath(".//atm-hide-value-input[@formcontrolname='secretKey']//input")
                        this.findElements(locator).size == 0
                    }
                }
                return@check true
            }
            false
        }
    }

    fun submitConfirmationCode(user: User) {
        if (user is HasTwoFA) {
            submitConfirmationCode(user.oAuthSecret)
        }
    }

    @Step("Submit confirmation code")
    open fun submitConfirmationCode(oAuthSecret: String?): Boolean {
        repeat(3) {
            enterConfirmationCode(oAuthSecret)
            if (driver.findElements(By.xpath("//*[text()=' Wrong code ']")).size == 0) {
                return true
            }
            attachScreenshot("Wrong code", driver)
            updateStep {
                status = Status.BROKEN
            }
        }
        return false
    }

    @Step("Enter confirmation code")
    fun enterConfirmationCode(oAuthSecret: String?) {
        check {
            atmOtpConfirmationInput.ifPresented {
                val code = oAuthSecret?.let {
                    OAuth.generateCode(it)
                } ?: Constants.DEFAULT_AUTH_CODE

                e {
                    sendKeys(it, code)
                    click(atmOtpConfirmationConfirmButton)
                }
            }
        }
    }

//    @Step("Sign message and submit with confirmation code")
//    fun signAndSubmitMessage(user: User = Users.getUser()) = signAndSubmitMessage(user.manualSecretKey, user.oAuthSecret)

    @Step("Sign message and submit with confirmation code")
    fun signAndSubmitMessage(
        oAuthSecret: String?,
        manualSecretKey: String?
    ) {
        (signMessage(manualSecretKey) and submitConfirmationCode(oAuthSecret))
            .alsoIf(false) {
                attachScreenshot("Sign and submit", driver)
                updateStep {
                    status = Status.BROKEN
                }
            }
    }

    @Step("Sign message and submit with confirmation code")
    fun signAndSubmitMessage(
        user: User,
        manualSecretKey: String?
    ) {
        if (user is HasTwoFA) {
            signAndSubmitMessage(user.oAuthSecret, manualSecretKey)
        } else {
            signAndSubmitMessage(null, manualSecretKey)
        }
    }

}