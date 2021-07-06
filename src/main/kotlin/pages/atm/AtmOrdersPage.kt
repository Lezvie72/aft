package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.user.classes.MainWallet
import models.user.interfaces.SimpleWallet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.orders.OrdersItem
import pages.htmlelements.elements.AtmAmount
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.AtmSelectLazy
import pages.htmlelements.elements.AtmTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.containsIgnoreCaseXpath
import utils.helpers.to
import java.math.BigDecimal

@PageUrl("/orders")
class AtmOrdersPage(driver: WebDriver) : AtmPage(driver) {

    enum class FilterByDealType {
        ALL, DISTRIBUTIONAL, REDEMPTION, URGENT
    }

    enum class FilterByStatus {
        ALL, EXPIRED, ERROR, DECLINED, EXECUTED, SUBMITTED,
    }

    @Name("Filter By Deal Type")
    @FindBy(xpath = "//atm-deal-type-select-control//nz-select")
    lateinit var filterByDealType: AtmSelectLazy

    @Name("Filter By Status")
    @FindBy(xpath = "//atm-status-select-control//nz-select")
    lateinit var filterByStatus: AtmSelectLazy

    @Name("Signature details")
    @FindBy(xpath = "//atm-order-signature-details//nz-collapse")
    lateinit var signatureDetails: Button

    @Name("Currency coin")
    @FindBy(xpath = "//div[contains(text(), 'CC')]")
    lateinit var currencyCoinButton: Button

    @Name("Fractionalized token")
    @FindBy(xpath = "//div[contains(text(), 'FT')]")
    lateinit var fractionalizedTokenButton: Button

    @Name("Validation token")
    @FindBy(xpath = "//div[contains(text(), 'VT')]")
    lateinit var validationTokenButton: Button

    @Name("Industrial token")
    @FindBy(xpath = "//div[contains(text(), 'IT')]")
    lateinit var industrialTokenButton: Button

    @Name("Select wallet")
    @FindBy(xpath = "//atm-wallet-address-select-control//nz-select")
    lateinit var selectWallet: AtmSelect

    @Name("Token quantity")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='quantity']//input")
    lateinit var tokenQuantity: TextInput

    @Name("Limit amount")
    @FindBy(xpath = "//span[contains(@class, 'token-buy-order')]")
    lateinit var amountLimit: AtmAmount

    @Name("Balance")
    @FindBy(xpath = "//div[contains(text(), 'AVAILABLE BALANCE')]/ancestor::atm-property-value//atm-amount")
    lateinit var balanceUser: AtmAmount

    @Name("Order token item")
    @FindBy(xpath = "//atm-orders-token-item")
    lateinit var orderTokenItem: AtmAmount

    @Name("EXECUTED")
    @FindBy(xpath = "//nz-tag[contains(text(),'EXECUTED')]")
    lateinit var executed: AtmAmount

    @Name("PENDING")
    @FindBy(xpath = "//nz-tag[contains(text(),'PENDING')]")
    lateinit var pending: AtmAmount

    @Name("Order list")
    @FindBy(css = "atm-orders-list")
    lateinit var orderList: AtmTable<OrdersItem>

    @Name("Orders table")
    @FindBy(xpath = "//div[contains(@class, 'orders-list__request-list')]")
    lateinit var ordersTable: AtmTable<OrdersItem>

    @Name("Available Balance")
    @FindBy(xpath = "//span[contains(text(), ' AVAILABLE ')]/ancestor::div[contains(@class, 'property')]//atm-amount")
    lateinit var availableBalance: AtmAmount

    @Name("Expected Balance")
    @FindBy(xpath = "//span[contains(text(), ' EXPECTED ')]/ancestor::div[contains(@class, 'property')]//atm-amount")
    lateinit var expectedBalance: AtmAmount

    @Name("Chat input")
    @FindBy(xpath = "//atm-chat//textarea[@formcontrolname='message']")
    lateinit var chatInput: TextInput

    @Name("Chat send button")
    @FindBy(xpath = "//atm-chat//button")
    lateinit var chatSendButton: Button

    @Name("ITEMS DETAILS")
    @FindBy(xpath = "//atm-collapse-panel")
    lateinit var itemsDetails: Button

    @Name("Bar list")
    @FindBy(xpath = "//atm-bars-list")
    lateinit var barList: Button

    @FindBy(xpath = ".//atm-order-request-status//nz-tag")
    @Name("Status")
    lateinit var orderStatus: Button

    @FindBy(xpath = ".//span[contains(text(), 'AMOUNT')]/ancestor::div[contains(@class, 'property')]//atm-amount")
    @Name("Amount")
    lateinit var amountInOrder: AtmAmount

    data class TokenBalance(
        val availableBalance: String,
        val expectedBalance: String
    )

    @Step("Choose and click wallet")
    @Action("Choose and click wallet")
    fun chooseWallet(walletName: String) {
        val wallet = wait {
            untilPresented<WebElement>(By.xpath("//atm-orders//a[text()=' $walletName ']"))
        }.to<Button>("Wallet '$walletName'")
        e {
            click(wallet)
        }
    }

    @Step("User choose token")
    @Action("choose token")
    fun chooseToken(coinType: CoinType) {
       Thread.sleep(5000)
        val tokenLocator = containsIgnoreCaseXpath("div", "text()", coinType.tokenSymbol)
        val tokenButton = try {
            wait {
                untilPresented<Button>(tokenLocator, "${coinType.tokenSymbol} button")
            }
        } catch (e: TimeoutException) {
            error("Couldn't find token with name ${coinType.tokenSymbol}. Expected token name to be equal to name in Admin Token Panel")
        }
        e {
            click(tokenButton)
        }
    }

    @Step("User find and open redemption offer")
    fun findOrderAndCheckStatus(wallet: MainWallet, coinType: CoinType, amount: BigDecimal, expectedStatus: String) {
        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
        }
        val order = orderList.find {
            it.amountOrder == amount
        } ?: error("Can't find offer with unit price '$amount'")
        order.checkStatus(expectedStatus)
    }

    @Step("User find, open order card and check data")
    fun findOrderOpenCardAndCheckData(
        wallet: MainWallet,
        coinType: CoinType,
        amount: BigDecimal,
        expectedStatus: String
    ) {
        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
        }
        val order = orderList.find {
            it.amountOrder == amount
        } ?: error("Can't find offer with unit price '$amount'")
        e {
            click(order)
        }
        checkStatus(expectedStatus)
        val amountExpected = amountInOrder.amount.toString()
        assertThat("Order", amountExpected, Matchers.`is`(amount.toString()))
    }

    @Step("User find and open order card")
    fun findOrderAndOpenCard(
        wallet: MainWallet,
        coinType: CoinType,
        amount: BigDecimal
    ) {
        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
        }
        val order = orderList.find {
            it.amountOrder == amount
        } ?: error("Can't find offer with unit price '$amount'")
        e {
            click(order)
        }
    }

    fun findSubmittedOrder(
        coinType: CoinType,
        wallet: SimpleWallet,
        requestor: String,
        amount: BigDecimal
    ): OrdersItem {
        return getOrdersTable(coinType, wallet).find {
            it.status.text == "SUBMITTED"
                    && it.requestor.text == requestor
                    && it.totalRequested.amount == amount
        } ?: error("Specific order was not found by requestor: $requestor and amount: $amount")
    }

    fun findDeclinedOrder(coinType: CoinType, wallet: SimpleWallet, requestor: String, amount: BigDecimal): OrdersItem {
        return getOrdersTable(coinType, wallet).find {
            it.status.text == "DECLINED"
                    && it.requestor.text == requestor
                    && it.totalRequested.amount == amount
        } ?: error("Specific order was not found by requestor: $requestor and amount: $amount")
    }

    // TODO: Можно оптимизировать
    @Step("Find submitted ordersTable")
    @Action("User trying to find new order")
    private fun getOrdersTable(coinType: CoinType, wallet: SimpleWallet): AtmTable<OrdersItem> {
        return e {
            select(selectWallet, wallet.publicKey)
            chooseToken(coinType)

            ordersTable
        }
    }

    @Step("check the message")
    fun checkTheMessageFromChat(message: String) {
        val textMessage = wait {
            untilPresented<WebElement>(By.xpath(".//atm-chat-message//atm-span[contains(text(),'${message}')]"))
        }.to<Button>("Card '$message'")
        assert { elementPresented(textMessage) }
        assertThat("Order", textMessage.text, Matchers.`is`(message))
    }

    @Step("check the message")
    fun checkStatus(status: String) {
        val cardWithStatus = wait {
            untilPresented<WebElement>(By.xpath(".//atm-order-request-status//nz-tag[contains(@class,'request__status')][contains(text(),'${status.toUpperCase()}')]"))
        }.to<Button>("Card '$status'")
        assert { elementPresented(cardWithStatus) }
        assertThat("Order", cardWithStatus.text, Matchers.`is`(status.toUpperCase()))
    }
}