package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput


@PageUrl("/user-management")
open class AtmAdminUserManagementPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val EMAIL = "eMail"
        const val KEY = "Key"
        const val KYC = "Kyc"
        const val FIRST_NAME = "First Name"
        const val LAST_NAME = "Last Name"
    }

    @Name("Request e-Mail verification")
    @FindBy(xpath = "//span[contains(text(), 'Request e-Mail verification')]")
    lateinit var requestEmailButton: Button

    @Name("Verify KYC")
    @FindBy(xpath = "//span[contains(text(), 'Verify KYC')]")
    lateinit var verifyKYC: Button

    @Name("Verify KYB")
    @FindBy(xpath = "//span[contains(text(), 'Verify KYB')]")
    lateinit var verifyKYB: Button

    @Name("Edit")
    @FindBy(xpath = "//span[contains(text(), 'EDIT')]")
    lateinit var editButton: Button

    @Name("Search")
    @FindBy(xpath = "//label//mat-label[contains(text(), 'Search')]/ancestor::div[3]//input")
    lateinit var search: TextInput

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

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(), 'CANCEL')]")
    lateinit var cancelButton: Button

    @Name("First row")
    @FindBy(xpath = "//table//tbody//tr")
    lateinit var firstRow: Button

    @Name("Email is confirmed")
    @FindBy(xpath = "//mat-dialog-content//span[contains(text(), 'Email  is confirmed')]/ancestor::mat-checkbox")
    lateinit var emailIsConfirmedCheckbox: CheckBox

    @Name("KYC checkbox")
    @FindBy(xpath = "//mat-dialog-content//span[contains(text(), 'KYC')]/ancestor::mat-checkbox")
    lateinit var kycCheckbox: CheckBox

    @Name("User list")
    @FindBy(xpath = "//sdex-user-management//article")
    lateinit var userList: SdexTable

    @Action("check first and last name")
    @Step("User checks first name and last name in user list")
    fun checkName(email: String, firstNameInput: String, lastNameInput: String): String {
        e {
            clear(search)
            sendKeys(search, email)
        }
        val row = userList.find {
            it[EMAIL]?.text == email
        } ?: error("Can't find row with email '$email'")
        val firstName = row[FIRST_NAME]?.text
        val lastName = row[LAST_NAME]?.text
        assertThat("Row found with email '$email'", row, Matchers.notNullValue())
        assertThat("Row found with '$firstNameInput'", firstName, Matchers.`is`(firstNameInput))
        assertThat("Row found with '$lastNameInput'", lastName, Matchers.`is`(lastNameInput))
        return row[EMAIL]?.text ?: ""
    }

    @Action("check first and last name is not save")
    @Step("User checks first name {firstNameInput} and last name {lastNameInput} in user list not save")
    fun checkNotSaveName(email: String): String {
        e {
            clear(search)
            sendKeys(search, email.substring(0, 15))
        }
        val row = userList.find {
            it[EMAIL]?.text == email
        } ?: error("Can't find row with email '$email'")
        val firstName = row[FIRST_NAME]?.text
        val lastName = row[LAST_NAME]?.text
        assertThat("Row found with email '$email'", row, Matchers.notNullValue())
        assertThat("Row found with '$firstNameInput'", firstName, Matchers.`is`(""))
        assertThat("Row found with '$lastNameInput'", lastName, Matchers.`is`(""))
        return row[EMAIL]?.text ?: ""
    }

    @Action("check first and last name")
    @Step("User checks first name and last name in user list")
    fun setName(email: String, firstNameInput: String, lastNameInput: String): String {
        e {
            clear(search)
            sendKeys(search, email.substring(0, 15))
        }
        val row = userList.find {
            it[EMAIL]?.text == email
        } ?: error("Can't find row with email '$email'")
        val firstName = row[FIRST_NAME]?.text
        val lastName = row[LAST_NAME]?.text
        assertThat("Row found with email '$email'", row, Matchers.notNullValue())
        assertThat("Row found with '$firstNameInput'", firstName, Matchers.`is`(firstNameInput))
        assertThat("Row found with '$lastNameInput'", lastName, Matchers.`is`(lastNameInput))
        return row[EMAIL]?.text ?: ""
    }

}