package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import pages.atm.AtmEmployeesPage.Roles.MANAGER
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.generateEmail
import utils.helpers.openPage
import utils.helpers.to
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Employee approval")
class EmployeeApproval : BaseTest() {


    @TmsLink("ATMCH-2820")
    @Test
    @DisplayName("Employee approval by the Platform. Filter")
    fun employeeApprovalPlatformFilter() {

        val emailEmp = generateEmail()

        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }

        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val dateTo = LocalDate.now().toString()
            val dateFrom = LocalDate.now().minusDays(1).toString()

            applyFilterSearchByEmail(emailEmp)

            e {
                sendKeysAndEnter(updateDateFrom, dateFrom)
            }

            val rowWithStatusSubmitted = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")

            assert {
                elementPresented(rowWithStatusSubmitted)
            }

            e {
                deleteData(updateDateFrom)
                sendKeysAndReturn(updateDateTo, dateTo)
            }

            assert {
                elementPresented(rowWithStatusSubmitted)
            }

            e {
                sendKeysAndReturn(updateDateFrom, dateFrom)
                select(requestStatusSelect, "New requests")
            }
            //TODO: The test fails with rowWithStatusSubmitted, but OK with new element search. There isn't page reloading.
            assert {
                elementPresented(
                    inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                        ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                        ?: error("Row with email '$emailEmp' not found in table")
                )
            }

            e {
                approveUserWithEmail(emailEmp)
            }
            with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    applyFilterSearchByEmail(emailEmp)
                    select(requestStatusSelect, "History")
                }
            }
            val rowWithStatusApproved = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "approved"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")

            assert {
                elementPresented(rowWithStatusApproved)
            }

            e {
                select(requestStatusSelect, "All")
            }

            applyFilterSearchByEmail(emailEmp)
            assert {
                elementPresented(rowWithStatusApproved)
            }
        }
    }

    @TmsLink("ATMCH-2819")
    @Test
    @DisplayName("Employee approval by the Platform. Add employee")
    fun employeeApprovalPlatformAddingEmployee() {

        val emailEmp = generateEmail()
        val admin = Users.ATM_USER_FOR_EMPLOYEES_KYC1

        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }

        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val since = approveUserWithEmail(emailEmp)
            val row = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "approved"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            assert {
                elementPresented(row)
            }
            val href = GmailApi.getBodyATMApprovedEmployee(admin.email, since)
            assert {
                newTextEmail(
                    href,
                    "Atomyze account registration approved Congratulations," +
                            " your account has been approved Dear {{ full name }}," +
                            " Your uploaded documents have been reviewed and approved." +
                            " You now have full access to the Atomyze Platform." +
                            " We are very happy to welcome you and thank you for choosing Atomyze!"
                )
            }
        }
    }


    @TmsLink("ATMCH-2860")
    @Test
    @DisplayName("Employee approval by the Platform. Check statuses")
    fun employeeApprovalPlatformCheckStatuses() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }

        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            applyFilterSearchByEmail(emailEmp)
            val row = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            e {
                click(row)
            }
            assert {
                elementPresented(acceptInvite)
                elementPresented(rejectInvite)
                elementPresented(cancel)
            }
        }
    }

    @TmsLink("ATMCH-2845")
    @Test
    @DisplayName("Employee approval by the Platform. View notifications in Profile. Request is Rejected.")
    fun employeeApprovalRequestRejectedNotifications() {
        val emailEmp = generateEmail()
        val admin = Users.ATM_USER_FOR_EMPLOYEES_KYC1
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            val since = addAndRejectEmployee(emailEmp, MANAGER)
            val href = GmailApi.getBodyATMRejectedEmployee(admin.email, since)
            assert {
                textEmail(
                    href,
                    "    Please be informed that the request to add employee $emailEmp was Rejected."
                )
            }
        }
    }

    @TmsLink("ATMCH-2844")
    @Test
    @Story(Constants.POSITIVE)
    @DisplayName("Employee approval by the Platform.  User with role Admin notification.  Request is approved")
    fun employeeApprovalRequestApprovedNotifications() {
        val emailEmp = generateEmail()
        val admin = Users.ATM_USER_FOR_EMPLOYEES_KYC1
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            val since = addAndApproveEmployee(emailEmp, MANAGER)
            val href = GmailApi.getBodyATMApprovedEmployee(admin.email, since)
            assert {
                newTextEmail(
                    href,
                    "Atomyze account registration approved Congratulations," +
                            " your account has been approved Dear {{ full name }}," +
                            " Your uploaded documents have been reviewed and approved." +
                            " You now have full access to the Atomyze Platform." +
                            " We are very happy to welcome you and thank you for choosing Atomyze!"
                )
            }
        }
    }

    @Disabled("Нужен пользак платформ админ для получения письма, а там баг на аккесс райтс в админке")
    @TmsLink("ATMCH-2843")
    @Test
    @DisplayName("Employee approval by the Platform.  Platform Admin notification. Creating request ")
    fun employeeApprovalRequestCreatingNotifications() {
        val emailEmp = generateEmail()
        val user = Users.ATM_USER_FOR_EMPLOYEES_KYC1
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            val since = LocalDateTime.now(ZoneOffset.UTC)
            addAndApproveEmployee(emailEmp, MANAGER)
            val href = GmailApi.getBodyATMCreatedEmployee(user.email, since)
            assert {
                textEmail(
                    href,
                    "    Please be informed that the request to add employee $emailEmp of the company netrogatEmpAutotest was created."
                )
            }
        }
    }

    @TmsLink("ATMCH-2841")
    @Test
    @DisplayName("Employee approval by the Platform.  Cancellation scenario")
    fun employeeApprovalPlatformCancellation() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }

        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            applyFilterSearchByEmail(emailEmp)
            val row = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            e {
                click(row)
                click(cancel)
            }
            assert {
                elementPresented(row)
            }
        }
    }

    //TODO: is it duplicates 2181?
    @TmsLink("ATMCH-2842")
    @Test
    @DisplayName("Employee approval by the Platform. Adding a clone of rejected request")
    fun employeeApprovalPlatformAddingCloneRejectedRequest() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addAndRejectEmployee(emailEmp, MANAGER)
            addEmployee(emailEmp, MANAGER)
        }
        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {

            val rowSubmitted = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            assert {
                elementPresented(rowSubmitted)
            }

            e {
                click(rowSubmitted)
                click(cancel)
            }

            val rowRejected = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "rejected"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            assert {
                elementPresented(rowRejected)
            }

            val rowSubmittedAfterCancel = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            assert {
                elementPresented(rowSubmittedAfterCancel)
            }
        }
    }

    @TmsLink("ATMCH-2840")
    @Test
    @DisplayName("Employee approval by the Platform.  Reject a request")
    fun employeeApprovalPlatformAddingRejectRequest() {
        val emailEmp = generateEmail()
        val admin = Users.ATM_USER_FOR_EMPLOYEES_KYC1
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }
        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val since = rejectUserWithEmail(emailEmp)
            val rowRejected = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "rejected"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            assert {
                elementPresented(rowRejected)
            }
            val href = GmailApi.getBodyATMRejectedEmployee(admin.email, since)
            assert {
                textEmail(
                    href,
                    "Please be informed that the request to add employee $emailEmp was Rejected."
                )
            }
        }
    }

    @TmsLink("ATMCH-2825")
    @Test
    @DisplayName("Employee approval by the Platform. UI")
    fun employeeApprovalPlatformUI() {
        val emailEmp = generateEmail()
        with(openPage<AtmEmployeesPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {
            addEmployee(emailEmp, MANAGER)
        }
        with(openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }) {

            assert {
                elementContainingTextPresented("Request ID")
                elementContainingTextPresented("Requestor ID")
                elementContainingTextPresented("Requestor e-mail")
                elementContainingTextPresented("Company ID")
                elementContainingTextPresented("Company name")
                elementContainingTextPresented("Employee e-mail")
                elementContainingTextPresented("Employee role")
                elementContainingTextPresented("Created")
                elementContainingTextPresented("Status")
                elementContainingTextPresented("Updated")
                elementContainingTextPresented("Approver ID")
                elementContainingTextPresented("Approver e-mail")
            }

            applyFilterSearchByEmail(emailEmp)
            val row = inviteTable.find(findApprovalWithEmailAndStatus(emailEmp, "submitted"))
                ?.get(AtmAdminEmployeesPage.EMP_EMAIL)?.to<Button>("Employee $emailEmp")
                ?: error("Row with email '$emailEmp' not found in table")
            e {
                click(row)
            }
            assert {
                elementPresented(acceptInvite)
                elementPresented(rejectInvite)
                elementPresented(cancel)
            }
        }
    }

}