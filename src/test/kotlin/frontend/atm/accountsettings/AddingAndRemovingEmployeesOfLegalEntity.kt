package frontend.atm.accountsettings

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmEmployeesPage
import pages.atm.AtmLoginPage
import pages.atm.AtmProfilePage
import utils.Constants
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.generateEmail
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Account settings")
@Story("Adding and removing employees of legal entity")
class AddingAndRemovingEmployeesOfLegalEntity : BaseTest() {

    @TmsLink("ATMCH-1162")
    @Test
    @DisplayName("Adding employees of legal entities. Interface")
    fun addEmployeesOfLegalEntitiesInterface() {
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            e {
                click(addEmployees)
            }
            assert {
                elementIsDisplayed("Email")
                elementIsDisplayed("Admin radio")
                elementIsDisplayed("Manager radio")
                elementIsDisplayed("Add")
            }
        }
    }

    @TmsLink("ATMCH-1258")
    @Test
    @DisplayName("Checking the activation link. Adding employee of legal entities")
    fun checkingTheActivationLinkAddingEmployeeOfLegalEntities() {
        val emailEmp = generateEmail()
        val user = newUser()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addAndApproveEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
            val href = GmailApi.getHrefATMRegistrationEmployee(emailEmp)
            driver.navigate().to(href)
            with(AtmLoginPage(driver)) {
                e {
                    sendKeys(atmUserPasswordForEmployeeRegistration, user.password)
                    sendKeys(atmUserConfirmPasswordForEmployeeRegistration, user.password)
                    click(confirm)
                }
                assert {
                    elementWithTextPresented(" Password has been successfully set ")
                }
            }


        }
    }

    @TmsLink("ATMCH-1161")
    @Test
    @DisplayName("Re adding employee of legal entities")
    fun reAddingEmployeeOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
            reAddingByEmployeeEmail(emailEmp, "Manager")
            val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
            addAndApproveEmployee(emailEmp, AtmEmployeesPage.Roles.ADMIN)
            reAddingByEmployeeEmail(emailEmp, "Admin")

            val hrefRec = GmailApi.getBodyATMRegistrationEmployee(emailEmp, sinceRec)
            assert {
                textEmail(
                    hrefRec,
                    "You were authorized by your organization to register on the Atomyze platform.Please follow the link to complete the procedure.The link will expire in 30 days."
                )
            }

        }
    }

    @TmsLink("ATMCH-1157")
    @Test
    @DisplayName("Activate employee in status Blocked of legal entities ")
    fun activateEmployeeInStatusBlockedOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            blockEmployee(emailEmp)
            activeEmployee(emailEmp)
            checkStatus(emailEmp, AtmEmployeesPage.Status.ACTIVE)
        }
    }

    @TmsLink("ATMCH-1159")
    @Test
    @DisplayName("Check the activated employee  of legal entities ( after Blocking)")
    fun checkTheActivatedEmployeeOfLegalEntitiesAfterBlocking() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            blockEmployee(emailEmp)
            val since = LocalDateTime.now(ZoneOffset.UTC)
            activeEmployee(emailEmp)
            val href = GmailApi.getBodyATMActivatedEmployee(emailEmp.toLowerCase(), since)
            assert {
                textEmail(
                    href,
                    "Dear employee We inform you that your account on the platform is activated by your organization. For any details please contact your administrator.    "
                )
            }
        }
    }

    @TmsLink("ATMCH-1158")
    @Test
    @DisplayName("Deactivate employees with status Blocked of legal entities ")
    fun deactivateEmployeesWithStatusBlockedOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            blockEmployee(emailEmp)
            deactiveEmployee(emailEmp)
            checkStatus(emailEmp, AtmEmployeesPage.Status.INACTIVE)
        }
    }

    @TmsLink("ATMCH-173")
    @Test
    @DisplayName("Adding employees of legal entities. Interface")
    fun addingEmployeesOfLegalEntitiesInterface() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            e {
                click(employees)
            }
        }
        with(AtmEmployeesPage(driver)) {
            assert {
                elementWithTextPresented(" Employees ")
                elementPresented(addEmployees)
            }
        }
    }

    @TmsLink("ATMCH-1151")
    @Test
    @DisplayName("Check log in by blocked employee of legal entities")
    fun checkLogInByBlockedEmployeeOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        val since =
            openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }.blockEmployee(emailEmp)
        openPage<AtmLoginPage>(driver).logout()
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, emailEmp)
                sendKeys(atmUserPassword, Constants.DEFAULT_PASSWORD)
                click(signInButton)
            }

            assert {
                elementWithTextPresented(" Your account on the platform is blocked. Please contact your administrator ")
            }
            val href = GmailApi.getBodyATMBlockedEmployee(emailEmp, since)
            assert {
                newTextEmail(
                    href,
                    "Atomyze profile suspended Your account has been suspended " +
                            "Dear {{ full name }}, Your account has been suspended and is under investigation. " +
                            "For further information, please contact your relationship manager. " +
                            "Best Regards,ATOMYZE ATOMYZE by Tokentrust AG Baarerstrasse 22 6300 Zug Web: www.atomyze.chSocial: LinkedIn Support: support@atomyze.ch "
                )
            }
        }

    }

    @TmsLink("ATMCH-1152")
    @Test
    @DisplayName("Check log in by deactivated employee of legal entities")
    fun checkLogInByDeactivatedEmployeeOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }.deactiveEmployee(emailEmp)
        openPage<AtmLoginPage>(driver).logout()
        with(openPage<AtmLoginPage>(driver)) {
            e {
                sendKeys(atmUserEmail, emailEmp)
                sendKeys(atmUserPassword, Constants.DEFAULT_PASSWORD)
                click(signInButton)
            }
            assert {
                elementContainingTextPresented(" Your account on the platform is deactivated. Please contact your administrator ")
            }
            val href = GmailApi.getBodyATMDeactivatedEmployee(emailEmp)
            assert {
                textEmail(
                    href,
//                    "We inform you that you account on the platform is deactivated by your organization.For any details please contact your administrator."
                    "Dear employee We inform you that your account on the platform is deactivated by your organization. For any details please contact your administrator.    Thanks,    The Atomyze Team."
                )
            }

        }

    }

    @TmsLink("ATMCH-1102")
    @Test
    @DisplayName("Checking the activation link. Remote employee of legal entities")
    fun checkingTheActivationLinkRemoteEmployeeOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addAndApproveEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
            deleteByEmployeeEmail(emailEmp)
            val href = GmailApi.getHrefATMRegistrationEmployee(emailEmp)
            driver.navigate().to(href)
            with(AtmLoginPage(driver)) {
                assert {
                    elementWithTextPresented(" The activation link has expired. Please contact your administrator ")
                }
            }
        }
    }

    @TmsLink("ATMCH-1082")
    @Test
    @DisplayName("Delete employees of legal entities")
    fun deleteEmployeesOfLegalEntities() {
        val emailEmp = generateEmail()
        val user = Users.ATM_USER_FOR_EMPLOYEES_KYC1
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            e {
                click(employees)
            }
        }
        with(AtmEmployeesPage(driver)) {
            addAndApproveEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
            deleteByEmployeeEmail(emailEmp)
            assert {
                elementWithTextNotPresented(emailEmp)
            }
        }
    }

    @TmsLink("ATMCH-860")
    @Test
    @DisplayName("Adding employees of legal entities")
    fun addingEmployeesOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            e {
                click(employees)
            }
        }
        with(AtmEmployeesPage(driver)) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
//            assert {
//                elementWithTextPresented(" Awaiting ")
//            }
            val href = GmailApi.getBodyATMRegistrationEmployee(emailEmp)
            assert {
                textEmail(
                    href,
                    "You were authorized by your organization to register on the Atomyze platform.Please follow the link to complete the procedure.The link will expire in 30 day"
                )
            }
        }
    }

    @TmsLink("ATMCH-1084")
    @Test
    @DisplayName("Deactivate employees with status Active of legal entities")
    fun deactivateEmployeesWithStatusActiveOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            deactiveEmployee(emailEmp)
            checkStatus(emailEmp, AtmEmployeesPage.Status.INACTIVE)
        }
    }

    @TmsLink("ATMCH-379")
    @Test
    @DisplayName("Block employees of legal entities")
    fun blockEmployeesOfLegalEntities() {
        val emailEmp = generateEmail()
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            e {
                click(employees)
            }
            assert { urlEndsWith("/profile/employees") }
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addAndRegistrationEmployee(emailEmp, AtmEmployeesPage.Roles.MANAGER)
        }
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            findEmployee(emailEmp)
            e {
                click(blockEmployee)
            }
            assert { elementPresented(blockPopUp) }
            e {
                click(blockButtonPopUp)
            }
            Assertions.assertTrue(isEmployeeBlocked(emailEmp), "Expected employee $emailEmp to be blocked")
        }
    }

    @TmsLink("ATMCH-2960")
    @Test
    @DisplayName("Reject employee of legal entities (on Platform status Rejected)")
    fun rejectEmployeeOfLegalEntities() {

        val user = Users.ATM_USER_INVITE_TO_COMPANY

        val newEmployee = Users.ATM_USER_DUMB_INVATED_TO_COMPANY

        //TODO: look for some pages before search by email
        prerequisite {
            checkAndRejectEmployeeAdding(user, newEmployee.email)
        }

        with(openPage<AtmEmployeesPage>(driver) { submit(user) }) {
            addEmployee(newEmployee.email, AtmEmployeesPage.Roles.ADMIN)
            checkStatusOfApproval(newEmployee.email, AtmEmployeesPage.Status.REVIEWING)
        }
    }

    @TmsLink("ATMCH-1163")
    @Test
    @DisplayName("Adding employees of legal entities. Validation")
    fun addingEmployeesOfLegalEntitiesValidation() {
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES) }) {
            e {
                click(addEmployees)
                sendKeys(email, "aft.uat.sdex+0aft_user@gmail.com")
            }
            assert {
                elementWithTextPresented(" Email already exists ")
            }
            e {
                sendKeys(email, "aft.uat.sdex+0aft_usergmail.com")
            }
            assert {
                elementWithTextPresented(" Invalid Email format ")
            }
        }
    }

    @TmsLink("ATMCH-2959")
    @Test
    @DisplayName(" Adding employees of legal entities (on Platform status Awaiting)")
    fun addingEmployeesOfLegalEntitiesOnPlatformStatusAwaiting() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            val since = addAndApproveEmployee(emailEmp, AtmEmployeesPage.Roles.ADMIN)
            val hrefRec = GmailApi.getBodyATMRegistrationEmployee(emailEmp, since)
            assert {
                textEmail(
                    hrefRec,
                    " You were authorized by your organization to register on the Atomyze platform.Please follow the link to complete the procedure.The link will expire in 30 days."
                )
            }

        }
    }

    @TmsLink("ATMCH-2961")
    @Test
    @DisplayName("Try create clone-request for employee (now request in status Reviewing)")
    fun tryCreateCloneRequestForEmployeeNowRequestInStatusReviewing() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, AtmEmployeesPage.Roles.ADMIN)
            e {
                click(addEmployees)
                sendKeys(email, emailEmp)
                click(adminRadio)
                click(add)
            }
            assert {
                elementWithTextPresentedIgnoreCase("The request to add employee with this e-mail is under review")
                elementIsDisabled("Add")
            }
        }
    }

}