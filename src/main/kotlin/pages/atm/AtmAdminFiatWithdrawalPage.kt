package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminFiatWithdrawalPage.StatusType.*
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
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
}