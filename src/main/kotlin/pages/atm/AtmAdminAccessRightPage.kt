package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.time.LocalDateTime
import java.time.ZoneOffset

@PageUrl("/access-right")
class AtmAdminAccessRightPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val EMAIL = "Email"
        const val ROLE = "Role"
    }

    @Name("User list")
    @FindBy(xpath = "//sdex-access-right/article/div")
    lateinit var userList: SdexTable


    @Name("Add user")
    @FindBy(xpath = "//button//span[contains(text(), 'Add user')]")
    lateinit var addUser: Button

    @Name("Email input")
    @FindBy(xpath = "//input[@formcontrolname='email']")
    lateinit var emailInput: TextInput

    @Name("Add dialog button")
    @FindBy(xpath = "//mat-dialog-actions//button//span[contains(text(), 'Add')]")
    lateinit var addButtonDialog: Button

    @Name("Cancel dialog button")
    @FindBy(xpath = "//mat-dialog-actions//button//span[contains(text(), 'Cancel')]")
    lateinit var cancelButtonDialog: Button

    @Name("Navigate to last page")
    @FindBy(xpath = "//button[@aria-label='Last page']")
    lateinit var navigateLastPage: Button

    @Step("Find user {email}")
    fun findUser(email: String) {
        userList.waitUntilReady()
        var userFound: Boolean
        do {
            userFound = userList.table.columnsAsString[0]?.any {
                it.toLowerCase().contains(email)
            } == true
            if (userFound) {
                break
            }
            if (userList.hasNextPage()) {
                userList.nextPage()
            } else {
                break
            }
        } while (true)
        assertThat("Couldn't find user with email $email", userFound, Matchers.equalTo(true))
    }

    @Step("Get role for user {email}")
    fun getRoleForUser(email: String): String {
        val user = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${email}')]/ancestor::tr//mat-select"))
        }.to<AtmSelect>("User select")
        return user.text
    }

    @Step("Set role for user {email} to {role}")
    fun selectRole(email: String, role: String) {
        val user = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${email}')]/ancestor::tr//mat-select"))
        }.to<AtmSelect>("User select")

        e {
            select(user, role)
        }
    }

    @Step("Save changes for user {email}")
    fun saveUser(email: String) {
        val user = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${email.toLowerCase()}')]/ancestor::tr//mat-icon[contains(text(), 'save')]"))
        }.to<Button>("Employee '$email'")
        e {
            click(user)
        }
    }

    @Step("Send new  password to user {email}")
    fun sendNewPassword(email: String): LocalDateTime {
        Thread.sleep(5_000)
        val user = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${email.toLowerCase()}')]/ancestor::tr//mat-icon[contains(text(), 'vpn_key')]"))
        }.to<Button>("Employee '$email'")
        e {
            click(user)
        }
        return LocalDateTime.now(ZoneOffset.UTC)
    }

    @Step("Delete user {email}")
    fun deleteUser(email: String) {
        val user = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${email.toLowerCase()}')]/ancestor::tr//mat-icon[contains(text(), 'delete')]"))
        }.to<Button>("Employee '$email'")
        e {
            click(user)
        }
    }

}