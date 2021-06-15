package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Employees assigning")
class EmployeesAssigning : BaseTest() {

    @TmsLink("ATMCH-1394")
    @Test
    @DisplayName("Assignment to the manager the role of Controller in a OTF wallet-key with single authorization.")
    fun assignmentToTheManagerTheRoleOfControllerInOTFWalletKeyWithSingleAuthorization() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val otfWallet = user.otfWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(otfWallet.name)
            e {
                click(assign)
            }
            try {
                step("Pre test apply") {
                    findEmployeeAndSetControllerCheckBox(employee.email, false, user)
                }
            } catch (e: Exception) {
                print("Card with '${employee.email}' not found")
            }
            findEmployeeAndSetControllerCheckBox(employee.email, true, user)

//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(employee.email, true)
            try {
                step("Post test cleaning") {
                    findEmployeeAndSetControllerCheckBox(employee.email, false, user)
                }
            } catch (e: Exception) {
                print("Card with '${employee.email}' not found")
                false
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_EMPLOYEE_ADMIN_ROLE)
    @TmsLink("ATMCH-1382")
    @Test
    @DisplayName("Removing to the Admin the of Controller role in a MAIN wallet-key with single authorization.")
    fun removingToTheAdminTheOfControllerRoleInMAINWalletKeyWithSingleAuthorization() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = user.mainWallet

        prerequisite {
            setControllerStateForWallet(mainWallet.name, user, employee, true)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            val emp = assignEmployee.find {
                it.emailName == employee.email
            } ?: error("Can't find card with '$employee.email'")
            emp.setCheckBox(false)
            e {
                click(cancelForApply)
            }
            checkStateCheckBox(employee.email, true)
            emp.setCheckBox(false)
            e {
                click(backToWallet)
                click(assign)
            }
            checkStateCheckBox(employee.email, true)
            findEmployeeAndSetControllerCheckBox(employee.email, true, user)
//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(employee.email, true)
        }

    }

    @TmsLink("ATMCH-1381")
    @Test
    @DisplayName("Assignment to the Admin the role of Controller in a MAIN wallet-key with single authorization.")
    fun assignmentToTheAdminTheRoleOfControllerInMAINWalletKeyWithSingleAuthorization() {
        val admin = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = admin.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(admin) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            try {
                step("Pre test apply") {
                    findEmployeeAndSetControllerCheckBox(admin.email, true, admin)
                }
            } catch (e: Exception) {
                print("Card with '${admin.email}' not found")
            }
            val emp = assignEmployee.find {
                it.emailName == admin.email
            } ?: error("Can't find card with '$employee.email'")
            emp.setCheckBox(false)
            e {
                click(cancelForApply)
            }
            checkStateCheckBox(admin.email, true)
            emp.setCheckBox(false)
            e {
                click(backToWallet)
                click(assign)
            }
            checkStateCheckBox(admin.email, true)
            findEmployeeAndSetControllerCheckBox(admin.email, false, admin)
//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(admin.email, false)
            try {
                step("Post test cleaning") {
                    findEmployeeAndSetControllerCheckBox(employee.email, true, admin)
                }
            } catch (e: Exception) {
                print("Card with '${employee.email}' not found")
                false
            }
        }

    }

    @TmsLink("ATMCH-1380")
    @Test
    @DisplayName("Removing to the manager the of Controller role in a MAIN wallet-key with single authorization.")
    fun removingToTheManagerTheOfControllerRoleInMAINWalletKeyWithSingleAuthorization() {
        val admin = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = admin.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(admin) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            try {
                step("Pre test apply") {
                    findEmployeeAndSetControllerCheckBox(employee.email, true, admin)
                }
            } catch (e: Exception) {
                print("Card with '${employee.email}' not found")
            }
            val emp = assignEmployee.find {
                it.emailName == employee.email
            } ?: error("Can't find card with '$employee.email'")
            emp.setCheckBox(false)
            e {
                click(cancelForApply)
            }
            checkStateCheckBox(employee.email, true)
            emp.setCheckBox(false)
            e {
                click(backToWallet)
                click(assign)
            }
            checkStateCheckBox(employee.email, true)
            findEmployeeAndSetControllerCheckBox(employee.email, false, admin)
//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(employee.email, false)
        }

    }

    @TmsLink("ATMCH-1346")
    @Test
    @DisplayName("Assignment to the manager the role of Controller in a MAIN wallet-key with single authorization.")
    fun assignmentToTheManagerTheRoleOfControllerInMAINWalletKeyWithSingleAuthorization() {
        val admin = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = admin.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(admin) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            try {
                step("Pre test apply") {
                    findEmployeeAndSetControllerCheckBox(employee.email, true, admin)
                }
            } catch (e: Exception) {
                print("Card with '${employee.email}' not found")
            }
            checkStateCheckBox(employee.email, true)
            val emp = assignEmployee.find {
                it.emailName == employee.email
            } ?: error("Can't find card with '$employee.email'")
            emp.setCheckBox(false)
            e {
                click(cancelForApply)
            }
            checkStateCheckBox(employee.email, true)
            e {
                click(backToWallet)
                click(assign)
            }
            checkStateCheckBox(employee.email, true)
            findEmployeeAndSetControllerCheckBox(employee.email, false, admin)
//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(employee.email, false)
        }
    }

    @ResourceLock(Constants.ROLE_USER_EMPLOYEE_ADMIN_ROLE)
    @TmsLink("ATMCH-1403")
    @Test
    @DisplayName("Changing the role of employees of a legal entity for Main Wallet single authorization with 2FA enabled.")
    fun changingTheRoleOfEmployeesOfLegalEntityForMainWalletSingleAuthorizationWith2FAEnabled() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = user.mainWallet
        println(mainWallet.name)

        prerequisite {
            setControllerStateForWallet(mainWallet.name, user, employee, false)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            assert {
                elementWithTextPresented(" YOUR EMPLOYEE ROLE ")
                elementWithTextPresented(" YOUR WALLET ROLE ")
                elementWithTextPresented(" WALLET ID ")
                elementWithTextPresented(" WALLET TYPE ")
                elementWithTextPresented(" SIGNATURE TYPE ")
                elementWithTextPresented(" STORAGE ")
                elementWithTextPresented(" BALANCE ")
                elementPresented(backToWallet)
                elementPresented(controllerCheckbox)
            }
            findEmployeeAndSetControllerCheckBox(employee.email, true, user)
//            assert {
//                elementWithTextPresented(" Your updates have been applied ")
//            }
            checkStateCheckBox(employee.email, true)
        }

    }

    @ResourceLock(Constants.ROLE_USER_EMPLOYEE_ADMIN_ROLE)
    @TmsLink("ATMCH-1405")
    @Test
    @DisplayName("Changing the wallet role of employees of a legal entity with 2FA enabled. Validation.")
    fun changingTheWalletRoleOfEmployeesOfLegalEntityWith2FAEnabledValidation() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            e {
                click(assign)
            }
            assert {
                elementWithTextPresented(" YOUR EMPLOYEE ROLE ")
                elementWithTextPresented(" YOUR WALLET ROLE ")
                elementWithTextPresented(" WALLET ID ")
                elementWithTextPresented(" WALLET TYPE ")
                elementWithTextPresented(" SIGNATURE TYPE ")
                elementWithTextPresented(" STORAGE ")
                elementWithTextPresented(" BALANCE ")
                elementPresented(backToWallet)
                elementPresented(controllerCheckbox)
            }
            val newState = reverseCheckboxStatusForEmployee(employee.email, user)
            e {
                val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                sendKeys(atmOtpConfirmationInput, code)
                click(atmOtpConfirmationConfirmButton)
            }
            assert {
                elementContainingTextPresented("Wrong code")
            }
            e {
                click(atmOtpCancel)
            }
            checkStateCheckBox(employee.email, !newState)
        }

    }

    @TmsLink("ATMCH-1402")
    @Test
    @DisplayName("The “Assign” button in the wallet is not available for the role Manager.")
    fun assignButtonInTheWalletIsNotAvailableForTheRoleManager() {
        val user = Users.ATM_USER_EMPLOYEE_ADMIN_ROLE
        val employee = Users.ATM_USER_EMPLOYEE_MANAGER_ROLE
        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        with(openPage<AtmWalletPage>(driver) { submit(employee) }) {
            chooseWallet(mainWallet.name)
            assert {
                elementNotPresented(assign)
            }

            openPage<AtmWalletPage>(driver)

            chooseWallet(otfWallet.name)
            assert {
                elementNotPresented(assign)
            }
        }

    }


}