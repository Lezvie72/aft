package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.BankDetails.Companion.generate
import org.apache.commons.lang3.RandomStringUtils.random
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminBankDetailsPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Bank details of the Platform")
class BankDetailsOfThePlatform : BaseTest() {

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1181")
    @Test
    @DisplayName("Bank details. Adding new detail (only mandatory fields filled)")
    fun addingNewBankDetailsOnlyMandatoryFields() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            addBankAccountRequiredFields(newBankDetails)
            checkBankAccountRequiredFields(newBankDetails)
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_ONE)
    @TmsLink("ATMCH-1182")
    @Test
    @DisplayName("Bank details. Adding new detail (one or more mandatory fields not filled)")
    fun addingNewBankDetailsMandatoryFieldsNotFilled() {
        val errorText = "Field is required"
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            e {
                click(addNew)
                sendKeys(bankDetailsValue, newBankDetails.bankDetails)
                sendKeys(correpondentBankValue, newBankDetails.correspondentBank)
                sendKeys(correspondentAccountValue, newBankDetails.correspondentAccount)
                sendKeys(informationValue, newBankDetails.information)
                click(confirm)
            }

            assertThat(
                "Expected error text: $errorText",
                recipientNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                recipientAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                beneficiaryValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                paymentSystem.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                paymentSystemNumberValue.errorText == errorText
            )
            e {
                click(cancel)
            }
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementGone(confirm)
                    }
                }
            }
            driver.navigate().refresh()
            e {
                sendKeys(search, newBankDetails.bankName)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(newBankDetails.bankAddress)
            }

        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_ONE)
    @TmsLink("ATMCH-1187")
    @Test
    @DisplayName("Bank details. Editing details (filled to empty)")
    fun bankDetailsEditingDetailsFilledEmpty() {
        val errorText = "Field is required"
        val newBankDetails = generate()
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addBankAccountAllFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            step("Remove the values of following parameters") {
                e {
                    click(editButton)
                    recipientNameValue.delete()
                    recipientAddressValue.delete()
                    bankNameValue.delete()
                    bankAddressValue.delete()
                    beneficiaryValue.delete()
                    paymentSystemNumberValue.delete()
                    click(confirm)
                }
            }

            assertThat(
                "Expected error text: $errorText",
                recipientNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                recipientAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                beneficiaryValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                paymentSystemNumberValue.errorText == errorText
            )

            e {
                sendKeys(recipientNameValue, newBankDetails.recipientName)
                sendKeys(recipientAddressValue, newBankDetails.recipientAddress)
                sendKeys(bankNameValue, newBankDetails.bankName)
                sendKeys(bankAddressValue, newBankDetails.bankAddress)
                sendKeys(beneficiaryValue, newBankDetails.beneficiary)
                sendKeys(paymentSystemNumberValue, newBankDetails.paymentSystemNumber)
                click(confirm)
            }
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementGone(confirm)
                    }
                }
            }
            assert {
                elementNotPresented(confirm)
                elementWithTextNotPresented("Field is required")
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1255")
    @Test
    @DisplayName("Bank details. Adding new detail (validation check)")
    fun addingNewBankDetailsValidationCheck() {
        val errorText1 = "Must be less than 300 symbols"
        val errorText2 = "Must be less than 100 symbols"
        val errorText3 = "Must be in correct SWIFT format"
        val text = random(301, true, false)
        val shortText = random(10, true, false)
        val number = random(301, false, true)
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            step("WHEN admin fill in all fields with values that don't meet conditions") {
                e {
                    click(addNew)
                    sendKeys(bankDetailsValue, number)
                    sendKeys(recipientNameValue, number)
                    sendKeys(recipientAddressValue, number)
                    sendKeys(bankNameValue, number)
                    sendKeys(bankAddressValue, number)
                    sendKeys(beneficiaryValue, shortText)
                    sendKeys(correpondentBankValue, number)
                    select(paymentSystemValue, "SWIFT")
                    sendKeys(paymentSystemNumberValue, text)
                    sendKeys(correspondentAccountValue, text)
                    sendKeys(informationValue, text)
                    click(confirm)
                }
            }
            assertThat(
                "Expected error text: $errorText1",
                bankDetailsValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                recipientNameValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                recipientAddressValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                bankNameValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                bankAddressValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                correpondentBankValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText2",
                correspondentAccountValue.errorText == errorText2
            )
            assertThat(
                "Expected error text: $errorText3",
                paymentSystemNumberValue.errorText == errorText3
            )
            assertThat(
                "Expected error text: $errorText1",
                informationValue.errorText == errorText1
            )

            assert {
                elementContainingTextPresented("Must be more than 20 symbols")
                elementContainingTextPresented("This field can only use numbers")
            }
            e {
                click(cancel)
            }
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementGone(confirm)
                    }
                }
            }
            driver.navigate().refresh()
            e {
                sendKeys(search, number)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(number)
            }

        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-1257")
    @Test
    @DisplayName("Bank details. Editing details (cancellation scenario)")
    fun editBankDetailsCancelScenario() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            val newBankDetails1 = generate()
            addBankAccountAllFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            e {
                click(editButton)
            }
            assert {
                elementPresented(bankDetailsValue)
                elementPresented(recipientNameValue)
                elementPresented(recipientAddressValue)
                elementPresented(bankNameValue)
                elementPresented(bankAddressValue)
                elementPresented(beneficiaryValue)
                elementPresented(correpondentBankValue)
                elementPresented(correspondentAccountValue)
                elementPresented(paymentSystemNumberValue)
                elementPresented(paymentSystemValue)
                elementPresented(confirm)
                elementPresented(cancel)
                elementPresented(informationValue)
                elementPresented(active)
            }
            e {
                sendKeys(bankDetailsValue, newBankDetails1.bankDetails)
                sendKeys(recipientNameValue, newBankDetails1.recipientName)
                sendKeys(recipientAddressValue, newBankDetails1.recipientAddress)
                sendKeys(bankNameValue, newBankDetails1.bankName)
                sendKeys(bankAddressValue, newBankDetails1.bankAddress)
                sendKeys(beneficiaryValue, newBankDetails1.beneficiary)
                sendKeys(correpondentBankValue, newBankDetails1.correspondentBank)
                select(paymentSystemValue, newBankDetails1.paymentSystem)
                sendKeys(paymentSystemNumberValue, newBankDetails1.paymentSystemNumber)
                sendKeys(correspondentAccountValue, newBankDetails1.correspondentAccount)
                sendKeys(informationValue, newBankDetails1.information)
                click(cancel)
                wait {
                    until("dialog add bank account is gone", 15) {
                        check {
                            isElementGone(confirm)
                        }
                    }
                }
                sendKeys(search, newBankDetails1.bankName)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(newBankDetails1.bankAddress)
            }
            checkBankAccountAllFields(newBankDetails)
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1179")
    @Test
    @DisplayName("Bank details. Adding new detail (all fields filled)")
    fun addingNewBankDetailsAllFields() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            e {
                click(addNew)
            }
            assert {
                elementPresented(bankDetailsValue)
                elementPresented(recipientNameValue)
                elementPresented(recipientAddressValue)
                elementPresented(bankNameValue)
                elementPresented(bankAddressValue)
                elementPresented(beneficiaryValue)
                elementPresented(correpondentBankValue)
                elementPresented(correspondentAccountValue)
                elementPresented(paymentSystemNumberValue)
                elementPresented(paymentSystemValue)
                elementPresented(confirm)
                elementPresented(cancel)
                elementPresented(informationValue)
                elementPresented(active)
            }
            e {
                sendKeys(bankDetailsValue, newBankDetails.bankDetails)
                sendKeys(recipientNameValue, newBankDetails.recipientName)
                sendKeys(recipientAddressValue, newBankDetails.recipientAddress)
                sendKeys(bankNameValue, newBankDetails.bankName)
                sendKeys(bankAddressValue, newBankDetails.bankAddress)
                sendKeys(beneficiaryValue, newBankDetails.beneficiary)
                sendKeys(correpondentBankValue, newBankDetails.correspondentBank)
                select(paymentSystemValue, newBankDetails.paymentSystem)
                click(paymentSystemValue)
            }
            assert {
                elementContainingTextPresented("BIC")
                elementContainingTextPresented("SWIFT")
                elementContainingTextPresented("IBAN")
                elementContainingTextPresented("ABA RTN")
            }

            e {
                click(paymentSystemNumberValue)
                sendKeys(paymentSystemNumberValue, newBankDetails.paymentSystemNumber)
                sendKeys(correspondentAccountValue, newBankDetails.correspondentAccount)
                sendKeys(informationValue, newBankDetails.information)
                click(cancel)
                wait {
                    until("dialog add bank account is gone", 15) {
                        check {
                            isElementGone(confirm)
                        }
                    }
                }
                sendKeys(search, newBankDetails.bankName)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(newBankDetails.bankAddress)
            }
            addBankAccountAllFields(newBankDetails)
            checkBankAccountAllFields(newBankDetails)
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1251")
    @Test
    @DisplayName("Bank details. Editing details (all fields edited)")
    fun editingBankDetailsAllFields() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            val newBankDetails1 = generate()
            addBankAccountAllFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            e {
                click(editButton)
            }
            assert {
                elementPresented(bankDetailsValue)
                elementPresented(recipientNameValue)
                elementPresented(recipientAddressValue)
                elementPresented(bankNameValue)
                elementPresented(bankAddressValue)
                elementPresented(beneficiaryValue)
                elementPresented(correpondentBankValue)
                elementPresented(correspondentAccountValue)
                elementPresented(paymentSystemNumberValue)
                elementPresented(paymentSystemValue)
                elementPresented(confirm)
                elementPresented(cancel)
                elementPresented(informationValue)
                elementPresented(active)
            }
            e {
                sendKeys(bankDetailsValue, newBankDetails1.bankDetails)
                sendKeys(recipientNameValue, newBankDetails1.recipientName)
                sendKeys(recipientAddressValue, newBankDetails1.recipientAddress)
                sendKeys(bankNameValue, newBankDetails1.bankName)
                sendKeys(bankAddressValue, newBankDetails1.bankAddress)
                sendKeys(beneficiaryValue, newBankDetails1.beneficiary)
                sendKeys(correpondentBankValue, newBankDetails1.correspondentBank)
                select(paymentSystemValue, newBankDetails1.paymentSystem)
                click(paymentSystemValue)
            }
            assert {
                elementContainingTextPresented("BIC")
                elementContainingTextPresented("SWIFT")
                elementContainingTextPresented("IBAN")
                elementContainingTextPresented("ABA RTN")
            }

        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1186")
    @Test
    @DisplayName("Adm.panel. Bank details. Deleting details")
    fun deletingBankDetails() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            addBankAccountRequiredFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            clickDeleteIcon(newBankDetails.bankName)
            assert {
                elementContainingTextPresented("Do you really want to delete the selected Bank details?")
                elementContainingTextPresented("Yes")
                elementContainingTextPresented("No")
            }
            e {
                click(no)
                driver.navigate().refresh()
            }
            chooseRecord(newBankDetails.bankName)
            assert { elementContainingTextPresented(newBankDetails.bankName) }
            chooseRecord(newBankDetails.bankName)
            clickDeleteIcon(newBankDetails.bankName)
            e {
                click(yes)
                wait {
                    until("dialog delete bank account is gone", 15) {
                        check {
                            isElementGone(yes)
                        }
                    }
                }
                driver.navigate().refresh()
                sendKeys(search, newBankDetails.bankName)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(newBankDetails.bankAddress)
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-1264")
    @Test
    @DisplayName("Bank details. Bank details propagation to Fiat Deposit details")
    fun bankDetailsPropagationFiatDepositDetails() {
        val newBankDetails1 = generate()
        val newBankDetails2 = generate()
        val newBankDetails3 = generate()
        val newBankDetails4 = generate()
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            clearTestData()
            addBankAccountAllFields(newBankDetails1, AtmAdminBankDetailsPage.PaymentSystem.BIC)
            addBankAccountAllFields(newBankDetails2, AtmAdminBankDetailsPage.PaymentSystem.SWIFT)
            addBankAccountAllFields(newBankDetails3, AtmAdminBankDetailsPage.PaymentSystem.IBAN)
            addBankAccountAllFields(newBankDetails4, AtmAdminBankDetailsPage.PaymentSystem.ABA_RTN)
        }
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(mainWalletTicker)
                click(depositDetails)
                select(selectCurrency, "USD")
                select(selectBicType, "BIC")
            }
            assert {
                elementContainingTextPresented(newBankDetails1.recipientName)
                elementContainingTextPresented(newBankDetails1.recipientAddress)
                elementContainingTextPresented(newBankDetails1.bankName)
                elementContainingTextPresented(newBankDetails1.bankAddress)
                elementContainingTextPresented("BIC")
            }
            e {
                select(selectBicType, "SWIFT")
            }
            assert {
                elementContainingTextPresented(newBankDetails2.recipientName)
                elementContainingTextPresented(newBankDetails2.recipientAddress)
                elementContainingTextPresented(newBankDetails2.bankName)
                elementContainingTextPresented(newBankDetails2.bankAddress)
                elementContainingTextPresented("SWIFT")
            }
            e {
                select(selectBicType, "IBAN")
            }
            assert {
                elementContainingTextPresented(newBankDetails3.recipientName)
                elementContainingTextPresented(newBankDetails3.recipientAddress)
                elementContainingTextPresented(newBankDetails3.bankName)
                elementContainingTextPresented(newBankDetails3.bankAddress)
                elementContainingTextPresented("IBAN")
            }
            e {
                select(selectBicType, "ABA_RTN")
            }
            assert {
                elementContainingTextPresented(newBankDetails4.recipientName)
                elementContainingTextPresented(newBankDetails4.recipientAddress)
                elementContainingTextPresented(newBankDetails4.bankName)
                elementContainingTextPresented(newBankDetails4.bankAddress)
                elementContainingTextPresented("ABA RTN")
            }
        }
    }

    @TmsLink("ATMCH-1253")
    @Test
    @DisplayName("Bank details. Editing details (entering invalid data)")
    fun editingBankDetailsInvalidData() {
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val newBankDetails = generate()
            val errorText1 = "Must be less than 300 symbols"
            val errorText2 = "Must be less than 100 symbols"
            val errorText3 = "Must be in correct SWIFT format"
            val text = random(301, true, false)
            val shortText = random(10, true, false)
            val number = random(301, false, true)
            addBankAccountAllFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            e {
                click(editButton)
            }
            assert {
                elementPresented(bankDetailsValue)
                elementPresented(recipientNameValue)
                elementPresented(recipientAddressValue)
                elementPresented(bankNameValue)
                elementPresented(bankAddressValue)
                elementPresented(beneficiaryValue)
                elementPresented(correpondentBankValue)
                elementPresented(correspondentAccountValue)
                elementPresented(paymentSystemNumberValue)
                elementPresented(paymentSystemValue)
                elementPresented(confirm)
                elementPresented(cancel)
                elementPresented(informationValue)
                elementPresented(active)
            }
            step("WHEN admin fill in all fields with values that don't meet conditions") {
                e {
                    sendKeys(bankDetailsValue, number)
                    sendKeys(recipientNameValue, number)
                    sendKeys(recipientAddressValue, number)
                    sendKeys(bankNameValue, number)
                    sendKeys(bankAddressValue, number)
                    sendKeys(beneficiaryValue, shortText)
                    sendKeys(correpondentBankValue, number)
                    select(paymentSystemValue, "SWIFT")
                    sendKeys(paymentSystemNumberValue, text)
                    sendKeys(correspondentAccountValue, text)
                    sendKeys(informationValue, text)
                    click(confirm)
                }
            }
            assertThat(
                "Expected error text: $errorText1",
                bankDetailsValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                recipientNameValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                recipientAddressValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                bankNameValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                bankAddressValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText1",
                correpondentBankValue.errorText == errorText1
            )
            assertThat(
                "Expected error text: $errorText2",
                correspondentAccountValue.errorText == errorText2
            )
            assertThat(
                "Expected error text: $errorText3",
                paymentSystemNumberValue.errorText == errorText3
            )
            assertThat(
                "Expected error text: $errorText1",
                informationValue.errorText == errorText1
            )

            assert {
                elementContainingTextPresented("Must be more than 20 symbols")
                elementContainingTextPresented("This field can only use numbers")
            }
            e {
                click(cancel)
            }
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementGone(confirm)
                    }
                }
            }
            driver.navigate().refresh()
            e {
                sendKeys(search, number)
                pressEnter(search)
            }
            assert {
                elementWithTextNotPresented(number)
            }
        }
    }

    @Disabled("необходим доступ в бд")
    @TmsLink("ATMCH-1256")
    @Test
    @DisplayName("Bank details. Editing details (empty to filled)")
    fun bankDetailsEditingDetailsEmptyFilled() {
        val errorText = "Field is required"
        val newBankDetails = generate()
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addBankAccountAllFields(newBankDetails)
            chooseRecord(newBankDetails.bankName)
            step("Remove the values of following parameters") {
                e {
                    click(editButton)
                    recipientNameValue.delete()
                    recipientAddressValue.delete()
                    bankNameValue.delete()
                    bankAddressValue.delete()
                    beneficiaryValue.delete()
                    paymentSystemNumberValue.delete()
                    click(confirm)
                }
            }

            assertThat(
                "Expected error text: $errorText",
                recipientNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                recipientAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankNameValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                bankAddressValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                beneficiaryValue.errorText == errorText
            )
            assertThat(
                "Expected error text: $errorText",
                paymentSystemNumberValue.errorText == errorText
            )

            e {
                sendKeys(recipientNameValue, newBankDetails.recipientName)
                sendKeys(recipientAddressValue, newBankDetails.recipientAddress)
                sendKeys(bankNameValue, newBankDetails.bankName)
                sendKeys(bankAddressValue, newBankDetails.bankAddress)
                sendKeys(beneficiaryValue, newBankDetails.beneficiary)
                sendKeys(paymentSystemNumberValue, newBankDetails.paymentSystemNumber)
                click(confirm)
            }
            wait {
                until("dialog add bank account is gone", 15) {
                    check {
                        isElementGone(confirm)
                    }
                }
            }
            assert {
                elementNotPresented(confirm)
                elementWithTextNotPresented("Field is required")
            }
        }
    }

}