package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock

@PageUrl("/")
class AtmViewerAdminPage(driver: WebDriver): AtmAdminPage(driver) {

    @Name("Invite tab")
    @FindBy(xpath = "//span[contains(text(), 'Invites')]")
    lateinit var inviteTab: Button

    @Name("Employees approval tab")
    @FindBy(xpath = "//span[contains(text(), 'Employees approval')]")
    lateinit var employeesApprovalTab: Button

    @Name("Payments tab")
    @FindBy(xpath = "//span[contains(text(), 'Payments')]")
    lateinit var paymentsTab: Button

    @Name("Fiat withdraw tab")
    @FindBy(xpath = "//span[contains(text(), 'Fiat withdraw')]")
    lateinit var fiatWithdrawTab: Button

    @Name("Companies tab")
    @FindBy(xpath = "//span[contains(text(), 'Companies')]")
    lateinit var companiesTab: Button

    @Name("Bank details tab")
    @FindBy(xpath = "//span[contains(text(), 'Bank details')]")
    lateinit var bankDetailsTab: Button

    @Name("Tokens tab")
    @FindBy(xpath = "//span[contains(text(), 'Tokens')]")
    lateinit var tokensTab: Button

    @Name("Register of issuers tab")
    @FindBy(xpath = "//span[contains(text(), 'Register of issuers')]")
    lateinit var registerOfIssuersTab: Button

    @Name("Financial data sources management tab")
    @FindBy(xpath = "//span[contains(text(), 'Financial data sources management')]")
    lateinit var financialDataSourcesManagementTab: Button

    @Name("General settings tab")
    @FindBy(xpath = "//span[contains(text(), 'General settings')]")
    lateinit var generalSettingsTab: Button

    @Name("Streaming settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Streaming settings')]")
    lateinit var streamingSettingsTab: Button

    @Name("RFQ settings tab")
    @FindBy(xpath = "//span[contains(text(), 'RFQ settings')]")
    lateinit var rFQSettingsTab: Button

    @Name("Blocktrade settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Blocktrade settings')]")
    lateinit var blocktradeSettingsTab: Button

    @Name("TVE settings tab")
    @FindBy(xpath = "//span[contains(text(), 'TVE settings')]")
    lateinit var tVESettingsTab: Button

    @Name("Access right tab")
    @FindBy(xpath = "//span[contains(text(), 'Access right')]")
    lateinit var accessRightTab: Button

    @Name("User management tab")
    @FindBy(xpath = "//span[contains(text(), 'User management')]")
    lateinit var userManagementTab: Button

    @Name("Nodes management tab")
    @FindBy(xpath = "//span[contains(text(), 'Nodes management')]")
    lateinit var nodesManagementTab: Button

    @Name("Translate tab")
    @FindBy(xpath = "//span[contains(text(), 'Translate')]")
    lateinit var translateTab: Button

    @Name("KYC management tab")
    @FindBy(xpath = "//span[contains(text(), 'KYC management')]")
    lateinit var kYCManagementTab: Button

    @Name("Send invite button")
    @FindBy(xpath = "//span[contains(text(), 'Send invite')]")
    lateinit var sendInviteBtn: Button

    @Name("Add payment button")
    @FindBy(xpath = "//span[contains(text(), 'Add payment')]")
    lateinit var addPaymentBtn: Button

    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(), 'ADD')]")
    lateinit var addBtn: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(), 'EDIT')]")
    lateinit var editBtn: Button

    @Name("Update date source button")
    @FindBy(xpath = "//span[contains(text(), 'Update date source')]")
    lateinit var updateDateSourceBtn: Button

    @Name("Add user button")
    @FindBy(xpath = "//span[contains(text(), 'Add user')]")
    lateinit var addUserBtn: Button

//    private val tabsNotVisable = mapOf(
//        "Send invite" to sendInviteBtn, //Invites
//        "Employees approval" to same, //Employees approval
//        "Add payment" to addPaymentBtn, //Payments
//        "Fiat withdraw" to same, //Fiat withdraw
//        "ADD" to addBtn, //Companies
//        "ADD" to addBtn, //Bank details
//        "ADD" to addBtn, //Tokens
//        "EDIT" to editBtn, //Register of issuers
//        "Update date source" to updateDateSourceBtn, //Financial data sources management
//        "OTF general settings" to disabled, // другой метод (одинаковые во всем остальном) //OTF general settings
//        "ADD" to addBtn, //Streaming settings
//        "ADD" to addBtn, //Rfq settings
//        "ADD" to addBtn, //Blocktrade settings
//        "ADD" to addBtn, //Tve settings
//        "Add user" to addUserBtn, //Access right
//        "EDIT" to editBtn, //Users list
//        "EDIT" to editBtn, //Nodes management
//        "Available languages" to same, //Available languages
//        "KYC management" to same //KYC management
//    )

    private val tabs = mapOf(
        "Invites" to inviteTab,
        "Employees approval" to employeesApprovalTab,
        "Payments" to paymentsTab,
        "Fiat withdraw" to fiatWithdrawTab,
        "Companies" to companiesTab,
        "Bank details" to bankDetailsTab,
        "Tokens" to tokensTab,
        "Register of issuers" to registerOfIssuersTab,
        "Financial data sources management" to financialDataSourcesManagementTab,
        "OTF general settings" to generalSettingsTab,
        "Streaming settings" to streamingSettingsTab,
        "Rfq settings" to rFQSettingsTab,
        "Blocktrade settings" to blocktradeSettingsTab,
        "Tve settings" to tVESettingsTab,
        "Access right" to accessRightTab,
        "Users list" to userManagementTab,
        "Nodes management" to nodesManagementTab,
        "Available languages" to translateTab,
        "KYC management" to kYCManagementTab
    )

    @Step("Check all tabs are changed for admin with viewer role")
    fun checkAllTabsAreChangedForAdminWithViewerRole() {
        for ((tab, page) in tabs) {
            e {
                click(page)
            }
            assert {
                elementContainingTextPresented(tab) // Проверка заголовка страницы
                elementContainingTextNotPresented(tab) // Проверка видимости для админа с ролью VIEWER
            }
        }
    }
}
