package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import pages.atm.AtmEmployeesPage.Roles.MANAGER
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("KYC Status Model")
class KYCStatusModel : BaseTest() {

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

    @TmsLink("ATMCH-1566")
    @Test
    @DisplayName("Checking Green (auto) status in Atomyze for role administrator")
    fun checkGreenStatusTest() {
        val user = step("GIVEN user created") {
            createAndRegisterUser(true)
        }
        with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val status = getKycStatusForUserByEmail(user.email)
            assertThat("Wrong status", status, Matchers.equalTo("GREEN AUTO"))
        }
    }

    @TmsLink("ATMCH-1570")
    @Test
    @DisplayName("Checking Green (employee) status in Atomyze role administrator")
    fun checkGreenStatusForEmployeeTest() {
        val user = newUser()
        step("GIVEN user created") {
            with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
                addAndRegistrationEmployee(user.email, MANAGER)
            }
        }
        with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val status = getKycStatusForUserByEmail(user.email)
            assertThat("Wrong status", status, Matchers.equalTo("GREEN EMPLOYEE"))
        }
    }

}