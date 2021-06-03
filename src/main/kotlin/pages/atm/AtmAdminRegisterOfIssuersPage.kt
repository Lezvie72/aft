package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button

@PageUrl("/issuers")
class AtmAdminRegisterOfIssuersPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val ID = "ID"
        const val NAME = "Name"
        const val DESCRIPTION = "Description"
        const val FULL_NAME = "Full name"
        const val SHORT_NAME = "Short name"
        const val TOKENS = "Tokens"
        const val ADDITIONAL_INFO = "Additional info"
    }

    @Name("Register of issuers table")
    @FindBy(xpath = "//sdex-grid")
    lateinit var registerOfIssuersTable: SdexTable

    @Name("First row at table")
    @FindBy(xpath = "//sdex-grid//table//td")
    lateinit var firstRowInGrid: Button

    @Name("Search")
    @FindBy(xpath = "//input[@formcontrolname='searchString']/ancestor::mat-form-field")
    lateinit var search: AtmInput

    @Name("Find in page button")
    @FindBy(xpath = "//mat-icon[contains(text(),'find_in_page')]")
    lateinit var findInPage: Button

    @Name("Issuers select")
    @FindBy(xpath = "//mat-select[@formcontrolname='issuers']")
    lateinit var issuers: AtmSelect

    @Name("Tokens select")
    @FindBy(xpath = "//mat-select[@formcontrolname='tokens']")
    lateinit var tokens: AtmSelect

    @Name("Update date from select")
    @FindBy(xpath = "//input[@formcontrolname='updateDateFrom']/ancestor::mat-form-field")
    lateinit var updateDateForm: AtmSelect

    @Name("Update date to select")
    @FindBy(xpath = "//input[@formcontrolname='updateDateTo']/ancestor::mat-form-field")
    lateinit var updateDateTo: AtmSelect

    @Name("Create date from select")
    @FindBy(xpath = "//input[@formcontrolname='createDateFrom']/ancestor::mat-form-field")
    lateinit var createDateFrom: AtmSelect

    @Name("Create date to select")
    @FindBy(xpath = "//input[@formcontrolname='createDateTo']/ancestor::mat-form-field")
    lateinit var createDateTo: AtmSelect

    @Name("Edit button")
    @FindBy(xpath = "//button//span[contains(text(),'EDIT')]")
    lateinit var edit: Button

//    region edit popup

    @Name("Name")
    @FindBy(xpath = "//input[@formcontrolname='issuername']/ancestor::mat-form-field")
    lateinit var nameDialog: AtmInput

    @Name("Description")
    @FindBy(xpath = "//input[@formcontrolname='issuerdescription']/ancestor::mat-form-field")
    lateinit var descriptionDialog: AtmInput

    @Name("Additional info")
    @FindBy(xpath = "//input[@formcontrolname='additionalinfo']/ancestor::mat-form-field")
    lateinit var additionalInfoDialog: AtmInput

    @Name("Confirm")
    @FindBy(xpath = "//button//span[contains(text(),'CONFIRM')]")
    lateinit var confirmDialog: Button
//    endregion

    @Action("check issuer editable data")
    @Step("Admin checks editable data in the table")
    fun checkEditableFields(issuersName: String): String {
        e {
            pressEnter(search)
            sendKeys(search, issuersName)
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementPresented(By.xpath("//td[contains(text(),'$issuersName')]"))
                    }
                }
            }
        }
        val row = registerOfIssuersTable.find {
            it[NAME]?.text == issuersName
        } ?: error("Can't find row with issuer name '$issuersName'")

        val nameIssuer = row[NAME]?.text
        val descriptionIssuer = row[DESCRIPTION]?.text
        val additionInfoIssuer = row[ADDITIONAL_INFO]?.text

        assertThat("No row found with '$nameIssuer'", nameIssuer, hasToString(issuersName))
        assertThat("No row found with '$descriptionIssuer'", descriptionIssuer, hasToString(issuersName))
        assertThat("No row found with '$additionInfoIssuer'", additionInfoIssuer, hasToString(issuersName))
        return row[ID]?.text ?: ""
    }

}