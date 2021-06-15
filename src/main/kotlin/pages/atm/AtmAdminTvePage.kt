package pages.atm

import io.qameta.allure.Step
import models.CoinType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmRadio
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to

@PageUrl("/tve-settings")
class AtmAdminTvePage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val TOKEN = "Token"
        const val STATUS = "Price corridor status"
    }

    // Tables
    @Name("Token and price corridor status table")
    @FindBy(css = "sdex-tve-rules")
    lateinit var tokenAndPriceStatus: SdexTable

    @Name("Token and rate table")
    @FindBy(css = "sdex-tve-token-rates")
    lateinit var tokenAndRate: SdexTable


    // Elements - buttons
    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(),'ADD')]/ancestor::button")
    lateinit var add: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(),'EDIT')]/ancestor::button")
    lateinit var edit: Button

    @Name("Delete button")
    @FindBy(xpath = "//span[contains(text(),'DELETE')]/ancestor::button")
    lateinit var delete: Button

    @Name("Delete button")
    @FindBy(xpath = "//span[contains(text(),' USD equivalent settings ')]/ancestor::button")
    lateinit var usdQquivalentSettings: Button

    @Name("Yes button dialog")
    @FindBy(xpath = "//span[contains(text(),'Yes')]/ancestor::button")
    lateinit var yes: Button

    @Name("Save button Green corridor")
    @FindBy(xpath = "//mat-label[text()='Allowed price deviation percentage boundary (Green corridor)']/ancestor::div[contains(@class,'mat-form-field-flex')]//mat-icon[contains(text(),'save')]")
    lateinit var saveGreenCorridor: Button

    @Name("Save button Green corridor")
    @FindBy(xpath = "//mat-label[text()='Exception trade price deviation percentage boundary (Yellow corridor)']/ancestor::div[contains(@class,'mat-form-field-flex')]//mat-icon[contains(text(),'save')]")
    lateinit var saveYellowCorridor: Button

    @Name("Confirm button")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CONFIRM')]/ancestor::button")
    lateinit var confirm: Button

    @Name("Cancel button")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]/ancestor::button")
    lateinit var cancel: Button


    // Elements - input
    @Name("Allowed price deviation percentage boundary (Green corridor)")
    @FindBy(xpath = "//mat-label[text()='Allowed price deviation percentage boundary (Green corridor)']/ancestor::div[contains(@class,'mat-form-field-infix')]/input")
    lateinit var greenCorridor: TextInput

    @Name("Exception trade price deviation percentage boundary (Yellow corridor)")
    @FindBy(xpath = "//mat-label[text()='Exception trade price deviation percentage boundary (Yellow corridor)']/ancestor::div[contains(@class,'mat-form-field-infix')]/input")
    lateinit var yellowCorridor: TextInput

    @Name("USD equivalent")
    @FindBy(xpath = "//mat-dialog-container//span[text()='USD equivalent']/ancestor::div[contains(@class,'mat-form-field-infix')]/input")
    lateinit var usdEquivalent: TextInput


    // Lables
    @Name("Label of Tve settings window")
    @FindBy(xpath = "//h1[contains(text(), 'Tve settings')]")
    lateinit var tveSettingsLabel: TextBlock

    @Name("Label of Edit TVE rule window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit TVE rule')]")
    lateinit var editTveRuleLabel: TextBlock

    @Name("CC USD equivalent settings")
    @FindBy(xpath = "//h1[contains(text(), 'CC USD equivalent settings')]")
    lateinit var equivalentSettingsLabel: TextBlock

    // Checkbox
    @Name("Price corridor status")
    @FindBy(xpath = "//span[contains(text(), 'Price corridor status')]/ancestor::label")
    lateinit var priceCheckbox: CheckBox

    // Radio
    @Name("Fixed radio")
    @FindBy(xpath = "//mat-dialog-container//span[contains(text(), ' Fixed ')]")
    lateinit var fixed: AtmRadio

    @Name("From market data radio")
    @FindBy(xpath = "//mat-dialog-container//span[contains(text(), ' From market data ')]")
    lateinit var fromMarketData: AtmRadio

    @Name("Disabled radio")
    @FindBy(xpath = "//mat-dialog-container//span[contains(text(), ' Disabled ')]")
    lateinit var disabled: AtmRadio

    @Action("Select state for token")
    @Step("Select state for token")
    fun setStateForToken(
        token: CoinType,
        stateCheckBox: Boolean
    ) {
        val row = tokenAndPriceStatus.find { it[TOKEN]?.text == token.tokenSymbol }?.get(TOKEN)
            ?.to<Button>("Row with token $TOKEN")
            ?: error("Can't find row with token symbol: $TOKEN")
        e {
            click(row)
            click(edit)
            setCheckbox(priceCheckbox, stateCheckBox)
            click(confirm)
            alert { checkErrorAlert() }
        }
    }

    @Action("Setup corridor")
    @Step("Setup corridor")
    fun setupCorridors(
        greenCorridorSize: String,
        yellowCorridorSize: String
    ) {
        e {
            wait(5) { untilPresented(tveSettingsLabel) }
            sendKeys(greenCorridor, greenCorridorSize)
            click(saveGreenCorridor)
            sendKeys(yellowCorridor, yellowCorridorSize)
            click(saveYellowCorridor)
        }
    }

    @Action("Setup rate for token")
    @Step("Setup rate for token")
    fun setupRateForToken(
        coinType: CoinType,
        assetRate: String
    ) {
        e {
            val row = tokenAndRate.find { it[TOKEN]?.text == coinType.tokenSymbol }?.get(TOKEN)
                ?.to<Button>("Row rate with token $TOKEN")
                ?: error("Can't find row rate with token symbol: $TOKEN")
            click(row)
            click(usdQquivalentSettings)
            click(fixed)
            sendKeys(usdEquivalent, assetRate)
            click(confirm)
            alert { checkErrorAlert() }
        }
    }
}