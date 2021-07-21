package pages.atm

import io.qameta.allure.Step
import models.CoinType
import org.junit.jupiter.api.Assertions
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminStreamingSettingsPage.*
import pages.atm.AtmAdminStreamingSettingsPage.FeeModeState.*
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAdminSelect
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to

@PageUrl("/p2p-settings")
class AtmAdminBlocktradeSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val TOKEN = "Token"
    }

    @Name("Blocktrade settings table")
    @FindBy(css = "sdex-p2p-tokens")
    lateinit var blocktradeSettingsTable: SdexTable

    @Name("Default asset input")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field")
    lateinit var defaultAsset: AtmInput

    @Name("First row in the table")
    @FindBy(xpath = "//table//td")
    lateinit var firstRow: Button

    @Name("FIAT toke")
    @FindBy(xpath = "//td[contains(text(),'FIAT')]")
    lateinit var fiatToken: Button

    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(),'ADD')]")
    lateinit var add: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(),'EDIT')]")
    lateinit var edit: Button

    @Name("disabled EDIT button")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(), 'EDIT')]")
    lateinit var editDisabled: Button

    @Name("Delete button")
    @FindBy(xpath = "//span[contains(text(),'DELETE')]")
    lateinit var delete: Button

    @Name("disabled DELETE button")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(), 'DELETE')]")
    lateinit var deleteDisabled: Button

    @Name("Default fee placing offer (Maker) input")
    @FindBy(xpath = "//input[@data-placeholder='Set default placing fee']/ancestor::mat-form-field")
    lateinit var defaultFeePlacingOfferMaker: AtmInput

    @Name("Default fee accepting offer (Taker)")
    @FindBy(xpath = "//input[@data-placeholder='Set default accepting fee']/ancestor::mat-form-field")
    lateinit var defaultFeeAcceptingOfferTaker: AtmInput

    @Name("Save button")
    @FindBy(xpath = "//input[@data-placeholder='Set default accepting fee']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var acceptSave: Button

    @Name("Save button")
    @FindBy(xpath = "//input[@data-placeholder='Set default placing fee']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var placeSave: Button

    @Name("Save button")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var assetSave: Button


    @Name("Token IT_202010 ")
    @FindBy(xpath = "//td[contains(text(),'IT_202010')]")
    lateinit var token: Button

    @Name("Yes button in popup dialog")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'Yes')]")
    lateinit var yes: Button

    @Name("No button in popup dialog")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'No')]")
    lateinit var no: Button

//region Addtoken rate dialog

    @Name("Token input")
    @FindBy(xpath = "//sdex-token-selector[@formcontrolname='token']/ancestor::mat-form-field")
    lateinit var tokenInput: AtmInput

    @Name("Available base checkbox")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='available']//label")
    lateinit var available: CheckBox

    @Name("Fee placing asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']")
    lateinit var feePlacingAsset: AtmInput

    @Name("Fee placing amount input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAmount']")
    lateinit var feePlacingAmount: AtmInput

    @Name("Fee placing mode")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeMode']//div//mat-select")
    lateinit var feePlacingMode: AtmAdminSelect

    @Name("Fee accepting asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']")
    lateinit var feeAcceptingAsset: AtmInput

    @Name("Fee accepting asset amount")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAmount']")
    lateinit var feeAcceptingAmount: AtmInput

    @Name("Fee accepting mode")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeMode']//div//mat-select")
    lateinit var feeAcceptingMode: AtmAdminSelect

    @Name("Fee placing asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[1]")
    lateinit var feePlacingAssetSelect: AtmAdminSelect

    @Name("Fee accepting asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[2]")
    lateinit var feeAcceptingAssetSelect: AtmAdminSelect

    @Name("Confirm add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CONFIRM')]//ancestor::button")
    lateinit var confirmDialog: Button

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    @Name("Clear button")
    @FindBy(xpath = "//mat-icon[text()='clear']")
    lateinit var clearButton: Button

    @Name("Token clear button")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='token']//mat-icon[text()='clear']")
    lateinit var tokenClearButton: Button

    @Name("Clear button fee accept")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var feeAcceptClearButton: Button

    @Name("Clear button fee place")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var feePlaceClearButton: Button

    @Name("Token clear button")
    @FindBy(xpath = ".//mat-option//span[@class='mat-option-text']")
    lateinit var popUpWindow: TextInput



//endregion

    @Step("Add new token")
    @Action("Admin add new token")
    fun addNewToken(
        tokenName: String,
        availableCheckbox: Boolean,
        feePlacingAmountValue: String,
        feePlacingAssetValue: String,
        feePlacingModeValue: String,
        feeAcceptingAssetValue: String,
        feeAcceptingAmountValue: String,
        feeAcceptingModeValue: String
    ) {
        e {
            click(add)
            chooseToken(tokenInput, tokenName)
            setCheckbox(available, availableCheckbox)
            sendKeys(feePlacingAmount, feePlacingAmountValue)
            chooseToken(feePlacingAsset, feePlacingAssetValue)
            select(feePlacingMode, feePlacingModeValue)
            chooseToken(feeAcceptingAsset, feeAcceptingAssetValue)
            sendKeys(feeAcceptingAmount, feeAcceptingAmountValue)
            select(feeAcceptingMode, feeAcceptingModeValue)
            click(confirmDialog)
        }
        assert {
            elementContainingTextPresented(tokenName)
        }

    }

    @Action("add token")
    @Step("add token")
    fun addTokenIfNotPresented(
        tokenName: String,
        availableCheckbox: Boolean,
        feePlacingAmountValue: String,
        feePlacingAssetValue: String,
        feePlacingModeValue: String,
        feeAcceptingAssetValue: String,
        feeAcceptingAmountValue: String,
        feeAcceptingModeValue: String
    ) {
        val row = blocktradeSettingsTable.find {
            it[TOKEN]?.text == tokenName
        }?.get(TOKEN)
        if (row == null) {
            addNewToken(
                tokenName,
                availableCheckbox,
                feePlacingAmountValue,
                feePlacingAssetValue,
                feePlacingModeValue,
                feeAcceptingAssetValue,
                feeAcceptingAmountValue,
                feeAcceptingModeValue
            )
        }
    }

    @Step("Add new token")
    @Action("Admin add new token")
    fun addNewDefaultTokenIfNotPresented(tokenName: String) {
        val row = blocktradeSettingsTable.find {
            it[TOKEN]?.text == tokenName
        }?.get(TOKEN)
        if (row == null) {
            e {
                click(add)
                chooseToken(tokenInput, tokenName)
                setCheckbox(available, true)
                click(confirmDialog)
            }
        }
    }

    @Step("Add new token")
    @Action("Admin add new token")
    fun addNewDefaultToken(tokenName: String) {
        e {
            click(add)
            chooseToken(tokenInput, tokenName)
            setCheckbox(available, true)
            click(confirmDialog)
        }
        assert {
            elementContainingTextPresented(tokenName)
        }

    }

    @Step("Delete token")
    @Action("Admin delete new token")
    fun deleteToken(
        tokenName: String
    ) {
        e {
            chooseToken(tokenName)
            click(delete)
            click(yes)
        }
        wait {
            until("dialog delete token is gone", 15) {
                check {
                    isElementGone(yes)
                }
            }
        }
        val row = blocktradeSettingsTable.find {
            it[TOKEN]?.text == tokenName
        }?.get(TOKEN)
        Assertions.assertTrue(
          row == null,
            "Row with $tokenName is found"
        )

    }

    @Step("Admin choose token {tokenName}")
    @Action("choose token")
    fun chooseToken(tokenName: String) {
        val row = blocktradeSettingsTable.find {
            it[TOKEN]?.text == tokenName
        }?.get(TOKEN)?.to<Button>("token name $tokenName")
            ?: error("Row with Ticker symbol $tokenName not found in table")
        e {
            click(row)
        }
    }

    @Step("Admin choose token in popup window")
    @Action("choose token in popup window")
    fun chooseToken(input: WebElement, tokenName: String) {
        e {
            click(input)
            sendKeys(input, tokenName)
            wait {
                untilPresented<Button>(By.xpath("//mat-option//span[contains(text(),'$tokenName')]"))
            }.click()
        }
    }


    @Step("Admin change fee for token in blocktrade")
    @Action("change fee for token in blocktrade")
    fun changeFeeSettingsForTokenBlocktrade(
        baseToken: CoinType,
        makerState: String =
            FIXED.state,
        feeMakerSize: String = "1",
        takerState: String =
            FIXED.state,
        feeTakerSize: String = "1.0001",
        feeToken: CoinType = CoinType.CC
    ) {
        chooseToken(baseToken.tokenSymbol)
        e {
            click(edit)

            feeAcceptingAsset.delete()
            if (check { isElementPresented(feeAcceptClearButton, 5L) }) click(feeAcceptClearButton)

            feeAcceptingAssetSelect.sendAndSelect(
                feeToken.tokenSymbol,
                feeToken.tokenSymbol,
                this@AtmAdminBlocktradeSettingsPage
            )
            feePlacingAsset.delete()
            if (check { isElementPresented(feePlaceClearButton, 5L) }) click(feePlaceClearButton)
            feePlacingAssetSelect.sendAndSelect(
                feeToken.tokenSymbol,
                feeToken.tokenSymbol,
                this@AtmAdminBlocktradeSettingsPage
            )
            feePlacingAmount.delete()
            sendKeys(feePlacingAmount, feeMakerSize)
            feeAcceptingAmount.delete()
            sendKeys(feeAcceptingAmount, feeTakerSize)

            select(feeAcceptingMode, takerState)
            select(feePlacingMode, makerState)

            click(confirmDialog)
        }
    }
}