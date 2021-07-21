package pages.atm

import io.qameta.allure.Step
import models.CoinType
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
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

@PageUrl("/rfq-settings")
class AtmAdminRfqSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val TOKEN = "Token"
        const val ROLE = "Role"
    }

    @Name("Rfq settings table")
    @FindBy(css = "sdex-rfq-settings")
    lateinit var rfqSettingsTable: SdexTable

    @Name("First row in the table")
    @FindBy(xpath = "//table//td")
    lateinit var firstRow: Button

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

    @Name("Default asset")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field")
    lateinit var defaultAsset: AtmInput

    @Name("Default fee placing offer (Maker) input")
    @FindBy(xpath = "//input[@placeholder='Set default placing fee']/ancestor::mat-form-field")
    lateinit var defaultFeePlacingOfferMaker: AtmInput

    @Name("Default fee placing offer (Taker) input")
    @FindBy(xpath = "//input[@data-placeholder='Set default accepting fee']/ancestor::mat-form-field")
    lateinit var defaultFeeAcceptingOfferTaker: AtmInput

    @Name("Save button")
    @FindBy(xpath = "//mat-icon[contains(text(),'save')]")
    lateinit var save: Button

    @Name("Token IT_202010 ")
    @FindBy(xpath = "//td[contains(text(),'IT_202010')]")
    lateinit var token: Button

    @Name("Token ETT ")
    @FindBy(xpath = "//td[contains(text(),'ETT')]")
    lateinit var ettToken: Button

    @Name("Yes button")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'Yes')]")
    lateinit var yes: Button

    @Name("No button")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'No')]")
    lateinit var no: Button
//region Addtoken rate dialog

    @Name("Token input")
    @FindBy(xpath = "//input[@data-placeholder='Token']/ancestor::mat-form-field")
    lateinit var tokenInput: AtmInput

    @Name("Clear Token input")
    @FindBy(xpath = "//input[@data-placeholder='Token']/ancestor::mat-form-field//mat-icon[text()='clear']")
    lateinit var clearTokenInput: AtmInput

    @Name("Token input")
    @FindBy(xpath = "//sdex-token-selector[@formcontrolname='token']//input")
    lateinit var tokenInputSelect: AtmAdminSelect

    @Name("Available base checkbox")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='availableasbase']//label")
    lateinit var availableBase: CheckBox

    @Name("Available quote checkbox")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='availableasquote']//label")
    lateinit var availableQuote: CheckBox

    @Name("Fee placing asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']")
    lateinit var feePlacingAsset: AtmInput

    @Name("Fee placing amount input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAmount']")
    lateinit var feePlacingAmount: AtmInput

    @Name("Fee placing mode")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeMode']//mat-select[@formcontrolname='feeMode']")
    lateinit var feePlacingMode: AtmAdminSelect

    @Name("Fee accepting asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']")
    lateinit var feeAcceptingAsset: AtmInput

    @Name("Fee accepting amount")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAmount']")
    lateinit var feeAcceptingAmount: AtmInput

    @Name("Fee accepting mode")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeMode']//mat-select[@formcontrolname='feeMode']")
    lateinit var feeAcceptingMode: AtmAdminSelect

    @Name("Fee placing asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[1]")
    lateinit var feePlacingAssetSelect: AtmAdminSelect

    @Name("Fee accepting asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[2]")
    lateinit var feeAcceptingAssetSelect: AtmAdminSelect

    @Name("Confirm add token")
    @FindBy(xpath = "//span[contains(text(),'CONFIRM')]//ancestor::button")
    lateinit var confirmDialog: Button

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    @Name("Clear button")
    @FindBy(xpath = "//mat-icon[text()='clear']")
    lateinit var clearButton: Button

    @Name("Clear button fee accept")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var feeAcceptClearButton: Button

    @Name("Clear button fee place")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var feePlaceClearButton: Button

    @Name("Pop up token window ")
    @FindBy(xpath = ".//mat-option//span[@class='mat-option-text']")
    lateinit var popUpWindow: TextInput

    enum class FeeModeState(val state: String) {
        MODE_UNDEFINED(" FEE MODE_UNDEFINED "),
        FIXED(" FIXED "),
        VOLUME(" VOLUME "),
    }
//endregion

    @Action("add token")
    @Step("add token")
    fun addToken(
        tokenNameValue: String,
        availableBaseValue: Boolean,
        availableQuoteValue: Boolean,
        feePlacingAmountValue: String,
        feePlaceModeValue: String,
        feeAcceptAmountValue: String,
        feeAcceptModeValue: String
    ) {
        e {
            click(add)
            chooseToken(tokenInput, tokenNameValue)
            setCheckbox(availableBase, availableBaseValue)
            setCheckbox(availableQuote, availableQuoteValue)
            chooseToken(feePlacingAsset, tokenNameValue)
            sendKeys(feePlacingAmount, feePlacingAmountValue)
            chooseToken(feeAcceptingAsset, tokenNameValue)
            sendKeys(feeAcceptingAmount, feeAcceptAmountValue)
            select(feePlacingMode, feePlaceModeValue)
            select(feeAcceptingMode, feeAcceptModeValue)
            click(confirmDialog)
            wait {
                until("dialog add token is gone", 15) {
                    check {
                        isElementGone(confirmDialog)
                    }
                }
            }
        }
    }

    @Action("add token")
    @Step("add token")
    fun addToken(
        tokenNameValue: String,
        availableBaseValue: Boolean,
        availableQuoteValue: Boolean
    ) {
        e {
            click(add)
            tokenInputSelect.sendAndSelect(tokenNameValue, tokenNameValue, this@AtmAdminRfqSettingsPage)
            setCheckbox(availableBase, availableBaseValue)
            setCheckbox(availableQuote, availableQuoteValue)
            click(confirmDialog)
            wait {
                until("dialog add token is gone", 15) {
                    check {
                        isElementGone(confirmDialog)
                    }
                }
            }
        }
    }

    @Action("add token")
    @Step("add token")
    fun addTokenIfNotPresented(
        tokenNameValue: CoinType
    ) {
        val row = rfqSettingsTable.find {
            it[TOKEN]?.text == tokenNameValue.tokenSymbol
        }?.get(TOKEN)
        if (row == null) {
            addToken(tokenNameValue.tokenSymbol, true, true)
        }
    }

    @Step("Admin choose token {token}")
    @Action("choose token")
    fun chooseToken(token: String) {
        val row = rfqSettingsTable.find {
            it[TOKEN]?.text == token
        }?.get(TOKEN)?.to<Button>("token name: $token")
            ?: error("Row with Ticker symbol $token not found in table")
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

    @Step("Delete token")
    @Action("Admin delete new token")
    fun deleteToken(
        tokenName: String
    ) {

        chooseToken(tokenName)
        e {
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
        assert {
            elementWithTextNotPresented(tokenName)
        }
    }

    @Step("Admin change fee for token in rfq")
    @Action("change fee for token in rfq")
    fun changeFeeSettingsForToken(firstToken: CoinType, secondToken: CoinType, valueMaker: String, valueTaker: String,  state: FeeModeState) {
        chooseToken(firstToken.tokenSymbol)
        e {
            click(edit)

            feeAcceptingAsset.delete()
            if (check { isElementPresented(feeAcceptClearButton, 5L) }) click(feeAcceptClearButton)
            feeAcceptingAssetSelect.sendAndSelect(
                secondToken.tokenSymbol,
                secondToken.tokenSymbol,
                this@AtmAdminRfqSettingsPage
            )

            feePlacingAsset.delete()
            if (check { isElementPresented(feePlaceClearButton, 5L) }) click(feePlaceClearButton)
            feePlacingAssetSelect.sendAndSelect(
                secondToken.tokenSymbol,
                secondToken.tokenSymbol,
                this@AtmAdminRfqSettingsPage
            )

            feeAcceptingAmount.delete()
            sendKeys(feeAcceptingAmount, valueTaker)
            feePlacingAmount.delete()
            sendKeys(feePlacingAmount, valueMaker)

            select(feeAcceptingMode, state.state)
            select(feePlacingMode, state.state)

            click(confirmDialog)
        }

    }

}