package pages.atm

import io.qameta.allure.Step
import models.user.classes.DefaultUser
import models.user.interfaces.User
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.AuthorizationProvider
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.getToken
import utils.helpers.openPage

@PageUrl("/login", authRequired = false)
open class AtmAdminLoginPage(driver: WebDriver) : AtmAdminPage(driver), AuthorizationProvider<AtmAdminPage> {

    //region authorization form
    @Name("Email")
    @FindBy(css = "input[formcontrolname='email']")
    lateinit var adminEmail: TextInput

    @Name("Password")
    @FindBy(css = "input[formcontrolname='password']")
    lateinit var adminPassword: TextInput

    @Name("Login")
    @FindBy(css = "sdex-login form button")
    lateinit var login: Button
    //endregion

    //region password restoration
    @Name("Password restore new")
    @FindBy(xpath = "//input[@formcontrolname='password']")
    lateinit var atmUserPasswordForRestore: TextInput

    @Name("Confirm Restore Password")
    @FindBy(xpath = "//input[@formcontrolname='passwordConfirm']")
    lateinit var atmUserConfirmRestorePassword: TextInput

    @Name("Confirm recovery password for email")
    @FindBy(css = "button[type='submit']")
    lateinit var confirm: Button
    //endregion

    @Step("Enter new password")
    @Action("Enter new password")
    fun enterNewPassword(newPassword: String): AtmAdminLoginPage {
        e {
            sendKeys(atmUserPasswordForRestore, newPassword)
            sendKeys(atmUserConfirmRestorePassword, newPassword)
            click(confirm)
        }
        return this
    }

    @Step("Submit login form with {email} and {password}")
    fun submit(email: String, password: String): AtmAdminPage {
        val user = DefaultUser(
            email = email,
            password = password,
            project = 1
        )
        return submit(user)
    }

    override fun openLoginPage(driver: WebDriver) {
        openPage<AtmAdminLoginPage>(driver)
    }

    override fun submit(user: User): AtmAdminPage {
        e {
            click(adminEmail)
            sendKeys(adminEmail, user.email)
            sendKeys(adminPassword, user.password)
            click(login)
        }
        wait(15) {
            until("Couldn't authorize with email '${user.email}'") {
                this.getToken()
            }
        }
        return this
    }
}