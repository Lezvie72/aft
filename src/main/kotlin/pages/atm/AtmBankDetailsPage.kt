package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/bank-details")
open class AtmBankDetailsPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val BANK_DETAILS = "Bank details"
        const val RECIPIENT_NAME = "Recipient Name"
        const val RECIPIENT_ADDRESS = "Recipient Address"
        const val BANK_NAME = "Bank Name"
        const val BANK_ADDRESS = "Bank Address"
        const val BENEFICIARY = "Beneficiary’s Acc. №"
        const val PAYMENT_SYSTEM = "Payment system"
    }

    @Name("Bank details table")
    @FindBy(css = "sdex-bank-details")
    lateinit var bankDetailsTable: SdexTable

    @Name("Search")
    @FindBy(xpath = "//mat-form-field//input")
    lateinit var search: TextInput

    @Action("check bank account")
    @Step("User checks bank account data in bank details table")
    fun checkBankAccount(
        system: String,
        paymentSystem: String,
        recipientName: String,
        recipientAddress: String,
        bankName: String,
        bankAddress: String,
        accNumber: String

    ): String {
        e {
            sendKeys(search, system)
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
        return row.get(PAYMENT_SYSTEM)?.text ?: ""
    }
}