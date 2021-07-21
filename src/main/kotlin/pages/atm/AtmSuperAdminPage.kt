package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.Link
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/home")
@PageName("Super admin page")
class AtmSuperAdminPage(driver: WebDriver) : AtmAdminPage(driver)  {

    @Name("Invite tab")
    @FindBy(xpath = "//span[contains(text(), 'Invites')]")
    lateinit var inviteTab: Button

    @Name("Send invite button")
    @FindBy(xpath = "//span[contains(text(), 'Send invite')]")
    lateinit var sendInviteBtn: Button

    @Name("Send invite window")
    @FindBy(xpath = "//h1[contains(text(), 'Send invite')]")
    lateinit var sendInviteWindow: TextBlock

    @Name("Employees approval tab")
    @FindBy(xpath = "//span[contains(text(), 'Employees approval')]")
    lateinit var employeesApprovalTab: Button

    @Name("Employees approval input")
    @FindBy(xpath = "(//div[contains(@class,'mat-form-field-wrapper')]//mat-icon[contains(@role, 'img')])[1]")
    lateinit var employeesApprovalInput: TextBlock

    @Name("Employees approval calendar")
    @FindBy(xpath = "//mat-calendar")
    lateinit var employeesApprovalCalendar: Button

    @Name("Payments tab")
    @FindBy(xpath = "//span[contains(text(), 'Payments')]")
    lateinit var paymentsTab: Button

    @Name("Add payment button")
    @FindBy(xpath = "//span[contains(text(), 'Add payment')]")
    lateinit var addPaymentBtn: Button

    @Name("Manual deposit window")
    @FindBy(xpath = "//h1[contains(text(), 'Manual deposit')]")
    lateinit var manualDepositWindow: TextBlock

    @Name("Fiat withdraw tab")
    @FindBy(xpath = "//span[contains(text(), 'Fiat withdraw')]")
    lateinit var fiatWithdrawTab: Button

    @Name("Withdraw info window")
    @FindBy(xpath = "//h1[contains(text(), 'Withdraw info')]")
    lateinit var withdrawInfoWindow: TextBlock

    @Name("Companies tab")
    @FindBy(xpath = "//span[contains(text(), 'Companies')]")
    lateinit var companiesTab: Button

    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(), 'ADD')]")
    lateinit var addBtn: Button

    @Name("Add company window")
    @FindBy(xpath = "//h1[contains(text(), 'Add Company')]")
    lateinit var addCompanyWindow: TextBlock

    @Name("Custodian fee tab")
    @FindBy(xpath = "//span[contains(text(), 'Custodian fee')]")
    lateinit var custodianFeeTab: Button

    @Name("Set custodian fee button")
    @FindBy(xpath = "//span[contains(text(), 'Set custodian fee')]")
    lateinit var setCustodianFeeBtn: Button

    @Name("Set custodian fee window")
    @FindBy(xpath = "//h1[contains(text(), 'Set custodian fee')]")
    lateinit var setCustodianFeeWindow: TextBlock

    @Name("Bank details tab")
    @FindBy(xpath = "//span[contains(text(), 'Bank details')]")
    lateinit var bankDetailsTab: Button

    @Name("Bank details window")
    @FindBy(xpath = "//h1[contains(text(), 'Add Bank details')]")
    lateinit var bankDetailsWindow: TextBlock

    @Name("Tokens tab")
    @FindBy(xpath = "//span[contains(text(), 'Tokens')]")
    lateinit var tokensTab: Button

    @Name("Add Token window")
    @FindBy(xpath = "//h1[contains(text(), 'Add Token')]")
    lateinit var addTokenWindow: TextBlock

    @Name("Register of issuers tab")
    @FindBy(xpath = "//span[contains(text(), 'Register of issuers')]")
    lateinit var registerOfIssuersTab: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(), 'EDIT')]")
    lateinit var editBtn: Button

    @Name("Edit issuer info window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit issuer info')]")
    lateinit var editIssuerInfoWindow: TextBlock

    @Name("Primary deals tab")
    @FindBy(xpath = "//span[contains(text(), 'Primary deals')]")
    lateinit var primaryDealsTab: Button

    @Name("Primary deals window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit token operation')]")
    lateinit var primaryDealsWindow: TextBlock

    @Name("Financial data sources management tab")
    @FindBy(xpath = "//span[contains(text(), 'Financial data sources management')]")
    lateinit var financialDataSourcesManagementTab: Button

    @Name("Update date source button")
    @FindBy(xpath = "//span[contains(text(), 'Update date source')]")
    lateinit var updateDateSourceBtn: Button

    @Name("Update date source CONFIRM button")
    @FindBy(xpath = "//span[contains(text(), 'CONFIRM')]")
    lateinit var updateDateSourceCONFIRMBtn: TextBlock

    @Name("Streaming settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Streaming settings')]")
    lateinit var streamingSettingsTab: Button

    @Name("Add trading pair window")
    @FindBy(xpath = "//h1[contains(text(), 'Add trading pair')]")
    lateinit var addTradingPairWindow: TextBlock

    @Name("Add TVE rule window")
    @FindBy(xpath = "//h1[contains(text(), 'Add TVE rule')]")
    lateinit var addTVERuleWindow: TextBlock

    @Name("Access right tab")
    @FindBy(xpath = "//span[contains(text(), 'Access right')]")
    lateinit var accessRightTab: Button

    @Name("Add user button")
    @FindBy(xpath = "//span[contains(text(), 'Add user')]")
    lateinit var addUserBtn: Button

    @Name("Add user window")
    @FindBy(xpath = "//h1[contains(text(), 'Add new user')]")
    lateinit var addUserWindow: TextBlock

    @Name("User management tab")
    @FindBy(xpath = "//span[contains(text(), 'User management')]")
    lateinit var userManagementTab: Button

    @Name("Edit user window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit user info')]")
    lateinit var editUserWindow: TextBlock

    @Name("User list row")
    @FindBy(xpath = "//tbody//td[1]")
    lateinit var userListRow: Button

    @Name("Logs tab")
    @FindBy(xpath = "//span[contains(text(), 'Finstar logs')]")
    lateinit var logsTab: Button

    @Name("Update date from")
    @FindBy(xpath = "(//*[contains(@class,'mat-datepicker-toggle-default-icon')])[1]")
    lateinit var updatDateFromInput: TextBlock

    @Name("Translate tab")
    @FindBy(xpath = "//span[contains(text(), 'Translate')]")
    lateinit var translateTab: Button

    @Name("KYC management tab")
    @FindBy(xpath = "//span[contains(text(), 'KYC management')]")
    lateinit var kYCManagementTab: Button

    @Name("Nodes management tab")
    @FindBy(xpath = "//span[contains(text(), 'Nodes management')]")
    lateinit var nodesManagementTab: Button

    @Name("Edit node window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit node')]")
    lateinit var editNodeWindow: TextBlock

    @Name("General settings tab")
    @FindBy(xpath = "//span[contains(text(), 'General settings')]")
    lateinit var generalSettingsTab: Button

    @Name("RFQ settings tab")
    @FindBy(xpath = "//span[contains(text(), 'RFQ settings')]")
    lateinit var rFQSettingsTab: Button

    @Name("Blocktrade settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Blocktrade settings')]")
    lateinit var blocktradeSettingsTab: Button

    @Name("TVE settings tab")
    @FindBy(xpath = "//span[contains(text(), 'TVE settings')]")
    lateinit var tVESettingsTab: Button

    @Name("KYC agent response window")
    @FindBy(xpath = "//h3[contains(text(), 'KYC agent response')]")
    lateinit var kYCAgentResponseWindow: TextBlock

    @Name("Update Date From")
    @FindBy(xpath = "//input[@formcontrolname='updateDateFrom']")
    lateinit var updateDateFrom: TextInput

    @Name("Update Date To")
    @FindBy(xpath = "//input[@formcontrolname='updateDateTo']")
    lateinit var updateDateTo: TextInput

    @Name("English language")
    @FindBy(xpath = "(//label[contains(@class, 'mat-radio-label')])[1]")
    lateinit var engBtn: Button

    @Name("Rfq link")
    @FindBy(xpath = "//a[contains(text(), 'Rfq settings')]")
    lateinit var rFQLink: Link

    @Name("Rfq page")
    @FindBy(xpath = "//h1[contains(text(), 'Rfq settings')]")
    lateinit var rFQPage: TextBlock

    @Step("Checking every links works and opens in edit mode for super admin")
    fun allTabsAreDisplayedAndOpensInEditModeForSuperAdmin() {
        val settings = mapOf(
            "Invite" to arrayOf(inviteTab, sendInviteBtn, sendInviteWindow),
            "Employees Approval" to arrayOf(employeesApprovalTab, employeesApprovalInput, employeesApprovalCalendar),
            "Payments" to arrayOf(paymentsTab, addPaymentBtn, manualDepositWindow),
            "Fiat withdraw" to arrayOf(fiatWithdrawTab, userListRow, withdrawInfoWindow),
            "Companies" to arrayOf(companiesTab, addBtn, addCompanyWindow),
            "Custodian fee" to arrayOf(custodianFeeTab, setCustodianFeeBtn, setCustodianFeeWindow, userListRow),
            "Bank details" to arrayOf(bankDetailsTab, addBtn, bankDetailsWindow),
            "Tokens" to arrayOf(tokensTab, addBtn, addTokenWindow),
            "Register of issuers" to arrayOf(registerOfIssuersTab, editBtn, editIssuerInfoWindow, userListRow),
            "Primary deals" to arrayOf(primaryDealsTab, editBtn, primaryDealsWindow, userListRow),
            "Financial data sources management" to arrayOf(financialDataSourcesManagementTab, updateDateSourceBtn, updateDateSourceCONFIRMBtn),
            "General settings" to arrayOf(generalSettingsTab, rFQLink, rFQPage),
            "Streaming settings" to arrayOf(streamingSettingsTab, addBtn, addTradingPairWindow),
            "RFQ settings" to arrayOf(rFQSettingsTab, addBtn, addTokenWindow),
            "Blocktrade settings" to arrayOf(blocktradeSettingsTab, addBtn, addTokenWindow),
            "TVE settings" to arrayOf(tVESettingsTab, addBtn, addTVERuleWindow),
            "Access right" to arrayOf(accessRightTab, addUserBtn, addUserWindow),
            "User Management" to arrayOf(userManagementTab, editBtn, editUserWindow, userListRow),
            "Logs" to arrayOf(logsTab, updatDateFromInput, employeesApprovalCalendar),
            "Nodes Management" to arrayOf(nodesManagementTab, editBtn, editNodeWindow, userListRow),
            "KYC Management" to arrayOf(kYCManagementTab, userListRow, kYCAgentResponseWindow),
            "Translate" to arrayOf(translateTab, engBtn, engBtn)
        )
        for ((key, value) in settings) {
            if ((value[0] == custodianFeeTab)&&(value[0] == registerOfIssuersTab)
            &&(value[0] == primaryDealsTab)&&(value[0] == userManagementTab)
                &&(value[0] == logsTab)&&(value[0] == nodesManagementTab)) {
                e {
                    click(value[0] as WebElement)
                    click(value[1] as WebElement)
                    click(value[3] as WebElement)
                }
            } else {
                e {
                    click(value[0] as WebElement)
                    click(value[1] as WebElement)
                }
            }
            check {
                isElementPresented(value[2] as WebElement)
            }
            driver.navigate().refresh()
            Thread.sleep(2000)
            e {
                click(inviteTab)
            }
        }
    }

}