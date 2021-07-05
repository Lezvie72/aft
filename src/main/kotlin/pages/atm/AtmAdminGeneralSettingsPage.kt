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

    @Name("Rfq toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'RFQ')]] //label")
    lateinit var rfqToggleAlt: CheckBox

    @Name("Rfq string")
    @FindBy(xpath = "//div/mat-panel-title[contains(text(), 'RFQ')]")
    lateinit var rfqStringAlt: CheckBox

    @Name("Rfq toggle status")
    @FindBy(xpath = "//div[*[contains(text(), 'RFQ')]] //label/span")
    lateinit var rfqToggleStatus: TextBlock

    @Name("Streaming toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Streaming')]/ancestor::div//mat-slide-toggle/label")
    lateinit var streamingToggle: CheckBox

    @Name("Streaming toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'Streaming')]] //label")
    lateinit var streamingToggleAlt: CheckBox

    @Name("Streaming toggle status")
    @FindBy(xpath = "//div[*[contains(text(), 'Streaming')]] //label/span")
    lateinit var streamingToggleStatus: TextBlock

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Blocktrade')]/ancestor::div//mat-slide-toggle/label")
    lateinit var blocktradeToggle: CheckBox

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'Blocktrade')]] //label")
    lateinit var blocktradeToggleAlt: CheckBox

    @Name("Blocktrade toggle status")
    @FindBy(xpath = "//div[*[contains(text(), 'Blocktrade')]] //label/span")
    lateinit var blocktradeToggleStatus: TextBlock

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

    @Step("Checking the displaying title on page")
    fun pageIsDisplayed() {
        assert {
            elementContainingTextPresented("OTF general settings")
            elementPresented(rfqToggleAlt)
            elementPresented(streamingToggleAlt)
            elementPresented(blocktradeToggleAlt)
        }
    }

    @Step("Checking toggle's status and corresponding links")
    fun checkingTogglesStatusAndCorrespondingLinks(rfqToggleStatusText: String, streamingToggleStatusText: String, blocktradeToggleStatusText: String) {
        val settings = mapOf(
            "RFQ" to arrayOf(rfqToggleStatusText, rfqToggleStatus, rfqVisibleLink),
            "Streaming" to arrayOf(streamingToggleStatusText, streamingToggleStatus, streamingVisibleLink),
            "Blocktrade" to arrayOf(blocktradeToggleStatusText, blocktradeToggleStatus, blocktradeVisibleLink)
        )
        Thread.sleep(2000)
        for ((key, value) in settings) {
            assert {
                elementPresented(value[1] as WebElement)
                elementContainsText(value[1] as WebElement, value[0] as String)
                when (value[0]) {
                    "enable" -> elementPresentedWithCustomTimeout(value[2] as WebElement, 1)
                    "disable" -> elementNotPresentedWithCustomTimeout(value[2] as WebElement, 1)
                }
            }
        }
    }

    @Step("Change status of streaming settings then check")
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
                if(!isElementPresented(value[0] as WebElement, 1)) {
                    e { click(value[1] as WebElement) }
                }
            }
        }
    }

}