package pages.atm

import io.qameta.allure.Step
import models.CoinType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmInput
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.to
import java.math.BigDecimal


@PageUrl("/custodian-fee")
open class AtmAdminCustodianFeePage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val TOKEN_STATUS = "Token status"
        const val TOKEN_NAME = "Token name"
        const val TOKEN_DESCRIPTION = "Token description"
        const val ISSUER = "Issuer"
        const val PRICE = "Price"
        const val FEE_IN = "Fee in % of AUM"
        const val UNDERLYING_ASSET = "Underlying asset"
    }

    @Name("Custodian Fee table")
    @FindBy(xpath = "//sdex-custodian-fee//sdex-grid")
    lateinit var custodianFeeTable: SdexTable

    @Name("Search")
    @FindBy(xpath = "//input[@data-placeholder='Filter']/ancestor::mat-form-field")
    lateinit var search: AtmInput

    @Name("Custodian Fee Input")
    @FindBy(xpath = "//sdex-amount-input[@formcontrolname='custodianfee']//input")
    lateinit var custodianFeeInput: TextInput

    @Name(" Set custodian fee ")
    @FindBy(xpath = "//button//span[contains(text(), 'Set custodian fee')]")
    lateinit var setCustodianFee: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(), 'CANCEL')]")
    lateinit var cancelButton: Button

    @Name("Confirm")
    @FindBy(xpath = "//span[contains(text(), 'CONFIRM')]")
    lateinit var confirmButton: Button

    data class TokenAllInfo(
        val tokenName: String,
        val tokenDescription: String,
        val tokenStatus: String,
        val issuer: String,
        val underlyingAsset: String,
        val price: String,
        val fee: String
    )


    @Step("set custodian fee")
    @Action("User set custodian fee")
    fun setFee(
        coinType: CoinType,
        amount: BigDecimal
    ): AtmAdminCustodianFeePage {
        e {
            sendKeys(search, coinType.tokenName)
            pressEnter(search)
        }
        val row = custodianFeeTable.find {
            it[TOKEN_NAME]?.text == coinType.tokenName
        }?.get(TOKEN_NAME)?.to<Button>("Ticker symbol ${coinType.tokenName}")
            ?: error("Row with Ticker symbol ${coinType.tokenName} not found in table")
        e {
            click(row)
            click(setCustodianFee)
            sendKeys(custodianFeeInput, amount.toString())
            click(confirmButton)
        }
        return AtmAdminCustodianFeePage(driver)
    }

    @Step("check custodian fee")
    @Action("User check custodian fee")
    fun checkFeeValueForToken(
        coinType: CoinType,
        expectedFeeAmount: BigDecimal
    ): AtmAdminCustodianFeePage {
        e {
            sendKeys(search, coinType.tokenName)
            pressEnter(search)
        }
        val row = custodianFeeTable.find {
            it[TOKEN_NAME]?.text == coinType.tokenName
        } ?: error("Row with Ticker symbol ${coinType.tokenName} not found in table")
        val feeAmount = row[FEE_IN]?.text
        MatcherAssert.assertThat(
            "Expected: $feeAmount, was: $expectedFeeAmount",
            feeAmount?.toBigDecimal(),
            closeTo(expectedFeeAmount, BigDecimal("0.01"))
        )

        return AtmAdminCustodianFeePage(driver)
    }

    @Step("check custodian fee")
    @Action("User check custodian fee")
    fun checkValuesOfColumns(
        tokenName: String,
        tokenDescription: String,
        status: String
    ): AtmAdminCustodianFeePage {
        e {
            sendKeys(search, tokenName)
            pressEnter(search)
        }
        val row = custodianFeeTable.find {
            it[TOKEN_NAME]?.text == tokenName
        } ?: error("Row with Ticker symbol $tokenName not found in table")
        val tokenStatusValue = row[TOKEN_STATUS]?.text
        val tokenDescriptionValue = row[TOKEN_DESCRIPTION]?.text


        MatcherAssert.assertThat(
            "No row found with '$tokenDescriptionValue'",
            tokenDescriptionValue,
            hasToString(tokenDescription)
        )
        MatcherAssert.assertThat(
            "No row found with '$tokenStatusValue'",
            tokenStatusValue,
            hasToString(status)
        )

        return AtmAdminCustodianFeePage(driver)
    }

    @Step("Get all values of the token")
    @Action("Admin get all values of the token")
    fun getAllTokenValues(tokenName: String): TokenAllInfo {
        e {
            sendKeys(search, tokenName)
            pressEnter(search)
        }
        val row = custodianFeeTable.find {
            it[TOKEN_NAME]?.text == tokenName
        } ?: error("Row with Ticker symbol $tokenName not found in table")
        val tokenStatus = row[TOKEN_STATUS]?.text.toString()
        val tokenDescription = row[TOKEN_DESCRIPTION]?.text.toString()
        val issuer = row[ISSUER]?.text.toString()
        val underlyingAsset = row[UNDERLYING_ASSET]?.text.toString()
        val price = row[PRICE]?.text.toString()
        val fee = row[FEE_IN]?.text.toString()
        return TokenAllInfo(
            tokenName,
            tokenDescription,
            tokenStatus,
            issuer,
            underlyingAsset,
            price,
            fee
        )
    }

    @Step("get custodian fee")
    @Action("User get custodian fee")
    fun getFeeValueForToken(
        coinType: CoinType
    ): String {
        e {
            sendKeys(search, coinType.tokenName)
            pressEnter(search)
        }
        val row = custodianFeeTable.find {
            it[TOKEN_NAME]?.text == coinType.tokenName
        } ?: error("Row with Ticker symbol ${coinType.tokenName} not found in table")
        val feeAmount = row[FEE_IN]?.text

        return feeAmount!!
    }
}