package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
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

    @Step("Checking the displaying title and all toggles")
    fun pageIsDisplayed(isrfqLink: String? = null, isstreamingLink: String? = null, isblocktradeLink: String? = null) {
        assert {
            elementContainingTextPresented("OTF general settings")
            elementPresented(rfqToggleAlt)
            if (isrfqLink != null) {
                elementPresented(rfqLink)
            }
            elementPresented(streamingToggleAlt)
            if (isstreamingLink != null) {
                elementPresented(streamingLink)
            }
            elementPresented(blocktradeToggleAlt)
            if (isblocktradeLink != null) {
                elementPresented(blocktradeLink)
            }
        }
    }

    @Step("Checking toggle's status")
    fun checkingTogglesInitialStatus() {
        assert {
            elementContainsText(rfqToggleStatus, "enable")
            elementContainsText(streamingToggleStatus, "enable")
            elementContainsText(blocktradeToggleStatus, "enable")
        }
    }

    @Step("Change status of streaming settings then check")
    fun changeStreamingStatusAndCheckResult() {
        e {
            click(streamingToggleAlt)
        }
        assert {
            elementContainsText(streamingToggleStatus, "disable")
            elementNotPresented(streamingLink)
        }
    }

}