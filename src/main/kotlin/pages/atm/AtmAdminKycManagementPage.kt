package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminKycManagementPage.StatusType.*
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.attach
import utils.helpers.to


@PageUrl("/kyc-management")
open class AtmAdminKycManagementPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val EMAIL = "Email"
        const val FIRST_NAME = "First name"
        const val LAST_NAME = "Last name"
        const val KYC_STATUS = "KYC status"
        const val REQUEST_DATE = "Request Date"
        const val MODIFIED_DATE = "Modified Date"
        const val LOGGED_BY = "Logged by"
        const val NEW_STATUS = "New status"
        const val DATE = "Date"
        const val NOTE = "Note"
    }

    enum class StatusType {
        APPROVE, PASSED, BLOCK, DECLINE, CLEAR
    }

    @Name("User list")
    @FindBy(xpath = "//sdex-kyc-management//article")
    lateinit var userList: SdexTable

    @Name("Log list")
    @FindBy(xpath = ".//mat-dialog-container")
    lateinit var logList: SdexTable

    @Name("Email search")
    @FindBy(css = "input[formcontrolname='searchText']")
    lateinit var emailSearch: TextInput

    @Name("Apply filters button")
    @FindBy(xpath = "//button//span[contains(text(), 'Apply')]")
    lateinit var applyFiltersButton: Button

    //Review kyc application block sdex-kyc-info-dialog

    @Name("Mark KYC as Passed button")
    @FindBy(xpath = ".//button//span[contains(text(), 'Mark KYC as passed')]")
    lateinit var markKycAsPassedButton: Button

    @Name("Clear KYC status button")
    @FindBy(xpath = "//sdex-kyc-info-dialog//button//span[contains(text(), 'Clear KYC status')]")
    lateinit var clearKycStatusButton: Button

    @Name("Block KYC button")
    @FindBy(xpath = "//sdex-kyc-info-dialog//button//span[contains(text(), 'Block KYC')]")
    lateinit var blockKycButton: Button

    @Name("Approve KYC button")
    @FindBy(xpath = "//sdex-kyc-info-dialog//button//span[contains(text(), 'Approve KYC')]")
    lateinit var approveKycButton: Button

    @Name("Decline KYC button")
    @FindBy(xpath = "//sdex-kyc-info-dialog//button//span[contains(text(), 'Decline KYC')]")
    lateinit var declineKycButton: Button

    @Name("Edit button")
    @FindBy(xpath = "//button//mat-icon[contains(text(), 'edit')]")
    lateinit var editButton: Button

    @Name("First Name")
//    @FindBy(xpath = "//label//span[contains(text(), 'First Name')]/ancestor::div[1]//input")
    @FindBy(xpath = "//input[@formcontrolname='firstname']")
    lateinit var firstNameInput: TextInput

    @Name("Last Name")
//    @FindBy(xpath = "//label//span[contains(text(), 'Last Name')]/ancestor::div[1]//input")
    @FindBy(xpath = "//input[@formcontrolname='lastname']")
    lateinit var lastNameInput: TextInput

    @Name("Save")
    @FindBy(xpath = "//button//span[contains(text(), 'SAVE')]")
    lateinit var saveButton: Button

    //Review kyc application - confirmation dialog

    @Name("Note")
    @FindBy(xpath = "(//mat-form-field//input[@formcontrolname='note'])[1]")
    lateinit var noteTextInput: TextInput

    @Name("Note from set status")
    @FindBy(xpath = "(//mat-form-field//input[@formcontrolname='note'])[2]")
    lateinit var noteTextFromSetStatusInput: TextInput

    @Name("Add")
    @FindBy(xpath = "//button//span[contains(text(), 'ADD')]")
    lateinit var addButton: Button

    @Name("Confirm button")
    @FindBy(xpath = "//sdex-status-confirmation-dialog//button//span[contains(text(), 'CONFIRM')]")
    lateinit var confirmDialogButton: Button

    @Name("Cancel button")
    @FindBy(xpath = "//sdex-status-confirmation-dialog//button//span[contains(text(), 'CANCEL')]")
    lateinit var cancelDialogButton: Button

    @Name("Confirmation dialog")
    @FindBy(xpath = ".//sdex-status-confirmation-dialog")
    lateinit var confirmationDialog: Button

    @Step("User opens review KYC application for user {email}")
    fun openKycReviewApplicationByEmail(email: String) {
        e {
            sendKeys(emailSearch, email)
            click(applyFiltersButton)
        }
        val emailElement = userList.find {
            it[EMAIL]?.text?.contains(email) ?: false
        }?.get(EMAIL)?.to<Button>(email) ?: error("No user with email $email found in table")
        e {
            click(emailElement)
        }
    }

    @Step("User gets KYC status for user {email}")
    fun getKycStatusForUserByEmail(email: String): String {
        e {
            sendKeys(emailSearch, email)
            click(applyFiltersButton)
        }
        val status = userList.find {
            it[EMAIL]?.text?.contains(email) ?: false
        }?.get(KYC_STATUS)?.text ?: error("No user with email $email found in table")
        attach("status", status)
        return status
    }

    @Step("User opens review KYC application for user {email} and set name")
    fun openKycReviewApplicationByEmailAndSetName(email: String, firstName: String, lastName: String) {
        e {
            sendKeys(emailSearch, email)
            click(applyFiltersButton)
        }
        val emailElement = userList.find {
            it[EMAIL]?.text?.contains(email) ?: false
        }?.get(EMAIL)?.to<Button>(email) ?: error("No user with email $email found in table")
        e {
            click(emailElement)
            click(editButton)
            sendKeys(firstNameInput, firstName)
            sendKeys(lastNameInput, lastName)
            click(saveButton)
        }
    }

    @Step("User opens review KYC application for user {email} and set name")
    fun checkName(email: String, firstName: String, lastName: String) {
        e {
            sendKeys(emailSearch, email)
            click(applyFiltersButton)
        }
        val row = userList.find {
            it[EMAIL]?.text == email
        } ?: error("Can't find row with email '$email'")
        val firstNameRow = row[FIRST_NAME]?.text
        val lastNameRow = row[LAST_NAME]?.text
        assertThat("Row found with email '$email'", row, notNullValue())
        assertThat("Row found with '$firstNameRow'", firstNameRow, `is`(firstName))
        assertThat("Row found with '$lastNameRow'", lastNameRow, `is`(lastName))
    }

    @Step("User opens review KYC application for user {email} and set name")
    fun openKycApplicationByEmailAndSetNewStatus(email: String, statusType: StatusType, noteText: String) {
        e {
            sendKeys(emailSearch, email)
            click(applyFiltersButton)
        }
        val emailElement = userList.find {
            it[EMAIL]?.text?.contains(email) ?: false
        }?.get(EMAIL)?.to<Button>(email) ?: error("No user with email $email found in table")
        e {
            click(emailElement)
            when (statusType) {
                PASSED -> click(markKycAsPassedButton)
                APPROVE -> click(approveKycButton)
                BLOCK -> click(blockKycButton)
                CLEAR -> click(clearKycStatusButton)
                DECLINE -> click(declineKycButton)
            }
            sendKeys(noteTextFromSetStatusInput, noteText)

            val confirm = wait {
                untilPresented<WebElement>(By.xpath(".//sdex-status-confirmation-dialog//button//span[contains(text(), 'CONFIRM')]"))
            }.to<Button>("Confirm")

            click(confirm)

            wait {
                until("confirmation dialog is gone", 15) {
                    check {
                        isElementGone(confirmationDialog)
                    }
                }
            }
        }
    }

    @Step("User opens review KYC and check note")
    fun checkNote(email: String, noteText: String) {
        val row = logList.find {
            it[NOTE]?.text == noteText
        } ?: error("Can't find row with email '$email'")

        assertThat("Row found with email '$email'", row, notNullValue())
    }

    @Step("Admin opens review KYC and check note")
    fun checkDataInLogTable(loggedBy: String, status: String, note: String) {
        val row = logList.find {
            it[NEW_STATUS]?.text == status
        } ?: error("Can't find row with email '$status'")
        val statusRow = row[NEW_STATUS]?.text
        val noteRow = row[NOTE]?.text
        val dateRow = row[DATE]?.text
        val loggedByRow = row[LOGGED_BY]?.text
        assertThat("Row found with email '$loggedBy'", statusRow, `is`(status))
        assertThat("Row found with email '$loggedBy'", dateRow, notNullValue())
        assertThat("Row found with email '$loggedBy'", noteRow, `is`(note))
        assertThat("Row found with email '$loggedBy'", loggedByRow, `is`(loggedBy))
    }
}