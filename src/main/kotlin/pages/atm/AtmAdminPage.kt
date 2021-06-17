package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.BasePage
import pages.core.annotations.Action
import pages.core.annotations.PageDomain
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import utils.Environment
import utils.helpers.logout

@PageDomain(
    baseUrlProperty = "front.atm_admin.url",
    baseUrlLoginProperty = "front.atm_admin.login",
    baseUrlPasswordProperty = "front.atm_admin.password",
    authProvider = AtmAdminLoginPage::class
)
@PageUrl("/", authRequired = false)
open class AtmAdminPage(driver: WebDriver) : BasePage(driver) {

    @Name("Invites")
    @FindBy(xpath = "//span[contains(text(),'Invites')]")
    lateinit var ATMadminInvites: Button

    @Name("Entities")
    @FindBy(xpath = "//span[contains(text(),'Entities')]")
    lateinit var entitesButton: Button

    @Name("User management")
    @FindBy(xpath = "//span[contains(text(),'User management')]")
    lateinit var userManagement: Button

    @Name("Log out")
    @FindBy(xpath = "//span[text()='Log out']")
    lateinit var logoutButton: Button

    @Step("Go to invites tab")
    @Action("click \"Invites\" on the menu")
    fun clickInvites(): AtmAdminPage {
        e {
            click(ATMadminInvites)
        }
        return this
    }

    @Step("Logout from admin page")
    fun logout() {
        e {
            click(logoutButton)
        }
    }
    //test
}