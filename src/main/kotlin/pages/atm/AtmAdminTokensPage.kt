package pages.atm

import io.qameta.allure.Step
import models.CoinType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.atm.AtmAdminTokensPage.EquivalentType.*
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.marketplace.SdexTableTokens
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to

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
    }

    enum class EquivalentType {
        FIXED, FROM_MARKET;
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
    @FindBy(xpath = "//sdex-tokens")
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

    @Name("Fixed")
    @FindBy(xpath = "//span[contains(text(), 'Fixed')]/ancestor::mat-radio-button")
    lateinit var fixed: AtmAdminRadio

    @Name("From market")
    @FindBy(xpath = "//div[contains(text(), 'From market data')]/ancestor::mat-radio-button")
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

//    endregion



    // TODO: Данный метод в связи с отстутствием кнопки "save" работает неправильно
    // TODO: Если все так и останется, то *ATMCH-1917* можно взять как пример реализации
    @Step("Change fee for token")
    @Action("Change fee for token")
    fun changeFeeForToken(
        token: String,
        chargeInToken: String,
        feeRate: String,
        floorAmount: String,
        capAmount: String
    ): AtmAdminTokensPage {
        e {
            sendKeys(search, token)
        }
        val row = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token
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
            deleteData(chargeInInput)
            chargeIn.sendAndSelect(chargeInToken, chargeInToken, this@AtmAdminTokensPage)
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
        token: String,
        equivalentType: EquivalentType,
        value: String,
        id: String
    ): AtmAdminTokensPage {
        e {
            sendKeys(search, token)
        }
        val row = tokensTable.find {
            it[TICKER_SYMBOL]?.text == token
        }?.get(TICKER_SYMBOL)?.to<Button>("Ticker symbol $token")
            ?: error("Row with Ticker symbol $token not found in table")
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
    fun editTokenStatus(tokenButton: Button, status: String): AtmAdminTokensPage {
        e {
            click(tokenButton)
            click(editToken)

            select(tokenStatus, status.toLowerCase())
            click(confirm)
        }

        return AtmAdminTokensPage(driver)
    }

    data class TokenMainInfo(
        val tokenName: String,
        val tokenDescription: String,
        val status: String
    )

    @Step("get token main information")
    @Action("Admin get token main information")
    fun getTokenMainInformation(tokenButton: Button): TokenMainInfo {
        return e {
            click(tokenButton)
            click(editToken)

            val tokenName = tokenName.text
            val tokenDescription = tokenDescription.text
            val status = tokenStatus.text

            e {
                click(cancel)
            }
            click(tokenButton)
            return@e TokenMainInfo(
                tokenName,
                tokenDescription,
                status
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
    fun getTokenMainInformation(tokenSymbol: CoinType, tokenName: String): TokenMainInfo {
        val tokenButton = tokensTable.find {
            it[TICKER_SYMBOL]?.text == tokenSymbol.tokenSymbol
        }?.get(TICKER_SYMBOL)?.to<Button>(tokenName)
            ?: error("Row with $tokenSymbol not found in table")

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
}