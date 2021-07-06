package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock

@PageUrl("/otc-settings")
@PageName("Admin general settings page")
class AtmAdminGeneralSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    @Name("Rfq toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'RFQ')]/ancestor::div//mat-slide-toggle/label")
    lateinit var rfqToggle: CheckBox

    @Name("Rfq string")
    @FindBy(xpath = "//div/mat-panel-title[contains(text(), 'RFQ')]")
    lateinit var rfqStringAlt: CheckBox

    @Name("General settings tab")
    @FindBy(xpath = "//div[*[contains(text(), 'General settings')]]")
    lateinit var generalSettingsTab: TextBlock

    @Name("RFQ settings tab")
    @FindBy(xpath = "//div[*[contains(text(), 'RFQ settings')]]")
    lateinit var rfqSettingsTab: TextBlock

    @Name("Blocktrade settings tab")
    @FindBy(xpath = "//div[*[contains(text(), 'Blocktrade settings')]]")
    lateinit var blocktradeSettingsTab: TextBlock

    @Name("TVE settings tab")
    @FindBy(xpath = "//div[*[contains(text(), 'TVE settings')]]")
    lateinit var tveSettingsTab: TextBlock

    @Name("Streaming settings tab")
    @FindBy(xpath = "//div[*[contains(text(), 'Streaming settings')]]")
    lateinit var streamingSettingsTab: TextBlock

    @Name("Streaming toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Streaming')]/ancestor::div//mat-slide-toggle/label")
    lateinit var streamingToggle: CheckBox

    @Name("Streaming toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'Streaming')]] //label")
    lateinit var streamingToggleAlt: CheckBox

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Blocktrade')]/ancestor::div//mat-slide-toggle/label")
    lateinit var blocktradeToggle: CheckBox

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'Blocktrade')]] //label")
    lateinit var blocktradeToggleAlt: CheckBox

    @Name("Rfq link")
    @FindBy(xpath = "(//a[@href='/rfq-settings'])[2]")
    lateinit var rfqLink: Button

    @Name("Streaming link")
    @FindBy(xpath = "(//a[@href='/streaming-settings'])[2]")
    lateinit var streamingLink: Button

    @Name("blocktrade link")
    @FindBy(xpath = "(//a[@href='/p2p-settings'])[2]")
    lateinit var blocktradeLink: Button

    @Name("Rfq visible link")
    @FindBy(xpath = "//mat-expansion-panel[.//mat-panel-title[contains(text(),'RFQ')]]//div[@style='visibility: visible;']")
    lateinit var rfqVisibleLink: Button

    @Name("Streaming visible link")
    @FindBy(xpath = "//mat-expansion-panel[.//mat-panel-title[contains(text(),'Streaming')]]//div[@style='visibility: visible;']")
    lateinit var streamingVisibleLink: Button

    @Name("blocktrade visible link")
    @FindBy(xpath = "//mat-expansion-panel[.//mat-panel-title[contains(text(),'Blocktrade')]]//div[@style='visibility: visible;']")
    lateinit var blocktradeVisibleLink: Button

    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(), 'ADD')]")
    lateinit var addBtn: Button

    @Name("Add window")
    @FindBy(xpath = "//mat-dialog-container[contains(@class,'mat-dialog-container')]")
    lateinit var addWindow: TextBlock

    @Name("Rfq toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'RFQ')]] //label")
    lateinit var rfqToggleAlt: CheckBox

    @Step("Checking the displaying title on page")
    fun pageIsDisplayed() {
        assert {
            elementContainingTextPresented("OTF general settings")
            elementPresented(rfqToggleAlt)
            elementPresented(streamingToggleAlt)
            elementPresented(blocktradeToggleAlt)
        }
    }

    @Step("Change statuses of general settings then check")
    fun changeToggleStatus(toggle1: String? = null, toggle2: String? = null, toggle3: String? = null) {
        val toggles = arrayOf(toggle1, toggle2, toggle3)
        for (toggle in toggles) {
            e {
                when (toggle) {
                    "RFQ" -> {click(rfqStringAlt)}
                    "Streaming" -> {click(streamingToggleAlt)}
                    "Blocktrade" -> {click(blocktradeToggleAlt)}
                }
            }
            Thread.sleep(2000)
        }
    }

    @Step("Checking toggle's status and switching to correct")
    fun checkingTogglesStatusAndSwitchingToCorrect() {
        val settings = mapOf(
            "RFQ" to arrayOf(rfqVisibleLink, rfqStringAlt),
            "Streaming" to arrayOf(streamingVisibleLink, streamingToggleAlt),
            "Blocktrade" to arrayOf(blocktradeVisibleLink, blocktradeToggleAlt)
        )
        for ((key, value) in settings) {
            check {
                if(!isElementPresented(value[0] as WebElement)) {
                    e { click(value[1] as WebElement) }
                }
            }
        }
    }

    @Step("Checking every links works and opens in edit mode")
    fun allTabsAreDisplayedAndOpensInEditMode() {
        val settings = mapOf(
            "RFQ" to arrayOf(rfqSettingsTab, addBtn, addWindow),
            "Streaming" to arrayOf(streamingSettingsTab, addBtn, addWindow),
            "Blocktrade" to arrayOf(blocktradeSettingsTab, addBtn, addWindow),
            "TVE" to arrayOf(tveSettingsTab, addBtn, addWindow)
        )
        for ((key, value) in settings) {
            e {
                click(value[0] as WebElement)
                click(value[1] as WebElement)
            }
            check {
                isElementPresented(value[2] as WebElement)
            }
            driver.navigate().refresh()
            Thread.sleep(2000)
            e {
                click(generalSettingsTab)
            }
        }
    }

    @Step("Checking every links works and opens in edit mode")
    fun allTabsAreDisplayedAndOpensInEditModeForAmlKycManager() {
        val settings = mapOf(
            "RFQ" to arrayOf(rfqSettingsTab, addBtn, addWindow),
            "Streaming" to arrayOf(streamingSettingsTab, addBtn, addWindow),
            "Blocktrade" to arrayOf(blocktradeSettingsTab, addBtn, addWindow),
            "TVE" to arrayOf(tveSettingsTab, addBtn, addWindow)
        )
        for ((key, value) in settings) {
            e {
                click(value[0] as WebElement)
                click(value[1] as WebElement)
            }
            check {
                isElementPresented(value[2] as WebElement)
            }
            driver.navigate().refresh()
            Thread.sleep(2000)
            e {
                click(generalSettingsTab)
            }
        }
    }

}