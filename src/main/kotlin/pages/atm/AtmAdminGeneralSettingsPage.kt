package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock

@PageUrl("/otc-settings")
class AtmAdminGeneralSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    @Name("Rfq toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'RFQ')]/ancestor::div//mat-slide-toggle/label")
    lateinit var rfqToggle: CheckBox

    @Name("Rfq toggle status")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'RFQ')]/ancestor::div//mat-slide-toggle/label/span")
    lateinit var rfqToggleStatus: TextBlock

    @Name("Streaming toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Streaming')]/ancestor::div//mat-slide-toggle/label")
    lateinit var streamingToggle: CheckBox

    @Name("Streaming toggle status")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Streaming')]/ancestor::div//mat-slide-toggle/label/span")
    lateinit var streamingToggleStatus: TextBlock

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Blocktrade')]/ancestor::div//mat-slide-toggle/label")
    lateinit var blocktradeToggle: CheckBox

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Blocktrade')]/ancestor::div//mat-slide-toggle/label/span")
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

    @Step("Checking the displaying of all toggles")
    fun togglesAreDisplayed() {
        assert {
            elementPresented(rfqToggle)
            elementPresented(rfqLink)
            elementPresented(streamingToggle)
            elementPresented(streamingLink)
            elementPresented(blocktradeToggle)
            elementPresented(blocktradeLink)
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
            click(streamingToggle)
        }
        assert {
            elementContainsText(streamingToggleStatus, "disable")
            elementNotPresented(streamingLink)
        }
    }

}