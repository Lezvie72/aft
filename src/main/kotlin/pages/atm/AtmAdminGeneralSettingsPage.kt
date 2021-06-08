package pages.atm

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox

@PageUrl("/otc-settings")
class AtmAdminGeneralSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    @Name("Rfq toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'RFQ')]/ancestor::div//mat-slide-toggle/label")
    lateinit var rfqToggle: CheckBox

    @Name("Streaming toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Streaming')]/ancestor::div//mat-slide-toggle/label")
    lateinit var streamingToggle: CheckBox

    @Name("Blocktrade toggle")
    @FindBy(xpath = "//mat-panel-title[contains(text(),'Blocktrade')]/ancestor::div//mat-slide-toggle/label")
    lateinit var blocktradeToggle: CheckBox

    @Name("Rfq link")
    @FindBy(xpath = "//a[@href='/rfq-settings']")
    lateinit var rfqLink: Button

    @Name("Streaming link")
    @FindBy(xpath = "//a[@href='/streaming-settings']")
    lateinit var streamingLink: Button

    @Name("blocktrade link")
    @FindBy(xpath = "//a[@href='/p2p-settings']")
    lateinit var blocktradeLink: Button

}