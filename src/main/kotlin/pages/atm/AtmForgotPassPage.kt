package pages.atm

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/forgot-password")
class AtmForgotPassPage(driver: WebDriver) : AtmPage(driver) {

    @Name("Sign UP Button")
    @FindBy(xpath = "//atm-simple-card//a[contains(text(), 'Sign up')]")
    lateinit var signUpButton: Button

    @Name("Sign IN Button")
    @FindBy(xpath = "//atm-simple-card//a[contains(text(), 'Sign in')]")
    lateinit var signInButton: Button

    @Name("Eamil for recovery")
    @FindBy(xpath = "//input[@formcontrolname='email']")
    lateinit var emailRecovery: TextInput

    @Name("Send instructions")
    @FindBy(xpath = "//span[contains(text(),' Send instructions ')]//ancestor::button")
    lateinit var sendInstructionsButton: Button

}