package pages.atm

import io.qameta.allure.Step
import models.CompanyDetails
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import utils.helpers.to

@PageUrl("/companies")
class AtmAdminCompaniesPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val SHORT_NAME = "Short name"
        const val FULL_NAME = "Full Name"
        const val ADDRESS = "Address"
        const val SHOW_ON_OTF = "Show on otf"
    }

    @Name("Companies table")
    @FindBy(css = "sdex-companies")
    lateinit var companiesTable: SdexTable

    //region company details
    @Name("Full name")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='fullname']")
    lateinit var fullNameInput: AtmInput

    @Name("Shortname")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='shortname']")
    lateinit var shortNameInput: AtmInput

    @Name("Email")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='email']")
    lateinit var emailInput: AtmInput

    @Name("Address")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='address']")
    lateinit var addressInput: AtmInput

    @Name("Registration country")
    @FindBy(xpath = "//mat-select[@formcontrolname='countryid']")
    lateinit var registrationCountry: AtmSelect

    @Name("Onboarding date")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='regdate']")
    lateinit var onboardingDate: AtmInput

    @Name("Registration document number")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='regdocumentnum']")
    lateinit var registrationDocNum: AtmInput

    @Name("Registration document date")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='regdocumentdate']")
    lateinit var regDocDate: AtmInput

    @Name("Legal entity")
    @FindBy(xpath = "//span[contains(text(), 'Legal entity')]/ancestor::label")
    lateinit var legalEntityCheckbox: CheckBox

    @Name("Issuer")
    @FindBy(xpath = "//span[contains(text(), 'Issuer')]/ancestor::label")
    lateinit var issuerCheckbox: CheckBox

    @Name("Validator")
    @FindBy(xpath = "//span[contains(text(), 'Validator')]/ancestor::label")
    lateinit var validatorCheckbox: CheckBox

    @Name("Industrial")
    @FindBy(xpath = "//span[contains(text(), 'Industrial')]/ancestor::label")
    lateinit var industrialCheckbox: CheckBox

    @Name("Show on otf")
    @FindBy(xpath = "//span[contains(text(), 'Show on otf')]/ancestor::label")
    lateinit var showOnOtfCheckbox: CheckBox

    @Name("ETC company")
    @FindBy(xpath = "//span[contains(text(), 'ETC company')]/ancestor::label")
    lateinit var etcCompanyCheckbox: CheckBox

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]")
    lateinit var cancelButton: Button

    @Name("Add company")
    @FindBy(xpath = "//sdex-edit-company-form//span[contains(text(),' ADD ')]")
    lateinit var addCompanyButton: Button
    //endregion

    @Name("Search")
    @FindBy(xpath = "//mat-form-field")
    lateinit var search: AtmInput

    @Name("Add")
    @FindBy(xpath = "//span[contains(text(),' ADD ')]")
    lateinit var addButton: Button

    @Name("View button")
    @FindBy(xpath = "//span[contains(text(),'VIEW')]")
    lateinit var viewButton: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(),'EDIT')]")
    lateinit var editButton: Button

    @Name("Delete icon")
    @FindBy(xpath = "//mat-icon[contains(text(),'delete')]")
    lateinit var delete: Button

    @FindBy(xpath = "//mat-paginator//button[@aria-label='Previous page']")
    @Name("Prev page")
    lateinit var firstPage: Button

    @FindBy(xpath = "//sdex-companies//tbody//td")
    @Name("First row in body of table")
    lateinit var firstRowInTable: SdexSelect

    @FindBy(xpath = "//*[contains(text(),'SAVE')]/..")
    @Name("Save button in edit form")
    lateinit var saveInEditForm: Button

    @Step("Add company")
    @Action("Add company")
    fun addCompany(
        fullName: String, shortName: String, address: String,
        legalCompany: Boolean, issuer: Boolean, validator: Boolean
    ): AtmAdminCompaniesPage {
        e {
            Thread.sleep(10000)
            click(addButton)
            sendKeys(fullNameInput, fullName)
            sendKeys(shortNameInput, shortName)
            sendKeys(addressInput, address)
            setCheckbox(legalEntityCheckbox, legalCompany)
            setCheckbox(issuerCheckbox, issuer)
            setCheckbox(validatorCheckbox, validator)
            click(addCompanyButton)
            wait {
                until("Pop up should've been disappeared", 15) {
                    check {
                        isElementGone(addCompanyButton)
                    }
                }
            }
        }
        return this
    }

    @Step("Add company")
    @Action("Add company")
    fun addCompany(
        companyDetails: CompanyDetails,
        legalCompany: Boolean, issuer: Boolean, validator: Boolean
    ): AtmAdminCompaniesPage {
        e {
            Thread.sleep(10000)
            click(addButton)
            sendKeys(fullNameInput, companyDetails.fullName)
            sendKeys(shortNameInput, companyDetails.shortName)
            sendKeys(addressInput, companyDetails.address)
            select(registrationCountry, companyDetails.registrationCountry)
            sendKeys(registrationDocNum, companyDetails.regDocNumber)
            sendKeys(regDocDate, companyDetails.regDocDate)
            sendKeys(onboardingDate, companyDetails.registrationDate)
            setCheckbox(legalEntityCheckbox, legalCompany)
            setCheckbox(issuerCheckbox, issuer)
            setCheckbox(validatorCheckbox, validator)
            click(addCompanyButton)
            wait {
                until("Pop up should've been disappeared", 15) {
                    check {
                        isElementGone(addCompanyButton)
                    }
                }
            }
        }
        return this
    }

    @Action("check company details")
    @Step("User checks company details data in company details table")
    fun checkCompanyDetails(companyDetails: CompanyDetails): String {
        e {
            sendKeys(search, companyDetails.shortName)
            pressEnter(search)
        }
        val row = companiesTable.find {
            it[SHORT_NAME]?.text == companyDetails.shortName
        } ?: error(String.format("Can't find row with short name %s", companyDetails.shortName))

        val shortNam = row[SHORT_NAME]?.text
        val addres = row[ADDRESS]?.text

        assertThat(
            "No row found with '$shortNam'",
            shortNam,
            hasToString(companyDetails.shortName)
        )
        assertThat(
            "No row found with '$addres'",
            addres,
            hasToString(companyDetails.address)
        )

        return row[SHORT_NAME]?.text ?: ""
    }

    @Action("check bank account")
    @Step("User checks bank account data in bank details table")
    fun checkCompanyDetails(shortName: String, address: String): String {
        e {
            sendKeys(search, shortName)
            pressEnter(search)
        }
        val row = companiesTable.find {
            it[SHORT_NAME]?.text == shortName
        } ?: error("Can't find row with short name '$shortName'")

        val shortNam = row[SHORT_NAME]?.text
        val addres = row[ADDRESS]?.text

        assertThat(
            "No row found with '$shortNam'",
            shortNam,
            hasToString(shortName)
        )
        assertThat(
            "No row found with '$addres'",
            addres,
            hasToString(address)
        )

        return row[SHORT_NAME]?.text ?: ""
    }

    @Action("Find checkbox by short name")
    @Step("User select necessary company by short name in search field and change edit flag show on otf")
    fun setFlagShowOnUtf(shortName: String, stateFlag: Boolean) {
        e {
            sendKeys(search, shortName)
            pressEnter(search)
            Thread.sleep(4000)
        }
        companiesTable.find {
            it[SHORT_NAME]?.text == shortName
        } ?: error("Can't find row with short name '$shortName'")

        e {
            click(firstRowInTable)
            click(editButton)
            setCheckbox(showOnOtfCheckbox, stateFlag)
            click(saveInEditForm)
        }
    }

    @Step("User choose company details data in company details table")
    fun chooseCompanyDetails(companyDetails: CompanyDetails){
        e {
            sendKeys(search, companyDetails.shortName)
            pressEnter(search)
        }
        val row = companiesTable.find {
            it[SHORT_NAME]?.text == companyDetails.shortName
        }?.get(SHORT_NAME)
            ?.to<Button>("Company with short name '$companyDetails.shortName'")
            ?: error("Can't find row with short name '$companyDetails.shortName'")
        e{
            click(row)
        }
    }

    @Step("User choose company details data in company details table")
    fun chooseCompanyDetails(shortName:String){
        e {
            sendKeys(search, shortName)
            pressEnter(search)
        }
        val row = companiesTable.find {
            it[SHORT_NAME]?.text == shortName
        }?.get(SHORT_NAME)
            ?.to<Button>("Company with short name '$shortName'")
            ?: error("Can't find row with short name '$shortName'")
        e{
            click(row)
        }
    }
}