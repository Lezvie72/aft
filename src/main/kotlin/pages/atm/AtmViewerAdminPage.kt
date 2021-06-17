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

    @Name("Tokens text block")
    @FindBy(xpath = "//h1[contains(text(), 'Tokens')]")
    lateinit var tokensText: TextBlock

    @Name("Register of issuers text block")
    @FindBy(xpath = "//h1[contains(text(), 'Register of issuers')]")
    lateinit var registerOfIssuersText: TextBlock

    private val tabs = mapOf(
        "Invites" to inviteTab,
        "Employees approval" to employeesApprovalTab,
        "Payments" to paymentsTab,
        "Fiat withdraw" to fiatWithdrawTab,
        "Companies" to companiesTab,
        "Bank details" to bankDetailsTab,
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
        "KYC management" to kYCManagementTab,
        tokensText to tokensTab,
        registerOfIssuersText to registerOfIssuersTab
        //TODO: Дописать мапу
    )

    @Step("Check all tabs are changed for admin with viewer role")
    fun checkAllTabsAreChangedForAdminWithViewerRole() {
        for ((tab, page) in tabs) {
            e {
                click(page)
            }
//            if ((tab != String) in tabs) {
//            if (tab in tabs != "") {
//                assert {
//                    elementPresented({$tab})
//                }
//            } else {
//                assert {
//                    elementWithTextPresented(" $tab ")
//                }
//            }
            e {
                click(tokensTab)
            }
            assert {
                elementPresented(tokensText)
            }
            e {
                click(registerOfIssuersTab)
            }
            assert {
                elementPresented(registerOfIssuersText)
            }
        }
    }
}

//    @Step("Check all tabs are changed for admin with viewer role")
//    fun checkAllTabsAreChangedForAdminWithViewerRole() {
//        for ((tab, page) in tabs) {
//            e {
//                click(page)
//            }
//            assert {
//                elementWithTextPresented(" $tab ")
//            }
//        }
//        e {
//            click(tokensTab)
//        }
//        assert {
//            elementPresented(tokensText)
//        }
//        e {
//            click(registerOfIssuersTab)
//        }
//        assert {
//            elementPresented(registerOfIssuersText)
//        }
//    }
