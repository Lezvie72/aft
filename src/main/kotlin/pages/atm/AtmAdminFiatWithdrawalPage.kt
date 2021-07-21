package pages.atm

import io.qameta.allure.Step
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminFiatWithdrawalPage.StatusType.*
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import utils.helpers.to

@PageUrl("/withdraw")
open class AtmAdminFiatWithdrawalPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val WALLET_ADDRESS = "Wallet address"
        const val WALLET_OWNER = "Wallet owner"
        const val ACCOUNT_OWNER = "Account owner"
        const val STATUS = "Status"
        const val AMOUNT = "Amount"
        const val NOTE = "Note"
    }

    enum class StatusType {
        WITHDRAWAL, REJECT
    }

    @Name("Withdraw table")
    @FindBy(css = "sdex-withdraw")
    lateinit var withdrawTable: SdexTable

    @Name("Cashout dialog")
    @FindBy(xpath = "//sdex-info-cashout-dialog")
    lateinit var cashOutDialog: Button
    //region Bank Details Form

    @Name("Withdrawal")
    @FindBy(xpath = "//span[contains(text(),'Withdrawal')]")
    lateinit var withdrawal: Button

    @Name("Reject")
    @FindBy(xpath = "//span[contains(text(),'Reject')]")
    lateinit var reject: Button

    //endregion
    @Name("Row of request")
    @FindBy(xpath = "//tr[@role='row'][.//td[@role='gridcell']]")
    lateinit var requestRow: TextBlock

    @Name("Delete button")
    @FindBy(xpath = "//button//mat-icon[contains(text(), 'delete')]")
    lateinit var deleteBtn: Button

    @Name("Status NEW")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'NEW')] and *[contains(@class, 'mat-cell cdk-cell cdk-column-status')]]")
    lateinit var statusNew: TextBlock

    @Name("Status REJECTED")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'REJECTED')] and *[contains(@class, 'mat-cell cdk-cell cdk-column-status')]]")
    lateinit var statusRejected: TextBlock

    @Name("Status EXECUTED")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'EXECUTED')] and *[contains(@class, 'mat-cell cdk-cell cdk-column-status')]]")
    lateinit var statusExecuted: TextBlock

    @Name("Official name")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), 'Wallet owner')]]")
    lateinit var officialName: TextBlock

    @Name("Wallet address")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), 'Wallet address')]]")
    lateinit var walletAddress: TextBlock

    @Name("Account owner")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), 'Account owner')]]")
    lateinit var ownerAccount: TextBlock

    @Name("ID")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), '#')]]")
    lateinit var iDInWindow: TextBlock

    @Name("Amount")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), 'Amount')]]")
    lateinit var amountValueRow: TextBlock

    @Name("Note")
    @FindBy(xpath = "//mat-form-field[.//*[contains(@class,'mat-input-element')]][.//*[contains(text(), 'Note')]]")
    lateinit var noteRow: TextBlock

    @Action("add Status To Withdrawal Order")
    @Step("Admin add Status To Withdrawal Order")
    fun addStatusToWithdrawalOrder(withdrawalAmount: String, statusType: StatusType) {
        val row = withdrawTable.find {
            it[AMOUNT]?.text == withdrawalAmount
        }?.get(AMOUNT)?.to<Button>("Ticker symbol $withdrawalAmount")
            ?: error("Row with Ticker symbol $withdrawalAmount not found in table")

        e {
            click(row)
            when (statusType) {
                WITHDRAWAL -> click(withdrawal)
                REJECT -> click(reject)
            }
        }
        wait {
            until("dialog for cashout is gone", 20) {
                check {
                    isElementGone(cashOutDialog)
                }
            }
        }
    }

    @Action("check Status for Withdrawal Order")
    @Step("Admin check Status for Withdrawal Order")
    fun checkStatusToWithdrawalOrder(withdrawalAmount: String, expectedStatus: String) {
        val row = withdrawTable.find {
            it[AMOUNT]?.text == withdrawalAmount
        }?: error("Row with Ticker symbol $withdrawalAmount not found in table")

        val status = row[STATUS]?.text?.toLowerCase()

        MatcherAssert.assertThat(
            "No row found with '$status'",
            status,
            Matchers.hasToString(expectedStatus.toLowerCase())
        )

    }

    @Step("User checks that the fiat money withdrawal statuses are displayed")
    fun checkingTheDisplayOfFiatMoneyWithdrawalStatuses() {
        val list = listOf( requestRow, statusNew, statusRejected, statusExecuted )
        for (i in list)
            check { assertTrue(isElementPresented(i)) }
        assert { elementNotPresentedWithCustomTimeout(deleteBtn, 1) }
    }

    @Step("User checks that the elements are displayed in the popup for fiat withdraw section")
    fun checksThatTheElementsAreDisplayedInThePopupForFiatWithdrawSection() {
        e { click(requestRow) }
        val list = listOf(officialName, walletAddress, ownerAccount, iDInWindow, amountValueRow, noteRow,
            withdrawal, reject)
        for (i in list)
            check { assertTrue(isElementPresented(i)) }
        assert {
            elementEnabled(withdrawal)
            elementEnabled(reject)
        }
    }
}
