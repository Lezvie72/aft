package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminInvitesPage
import pages.atm.AtmAdminUserManagementPage
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Atomyze Users management")
class AtomyzeUsersManagement : BaseTest() {

    private fun createAndRegisterUser(kycPassed: Boolean = false): DefaultUser {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, kycPassed)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        return user
    }

    @TmsLink("ATMCH-448")
    @Test
    @DisplayName("Set First and Last name in Admin Panel")
    fun setFirstAndLastNameInAdminPanel() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(userManagement)
            }
            assert {
                urlEndsWith("/user-management")
            }
        }
        with(AtmAdminUserManagementPage(driver)) {
            assert {
                elementWithTextPresented("Id ")
                elementWithTextPresented("First Name ")
                elementWithTextPresented("Last Name ")
                elementWithTextPresented("eMail ")
                elementWithTextPresented("KYC ")
                elementWithTextPresented("2FA ")
                elementWithTextPresented("Created ")
                elementWithTextPresented("Updated ")
//                elementPresented(verifyKYB)
//                elementPresented(verifyKYC)
                elementPresented(search)
                elementPresented(editButton)
//                elementPresented(requestEmailButton)
            }
            e {
                sendKeys(search, user.email.substring(0, 15))
                click(firstRow)
                click(editButton)
            }
            assert {
                elementPresented(firstNameInput)
                elementPresented(lastNameInput)
                elementPresented(saveButton)
                elementPresented(cancelButton)
//                elementPresented(emailIsConfirmedCheckbox)
//                elementPresented(kycCheckbox)
            }
            e {
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(cancelButton)
                checkNotSaveName(user.email)
                click(firstRow)
                click(editButton)
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(saveButton)
                checkName(user.email, "test", "test")
            }
        }
    }

    @Disabled("???? ?????????? ?????? ????????????????????, ???????????????????????? ????????????????, KYC ?? ????.")
    @TmsLink("ATMCH-861")
    @Test
    @DisplayName("User management")
    fun userManagement() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(userManagement)
            }
            assert {
                urlEndsWith("/user-management")
            }
        }
        with(AtmAdminUserManagementPage(driver)) {
            assert {
                elementWithTextPresented("Id ")
                elementWithTextPresented("First Name ")
                elementWithTextPresented("Last Name ")
                elementWithTextPresented("Status ")
                elementWithTextPresented("KYC ")
                elementWithTextPresented("2FA ")
                elementWithTextPresented("Created ")
                elementWithTextPresented("Updated ")
                elementPresented(verifyKYB)
                elementPresented(verifyKYC)
                elementPresented(search)
                elementPresented(editButton)
                elementPresented(requestEmailButton)
            }
            e {
                sendKeys(search, user.email.substring(0, 15))
                click(firstRow)
                click(editButton)
            }
            assert {
                elementPresented(firstNameInput)
                elementPresented(lastNameInput)
                elementPresented(saveButton)
                elementPresented(cancelButton)
                elementPresented(emailIsConfirmedCheckbox)
                elementPresented(kycCheckbox)
            }
            e {
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(cancelButton)
                checkNotSaveName(user.email)
                click(editButton)
                sendKeys(firstNameInput, "test")
                sendKeys(lastNameInput, "test")
                click(saveButton)
                checkName(user.email, "test", "test")
            }
        }
    }

}