package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.Assert.assertTrue
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.employees.EmployeesItem
import pages.htmlelements.blocks.atm.p2p.P2PItem
import pages.htmlelements.elements.AtmRadio
import pages.htmlelements.elements.AtmTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Constants
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to
import java.time.LocalDateTime
import java.time.ZoneOffset

@PageUrl("/profile/employees")
class AtmEmployeesPage(driver: WebDriver) : AtmPage(driver) {


    @Name("Add employees")
    @FindBy(xpath = "//span[contains(text(),'ADD EMPLOYEE')]")
    lateinit var addEmployees: Button

    @Name("Email")
    @FindBy(xpath = "//nz-input-group//input")
    lateinit var email: TextInput

    @Name("Admin radio")
    @FindBy(xpath = "//nz-radio-group//span[contains(text(),'Admin')]/ancestor::label")
    lateinit var adminRadio: AtmRadio

    @Name("Manager radio")
    @FindBy(xpath = "//nz-radio-group//span[contains(text(),'Manager')]/ancestor::label")
    lateinit var managerRadio: AtmRadio

    @Name("Add")
    @FindBy(xpath = "//span[contains(text(),' Add ')]/ancestor::button")
    lateinit var add: Button

    @Name("Delete employee")
    @FindBy(xpath = "//button//span[contains(text(), ' Delete ')]")
    lateinit var deleteEmployee: Button

    @Name("Delete button from pop-up")
    @FindBy(xpath = "//nz-modal-container//button//span[contains(text(), ' Delete ')]")
    lateinit var deleteButtonPopUp: Button

    @Name("Add employee dialog")
    @FindBy(xpath = "//atm-add-employee-dialog")
    lateinit var addEmployeeDialog: Button

    @Name("Change")
    @FindBy(xpath = "//span[contains(text(),'Change')]")
    lateinit var change: Button

    @Name("Ticker Manager")
    @FindBy(xpath = "//atm-employee-item[contains(@class,'selected')]//div[contains(text(),'Manager')]")
    lateinit var tickerManager: Button

    @Name("Ticker Admin")
    @FindBy(xpath = "//atm-employee-item[contains(@class,'selected')]//div[contains(text(),'Admin')]")
    lateinit var tickerAdmin: Button

    @Name("Block employee")
    @FindBy(xpath = "//atm-employee-item//button//span[contains(text(), ' Block ')]")
    lateinit var blockEmployee: Button

    @Name("Block button from pop-up")
    @FindBy(xpath = "//nz-modal-container//button//span[contains(text(), ' Block ')]")
    lateinit var blockButtonPopUp: Button

    @Name("Deactivate employee")
    @FindBy(xpath = "//atm-employee-item//button//span[contains(text(), ' Deactivate ')]")
    lateinit var deactivateEmployee: Button

    @Name("Deactive button from pop-up")
    @FindBy(xpath = "//nz-modal-container//button//span[contains(text(), ' Deactivate ')]")
    lateinit var deactivateButtonPopUp: Button

    @Name("Activate employee")
    @FindBy(xpath = "//atm-employee-item//button//span[contains(text(), ' Activate ')]")
    lateinit var activateEmployee: Button

    @Name("Active button from pop-up")
    @FindBy(xpath = "//nz-modal-container//button//span[contains(text(), ' Confirm ')]")
    lateinit var activeButtonPopUp: Button

    @Name("Active")
    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'ACTIVATE EMPLOYEE ?')]")
    lateinit var activePopUp: Button

    @Name("Deactive")
    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'DEACTIVATE EMPLOYEE ?')]")
    lateinit var deactivePopUp: Button

    @Name("Block")
    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'BLOCK EMPLOYEE ?')]")
    lateinit var blockPopUp: Button

    @Name("Delete")
    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'DELETE EMPLOYEE ?')]")
    lateinit var deleteEmployeeDialog: Button

    @Name("Active employee")
    @FindBy(xpath = "//span[@class='status'][contains(text(),'Active')]")
    lateinit var activeEmployee: Button

    @Name("Employees")
    @FindBy(css = "atm-employees")
    lateinit var employees: AtmTable<EmployeesItem>

    enum class Roles {
        ADMIN,
        MANAGER
    }

    enum class Status {
        ACTIVE,
        INACTIVE,
        REJECTED,
        REVIEWING
    }


    @Step("Delete employee {email}")
    fun deleteByEmployeeEmail(email: String) {
        findEmployee(email)
        e {
            click(deleteEmployee)
            click(deleteButtonPopUp)
        }
        wait {
            until("dialog for delete users is gone", 20) {
                check {
                    isElementGone(deleteEmployeeDialog)
                }
            }
        }
    }

    @Step("Add employee {emailEmp}")
    fun addEmployee(emailEmp: String, role: Roles) {
        val radioRole = when (role) {
            Roles.ADMIN -> adminRadio
            Roles.MANAGER -> managerRadio
        }

        e {
            click(addEmployees)
            sendKeys(email, emailEmp)
            click(radioRole)
            click(add)
        }
        //TODO:  remove noncritical after bug is fixed
        nonCriticalWait {
            until("dialog for add users is gone", 15) {
                check {
                    isElementGone(addEmployeeDialog)
                }
            }
        }
        driver.navigate().refresh()
    }

    @Step("Add and approve employee {emailEmp}")
    fun addAndApproveEmployee(emailEmp: String, role: Roles): LocalDateTime {
        addEmployee(emailEmp, role)
        val since = openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }.approveUserWithEmail(emailEmp)
        openPage<AtmEmployeesPage>(driver)
        return since
    }

    @Step("Add and reject employee {emailEmp}")
    fun addAndRejectEmployee(emailEmp: String, role: Roles): LocalDateTime {
        addEmployee(emailEmp, role)
        val since = openPage<AtmAdminEmployeesPage>(driver) { submit(Users.ATM_ADMIN) }.rejectUserWithEmail(emailEmp)
        openPage<AtmEmployeesPage>(driver)
        return since
    }

    @Step("Add, approve and register employee {emailEmp}")
    fun addAndRegistrationEmployee(emailEmp: String, role: Roles): AtmProfilePage {
        val since = LocalDateTime.now(ZoneOffset.UTC)
        addAndApproveEmployee(emailEmp, role)

        val href = GmailApi.getHrefATMRegistrationEmployee(emailEmp, since)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            e {
                sendKeys(atmUserPasswordForEmployeeRegistration, Constants.DEFAULT_PASSWORD)
                sendKeys(atmUserConfirmPasswordForEmployeeRegistration, Constants.DEFAULT_PASSWORD)
                click(confirm)
            }
            nonCriticalWait {
                untilInvisibility(atmUserConfirmPassword)
            }
        }
        openPage<AtmProfilePage>(driver).logout()
        return AtmProfilePage(driver)
    }

    @Step("Block employee {emailEmp}")
    fun blockEmployee(emailEmp: String): LocalDateTime {
        wait {
            until("list of employee is displayed", 15) {
                check {
                    isElementPresented(By.xpath("//div//h3[text()=' Employees ']"))
                }
            }
        }
        findEmployee(emailEmp)
        e {
            click(blockEmployee)
            click(blockButtonPopUp)
        }
        val since: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        wait {
            until("block user pop up is gone", 15) {
                check {
                    isElementGone(blockPopUp)
                }
            }
        }
        return since
    }

    @Step("Activate employee {emailEmp}")
    fun activeEmployee(emailEmp: String) {
        wait {
            until("block user pop up is gone", 15) {
                check {
                    isElementGone(blockPopUp)
                }
            }
        }
        findEmployee(emailEmp)
        e {
            click(activateEmployee)
            click(activeButtonPopUp)
        }
        wait {
            until("activate user pop up is gone", 15) {
                check {
                    isElementGone(activePopUp)
                }
            }
        }
    }

    @Step("Deactivate employee {emailEmp}")
    fun deactiveEmployee(emailEmp: String): LocalDateTime {
        wait {
            until("list of employee is displayed", 15) {
                check {
                    isElementPresented(By.xpath("//div//h3[text()=' Employees ']"))
                }
            }
        }
        findEmployee(emailEmp)
        e {
            click(deactivateEmployee)
            click(deactivateButtonPopUp)
        }
        val since: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        wait {
            until("deactive user pop up is gone", 15) {
                check {
                    isElementGone(deactivePopUp)
                }
            }
        }
        return since
    }

    @Step("Re-add employee {emailEmp}")
    fun reAddingByEmployeeEmail(email: String, role: String) {
        val roleChanged = wait {
            untilPresented<WebElement>(By.xpath("//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//div[@class='employee-item__caption']"))
        }.to<Button>("Employee '$email' role").text

        MatcherAssert.assertThat(roleChanged, Matchers.`is`(role))
    }

    fun employeeEmailDeleted(email: String) {
        val isEmailPresented = check {
            isElementPresented(By.xpath("//atm-property-value//div[contains(text(), '$email')]"))
        }
        assertTrue("Employee with email '$email' is found", isEmailPresented)
    }


    @Step("Find employee {email}")
    fun findEmployee(email: String) {
        val emp = employees.find {
            it.useremail == email
        } ?: error("Can't find employee with email $email")
//        val emp = wait {
//            untilPresented<WebElement>(By.xpath("//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]"))
//        }.to<Button>("Employee '$email'")
        e {
            click(emp)
        }
    }

    fun findEmployee(email: String, role: String) {
        val emp = wait {
            untilPresented<WebElement>(By.xpath("//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]"))
        }.to<Button>("Employee '$email'")
        e {
            click(emp)
        }
        wait {
            untilPresented<WebElement>(By.xpath("//atm-employee-item[@class='employee-item ng-star-inserted employee-item--selected']//div[@class='employee-item__caption'][contains(text(),'$role')]"))
        }.to<Button>("Employee '$email'")
    }

    fun checkStatus(email: String, status: Status) {
        val empStatus = wait {
            untilPresented<WebElement>(By.xpath("//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//span[@class='status']|//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//span[@class='status status--inactive']"))
        }.to<Button>("Employee '$email'").text

        MatcherAssert.assertThat(empStatus, Matchers.`is`(status.name))
    }

    @Step("Check is employee {emailEmp} blocked")
    fun isEmployeeBlocked(email: String): Boolean {
        return check {
            isElementPresented(By.xpath("//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//span[@class='status']|//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//span[@class='status status--blocked']"))
        }
    }

    fun checkStatusOfApproval(email: String, status: Status) {
        val appStatus = wait {
            untilPresented<WebElement>(By.xpath("(//atm-property-value//div[contains(text(), '${email.toLowerCase()}')]//ancestor::atm-employee-item//span[@class='status status--awaiting'])[1]"))
        }.to<TextBlock>("Status of '$email'")

        MatcherAssert.assertThat(appStatus.text, equalToIgnoringCase(status.name))
    }

}