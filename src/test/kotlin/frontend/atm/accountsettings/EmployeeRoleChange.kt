package frontend.atm.accountsettings

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmEmployeesPage
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.generateEmail
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Account settings")
@Story("Employee role change")
class EmployeeRoleChange : BaseTest() {

    @TmsLink("ATMCH-1466")
    @Test
    @DisplayName("Changing the role of an employee of a legal entity")
    fun changingTheRoleOfAnEmployeeOfaLegalEntity() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            findEmployee(emailEmp)
            e {
                click(tickerManager)
                click(adminRadio)
                click(change)
            }
            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
            val hrefRec = GmailApi.getBodyATMRoleChanged(emailEmp, sinceRec)
            assert {
                textEmail(
                    hrefRec,
                    "    Dear user,We inform you that your employee role on the platform is changed from MANAGER to ADMIN by your organization.For any details please contact your administrator."
                )
            }
            findEmployee(emailEmp)
            e {
                click(tickerAdmin)
                click(managerRadio)
                click(change)
            }
            val sinceRec1 = LocalDateTime.now(ZoneOffset.UTC)
            val hrefRec1 = GmailApi.getBodyATMRoleChanged(emailEmp, sinceRec1)
            assert {
                textEmail(
                    hrefRec1,
                    "    Dear user,We inform you that your employee role on the platform is changed from ADMIN to MANAGER by your organization.For any details please contact your administrator."
                )
            }
        }
    }
}