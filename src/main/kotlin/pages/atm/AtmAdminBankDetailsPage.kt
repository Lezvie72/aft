package pages.atm

import io.qameta.allure.Step
import models.BankDetails
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to

@PageUrl("/bank-details")
open class AtmAdminBankDetailsPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val BANK_DETAILS = "Bank details"
        const val RECIPIENT_NAME = "Recipient Name"
        const val RECIPIENT_ADDRESS = "Recipient Address"
        const val BANK_NAME = "Bank Name"
        const val BANK_ADDRESS = "Bank Address"
        const val BENEFICIARY = "Beneficiary’s Acc. №"
        const val CORRESPONDENT_BANK = "Correspondent bank"
        const val CORRESPONDENT_ACCOUNT = "Correspondent account №"
        const val PAYMENT_SYSTEM = "Payment system"
        const val ADDITIONAL_INFORMATION = "Additional information"
    }

    enum class PaymentSystem {
        BIC, SWIFT, IBAN, ABA_RTN
    }

    @Name("Bank details table")
    @FindBy(css = "sdex-bank-details")
    lateinit var bankDetailsTable: SdexTable

    @Name("Search")
    @FindBy(xpath = "//mat-form-field")
    lateinit var search: AtmInput

    @Name("Edit")
    @FindBy(xpath = "//span[text()=' EDIT ']/ancestor::button")
    lateinit var editButton: Button

    @Name("Yes button in the dialog form")
    @FindBy(xpath = "//span[contains(text(),'Yes')]")
    lateinit var yes: Button

    @Name("No button in the dialog form")
    @FindBy(xpath = "//button//span[contains(text(),'No')]")
    lateinit var no: Button

    @Name("Delete icon")
    @FindBy(xpath = "//mat-icon[contains(text(),'delete')]")
    lateinit var delete: Button

    //region Bank Details Form

    @Name("Add new")
    @FindBy(xpath = "//span[contains(text(),'ADD')]")
    lateinit var addNew: Button

    @Name("Bank details")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='bankdetail']")
    lateinit var bankDetailsValue: AtmInput

    @Name("Recipient name")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='recipientname']")
    lateinit var recipientNameValue: AtmInput

    @Name("Recipient address")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='recipientaddress']")
    lateinit var recipientAddressValue: AtmInput

    @Name("Bank Name")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='bankname']")
    lateinit var bankNameValue: AtmInput

    @Name("Bank Address")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='bankaddress']")
    lateinit var bankAddressValue: AtmInput

    @Name("Beneficiary’s Acc. №")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='beneficiaryaccnumber']")
    lateinit var beneficiaryValue: AtmInput

    @Name("Correpondent Bank")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='correspondentbank']")
    lateinit var correpondentBankValue: AtmInput

    @Name("Correspondent Account Number")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='correspondentaccnumber']")
    lateinit var correspondentAccountValue: AtmInput

    @Name("Information")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='additionalinfo']")
    lateinit var informationValue: AtmInput

    @Name("Payment System")
    @FindBy(xpath = "//mat-select[@formcontrolname='paymentsystemtype']")
    lateinit var paymentSystemValue: AtmSelect

    @Name("Mat field of payment system select")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='paymentsystemtype']")
    lateinit var paymentSystem: AtmInput

    @Name("Payment System Number")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='paymentsystemaccnumber']")
    lateinit var paymentSystemNumberValue: AtmInput

    @Name("Active checkbox")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='active']//input")
    lateinit var active: CheckBox

    @Name("Confirm")
    @FindBy(xpath = "//span[contains(text(),'CONFIRM')]")
    lateinit var confirm: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'CANCEL')]")
    lateinit var cancel: Button

    //endregion


    @Step("Admin add bank account with only required fields filled")
    @Action("Admin add bank account")
    fun addBankAccountRequiredFields(bankDetails: BankDetails) {
        e {
            click(addNew)
            sendKeys(recipientNameValue, bankDetails.recipientName)
            sendKeys(recipientAddressValue, bankDetails.recipientAddress)
            sendKeys(bankNameValue, bankDetails.bankName)
            sendKeys(bankAddressValue, bankDetails.bankAddress)
            sendKeys(beneficiaryValue, bankDetails.beneficiary)
            select(paymentSystemValue, bankDetails.paymentSystem)
            sendKeys(paymentSystemNumberValue, bankDetails.paymentSystemNumber)
            click(confirm)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(confirm)
                }
            }
        }
    }

    @Step("Admin add bank account with all fields filled")
    @Action("Admin add bank account")
    fun addBankAccountAllFields(bankDetails: BankDetails) {
        e {
            click(addNew)
            sendKeys(bankDetailsValue, bankDetails.bankDetails)
            sendKeys(recipientNameValue, bankDetails.recipientName)
            sendKeys(recipientAddressValue, bankDetails.recipientAddress)
            sendKeys(bankNameValue, bankDetails.bankName)
            sendKeys(bankAddressValue, bankDetails.bankAddress)
            sendKeys(beneficiaryValue, bankDetails.beneficiary)
            sendKeys(correpondentBankValue, bankDetails.correspondentBank)
            sendKeys(correspondentAccountValue, bankDetails.correspondentAccount)
            select(paymentSystemValue, bankDetails.paymentSystem)
            sendKeys(paymentSystemNumberValue, bankDetails.paymentSystemNumber)
            sendKeys(informationValue, bankDetails.information)
            click(confirm)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(confirm)
                }
            }
        }
    }

    @Step("Admin add bank account with all fields filled")
    @Action("Admin add bank account")
    fun addBankAccountAllFields(bankDetails: BankDetails, paymentSystem: PaymentSystem) {
        e {
            click(addNew)
            sendKeys(bankDetailsValue, bankDetails.bankDetails)
            sendKeys(recipientNameValue, bankDetails.recipientName)
            sendKeys(recipientAddressValue, bankDetails.recipientAddress)
            sendKeys(bankNameValue, bankDetails.bankName)
            sendKeys(bankAddressValue, bankDetails.bankAddress)
            sendKeys(beneficiaryValue, bankDetails.beneficiary)
            sendKeys(correpondentBankValue, bankDetails.correspondentBank)
            sendKeys(correspondentAccountValue, bankDetails.correspondentAccount)
            when (paymentSystem) {
                PaymentSystem.BIC -> select(paymentSystemValue, "BIC")
                PaymentSystem.SWIFT -> select(paymentSystemValue, "SWIFT")
                PaymentSystem.IBAN -> select(paymentSystemValue, "IBAN")
                PaymentSystem.ABA_RTN -> select(paymentSystemValue, "ABA RTN")
            }
            when (paymentSystem) {
                PaymentSystem.BIC -> sendKeys(paymentSystemNumberValue, "044525976")
                PaymentSystem.SWIFT -> sendKeys(paymentSystemNumberValue, "UBSWCHZH")
                PaymentSystem.IBAN -> sendKeys(paymentSystemNumberValue, "CH9300762011623852957")
                PaymentSystem.ABA_RTN -> sendKeys(paymentSystemNumberValue, "026013356")
            }
            sendKeys(informationValue, bankDetails.information)
            click(active)
            click(confirm)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(confirm)
                }
            }
        }
    }

    @Action("check bank account")
    @Step("User checks bank account data in bank details table")
    fun checkBankAccountRequiredFields(bankDetails: BankDetails): String {
        val  searchWord = bankDetails.bankName.replace(Regex("[^\\d]"), "")
        e {
            sendKeys(search, searchWord)
            pressEnter(search)
        }
        val row = bankDetailsTable.find {
            it[BANK_NAME]?.text == bankDetails.bankName
        } ?: error("Can't find row with bank name '$bankDetails.bankName'")

        val paymentSys = row[PAYMENT_SYSTEM]?.text
        val recName = row[RECIPIENT_NAME]?.text
        val recAddress = row[RECIPIENT_ADDRESS]?.text
        val addressOfBank = row[BANK_ADDRESS]?.text
        val nameOfBank = row[BANK_NAME]?.text
        val beneficiaryAccNum = row[BENEFICIARY]?.text

        assertThat("No row found with '$paymentSys'", paymentSys, hasToString(bankDetails.paymentSystem+ " " + bankDetails.paymentSystemNumber))
        assertThat("No row found with '$recAddress'", recAddress, hasToString(bankDetails.recipientAddress))
        assertThat("No row found with '$recName'", recName, hasToString(bankDetails.recipientName))
        assertThat("No row found with '$addressOfBank'", addressOfBank, hasToString(bankDetails.bankAddress))
        assertThat("No row found with '$nameOfBank'", nameOfBank, hasToString(bankDetails.bankName))
        assertThat("No row found with '$beneficiaryAccNum'", beneficiaryAccNum, hasToString(bankDetails.beneficiary))
        return row[PAYMENT_SYSTEM]?.text ?: ""
    }

    @Action("check bank account")
    @Step("User checks bank account data in bank details table")
    fun checkBankAccount(
        paymentSystem: String,
        recipientName: String,
        recipientAddress: String,
        bankName: String,
        bankAddress: String,
        accNumber: String
    ): String {
        val  searchWord = paymentSystem.replace(Regex("[^\\d]"), "")
        e {
            sendKeys(search, searchWord)
            pressEnter(search)
        }
        val row = bankDetailsTable.find {
            it[PAYMENT_SYSTEM]?.text == paymentSystem
        } ?: error("Can't find row with payment system '$paymentSystem'")

        val paymentSys = row[PAYMENT_SYSTEM]?.text
        val recName = row[RECIPIENT_NAME]?.text
        val recAddress = row[RECIPIENT_ADDRESS]?.text
        val addressOfBank = row[BANK_ADDRESS]?.text
        val nameOfBank = row[BANK_NAME]?.text
        val beneficiaryAccNum = row[BENEFICIARY]?.text

        assertThat("No row found with '$paymentSys'", paymentSys, hasToString(paymentSystem))
        assertThat("No row found with '$recAddress'", recAddress, hasToString(recipientAddress))
        assertThat("No row found with '$recName'", recName, hasToString(recipientName))
        assertThat("No row found with '$addressOfBank'", addressOfBank, hasToString(bankAddress))
        assertThat("No row found with '$nameOfBank'", nameOfBank, hasToString(bankName))
        assertThat("No row found with '$beneficiaryAccNum'", beneficiaryAccNum, hasToString(accNumber))
        return row[PAYMENT_SYSTEM]?.text ?: ""
    }

    @Action("check bank account with all fields filled")
    @Step("User checks bank account data in bank details table")
    fun checkBankAccountAllFields(bankDetails: BankDetails): String {
        e {
            sendKeys(search, bankDetails.bankName)
            pressEnter(search)
        }
        val row = bankDetailsTable.find {
            it[BANK_NAME]?.text == bankDetails.bankName
        } ?: error("Can't find row with bank name '$bankDetails.bankName'")

        val paymentSys = row[PAYMENT_SYSTEM]?.text
        val recName = row[RECIPIENT_NAME]?.text
        val recAddress = row[RECIPIENT_ADDRESS]?.text
        val addressOfBank = row[BANK_ADDRESS]?.text
        val nameOfBank = row[BANK_NAME]?.text
        val beneficiaryAccNum = row[BENEFICIARY]?.text
        val bankDet = row[BANK_DETAILS]?.text
        val corrBank = row[CORRESPONDENT_BANK]?.text
        val corrAccount = row[CORRESPONDENT_ACCOUNT]?.text
        val inf = row[ADDITIONAL_INFORMATION]?.text

        assertThat("No row found with '$paymentSys'", paymentSys, hasToString(bankDetails.paymentSystem+ " " + bankDetails.paymentSystemNumber))
        assertThat("No row found with '$recAddress'", recAddress, hasToString(bankDetails.recipientAddress))
        assertThat("No row found with '$recName'", recName, hasToString(bankDetails.recipientName))
        assertThat("No row found with '$addressOfBank'", addressOfBank, hasToString(bankDetails.bankAddress))
        assertThat("No row found with '$nameOfBank'", nameOfBank, hasToString(bankDetails.bankName))
        assertThat("No row found with '$beneficiaryAccNum'", beneficiaryAccNum, hasToString(bankDetails.beneficiary))
        assertThat("No row found with '$bankDet'", bankDet, hasToString(bankDetails.bankDetails))
        assertThat("No row found with '$corrBank'", corrBank, hasToString(bankDetails.correspondentBank))
        assertThat("No row found with '$corrAccount'", corrAccount, hasToString(bankDetails.correspondentAccount))
        assertThat("No row found with '$inf'", inf, hasToString(bankDetails.information))
        return row[PAYMENT_SYSTEM]?.text ?: ""
    }

    @Action("choose record")
    @Step("Admin choose existing record on the table")
    fun chooseRecord(bankName: String){
        e {
            sendKeys(search, bankName)
            pressEnter(search)
        }
        val row = bankDetailsTable.find {
            it[BANK_NAME]?.text == bankName
        }?.get(BANK_NAME)?.to<Button>("Record $bankName")
            ?: error("Can't find row with bank name '$bankName'")
        e {
            click(row)
        }
    }

    @Step("Click delete icon of {bankName}")
    fun clickDeleteIcon(bankName: String) {
        val deleteIcon = wait {
            untilPresented<WebElement>(By.xpath("//td[contains(text(), '${bankName}')]/ancestor::tr//mat-icon[contains(text(), 'delete')]"))
        }.to<Button>("Employee '$bankName'")
        e {
            click(deleteIcon)
        }
    }

    @Action("delete all bank details with payment system {paymentSystem}")
    fun deleteAllBankDetailsWithPaymentSystem(paymentSystem: String){
        e {
            search.delete()
            sendKeys(search, paymentSystem)
            pressEnter(search)
        }
        while (check {
                isElementPresented(delete)
            }){
            e{
                click(delete)
                wait {
                    until("dialog add bank account is gone", 15) {
                        check {
                            isElementPresented(yes)
                        }
                    }
                }
                click(yes)
                wait {
                    until("dialog add bank account is gone", 15) {
                        check {
                            isElementGone(yes)
                        }
                    }
                }
            }
        }
        search.delete()
    }

    @Step("clear test data for Bank Details")
    fun clearTestData(){
        deleteAllBankDetailsWithPaymentSystem("044525976")
        deleteAllBankDetailsWithPaymentSystem("UBSWCHZH")
        deleteAllBankDetailsWithPaymentSystem("CH9300762011623852957")
        deleteAllBankDetailsWithPaymentSystem("026013356")
    }
}