package pages.atm

import io.qameta.allure.Step
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import models.CoinType
import models.user.interfaces.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminTokensPage.EquivalentType.FIXED
import pages.atm.AtmAdminTokensPage.EquivalentType.FROM_MARKET
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.marketplace.SdexTableTokens
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import utils.isChecked

@PageUrl("/tokens")
class AtmAdminTokensPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val TICKER_SYMBOL = "Ticker Symbol"
        const val USD_EQUIVALENT = "USD Equivalent"
        const val TOKEN_NAME = "Token Name"
        const val TOKEN_STATUS = "Token Status"
        const val FEE_RATE = "Fee rate (%)"
        const val CAP = "Cap"
        const val TOKEN_TYPE = "Token Type"
        const val COMPANY = "Company"
        const val VISIBLE = "Visible"
        const val USER = "User"
        const val DATE_OF_CHANGE = "Date of change"
    }

    enum class EquivalentType {
        FIXED, FROM_MARKET;
    }

    enum class StatusToken {
        AVAILABLE, UNAVAILABLE, ARCHIVED;
    }

    @Name("Search")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='searchString']")
    lateinit var search: TextInput

    @Name("Update date from")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='updateDateFrom']")
    lateinit var updateDateFrom: DateSelect

    @Name("Update date to")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='updateDateTo']")
    lateinit var updateDateTo: DateSelect

    @Name("Create date from")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='createDateFrom']")
    lateinit var createDateFrom: DateSelect

    @Name("Create date to")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='createDateTo']")
    lateinit var createDateTo: DateSelect

    @Name("Transfer fee")
    @FindBy(xpath = "//sdex-tokens//span[contains(text(), 'Transfer fee')]")
    lateinit var transferFee: Button

    @Name("Transfer fee distribution")
    @FindBy(xpath = "//sdex-tokens//span[contains(text(), 'Transfer fee distribution')]")
    lateinit var transferFeeDistribution: Button

    @Name("Add token")
    @FindBy(xpath = "//sdex-tokens//span[contains(text(), 'ADD')]")
    lateinit var addToken: Button

    @Name("Edit token")
    @FindBy(xpath = "//sdex-tokens//span[contains(text(), 'EDIT')]")
    lateinit var editToken: Button

    @Name("Confirm")
    @FindBy(xpath = "//button//span[contains(text(), 'CONFIRM')]")
    lateinit var confirm: Button

    @Name("Cancel")
    @FindBy(xpath = "//button//span[contains(text(), 'CANCEL')]")
    lateinit var cancel: Button

    @Name("Close")
    @FindBy(xpath = "//button//span[contains(text(), 'Close')]")
    lateinit var close: Button

    @Name("Tokens table")
    @FindBy(xpath = "//sdex-tokens//sdex-grid | //sdex-tokens")
    lateinit var tokensTable: SdexTableTokens

    @Name("History table")
    @FindBy(xpath = "//sdex-transfer-fee-changes-history//tbody")
    lateinit var historyTable: SdexTable

    @Name("Charge In")
    @FindBy(xpath = "//sdex-token-selector[@formcontrolname='chargedIn']//input")
    lateinit var chargeIn: AtmAdminSelect

    @Name("Charge In Input")
    @FindBy(xpath = "//sdex-token-selector[@formcontrolname='chargedIn']")
    lateinit var chargeInInput: AtmInput

    @Name("Rate")
    @FindBy(xpath = "//sdex-amount-input[@formcontrolname='rate']/input")
    lateinit var rate: TextInput

    @Name("Floor")
    @FindBy(xpath = "//sdex-amount-input[@formcontrolname='floor']/input")
    lateinit var floor: TextInput

    @Name("Cap")
    @FindBy(xpath = "//sdex-amount-input[@formcontrolname='cap']/input")
    lateinit var cap: TextInput

    @Name("Token Issuer")
    @FindBy(xpath = "//input[@formcontrolname='issuername']")
    lateinit var tokenIssuer: TextInput

    @Name("Token Name")
    @FindBy(xpath = "//input[@formcontrolname='tokenname']")
    lateinit var tokenName: TextInput

    @Name("Ticker Symbol")
    @FindBy(xpath = "//mat-form-field[@sdexerrorcontrol='tickersymbol']")
    lateinit var tickerSymbol: AtmInput

    @Name("Token Type")
    @FindBy(xpath = "//mat-select[@formcontrolname='tokentype']")
    lateinit var tokenType: AtmSelect

    @Name("Token Status")
    @FindBy(xpath = "//mat-select[@formcontrolname='tokenstatusid']")
    lateinit var tokenStatus: AtmSelect

    @Name("Token Description")
    @FindBy(xpath = "//input[@formcontrolname='tokendescription']")
    lateinit var tokenDescription: TextInput

    @Name("Channel name")
    @FindBy(xpath = "//input[@formcontrolname='channelname']")
    lateinit var channelName: TextInput

    @Name("Chaincode name")
    @FindBy(xpath = "//input[@formcontrolname='chaincodename']")
    lateinit var chaincodeName: TextInput

    @Name("Underlying Asset")
    @FindBy(xpath = "//mat-select[@formcontrolname='underlyingasset']")
    lateinit var underlyingAsset: AtmSelect

    @Name("Financial ID")
    @FindBy(xpath = "//mat-select[@formcontrolname='financialID']")
    lateinit var financialId: AtmSelect

    @Name("Coefficient")
    @FindBy(xpath = "//mat-form-field//input[@formcontrolname='coefficient']")
    lateinit var coefficient: AtmAdminSelect

    @Name("Input for fixed")
    @FindBy(xpath = "//input[@data-placeholder='USD equivalent']")
    lateinit var inputForFixed: AtmInput

    @Name("Fixed")
    @FindBy(xpath = "//span[contains(text(), 'Fixed')]/ancestor::mat-radio-button")
    lateinit var fixed: AtmAdminRadio

    @Name("From market")
    @FindBy(xpath = "//*[contains(text(), 'From market data')]/ancestor::mat-radio-button")
    lateinit var fromMarket: AtmAdminRadio

    @Name("Usd equivalent settings")
    @FindBy(xpath = "//button//span[contains(text(), ' USD equivalent settings ')]")
    lateinit var usdEquivalentSettings: Button

    // TODO: Добавить функцию на ожидание в контейнер overlay
    @Name("Usd equivalent container")
    @FindBy(css = "div[class='cdk-overlay-container']")
    lateinit var usdEquivalentOverlay: AtmOverlayContainer

    @Name("Save")
    @FindBy(xpath = "//button//span[contains(text(), 'SAVE')]")
    lateinit var save: Button

    //    region TRANSFER FEE DISTRIBUTION POPUP
    @Name("Close transfer fee distribution popup")
    @FindBy(xpath = "//button//span[contains(text(), 'Close')]")
    lateinit var closeTransferFeeDistPopup: Button

    @Name("Validator share input")
    @FindBy(xpath = "//input[@formcontrolname='validatorsShare']/ancestor::mat-form-field")
    lateinit var validatorShare: AtmInput

    @Name(" Availability rules ")
    @FindBy(xpath = "//sdex-tokens//button//span[contains(text(), 'Visibility rules')]")
    lateinit var visibilityRules: Button

    @Name("Symbol")
    @FindBy(xpath = "//input[@formcontrolname='symbol']/ancestor::mat-form-field")
    lateinit var symbol: TextInput

    @Name("Standard visibility rules")
    @FindBy(xpath = "//mat-radio-button//span[contains(text(), 'Standard visibility rules')]")
    lateinit var standardVisibilityRules: AtmRadio

    @Name("Custom visibility rules")
    @FindBy(xpath = "//mat-radio-button//span[contains(text(), 'Custom visibility rules')]")
    lateinit var customVisibilityRules: AtmRadio

    @Name("Company name")
    @FindBy(xpath = "//div//input[@formcontrolname='companyName']")
    lateinit var companyName: AtmAdminSelect

    @Name("Delete")
    @FindBy(xpath = "//span[contains(text(),'DELETE')]/ancestor::button")
    lateinit var delete: Button

    @Name("Add")
    @FindBy(xpath = "//sdex-visibility-rule-add//button//span[contains(text(), 'ADD')]")
    lateinit var add: Button

    @Name("Visibility rules table")
    @FindBy(xpath = ".//mat-dialog-container")
    lateinit var visibilityRulesTable: SdexTable

    @Name("Table grid")
    @FindBy(xpath = ".//sdex-grid")
    lateinit var tableGrid: TextInput

    @Name("Table grid")
    @FindBy(xpath = "//sdex-visibility-rules-dialog")
    lateinit var visibilityRulesDialog: TextInput

    @Name("Clear button")
    @FindBy(xpath = "//mat-icon[text()='clear']")
    lateinit var clearButton: Button

    @Name("USD and CC row")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'USD')]][.//*[contains(text(), 'CC')]]")
    lateinit var uSDCCRow: AtmSelect

    @Name("USD and IT row")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'USD')]][.//*[contains(text(), 'Industrial Token')]]")
    lateinit var uSDIndustrialRow: AtmSelect

    @Name("Window equivalent settings CC")
    @FindBy(xpath = "//h1[contains(text(), 'CC') and contains(text(), 'USD')]")
    lateinit var windowEquivalentSettingsCC: TextBlock

    @Name("Window equivalent settings IT")
    @FindBy(xpath = "//h1[contains(text(), 'GF46ILN061A') and contains(text(), 'USD')]")
    lateinit var windowEquivalentSettingsIT: TextBlock

    @Name("Financial value")
    @FindBy(xpath = "//mat-dialog-content//*[contains(text(),'/')]")
    lateinit var financialValueInt: TextBlock

    @Name("Equivalent value")
    @FindBy(xpath = "//*[contains(@class,'auto-row')]//*[contains(text(),'equivalent')]/following::div[1]")
    lateinit var equivalentValueInt: TextBlock

    @Name("Financial button")
    @FindBy(xpath = "//div[@role='listbox']")
    lateinit var financialButton: AtmSelect

    @Name("New equivalent value")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'USD')]][.//*[contains(text(), 'CC')]]//*[contains(text(), ',')]")
    lateinit var newEquivalentValueInt: TextBlock

    @Name("New equivalent value sub 1,000")
    @FindBy(xpath = "//tr[.//*[contains(text(), 'USD')]][.//*[contains(text(), 'CC')]]")
    lateinit var newEquivalentValueIntMin: TextBlock

//    endregion


    // TODO: Данный метод в связи с отстутствием кнопки "save" работает неправильно
    // TODO: Если все так и останется, то *ATMCH-1917* можно взять как пример реализации
    @Step("Change fee for token")
    @Action("Change fee for token")
    fun changeFeeForToken(
        token: CoinType,
        chargeInToken: CoinType,
        feeRate: String,
        floorAmount: String,
        capAmount: String
    ): AtmAdminTokensPage {
//        e {
//            sendKeys(search, token.tokenSymbol)
//        }
        val row = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol $token")
            ?: error("Row with Ticker symbol $token not found in table")
        e {
            click(row)
            click(transferFee)
            wait {
                until("Table history of fee is displayed", 15) {
                    check {
                        isElementPresented(historyTable)
                    }
                }
            }
        }

        e {
            click(clearButton)
            chargeIn.sendAndSelect(chargeInToken.tokenSymbol, chargeInToken.tokenSymbol, this@AtmAdminTokensPage)
            sendKeys(rate, feeRate)
            sendKeys(floor, floorAmount)
            sendKeys(cap, capAmount)

            wait {
                until("Button Save is presented", 10) {
                    check {
                        isElementPresented(save)
                    }
                }
            }
            click(save)
            wait {
                until("Text Fee was updated is presented", 15) {
                    check {
                        isElementWithTextPresented("Fee was updated")
                    }
                }
            }
        }
        return AtmAdminTokensPage(driver)
    }

    @Step("edit usd equivalent")
    @Action("User edit usd equivalent")
    fun editUsdEquivalent(
        token: CoinType,
        equivalentType: EquivalentType,
        value: String,
        id: String
    ): AtmAdminTokensPage {
        e {
            sendKeys(search, token.tokenSymbol)
        }

        val row = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
            ?: error("Row with Ticker symbol ${token.tokenSymbol} not found in table")
        e {
            click(row)
            click(usdEquivalentSettings)
            e {
                until("Wait for hidden preloader", 5L) {
                    usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                }
                when (equivalentType) {
                    FIXED -> e {
                        click(fixed)
                        sendKeys(usdEquivalentOverlay.usdEquivalent, value)
                        click(confirm)
                    }
                    FROM_MARKET -> e {
                        click(fromMarket)
                        select(financialId, id)
                        sendKeys(coefficient, value)
                        click(confirm)
                        Thread.sleep(10000)
                    }
                }
            }
        }
        return AtmAdminTokensPage(driver)
    }


    @Step("edit token status")
    @Action("Admin edit status of token")
    fun editTokenStatus(tokenButton: Button, status: StatusToken): AtmAdminTokensPage {
        e {
            click(tokenButton)
            click(editToken)
            when (status) {
                StatusToken.AVAILABLE -> select(tokenStatus, "available")
                StatusToken.UNAVAILABLE -> select(tokenStatus, "unavailable")
                StatusToken.ARCHIVED -> select(tokenStatus, "archived")
            }
            click(confirm)
        }

        return AtmAdminTokensPage(driver)
    }

    data class TokenMainInfo(
        val tokenName: String,
        val tokenDescription: String,
        val status: StatusToken
    )

    @Step("get token main information")
    @Action("Admin get token main information")
    fun getTokenMainInformation(tokenButton: Button): TokenMainInfo {
        return e {
            click(tokenButton)
            click(editToken)

            val tokenName = tokenName.text
            val tokenDescription = tokenDescription.text
            val status = tokenStatus.text.toUpperCase()
            e {
                click(cancel)
            }
            click(tokenButton)
            return@e TokenMainInfo(
                tokenName,
                tokenDescription,
                StatusToken.valueOf(status)
            )
        }

    }

    @Step("get token equivalent information")
    @Action("Admin get token equivalent information")
    fun getTokenEquivalentInformation(tokenButton: Button): String {
        return e {
            click(tokenButton)

            wait {
                click(usdEquivalentSettings)

                until("Wait for hidden preloader", 5L) {
                    usdEquivalentOverlay.preloader.getAttribute("style") == "visibility: hidden;"
                }
            }

            val usdEquivalentValue = usdEquivalentOverlay.usdEquivalent.value

            e {
                click(cancel)
            }

            click(tokenButton)
            return@e usdEquivalentValue
        }
    }

    @Step("get token equivalent information")
    @Action("Admin get token equivalent information")
    fun getTokenEquivalentInformation(tokenSymbol: CoinType, tokenName: String): String {
        val tokenButton = tokensTable.find {
            it[TICKER_SYMBOL]?.text == tokenSymbol.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>(tokenName)
            ?: error("Row with $tokenSymbol not found in table")

        return this.getTokenEquivalentInformation(tokenButton)
    }

    @Step("get token main information")
    @Action("Admin get token main information")
    fun getTokenMainInformation(tokenSymbol: CoinType): TokenMainInfo {
        val tokenButton = tokensTable.find {
            it[TICKER_SYMBOL]?.text == tokenSymbol.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>(tokenSymbol.tokenName)
            ?: error("Row with ${tokenSymbol.tokenSymbol} not found in table")

        return this.getTokenMainInformation(tokenButton)
    }

    data class TokenFeeInfo(
        val rate: String,
        val floor: String,
        val cap: String
    )

    @Step("get token fee information")
    @Action("Admin get token fee information")
    fun getTokenFeeInformation(tokenButton: Button): TokenFeeInfo {
        return e {
            click(tokenButton)
            click(transferFee)

            val rate = rate.text
            val floor = floor.text
            val cap = cap.text

            e {
                click(close)
            }
            click(tokenButton)
            return@e TokenFeeInfo(
                rate,
                floor,
                cap
            )
        }
    }

    @Step("get token fee information")
    @Action("Admin get token fee information")
    fun getTokenFeeInformation(tokenSymbol: String, tokenName: String): TokenFeeInfo {
        val tokenButton = tokensTable.find {
            it[TICKER_SYMBOL]?.text == tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>(tokenName)
            ?: error("Row with $tokenSymbol not found in table")

        return this.getTokenFeeInformation(tokenButton)
    }

    @Step("get token status information")
    @Action("Admin get token status information")
    fun getTokenStatus(coinType: CoinType, expectedStatus: String): String {
        e {
            sendKeys(search, coinType.tokenSymbol)
        }
        val row = tokensTable.find {
            it[TICKER_SYMBOL]?.text == coinType.tokenSymbol
        } ?: error("Can't find row with payment system '${coinType.tokenSymbol}'")

        val status = row[TOKEN_STATUS]?.text?.toLowerCase()

        assertThat("No row found with '$coinType'", status, hasToString(expectedStatus))
        return row[TICKER_SYMBOL]?.text ?: ""
    }

    @Step("get all values by column name")
    fun getColumnAsList(vararg columnNames: String): HashMap<String, MutableList<String>>? {
        return tokensTable.getColumnsByNamesForAllPages(*columnNames)
    }

    @Step("delete company from rules")
    fun deleteCompanyFromRules(token: CoinType, companyName: String): AtmAdminTokensPage {
        tokensTable.waitUntilReady()

        val tokenRow = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
            ?: error("Row with Ticker symbol $token not found in table")

        e {
            click(tokenRow)
            click(visibilityRules)
        }

        val companyRow = visibilityRulesTable.find {
            it[COMPANY]?.text == companyName
        }?.get(COMPANY)?.to<Button>("Company $companyName")
            ?: error("Row with Ticker symbol $companyName not found in table")

        e {
            click(companyRow)
            click(delete)
            click(close)
        }

        return AtmAdminTokensPage(driver)
    }

    @Step("add new rules")
    fun addNewRules(token: CoinType, companyNameValue: String): AtmAdminTokensPage {
        tokensTable.waitUntilReady()

        val tokenRow = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
            ?: error("Row with Ticker symbol $token not found in table")

        e {
            click(tokenRow)
            click(visibilityRules)
            click(customVisibilityRules)
            wait {
                untilPresented(companyName)
            }
            companyName.sendAndSelect(companyNameValue, companyNameValue, this@AtmAdminTokensPage)
            click(add)
            click(close)
        }
        return AtmAdminTokensPage(driver)

    }

    @Step("check data for rules")
    fun checkDataForRules(
        token: CoinType,
        companyNameValue: String,
        visibleStatus: String,
        userMaker: User
    ): AtmAdminTokensPage {
        tokensTable.waitUntilReady()

        val tokenRow = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol ${token.tokenSymbol}")
            ?: error("Row with Ticker symbol $token not found in table")
        e {
            click(tokenRow)
            click(visibilityRules)
        }

        val companyRow = visibilityRulesTable.find {
            it[COMPANY]?.text == companyNameValue
        } ?: error("Row with Ticker symbol $companyNameValue not found in table")

        val company = companyRow[COMPANY]?.text
//            val dateOfChange = companyRow[DATE_OF_CHANGE]?.text
        val visible = companyRow[VISIBLE]?.to<CheckBox>()?.isChecked().toString()
        val user = companyRow[USER]?.text


        assertThat("No row found with '$company'", company, hasToString(companyNameValue))
//            assertThat("No row found with '$dateOfChange'", dateOfChange, hasToString(LocalDateTime.now().toString()))
        assertThat("No row found with '$visible'", visible, hasToString(visibleStatus))
        assertThat("No row found with '$user'", user, hasToString(userMaker.email))

        return AtmAdminTokensPage(driver)
    }

    @Step("User works with USD equivalent settings window")
    fun userWorksWithUSDEquivalentSettingsWindow(coefficientValue: String) {
        e{
            click(uSDCCRow)
            click(usdEquivalentSettings)
        }
        check { assertTrue(isElementPresented(windowEquivalentSettingsCC)) }
        e { click(fromMarket)}
        wait { fromMarket.getAttribute("mat-radio-checked") }
        e {
            click(financialId)
            pressEnter(financialButton)
            sendKeys(coefficient, coefficientValue )
        }
        val financialIdInt = financialValueInt.text.substringBefore(" ")
        val result = financialIdInt.toInt() * coefficientValue.toInt()
        val equivalentResult = equivalentValueInt.text.toInt()
        assert { assertEquals(result, equivalentResult) }
        e { click(confirm) }
        Thread.sleep(2000)
        assert { elementNotPresentedWithCustomTimeout(windowEquivalentSettingsCC, 1) }
        check { assertTrue(isElementPresented(tokensTable)) }
        assert { assertEquals(newEquivalentValueInt.text.substringBefore(".").replace(",", "").toInt(), equivalentResult) }
    }

    @Step("User works with USD equivalent settings window for IT token")
    fun userWorksWithUSDEquivalentSettingsWindowForITToken(coefficientValue: String) {
        e{
            click(uSDIndustrialRow)
            click(usdEquivalentSettings)
        }
        check { assertTrue(isElementPresented(windowEquivalentSettingsIT)) }
        e { click(fromMarket)}
        wait { fromMarket.getAttribute("mat-radio-checked") }
        e {
            click(financialId)
            pressEnter(financialButton)
            sendKeys(coefficient, coefficientValue )
        }
        val financialIdInt = financialValueInt.text.substringBefore(" ")
        val result = financialIdInt.toInt() * coefficientValue.toInt()
        val equivalentResult = equivalentValueInt.text.toInt()
        assert { assertEquals(result, equivalentResult) }
        e { click(confirm) }
        Thread.sleep(2000)
        assert { elementNotPresentedWithCustomTimeout(windowEquivalentSettingsIT, 1) }
        check { assertTrue(isElementPresented(tokensTable)) }
        assert { assertEquals(newEquivalentValueInt.text.substringBefore(".").replace(",", "").toInt(), equivalentResult) }
    }

    @Step("User works with USD equivalent settings window for token. Fixed")
    fun userWorksWithUSDEquivalentSettingsWindowForTokenFixed(coefficientValue: String) {
        e{
            click(uSDCCRow)
            click(usdEquivalentSettings)
        }
        check { assertTrue(isElementPresented(windowEquivalentSettingsCC)) }
        e { click(fixed) }
        wait { fixed.getAttribute("mat-radio-checked") }
        e { sendKeys(inputForFixed, coefficientValue ) }
        e { click(confirm) }
        Thread.sleep(2000)
        assert { elementNotPresentedWithCustomTimeout(windowEquivalentSettingsCC, 1) }
        check { assertTrue(isElementPresented(tokensTable)) }
        val result = newEquivalentValueIntMin.text.substringBefore(".").toInt()
        assert { assertEquals(result, coefficientValue) }
        println("Это не быдлокод, значения $coefficientValue и $result равны")
    }
}
