package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.CoinType.CC
import models.CoinType.IT
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import models.user.interfaces.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindAll
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.marketplace.AtmMarketplaceIndustrialCard
import pages.htmlelements.blocks.atm.marketplace.AtmMarketplaceTokens
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.containsIgnoreCaseXpath

@PageUrl("/trading/market")
class AtmMarketplacePage(driver: WebDriver) : AtmPage(driver) {

    @Name("Invalid or wrong code")
    @FindBy(xpath = "//div[contains(text(),'Invalid key')] | //div[contains(text(),'Wrong code')]")
    lateinit var invalidOrWrongCode: TextBlock

    @Name("Currency coin")
    @FindBy(xpath = "//div[contains(text(), 'CURRENCY COIN')]")
    lateinit var currencyCoinButton: Button

    @Name("Fractionalized token")
    @FindBy(xpath = "//div[contains(text(), 'FRACTIONALIZED TOKEN')]")
    lateinit var fractionalizedTokenButton: Button

    @Name("Validation token")
    @FindBy(xpath = "//div[contains(text(), 'VALIDATION TOKEN')]")
    lateinit var validationTokenButton: Button

    @Name("Industrial token")
    @FindBy(xpath = "//div[contains(text(), 'INDUSTRIAL TOKEN')]")
    lateinit var industrialTokenButton: Button

    @Name("New order")
    @FindBy(xpath = "//button//span[contains(text(), 'New order')]")
    lateinit var newOrderButton: Button

    @Name("Dates Radio Buttons")
    @FindBy(xpath = "//nz-radio-group[@formcontrolname='subIssues']")
    lateinit var datesRadio: AtmRadio

    @Name("Details Button")
    @FindBy(xpath = "//atm-collapse-panel")
    lateinit var detailsButton: Button

    @Name("Details Text")
    @FindBy(xpath = "//atm-ind-info-detail")
    lateinit var detailsText: TextBlock

    @Name("Buy")
    @FindBy(xpath = "//span[contains(text(), 'Buy')]")
    lateinit var buyButton: Button

    @Name("Receive")
    @FindBy(xpath = "//span[contains(text(),'Receive')]")
    lateinit var receiveButton: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(), 'Cancel')]")
    lateinit var cancelButton: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(), 'Cancel')]")
    lateinit var cancel: Button

    @Name("Submit")
    @FindBy(xpath = "//button//span[contains(text(), 'Submit')]")
    lateinit var submitButton: Button

    @Name("All tokens in contract")
    @FindBy(xpath = "//div[./label[contains(text(), 'All tokens in contract')]]//nz-switch")
    lateinit var allTokensButton: Button

    @Name("Select maturity date")
    @FindBy(xpath = "//atm-custom-select[contains(@formcontrolname, 'maturity')]/nz-select | //form/nz-form-item[2]//nz-select")
    lateinit var selectMaturityDate: AtmSelect

    @Name("Comment")
    @FindBy(xpath = "//input[contains(@formcontrolname, 'comment')]")
    lateinit var comment: TextInput

    @Name("Done")
    @FindBy(xpath = "//button//span[contains(text(), ' Done ')]")
    lateinit var doneButton: Button

    @Name("Select wallet")
    @FindBy(xpath = "//atm-wallet-address-select-control//nz-select")
    lateinit var selectWallet: AtmSelect

    @Name("Token multiple quantity")
    @FindAll(
        FindBy(xpath = "//atm-amount-input[@formcontrolname='amount']//input")
    )
    lateinit var tokenMultipleQuantity: MutableList<TextInput>

    @Name("Token quantity")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='quantity']//input")
    lateinit var tokenQuantity: TextInput

    @Name("Limit amount")
    @FindBy(xpath = "//span[contains(@class, 'token-buy-order')]")
    lateinit var amountLimit: AtmAmount

    @Name("Limit amount IT")
    @FindBy(xpath = "//span[contains(@class, 'token-vt-receive-order')]")
    lateinit var amountLimitIt: AtmAmount

    @Name("Balance")
    @FindBy(xpath = "//span[contains(text(), 'AVAILABLE BALANCE')]/ancestor::atm-property-value//atm-amount")
    lateinit var balanceUser: AtmAmount

    @Name("Token Info")
    @FindBy(xpath = "//atm-separated-card")
    lateinit var tokenInfo: AtmMarketplaceTokens

    @Name("Tokens List")
    @FindBy(xpath = "//atm-separated-card")
    lateinit var tokensList: AtmTable<AtmMarketplaceTokens>

    @Name("Tokens List")
    @FindBy(xpath = "//div[@class='market__list']")
    lateinit var tokensCards: AtmTable<AtmMarketplaceIndustrialCard>


    @Action("User buy token {coinType.tokenName}")
    @Step("buy token")
    fun buyOrReceiveToken(
        coinType: CoinType,
        amount: String,
        user: DefaultUser,
        wallet: SimpleWallet,
        maturityDate: String = "",
        needReceive: Boolean = false
    ) {
        e {
            chooseToken(coinType)
            click(newOrderButton)
            if (needReceive) click(receiveButton)
            deleteData(tokenQuantity)
            sendKeys(tokenQuantity, amount)
            select(selectWallet, wallet.publicKey)
            wait {
                until("Count should be $amount") {
                    check { isElementContainingTextPresented(amount.split(".").last()) } or
                            check { isElementContainingTextPresented(amount.split(".").first()) }
                }
            }
            when (coinType) {
                IT -> select(selectMaturityDate, maturityDate)
            }
            click(submitButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)
        if (check { isElementContainingTextPresented("Order failed", 10L) }) error("Found message Order failed")
    }

    @Step("User choose token {coinType.tokenName}")
    @Action("choose token")
    fun chooseToken(coinType: CoinType) {
        val tokenLocator = containsIgnoreCaseXpath(
            "atm-market-item//div[contains(@class, 'token-name')]",
            "text()",
            coinType.tokenName
        )
        val tokenButton = try {
            wait {
                untilPresented<Button>(tokenLocator, "${coinType.tokenName} button")
            }
        } catch (e: TimeoutException) {
            error("Couldn't find token with name ${coinType.tokenName}. Expected token name to be equal to name in Admin Token Panel")
        }
        e {
            click(tokenButton)
        }
    }

    //TODO неоюходим хороший дебаг метода Choose token так как он нормаьно не выбирает ЕТС
    @Step("User choose token {coinType.tokenName}")
    @Action("choose token")
    fun chooseEtcToken(coinType: CoinType) {
        val tokenLocator =
            By.xpath(".//atm-market-item//div[contains(@class, 'etc-item')][text()=' ${coinType.tokenName.toUpperCase()} ']")

        val tokenButton = try {
            wait {
                untilPresented<Button>(tokenLocator, "${coinType.tokenName} button")
            }
        } catch (e: TimeoutException) {
            error("Couldn't find token with name ${coinType.tokenName}. Expected token name to be equal to name in Admin Token Panel")
        }
        e {
            click(tokenButton)
        }
    }

    @Step("check wallet select name")
    @Action("User check wallet select name")
    fun checkWalletName(coinType: CoinType, walletName: String, walletNum: String) {
        val regex = "[.()\\r\\n]".toRegex()
        chooseToken(coinType)
        e {
            click(newOrderButton)
            select(selectWallet, walletNum)
        }
        val name = wait {
            untilPresented<WebElement>(By.xpath("//atm-wallet-address-select-control//nz-select-item//div"))
        }.text
        val result = name.replace(regex, "")
        assertThat(
            "Wallet name is $walletName and walletNum is $walletNum",
            result,
            hasToString("$walletName $walletNum")
        )
    }

    @Step("check token visibility")
    @Action("User check token")
    fun isTokenVisible(coinType: CoinType) {
        val tokenLocator = containsIgnoreCaseXpath("div", "text()", coinType.tokenName)
        try {
            wait {
                untilPresented<Button>(tokenLocator, "${coinType.tokenName} button")
            }
        } catch (e: TimeoutException) {
            error("Couldn't find token with name ${coinType.tokenName}. Expected token name to be equal to name in Admin Token Panel")
        }
    }
}