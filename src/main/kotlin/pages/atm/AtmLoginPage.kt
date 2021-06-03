package pages.atm

import io.qameta.allure.Step
import models.user.classes.DefaultUser
import models.user.interfaces.User
import models.user.interfaces.auth.HasTwoFA
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.AuthorizationProvider
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.Environment
import utils.gmail.GmailApi
import utils.helpers.authorize
import utils.helpers.getToken
import utils.helpers.openPage
import utils.helpers.takeScreenshot
import java.time.LocalDateTime
import java.time.ZoneOffset

@PageUrl("/login", authRequired = false)
@PageName("Login page")
class AtmLoginPage(driver: WebDriver) : AtmPage(driver),
    AuthorizationProvider<AtmProfilePage> {

    @Name("Email for registration")
    @FindBy(xpath = "//input[@formcontrolname='email']")
    lateinit var atmUserEmailForRegistration: TextInput

    @Name("Password restore new")
    @FindBy(xpath = "//input[@formcontrolname='password']")
    lateinit var atmUserPasswordForRestore: TextInput

    @Name("Confirm Restore Password")
    @FindBy(xpath = "//input[@formcontrolname='passwordConfirm']")
    lateinit var atmUserConfirmRestorePassword: TextInput

    @Name("Password for registration")
    @FindBy(xpath = "//atm-hide-value-input[@formcontrolname='password']//input")
    lateinit var atmUserPasswordForRegistration: TextInput

    @Name("Password for employee registration")
    @FindBy(xpath = "//input[@formcontrolname='password']")
    lateinit var atmUserPasswordForEmployeeRegistration: TextInput

    @Name("Confirm Password")
    @FindBy(xpath = "//atm-hide-value-input[@formcontrolname='passwordConfirm']//input")
    lateinit var atmUserConfirmPassword: TextInput

    @Name("Confirm Password for employee")
    @FindBy(xpath = "//input[@formcontrolname='passwordConfirm']")
    lateinit var atmUserConfirmPasswordForEmployeeRegistration: TextInput

    @Name("Accept terms and conditions")
    @FindBy(xpath = "//label[@formcontrolname='termsAccepted']//span")
    lateinit var atmAcceptTerms: CheckBox

    @Name("Agree recieve newsletter")
    @FindBy(xpath = "//label[@formcontrolname='emailsAllowed']//span")
    lateinit var atmAcceptNews: CheckBox

    @Name("Capcha")
    @FindBy(xpath = "//re-captcha/ancestor::nz-form-item")
    lateinit var capcha: CheckBox

    @Name("Register new user")
    @FindBy(css = "button[type='submit']")
    lateinit var registerNewAtmUser: Button

    @Name("Email")
    @FindBy(xpath = "//input[@formcontrolname='email']")
    lateinit var atmUserEmail: TextInput

    @Name("Password")
    @FindBy(xpath = "//atm-hide-value-input[@formcontrolname='password']//input")
    lateinit var atmUserPassword: TextInput

    @Name("Sign in")
    @FindBy(xpath = "//span[contains(text(), 'Sign in')]/ancestor::button")
    lateinit var signInButton: Button

    @Name("Sign in link")
    @FindBy(xpath = "//a[contains(text(),'Sign in')]")
    lateinit var signInLinkButton: Button

    @Name("Forgot password")
    @FindBy(xpath = "//a[contains(text(),'Forgot password?')]")
    lateinit var forgotPassword: Button

    @Name("Confirm recovery password for email")
    @FindBy(css = "button[type='submit']")
    lateinit var confirm: Button

    @Name("Error message unverified device")
    @FindBy(xpath = "//div[contains(text(), 'please confirm the new device')]")
    lateinit var verificationNeeded: TextBlock

    @Name("Captcha button")
    @FindBy(xpath = "//re-captcha")
    lateinit var captchaButton: Button

    @Step("Fill in the input fields of valid format and press register")
    @Action("Fill registration form and submit")
    fun fillRegForm(): AtmProfilePage {
        e {
            sendKeys(atmUserPasswordForRegistration, Constants.DEFAULT_PASSWORD)
            sendKeys(atmUserConfirmPassword, Constants.DEFAULT_PASSWORD)
            wait {
                until("Button 'Register' should be enabled") {
                    click(capcha)
                    registerNewAtmUser.getAttribute("disabled") == null
                }
            }
            click(registerNewAtmUser)
            nonCriticalWait(5L) {
                untilInvisibility(capcha)
            }
            driver.authorize(Environment.atm_front_base_url)
            nonCriticalWait(10L) {
                untilPresented(AtmProfilePage(driver).devices)
            }
        }
        return AtmProfilePage(driver)
    }

    @Step("Login without 2FA")
    @Action("Login without 2FA")
    fun loginWithout2FA(email: String, password: String = Constants.DEFAULT_PASSWORD): AtmLoginPage {
        e {
            sendKeys(atmUserEmail, email)
            sendKeys(atmUserPassword, password)
            click(signInButton)
        }

        check {
            captchaButton.ifPresented (5L) {
                wait {
                    until("Button 'Register' should be enabled") {
                        e {
                            click(captchaButton)
                            signInButton.getAttribute("disabled") == null

                        }
                    }
                }
                e {
                    click(signInButton)
                }

            }
        }
        driver.authorize(Environment.atm_front_base_url)
        return this
    }

    @Step("SingIn from unregistered device")
    @Action("SingIn from unregistered device")
    fun getLinkForDeviceRegistration() {
    }

    @Step("Password recovery request")
    @Action("Password recovery request")
    fun passwordRecovery(email: String): AtmLoginPage {
        e {
            click(forgotPassword)
            check {
                urlMatches(".*/forgot-password$")
            }
            sendKeys(atmUserEmail, email)
            click(confirm)
        }
        return this
    }

    @Step("Enter new password")
    @Action("Enter new password")
    fun enterNewPassword(newPassword: String): AtmLoginPage {
        e {
            sendKeys(atmUserPasswordForRestore, newPassword)
            sendKeys(atmUserConfirmRestorePassword, newPassword)
            click(confirm)
        }
        return this
    }

//    fun submit(email: String, password: String): AtmProfilePage {
//        return submit(User(email = email, password = password, project = 1, role = email))
//    }

    fun verifyDeviceIfNeeded(
        user: User,
        since: LocalDateTime = LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC)
    ) {
        check {
            verificationNeeded.ifPresented {
                val href = GmailApi.getHrefForVerification(user.email, since)
                e {
                    driver.navigate().to(href)
                    wait {
                        until("Login page should be visible") {
                            atmUserEmail.isPresented()
                        }
                    }
                    sendKeys(atmUserEmail, user.email)
                    sendKeys(atmUserPassword, user.password)
                    click(signInButton)
                }
            }
        }
    }


    override fun openLoginPage(driver: WebDriver) {
        openPage<AtmLoginPage>(driver)
    }

    @Step("Login as {user.email}")
    override fun submit(user: User): AtmProfilePage {
        val since = e {
            sendKeys(atmUserEmail, user.email)
            sendKeys(atmUserPassword, user.password)
            val since = LocalDateTime.now(ZoneOffset.UTC)
            click(signInButton)
            since
        }

        check {
            captchaButton.ifPresented (5L) {
                wait {
                    until("Button 'Register' should be enabled") {
                        e {
                            click(captchaButton)
                            signInButton.getAttribute("disabled") == null
                        }
                    }
                }
                e {
                    click(signInButton)
                }

            }
        }

        //Verification of device
        verifyDeviceIfNeeded(user, since)

        //2FA confirmation
        if(user is HasTwoFA) {
            submitConfirmationCode(user.oAuthSecret)
        }

        wait(10) {
            until("Couldn't authorize with email '${user.email}'") {
                this.getToken() != null
                        && check { urlMatches(".*/profile/info$") }
                        && check { isElementPresented(atmLogoutButton) }
            }
        }

        driver.authorize(Environment.atm_front_base_url)
        driver.takeScreenshot()

        return AtmProfilePage(driver)
    }

//test
}