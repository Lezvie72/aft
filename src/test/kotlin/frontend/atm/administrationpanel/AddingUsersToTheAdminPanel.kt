package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Adding users to the Admin Panel")
class AddingUsersToTheAdminPanel : BaseTest() {


    @TmsLink("ATMCH-1631")
    @Test
    @DisplayName("Settings. Access right. Change password (happyway)")
    fun settingsAccessRightChangePassword() {
        val user = Users.ATM_USER_KYC0_2FA_NONE
//        val since = LocalDateTime.now(ZoneOffset.UTC)
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"
        val email = user.email

        val since = with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
            findUser(email)
            val since = sendNewPassword(email)

            assert {
                elementWithTextPresented("A link was successfully sent to the user’s email")
            }
            logout()
            since
        }
        with(openPage<AtmAdminLoginPage>(driver)) {
            val href =
                GmailApi.getHrefPassRecoveryLinkUserADMIN(user.email, since)
            driver.navigate().to(href)
            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
            enterNewPassword(password)
            openPage<AtmAdminLoginPage>(driver)
            submit(email, password)
        }
    }

    @TmsLink("ATMCH-1634")
    @Test
    @DisplayName("Settings. Access right. Delete user")
    fun settingsAccessRightDeleteUser() {
        val user = newUser()
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"
        val since = LocalDateTime.now(ZoneOffset.UTC)
        val email = user.email
        with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(addUser)
                sendKeys(emailInput, email)
                click(addButtonDialog)
                click(navigateLastPage)
                selectRole(email, " SUPPORT ")
                saveUser(email)
                sendNewPassword(email)
                logout()
            }
        }
        with(openPage<AtmAdminLoginPage>(driver)) {
            val href = GmailApi.getHrefPassRecoveryLinkUserADMIN(user.email)
            driver.navigate().to(href)
            enterNewPassword(password)
            openPage<AtmAdminLoginPage>(driver)
            submit(email, password)
            logout()
            submit(Users.ATM_ADMIN)
        }
        with(openPage<AtmAdminAccessRightPage>(driver)) {
            e {
                findUser(email)
                deleteUser(email)
            }
            assert {
                elementWithTextPresented("User $email successfully deleted")
            }
            logout()
        }
        with(openPage<AtmAdminLoginPage>(driver)) {
            e {
                click(adminEmail)
                sendKeys(adminEmail, email)
                sendKeys(adminPassword, password)
                click(login)
            }
            assert {
                elementWithTextPresented("Invalid login. Such user does not exist.")
            }
        }
    }

    @TmsLink("ATMCH-1628")
    @Test
    @DisplayName("Settings. Access right. Change role")
    fun settingsAccessRightChangeRole() {
        //TODO: add DB checks
        val user = Users.ATM_USER_KYC0_2FA_NONE
        val email = user.email
        with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
            findUser(email)
            val currentRole = getRoleForUser(email)
            val newRole = if (currentRole == "SUPPORT") "CONTENT_MANAGER" else "SUPPORT"
            e {
                selectRole(email, newRole)
                driver.navigate().refresh()
                findUser(email)
            }
            assert {
                val roleAfterRefresh = getRoleForUser(email)
                assertThat("", roleAfterRefresh, Matchers.not(Matchers.equalTo(newRole)))
            }
            e {
                findUser(email)
                selectRole(email, newRole)
                saveUser(email)
                driver.navigate().refresh()
                findUser(email)
            }
            assert {
                val roleAfterSave = getRoleForUser(email)
                assertThat("", roleAfterSave, Matchers.equalTo(newRole))
            }
        }
    }

    @TmsLink("ATMCH-1627")
    @Test
    @DisplayName("Settings. Access right. Add user (add-cancel-add)")
    fun settingsAccessRightAddUser() {
        val since = LocalDateTime.now(ZoneOffset.UTC)
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"
        val user = newUser()

        with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(addUser)
            }
            assert {
                elementPresented(emailInput)
                elementPresented(addButtonDialog)
                elementPresented(cancelButtonDialog)
            }
            e {
                sendKeys(emailInput, user.email)
                click(cancelButtonDialog)
            }
            assert {
                elementContainingTextNotPresented(user.email)
            }

            e {
                click(addUser)
                sendKeys(emailInput, user.email)
                click(addButtonDialog)
                click(navigateLastPage)
                selectRole(user.email, "SUPPORT")
                saveUser(user.email)
            }
            assert {
                elementWithTextPresented("User ${user.email} successfully added")
            }
            e {
                sendNewPassword(user.email)
            }
            assert {
                elementWithTextPresented("A link was successfully sent to the user’s email")
            }
            logout()
        }
        with(openPage<AtmAdminLoginPage>(driver)) {
            val href = GmailApi.getHrefPassRecoveryLinkUserADMIN(user.email, since)
            driver.navigate().to(href)
            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
            enterNewPassword(password)

            openPage<AtmAdminLoginPage>(driver)
            submit(user.email, password)
        }
    }

    @TmsLink("ATMCH-1624")
    @Test
    @DisplayName("Settings. Access right. UI. Tab and overview")
    fun settingsAccessRightUItabOverview() {
        with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Email")
                elementContainingTextPresented("Role")
                elementWithTextPresented("vpn_key")
                elementWithTextPresented("save")
                elementWithTextPresented("undo")
                elementWithTextPresented("delete")
                elementPresented(addUser)
            }
        }
    }
}

