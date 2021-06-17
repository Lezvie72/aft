package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button

@PageUrl("/")
class AtmFinanceManagerAdminPage(driver: WebDriver): AtmAdminPage(driver) {

    @Name("Payments tab")
    @FindBy(xpath = "//span[contains(text(), 'Payments')]")
    lateinit var paymentsTab: Button

    @Name("Fiat withdraw tab")
    @FindBy(xpath = "//span[contains(text(), 'Fiat withdraw')]")
    lateinit var fiatWithdrawTab: Button

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

    @Name("Enable button")
    @FindBy(xpath = "//label/div/input")
    lateinit var enableBtn: Button

    @Name("Primary deals tab")
    @FindBy(xpath = "//span[contains(text(), 'Primary deals')]")
    lateinit var primaryDealsTab: Button

    private val tabs = mapOf(
        "Payments" to arrayOf<WebElement?>(paymentsTab, addPaymentBtn),
        "Fiat withdraw" to arrayOf<WebElement?>(fiatWithdrawTab, null),
        "Bank details" to arrayOf<WebElement?>(bankDetailsTab, addBtn),
        "Tokens" to arrayOf<WebElement?>(tokensTab, addBtn),
        "Register of issuers" to arrayOf<WebElement?>(registerOfIssuersTab, editBtn),
        "Primary deals" to arrayOf<WebElement?>(primaryDealsTab, editBtn),
        "Financial data sources management" to arrayOf<WebElement?>(financialDataSourcesManagementTab, null),
        "OTF general settings" to arrayOf<WebElement?>(generalSettingsTab, enableBtn),
        "Streaming settings" to arrayOf<WebElement?>(streamingSettingsTab, addBtn),
        "Rfq settings" to arrayOf<WebElement?>(rFQSettingsTab, addBtn),
        "Blocktrade settings" to arrayOf<WebElement?>(blocktradeSettingsTab, addBtn),
        "Tve settings" to arrayOf<WebElement?>(tVESettingsTab, addBtn)
    )

    @Step("Check all tabs are changed for admin with finance manager role")
    fun checkAllTabsAreChangedForAdminWithFinanceManagerRole() {
        for ((tab, page) in tabs) {
            e {
                click(page[0]!!)
            }
            assert {
                elementContainingTextPresented(tab) // Проверка заголовка страницы
                if (page[1] != null) {
                    elementPresented(page[1]!!) // Проверка видимости для админа с ролью FINANCE MANAGER
                }
                if (tab == "Financial data sources management") {
                    elementNotPresented(updateDateSourceBtn)
                }
            }
        }
    }
}
