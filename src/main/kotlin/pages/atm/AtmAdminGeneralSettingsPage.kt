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

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    @Name("Rfq toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'RFQ')]/ancestor::div//mat-slide-toggle/label")
    lateinit var rfqToggle: CheckBox

    @Name("Rfq toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'RFQ')]] //label")
    lateinit var rfqToggleAlt: CheckBox

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

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//div[*[contains(text(), 'Blocktrade')]] //label/span")
    lateinit var blocktradeToggleStatus: TextBlock

    @Name("Rfq link")
    @FindBy(xpath = "//a[@href='/rfq-settings']")
    lateinit var rfqLink: Button

    @Name("Streaming link")
    @FindBy(xpath = "//a[@href='/streaming-settings']")
    lateinit var streamingLink: Button

    @Name("blocktrade link")
    @FindBy(xpath = "//a[@href='/p2p-settings']")
    lateinit var blocktradeLink: Button

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
            "RFQ" to arrayOf(rfqToggleStatusText, rfqToggleStatus, rfqLink),
            "Streaming" to arrayOf(streamingToggleStatusText, streamingToggleStatus, streamingLink),
            "Blocktrade" to arrayOf(blocktradeToggleStatusText, rfqToggleStatus, rfqLink)
        )
        for ((key, value) in settings) {
            assert {
                elementContainsText(value[1] as WebElement, value[0] as String)
                when (rfqToggleStatusText) {
                    "enable" -> elementPresented(value[2] as WebElement)
                    "disable" -> elementNotPresented(value[2] as WebElement)
                }
            }
//            elementContainsText(streamingToggleStatus, streamingToggleStatusText)
//            when (streamingToggleStatusText) {
//                "enable" -> elementPresented(streamingLink)
//                "disable" -> elementNotPresented(streamingLink)
//            }
//            elementContainsText(blocktradeToggleStatus, blocktradeToggleStatusText)
//            when (blocktradeToggleStatusText) {
//                "enable" -> elementPresented(blocktradeLink)
//                "disable" -> elementNotPresented(blocktradeLink)
//            }
        }
    }

    @Step("Change status of streaming settings then check")
    fun changeToggleStatus(toggle1: String? = null, toggle2: String? = null, toggle3: String? = null) {
        val toggles = arrayOf(toggle1, toggle2, toggle3)
        for (toggle in toggles) {
            e {
                when (toggle1) {
                    "RFQ" -> click(rfqToggleAlt)
                    "Streaming" -> click(streamingToggleAlt)
                    "Blocktrade" -> click(blocktradeToggleAlt)
                }
            }
        }

    }

}