package pages.atm

import io.qameta.allure.Step
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

@PageUrl("/employees")
class AtmAdminEmployeesPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val EMP_EMAIL = "Employee e-mail"
        const val STATUS = "Status"
    }

    @Name("Invite tab")
    @FindBy(xpath = "//span[contains(text(), 'Invites')]")
    lateinit var inviteTab: Button

    @Name("Employees approval tab")
    @FindBy(xpath = "//span[contains(text(), 'Employees approval')]")
    lateinit var employeesApprovalTab: Button

    @Name("Payments tab")
    @FindBy(xpath = "//span[contains(text(), 'Payments')]")
    lateinit var paymentsTab: Button

    @Name("Fiat withdraw tab")
    @FindBy(xpath = "//span[contains(text(), 'Fiat withdraw')]")
    lateinit var fiatWithdrawTab: Button

    @Name("Companies tab")
    @FindBy(xpath = "//span[contains(text(), 'Companies')]")
    lateinit var companiesTab: Button

    @Name("Bank details tab")
    @FindBy(xpath = "//span[contains(text(), 'Bank details')]")
    lateinit var bankDetailsTab: Button

    @Name("Tokens tab")
    @FindBy(xpath = "//span[contains(text(), 'Tokens')]")
    lateinit var tokensTab: Button

    @Name("Register of issuers tab")
    @FindBy(xpath = "//span[contains(text(), 'Register of issuers')]")
    lateinit var registerOfIssuersTab: Button

    @Name("Employee table")
    @FindBy(css = "sdex-employees")
    lateinit var inviteTable: SdexTable

    @Name("Reject invite")
    @FindBy(xpath = "//span[text()=' REJECT ']/ancestor::button")
    lateinit var rejectInvite: TextInput

    @Name("Accept invite")
    @FindBy(xpath = "//span[text()=' APPROVE ']/ancestor::button")
    lateinit var acceptInvite: TextInput

    @Name("Cancel dialog with invite request")
    @FindBy(xpath = "//span[text()=' CANCEL ']/ancestor::button")
    lateinit var cancel: TextInput

    @Name("Search by email input")
    @FindBy(css = "input[formcontrolname='searchString']")
    lateinit var filterSearchByEmailInput: TextInput

    @Name("Update Date From")
    @FindBy(xpath = "//input[@formcontrolname='updateDateFrom']")
    lateinit var updateDateFrom: TextInput

    @Name("Update Date To")
    @FindBy(xpath = "//input[@formcontrolname='updateDateTo']")
    lateinit var updateDateTo: TextInput

    @Name("Request status dropdown")
    @FindBy(xpath = "//mat-select[@formcontrolname='type']")
    lateinit var requestStatusSelect: AtmSelect

    @Step("Apply filter by email: {email}")
    fun applyFilterSearchByEmail(email: String) {
        e {
            sendKeysAndEnter(filterSearchByEmailInput, email)
        }
    }

    @Step("Approve user {email}")
    fun approveUserWithEmail(email: String): LocalDateTime {
        applyFilterSearchByEmail(email)
        val row = inviteTable.find(findApprovalWithEmailAndStatus(email, "submitted"))
            ?.get(EMP_EMAIL)?.to<Button>("Employee $email") ?: error("Row with email '$email' not found in table")

        e {
            click(row)
            click(acceptInvite)
        }
        val since = LocalDateTime.now(ZoneOffset.UTC)
        nonCriticalWait(10L) {
            untilInvisibility(acceptInvite)
        }
        return since
    }

    @Step("Reject user {email}")
    fun rejectUserWithEmail(email: String): LocalDateTime {
        applyFilterSearchByEmail(email)
        val row = inviteTable.find(findApprovalWithEmailAndStatus(email, "submitted"))
            ?.get(EMP_EMAIL)?.to<Button>("Employee $email") ?: error("Row with email '$email' not found in table")

        e {
            click(row)
            click(rejectInvite)
        }
        val since = LocalDateTime.now(ZoneOffset.UTC)
        nonCriticalWait(10L) {
            untilInvisibility(rejectInvite)
        }
        return since
    }

    @Step("Find approval with {email} and {status}")
    fun findApprovalWithEmailAndStatus(email: String, status: String): (Map<String, WebElement>) -> Boolean {
        //TODO: is it possible without previously filtering?
        applyFilterSearchByEmail(email)
        return {
            it[EMP_EMAIL]?.text?.toLowerCase()?.contains(email.toLowerCase()) ?: false
                    && it[STATUS]?.text.equals(status, ignoreCase = true)
        }
    }

}