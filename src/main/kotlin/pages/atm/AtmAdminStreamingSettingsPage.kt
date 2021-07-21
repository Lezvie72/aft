package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.CoinType.IT
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.actions.isEnabledSafety
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAdminSelect
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import utils.helpers.to

@PageUrl("/streaming-settings")
class AtmAdminStreamingSettingsPage(driver: WebDriver) : AtmAdminPage(driver) {

    data class FeePreset(
        val feePlaceToken: CoinType,
        val feeAcceptToken: CoinType,
        val feePlaceAmountValue: String,
        val feeAcceptAmountValue: String,
        val feePlaceStateValue: FeeModeState,
        val feeAcceptStateValue: FeeModeState
    )

    enum class FeeModeState(val state: String) {
        MODE_UNDEFINED("FEE MODE_UNDEFINED"),
        FIXED("FIXED"),
        VOLUME("VOLUME"),
    }

    companion object Headers {
        const val BASE = "Base"
        const val QUOTE = "Quote"
        const val FEE_PLACE_OFFER = "Fee Place offer"
        const val FEE_ACCEPT_OFFER = "Fee Accept offer"
    }

    @Name("Streaming settings table")
    @FindBy(css = "sdex-trading-pairs")
    lateinit var streamingSettingsTable: SdexTable

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

    @Name("Yes button dialog")
    @FindBy(xpath = "//mat-dialog-actions//button//span[contains(text(),'Yes')]")
    lateinit var yes: Button

    @Name("No button dialog")
    @FindBy(xpath = "//mat-dialog-actions//button//span[contains(text(),'No')]")
    lateinit var no: Button

    @Name("disabled DELETE button")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(), 'DELETE')]")
    lateinit var deleteDisabled: Button

    @Name("Default asset")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field")
    lateinit var defaultAsset: AtmInput

    @Name("Default asset")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field//input")
    lateinit var defaultAssetSelect: AtmAdminSelect

    @Name("Default asset save")
    @FindBy(xpath = "//input[@data-placeholder='Set default asset']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var defaultAssetSave: Button

    @Name("Clear button")
    @FindBy(xpath = "//mat-icon[text()='clear']")
    lateinit var clearButton: Button

    @Name("Clear Base input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='base']//mat-icon[text()='clear']")
    lateinit var clearBaseInput: Button

    @Name("Clear Quote input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='quote']//mat-icon[text()='clear']")
    lateinit var clearQuoteInput: Button

    @Name("Fee place asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var clearFeePlaceAsset: Button

    @Name("Fee accept asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']//mat-icon[text()='clear']")
    lateinit var clearFeeAcceptAsset: Button

    @Name("Default fee placing offer (Maker) input")
    @FindBy(xpath = "//input[@placeholder='Set default placing fee']/ancestor::mat-form-field")
    lateinit var defaultFeePlacingOfferInputMaker: AtmInput

    @Name("Default fee placing offer (Maker) save button")
    @FindBy(xpath = "//input[@placeholder='Set default placing fee']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var defaultFeePlacingOfferInputMakerSave: Button

    @Name("Default fee placing offer (Taker) input")
    @FindBy(xpath = "//input[@placeholder='Set default accepting fee']/ancestor::mat-form-field | //input[@data-placeholder='Set default accepting fee']/ancestor::mat-form-field")
    lateinit var defaultFeePlacingOfferInputTaker: AtmInput

    @Name("Default fee placing offer (Taker) save button")
    @FindBy(xpath = "//input[@placeholder='Set default accepting fee']/ancestor::mat-form-field//mat-icon[contains(text(),'save')]")
    lateinit var defaultFeePlacingOfferInputTakerSave: AtmInput

    @Name("Save button")
    @FindBy(xpath = "//mat-icon[contains(text(),'save')]")
    lateinit var save: Button

    @Name("Token IT_202010 ")
    @FindBy(xpath = "//td[contains(text(),'IT_202010')]")
    lateinit var token: Button
//region Addtoken rate dialog

    @Name("Base input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='base']")
    lateinit var baseInput: AtmInput

    @Name("Base input select")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='base']//input")
    lateinit var baseInputSelect: AtmAdminSelect

    @Name("Quote input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='quote']")
    lateinit var quoteInput: AtmInput

    @Name("Quote input select")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='quote']//input")
    lateinit var quoteInputSelect: AtmAdminSelect

    @Name("Maturity date base")
    @FindBy(xpath = "(//mat-form-field[@sdexerrorcontrol='base']//span)[4]")
    lateinit var maturityDateBaseValue: AtmAdminSelect

    @Name("Maturity date quote")
    @FindBy(xpath = "(//mat-form-field[@sdexerrorcontrol='quote']//span)[4]")
    lateinit var maturityDateQuoteValue: AtmAdminSelect

    @Name("Maturity date place asset")
    @FindBy(xpath = "(//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']//span)[4]")
    lateinit var maturityDatePlaceAsset: AtmAdminSelect

    @Name("Maturity date accept asset")
    @FindBy(xpath = "(//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']//span)[4]")
    lateinit var maturityDateAcceptAsset: AtmAdminSelect

    @Name("Pair available checkbox")
    @FindBy(xpath = "//mat-checkbox[@formcontrolname='available']//label")
    lateinit var pairAvailable: CheckBox

    @Name("Fee place asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset'] | //mat-form-field[@sdexerrorcontrol='createFee.feeAsset']//input")
    lateinit var feePlaceAsset: AtmInput

    @Name("Fee place asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='createFee.feeAsset']")
    lateinit var feePlaceAssetSelect: AtmAdminSelect

    @Name("Fee place amount input")
    @FindBy(xpath = "//input[@data-placeholder='Fee place amount']/ancestor::mat-form-field")
    lateinit var feePlaceAmount: AtmInput

    @Name("Fee placing asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[1]")
    lateinit var feePlacingAssetSelect: AtmAdminSelect

    @Name("Fee accepting asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset']//input)[2]")
    lateinit var feeAcceptingAssetSelect: AtmAdminSelect

    @Name("Fee placing asset delete value")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset'])[1]//button")
    lateinit var feePlacingAssetDeleteValue: Button

    @Name("Fee accepting asset select")
    @FindBy(xpath = "(//sdex-token-selector[@formcontrolname='feeAsset'])[2]//button")
    lateinit var feeAcceptingAssetDeleteValue: Button

    @Name("Fee place mode")
    @FindBy(xpath = "//span[contains(text(),'Fee place mode')]/ancestor::mat-select | //span[contains(text(),'Fee place mode')]/ancestor::div[contains(@class, 'mat-form-field-infix')]")
    lateinit var feePlaceMode: AtmAdminSelect

    @Name("Fee accept asset input")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset'] | //mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']//input")
    lateinit var feeAcceptAsset: AtmInput

    @Name("Fee accept asset input select")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAsset']")
    lateinit var feeAcceptAssetSelect: AtmAdminSelect

    @Name("Fee accept asset amount")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='acceptFee.feeAmount']")
    lateinit var feeAcceptAmount: AtmInput

    @Name("Fee accept mode")
    @FindBy(xpath = "//span[contains(text(),'Fee accept mode')]/ancestor::mat-select | //span[contains(text(),'Fee accept mode')]/ancestor::div[contains(@class, 'mat-form-field-infix')]")
    lateinit var feeAcceptMode: AtmAdminSelect

    @Name("Available amount input")
    @FindBy(xpath = "//input[@formcontrolname='amountItem']/ancestor::mat-form-field")
    lateinit var availableAmounts: AtmInput

    @Name("Confirm add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CONFIRM')]")
    lateinit var confirmDialog: Button

    @Name("Cancel add token")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelDialog: Button

    //endregion
    @Action("add trading pair")
    @Step("add trading pair")
    fun addTradingPair(
        baseInputValue: String,
        quoteValue: String,
        maturityDate: String = "",
        availableAmountValue: String,
        feePlaceAmountValue: String,
        feeAcceptAmountValue: String,
        feePlaceModeValue: String,
        feeAcceptModeValue: String,
        available: Boolean
    ) {
        e {
            click(add)

            if (baseInputValue.startsWith(IT.tokenSymbol)) {
                chooseToken(
                    baseInput, baseInputValue
                        .replaceAfter("_", "")
                        .replace("_", "")
                )

                select(maturityDateBaseValue, maturityDate)

                chooseToken(
                    feePlaceAsset, baseInputValue
                        .replaceAfter("_", "")
                        .replace("_", "")
                )

                select(maturityDatePlaceAsset, maturityDate)
            } else {
                chooseToken(baseInput, baseInputValue)
                chooseToken(feePlaceAsset, baseInputValue)

            }

            if (quoteValue.startsWith(IT.tokenSymbol)) {
                chooseToken(
                    quoteInput, quoteValue
                        .replaceAfter("_", "")
                        .replace("_", "")
                )
                select(maturityDateQuoteValue, maturityDate)

                chooseToken(
                    feeAcceptAsset, quoteValue
                        .replaceAfter("_", "")
                        .replace("_", "")
                )
                select(maturityDateAcceptAsset, maturityDate)

            } else {
                chooseToken(quoteInput, quoteValue)
                chooseToken(feeAcceptAsset, quoteValue)

            }

            sendKeys(availableAmounts, availableAmountValue)
            select(feePlaceMode, feePlaceModeValue)
            sendKeys(feePlaceAmount, feePlaceAmountValue)

            sendKeys(feeAcceptAmount, feeAcceptAmountValue)

            select(feeAcceptMode, feeAcceptModeValue)
            setCheckbox(pairAvailable, available)

            click(confirmDialog)

            wait {
                until("dialog add trading pair is gone", 15) {
                    check {
                        isElementGone(confirmDialog)
                    }
                }
            }
        }
    }

    @Step("Admin choose trading pair")
    @Action("choose trading pair")
    fun chooseTradingPair(base: String, quote: String) {
        val row = streamingSettingsTable.find {
            it[BASE]?.text == base && it[QUOTE]?.text == quote
        }?.get(BASE)?.to<Button>("trading pair base: $base and quote: $quote")
            ?: error("Row with Ticker symbol base: $base and quote: $quote not found in table")
        e {
            click(row)
        }
    }

    @Action("add token")
    @Step("add token")
    fun addTradingPairIfNotPresented(
        baseInputValue: String,
        quoteValue: String,
        maturityDate: String = "",
        availableAmountValue: String,
        feePlaceAmountValue: String,
        feeAcceptAmountValue: String,
        feePlaceModeValue: String,
        feeAcceptModeValue: String,
        available: Boolean
    ) {
        val row = streamingSettingsTable.find {
            it[BASE]?.text == baseInputValue && it[QUOTE]?.text == quoteValue
        }

        if (row == null) {
            addTradingPair(
                baseInputValue, quoteValue, maturityDate, availableAmountValue, feePlaceAmountValue,
                feeAcceptAmountValue, feePlaceModeValue, feeAcceptModeValue, available
            )
        }
    }

    @Step("Admin delete trading pair {tradingPair}")
    @Action("delete trading pair")
    fun deleteTradingPair(base: String, quote: String) {
        chooseTradingPair(base, quote)
        e {
            click(delete)
            click(yes)
            wait {
                until("dialog delete trading pair is gone", 15) {
                    check {
                        isElementGone(yes)
                    }
                }
            }
        }
    }

    @Step("Admin choose token in popup window")
    @Action("choose token in popup window")
    fun chooseToken(input: WebElement, tokenName: String) {
        e {
            click(input)
            sendKeys(input, tokenName)
            wait {
                untilPresented<Button>(By.xpath("//mat-option//span[text()=' $tokenName ']"))
            }.click()
        }
    }

    @Step("Admin choose maturityDate in popup window")
    @Action("choose maturityDate in popup window")
    fun chooseMaturityDate(input: WebElement, maturityDate: String) {
        e {
            click(input)
            wait {
                untilPresented<Button>(By.xpath("//mat-option//span[contains(text(),'$maturityDate')]"))
            }.click()
        }
    }

    @Step("Administrator clear the fee setting in edit form")
    @Action("clear the fee setting in edit form")
    fun cleanFeeInEditForm() {
        e {
            select(feePlaceMode, FeeModeState.MODE_UNDEFINED.state)
            sendKeys(feePlaceAmount, "")
            sendKeys(feeAcceptAmount, "")
            sendKeys(feePlaceAsset, "")
            sendKeys(feeAcceptAsset, "")
            select(feeAcceptMode, FeeModeState.MODE_UNDEFINED.state)
        }
    }

    @Step("Administrator clear the fee setting in global settings form")
    @Action("clear the fee setting in global settings form")
    fun setUpDefaultFeeOptionsInGlobalForm(
        defaultAssetValue: CoinType,
        defaultFeePlacingOfferMaker: String,
        defaultFeePlacingOfferTaker: String
    ) {
        e {
            if (isEnabledSafety(page, "//mat-icon[text()='clear']")) click(clearButton)
            defaultAssetSelect.sendAndSelect(
                defaultAssetValue.tokenSymbol,
                defaultAssetValue.tokenSymbol,
                this@AtmAdminStreamingSettingsPage
            )
//            click(defaultAssetSave)

            sendKeys(defaultFeePlacingOfferInputMaker, "0.0000001")
            defaultFeePlacingOfferInputMaker.clear()
            sendKeys(defaultFeePlacingOfferInputMaker, defaultFeePlacingOfferMaker)
            click(defaultFeePlacingOfferInputMakerSave)

            sendKeys(defaultFeePlacingOfferInputTaker, "0.0000001")
            defaultFeePlacingOfferInputTaker.clear()
            sendKeys(defaultFeePlacingOfferInputTaker, defaultFeePlacingOfferTaker)
            click(defaultFeePlacingOfferInputTakerSave)
        }
    }

    @Step("Administrator setup fee in edit form")
    @Action("setup fee in edit form")
    fun setupFee(feePreset: FeePreset) {
        e {
            if (feePreset.feePlaceStateValue != FeeModeState.MODE_UNDEFINED) feePlaceAssetSelect.sendKeys(
                feePreset.feePlaceToken.tokenSymbol,
                page
            )
            if (feePreset.feeAcceptStateValue != FeeModeState.MODE_UNDEFINED) feeAcceptAssetSelect.sendKeys(
                feePreset.feeAcceptToken.tokenSymbol,
                page
            )
            select(feePlaceMode, feePreset.feePlaceStateValue.state)
            feePlaceAmount.sendKeys(feePreset.feePlaceAmountValue)
            feeAcceptAmount.sendKeys(feePreset.feeAcceptAmountValue)
            select(feeAcceptMode, feePreset.feeAcceptStateValue.state)
        }
    }

    @Step("Admin change fee for token in streaming")
    @Action("change fee for token in streaming")
    fun changeFeeSettingsForTokenStreaming(
        baseToken: String,
        quoteToken: String,
        feePlaceAmountValue: String,
        feeAcceptAmountValue: String
    ) {
        chooseTradingPair(baseToken, quoteToken)
        e {
            click(edit)
            feeAcceptAsset.delete()
            feeAcceptingAssetSelect.sendAndSelect(
                baseToken,
                baseToken,
                this@AtmAdminStreamingSettingsPage
            )

            feePlaceAsset.delete()
            feePlacingAssetSelect.sendAndSelect(
                baseToken,
                baseToken,
                this@AtmAdminStreamingSettingsPage
            )

            deleteData(feePlaceAmount)
            sendKeys(feePlaceAmount, feePlaceAmountValue)
            select(feePlaceMode, FeeModeState.FIXED.state)

            deleteData(feeAcceptAmount)
            sendKeys(feeAcceptAmount, feeAcceptAmountValue)
            select(feeAcceptMode, FeeModeState.FIXED.state)

            click(confirmDialog)
        }

    }

    @Action("change available for trading pair")
    @Step("change available for trading pair")
    fun changeAvailableStatus(
        baseValue: String,
        quoteValue: String,
        available: Boolean
    ) {
        chooseTradingPair(baseValue, quoteValue)
        e {
            click(edit)
            setCheckbox(pairAvailable, available)
            click(confirmDialog)

            wait {
                until("dialog add trading pair is gone", 15) {
                    check {
                        isElementGone(confirmDialog)
                    }
                }
            }
        }
    }
}
