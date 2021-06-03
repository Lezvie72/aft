package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CompanyDetails.Companion.generate
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminCompaniesPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Company information")
class CompanyInformation : BaseTest()  {

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1524")
    @Test
    @DisplayName("Add Company information (Entities). Interface.")
    fun addCompanyInformationInterface() {
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Id")
                elementContainingTextPresented("Short name")
                elementContainingTextPresented("Registration country")
                elementContainingTextPresented("Address")
                elementContainingTextPresented("Legal entity")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Validator")
                elementContainingTextPresented("Industrial")
                elementContainingTextPresented("Show on otf")
                elementContainingTextPresented("ETC company")
                elementContainingTextPresented("Created")
                elementContainingTextPresented("Updated")
                elementContainingTextPresented("delete")
                elementPresented(addButton)
                elementPresented(editButton)
                elementPresented(viewButton)
            }
            e{
                click(addButton)
            }
            assert {
                elementPresented(fullNameInput)
                elementPresented(shortNameInput)
                elementPresented(addressInput)
                elementPresented(registrationCountry)
                elementPresented(onboardingDate)
                elementPresented(registrationDocNum)
                elementPresented(regDocDate)
                elementPresented(legalEntityCheckbox)
                elementPresented(issuerCheckbox)
                elementPresented(validatorCheckbox)
                elementPresented(industrialCheckbox)
                elementPresented(showOnOtfCheckbox)
                elementPresented(etcCompanyCheckbox)
                elementPresented(addCompanyButton)
                elementPresented(cancelButton)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1525")
    @Test
    @DisplayName("Add Company information (Entities). Validation.")
    fun addingCompanyInfValidation() {
        val errorText1 = "Must be less than 300 symbols"
        val errorText2 = "Must be less than 100 symbols"
        val errorText3 = "Field is required"
        val errorText4 = "Invalid e-mail format"
        val text101 = RandomStringUtils.random(101, true, false)
        val text301 = RandomStringUtils.random(301, true, false)

        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(addButton)
                sendKeys(shortNameInput, text101)
                sendKeys(fullNameInput, text301)
                sendKeys(addressInput, text301)
                sendKeys(emailInput, "qwerty")
                select(registrationCountry, "Albania")
                sendKeys(onboardingDate, text101)
                sendKeys(registrationDocNum, text101)
                sendKeys(regDocDate, text101)
                setCheckbox(legalEntityCheckbox, true)
                setCheckbox(issuerCheckbox, true)
                setCheckbox(validatorCheckbox, true)
                setCheckbox(industrialCheckbox, true)
                setCheckbox(showOnOtfCheckbox, true)
            }

            assertThat(
                "Expected error text: $errorText2",
                shortNameInput.errorText == errorText2
            )
            assertThat(
                "Expected error text: $errorText1",
                fullNameInput.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText4",
                emailInput.errorText == errorText4
            )
            assertThat(
                "Expected error text: $errorText1",
                addressInput.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText2",
                registrationDocNum.errorText == errorText2
            )
            e{
                shortNameInput.delete()
                setCheckbox(legalEntityCheckbox, false)
            }
            assertThat(
                "Expected error text: $errorText3",
                shortNameInput.errorText == errorText3
            )
            e{
                sendKeys(shortNameInput, text101)
            }
            assert {
                elementWithTextPresented("Field is required")
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1526")
    @Test
    @DisplayName("Add Company information (Entities). Functional.")
    fun addingCompanyInfFunctional() {
        val newCompanyDetails = generate()
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(addButton)
            }
            assert {
                elementPresented(shortNameInput)
                elementPresented(fullNameInput)
                elementPresented(addressInput)
                elementPresented(emailInput)
                elementPresented(registrationCountry)
                elementPresented(onboardingDate)
                elementPresented(registrationDocNum)
                elementPresented(regDocDate)
                elementPresented(legalEntityCheckbox)
                elementPresented(issuerCheckbox)
                elementPresented(validatorCheckbox)
                elementPresented(industrialCheckbox)
                elementPresented(showOnOtfCheckbox)
                elementPresented(etcCompanyCheckbox)
            }
            e{
                click(cancelButton)
                addCompany(newCompanyDetails, legalCompany = true, issuer = true, validator = true)
                checkCompanyDetails(newCompanyDetails)
            }
        }
    }

    @Disabled("add не кликается, в таблице значения null")
    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1274")
    @Test
    @DisplayName("Editing Organizations data (Entities)")
    fun editOrganizationsDataEntities() {
        val newCompanyDetails1 = generate()
        val newCompanyDetails2 = generate()
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(addButton)
                elementPresented(editButton)
                elementPresented(viewButton)
            }
            e {
                addCompany(
                    newCompanyDetails1,
                    true, true, true
                )

                sendKeys(search, newCompanyDetails1.shortName)
                pressEnter(search)
                click(firstRowInTable)
                click(editButton)
                sendKeys(shortNameInput, newCompanyDetails2.shortName)
                sendKeys(fullNameInput, newCompanyDetails2.fullName)
                sendKeys(addressInput, newCompanyDetails2.address)
                select(registrationCountry, newCompanyDetails2.registrationCountry)
                sendKeys(onboardingDate, newCompanyDetails2.registrationDate)
                sendKeys(registrationDocNum, newCompanyDetails2.regDocNumber)
                sendKeys(regDocDate, newCompanyDetails2.regDocDate)
                setCheckbox(legalEntityCheckbox, true)
                setCheckbox(issuerCheckbox, true)
                setCheckbox(validatorCheckbox, true)
                setCheckbox(industrialCheckbox, true)
                setCheckbox(showOnOtfCheckbox, true)
                click(cancelButton)

                checkCompanyDetails(newCompanyDetails1.shortName, newCompanyDetails1.address)

                click(firstRowInTable)
                click(editButton)
                shortNameInput.delete()
                fullNameInput.delete()
                addressInput.delete()
                regDocDate.delete()
                registrationDocNum.delete()
                onboardingDate.delete()
                sendKeys(shortNameInput, newCompanyDetails2.shortName)
                sendKeys(fullNameInput, newCompanyDetails2.fullName)
                sendKeys(addressInput, newCompanyDetails2.address)
                select(registrationCountry, newCompanyDetails2.registrationCountry)
                sendKeys(onboardingDate, newCompanyDetails2.registrationDate)
                sendKeys(registrationDocNum, newCompanyDetails2.regDocNumber)
                sendKeys(regDocDate, newCompanyDetails2.regDocDate)
                click(saveInEditForm)

                checkCompanyDetails(newCompanyDetails2.shortName, newCompanyDetails2.address)

            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1410")
    @Test
    @DisplayName("Editing  Organizations data (Entities). Validation.")
    fun editOrganizationsDataEntitiesValidation() {
        val newCompanyDetails1 = generate()
        val errorText = "Field is required"
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addCompany(newCompanyDetails1, true, true, true)
            chooseCompanyDetails(newCompanyDetails1)
            assert {
                elementPresented(editButton)
                elementPresented(viewButton)
                elementPresented(addButton)
            }
            e {
                click(editButton)
            }
            assert {
                elementPresented(shortNameInput)
                elementPresented(emailInput)
                elementPresented(fullNameInput)
                elementPresented(registrationCountry)
                elementPresented(addressInput)
                elementPresented(registrationDocNum)
                elementPresented(onboardingDate)
                elementPresented(legalEntityCheckbox)
                elementPresented(issuerCheckbox)
                elementPresented(validatorCheckbox)
                elementPresented(industrialCheckbox)
                elementPresented(etcCompanyCheckbox)
                elementPresented(showOnOtfCheckbox)
                elementPresented(saveInEditForm)
                elementPresented(cancelButton)
            }
            e{
                shortNameInput.delete()
            }
            assertThat(
                "Expected error text: $errorText",
                shortNameInput.errorText == errorText
            )
            e{
                sendKeys(shortNameInput, "OTF1")
                click(saveInEditForm)
            }
            assert {
                elementWithTextPresented("Resource already exists")
            }

        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1413")
    @Test
    @DisplayName("Editing Organizations data (Entities). Interface.")
    fun editOrganizationsDataEntitiesInterface() {
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementContainingTextPresented("Id")
                elementContainingTextPresented("Short name")
                elementContainingTextPresented("Registration country")
                elementContainingTextPresented("Address")
                elementContainingTextPresented("Legal entity")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Validator")
                elementContainingTextPresented("Industrial")
                elementContainingTextPresented("Show on otf")
                elementContainingTextPresented("ETC company")
                elementContainingTextPresented("Created")
                elementContainingTextPresented("Updated")
                elementContainingTextPresented("delete")
                elementPresented(addButton)
                elementPresented(editButton)
                elementPresented(viewButton)
            }
            chooseCompanyDetails("netrogat_emp_autotest")
            e {
                click(viewButton)
            }
            assert {
                elementWithTextPresented("Short name")
                elementWithTextPresented("Full name")
                elementWithTextPresented("Email")
                elementWithTextPresented("Address")
                elementWithTextPresented("Registration country")
                elementWithTextPresented("Onboarding date")
                elementWithTextPresented("Registration document number")
            }
        }
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            chooseCompanyDetails("netrogat_emp_autotest")
            e {
                click(editButton)
            }
            assert {
                elementPresented(shortNameInput)
                elementPresented(emailInput)
                elementPresented(fullNameInput)
                elementPresented(registrationCountry)
                elementPresented(addressInput)
                elementPresented(registrationDocNum)
                elementPresented(onboardingDate)
                elementPresented(legalEntityCheckbox)
                elementPresented(issuerCheckbox)
                elementPresented(validatorCheckbox)
                elementPresented(industrialCheckbox)
                elementPresented(etcCompanyCheckbox)
                elementPresented(showOnOtfCheckbox)
                elementPresented(saveInEditForm)
                elementPresented(cancelButton)
            }
        }
    }
}