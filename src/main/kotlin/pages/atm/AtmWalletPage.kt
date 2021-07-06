package pages.atm


import io.qameta.allure.Step
import models.CoinType
import models.CoinType.*
import models.user.classes.DefaultUser
import models.user.classes.MainWallet
import models.user.classes.OtfWallet
import models.user.interfaces.SimpleWallet
import models.user.interfaces.User
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.junit.Assert.assertTrue
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.atm.AtmIssuancesPage.StatusType
import pages.atm.AtmWalletPage.RedemptionTypeETC.AUTO
import pages.atm.AtmWalletPage.RedemptionTypeETC.MANUAL
import pages.atm.AtmWalletPage.WalletType.*
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.assign.AssignEmployeesItem
import pages.htmlelements.blocks.atm.transactions.TransactionsItem
import pages.htmlelements.elements.*
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.*
import utils.helpers.*
import utils.isChecked
import java.math.BigDecimal

@PageName("Wallets")
@PageUrl("/wallets")
class AtmWalletPage(driver: WebDriver) : AtmPage(driver) {

    companion object {
        const val ASSERT_TEXT_HELD_IN_ORDERS = "HELD IN ORDERS"
        const val ASSERT_TEXT_AVAILABLE = " AVAILABLE "
        const val ASSERT_TEXT_BALANCES = "Balances"
    }

    override fun getTimeoutInSeconds(): Long {
        return 20L
    }

    enum class OperationType {
        REDEMPTION, TRANSFER;
    }

    enum class WalletType {
        OTF, MAIN, ISSUER;
    }


    enum class RedemptionTypeETC {
        AUTO, MANUAL;
    }

    @Name("My transactions")
    @FindBy(xpath = "//atm-transactions")
    lateinit var transactionsList: AtmTable<TransactionsItem>

    @Name("Register wallet")
    @FindBy(xpath = "//span[contains(text(), 'Register wallet')]")
    lateinit var registerWalletButton: Button

    @Name("Selected type wallet - main")
    @FindBy(xpath = "//label[.//span[contains(text(), 'Main wallet')]]")
    lateinit var typeWalletMainCheck: Button

    @Name("Selected secret key storage type - Custody")
    @FindBy(xpath = "//label[.//*[contains(text(), 'Custody')]]")
    lateinit var secretKeyStorageTypeCustody: Button

    @Name("Next button")
    @FindBy(xpath = "//button//span[contains(text(), 'Register wallet')]")
    lateinit var nextButton: Button

    @Name("Disabled Next button")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(), 'Register wallet')]")
    lateinit var disabledNextButton: Button

    @Name("Redeem auto button")
    @FindBy(xpath = "//span[contains(text(),'Auto')]/ancestor::label")
    lateinit var redeemAuto: Button

    @Name("Redeem manual button")
    @FindBy(xpath = "//span[contains(text(),'Manual')]/ancestor::label")
    lateinit var redeemManual: Button

    @Name("First bar ETC")
//    @FindBy(xpath = "//atm-bar-item[1]")
    @FindBy(xpath = "//atm-bar-item")
    lateinit var firstBarEtc: Button

    @Name("Disabled Submit button")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(), 'Submit')]")
    lateinit var disabledSubmitButton: Button

    @Name("Main wallet")
    @FindBy(xpath = "//span[contains(text(),'Main wallet')]/ancestor::label")
    lateinit var mainWalletRadioButton: AtmRadio

    @Name("OTF Wallet")
    @FindBy(xpath = "//span[contains(text(),'OTF wallet')]/ancestor::label")
    lateinit var otfWalletRadioButton: AtmRadio

    @Name("Issuer Wallet")
    @FindBy(xpath = "//span[contains(text(),'Issuer wallet')]/ancestor::label")
    lateinit var issuerWalletRadioButton: AtmRadio

    @Name("Label wallet")
    @FindBy(xpath = "//input[@formcontrolname='name']")
    lateinit var labelWallet: TextInput

    @Name("Single authorization")
    @FindBy(xpath = "//span[contains(text(),'Single authorization')]/ancestor::label")
    lateinit var singleAuthorizationRadioButton: AtmRadio

    @Name("Dual authorization")
    @FindBy(xpath = "//span[contains(text(),'Dual authorization')]/ancestor::label")
    lateinit var dualAuthorizationRadioButton: AtmRadio

    @Name("Field Secret key storage type with prefilled text: Manual")
    @FindBy(xpath = "//p[contains(text(),'Manual')]")
    lateinit var secretStorageTypeManual: TextBlock

    @Name("Input field Public key")
    @FindBy(xpath = "//input[@formcontrolname='publicKey']")
    lateinit var publicKey: TextInput

    @Name("Button Generate keys")
    @FindBy(xpath = "//span[contains(text(),'Generate keys')]")
    lateinit var generateKeys: Button

    @Name("Input field Message to sign")
    @FindBy(xpath = "//input[@formcontrolname='nonce']")
    lateinit var messageToSign: TextInput

    @Name("Link Paste from clipboard")
    @FindBy(xpath = "//button[contains(text(),'Paste from clipboard')]")
    lateinit var pasteFromClipboard: Link

    @Name("Copy to clipboard link")
    @FindBy(xpath = "//button[contains(text(),'Copy to clipboard')]")
    lateinit var copyToClipboard: Link

    @Name("Copy wallet ID to clipboard")
    @FindBy(xpath = "//button[contains(text(),'Copy wallet ID to clipboard')]")
    lateinit var copyWalletIDToClipboard: Link

    @Name("Button: sing message")
    @FindBy(xpath = "//span[contains(text(),'Sign message')]")
    lateinit var signMessage: Button

    @Name("Input field: Signature")
    @FindBy(xpath = "//input[@formcontrolname='sign']")
    lateinit var signatureInput: TextInput

    @Name("Input field: Enter you private key")
    @FindBy(xpath = "//atm-hide-value-input[@formcontrolname='secretKey']//textarea")
    lateinit var privateKeyInput: TextInput

    @Name("Button Cancel")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var cancel: Button

    @Name("Button Cancel Redemption/Transfer")
    @FindBy(xpath = "//button//span[contains(text(),'Cancel')]")
    lateinit var cancelButton: Button

    @Name("Register wallet")
    @FindBy(xpath = "//span[contains(text(),'Register wallet')]")
    lateinit var registerWallet: Button

    @Name("Refresh reference")
    @FindBy(xpath = "//span[contains(text(),' Refresh ')]")
    lateinit var refreshReference: Button

    @Name("Create wallet button")
    @FindBy(xpath = "//a//span[contains(text(),'CREATE WALLET')]")
    lateinit var refreshWalletData: Button

    @Name("Fiat deposit details")
    @FindBy(xpath = "//atm-separated-card//span[contains(text(), 'FIAT DEPOSIT DETAILS')]")
    lateinit var depositDetails: Button

    @Name("Reference number")
    @FindBy(xpath = "//atm-wallet//div[@class='fiat-requisites__val']//atm-span")
    lateinit var referenceNumber: Button

    @Name("Balance token")
    @FindBy(xpath = "//atm-property-value//span[contains(text(), 'Available')]//ancestor::div[3]//atm-amount[1]")
    lateinit var balanceTokenUser: AtmAmount

    @Name("Held in Orders/Offers")
    @FindBy(xpath = "//atm-property-value//span[contains(text(), 'Held in orders')]//ancestor::div[3]//atm-amount | //atm-property-value//span[contains(text(), 'Held in offers')]//ancestor::atm-property-value//atm-amount ")
    lateinit var heldInOrders: AtmAmount

    @Name("Currency coin")
    @FindBy(xpath = "//a[contains(@href, 'CC')]")
//    @FindBy(xpath = "//a[contains(text(), 'CUSDNN-01-2020')]")
    lateinit var currencyCoinButton: Button

    @Name("Validation token")
    @FindBy(xpath = "//a[contains(@href, 'VT')]")
//    @FindBy(xpath = "//a[contains(text(), 'VALIDTOK-01-2020')]")
    lateinit var validationTokenButton: Button

    @Name("Fractionalized token")
    @FindBy(xpath = "//a[contains(@href, 'FT')]")
//    @FindBy(xpath = "//a[contains(text(), 'Fractional Token')]")
    lateinit var fractionalizedTokenButton: Button

    @Name("Industrial token")
    @FindBy(xpath = "//a[contains(@href, 'IT')]")
//    @FindBy(xpath = "//a[contains(text(), 'Industrial Token')]")
    lateinit var industrialTokenButton: Button

    @Name("Industrial token")
    @FindBy(xpath = "//a[contains(text(), 'IDT')]")
    lateinit var industrialTokenButtonAlt: Button

    @Name("Fiat token")
    @FindBy(xpath = "//a[contains(@href, 'FIAT')]")
//    @FindBy(xpath = "//a[contains(text(), 'Fiat Token')]")
    lateinit var fiatTokenButton: Button

    @Name("Balance")
    @FindBy(xpath = "//span[contains(text(), 'BALANCE')]/ancestor::atm-property-value//atm-amount")
    lateinit var balanceUser: AtmAmount

    @Name("Balance in operation")
    @FindBy(xpath = "//span[contains(text(), 'AVAILABLE BALANCE')]/ancestor::atm-property-value//atm-amount")
//    @FindBy(xpath = "//atm-wallet-info//atm-amount")
    lateinit var balanceInOperation: AtmAmount

    @Name("Main wallet")
    @FindBy(xpath = "//atm-wallet-item//div[contains(text(),'Main 1')]")
    lateinit var mainWallet: Button

    @Name("Confirm Signature")
    @FindBy(xpath = "//span[contains(text(),'Confirm')]")
    lateinit var confirmSignature: Button

    @Name("OTF wallet ticker")
    @FindBy(xpath = "//div[contains(text(),'OTF 1')]")
    lateinit var otfWalletTicker: Button

    @Name("Main wallet ticker")
    @FindBy(xpath = "//div[contains(text(),'Main 1')]")
    lateinit var mainWalletTicker: Button

    @Name("Refresh message to sign")
    @FindBy(xpath = "//span[contains(text(),'Message to sign')]//ancestor::nz-form-control//button//i")
    lateinit var refreshMessageToSign: Button

    @Name("Label Wallet")
    @FindBy(css = "atm-wallet-info > h2.wallet-info__name")
    lateinit var walletName: TextBlock

    @Name("Wallet status")
    @FindBy(css = "atm-wallet-info nz-tag")
    lateinit var walletStatus: TextBlock

    @Name("Wallet id")
    @FindBy(xpath = "//span[contains(text(), ' WALLET ID ')]")
    lateinit var walletId: TextBlock

    @Name("Wallet balance")
    @FindBy(xpath = "//span[contains(text(),' BALANCE ')]")
    lateinit var walletBalance: TextBlock

    @Name("OTF wallet type")
    @FindBy(xpath = "//div//atm-span[contains(@class, 'wallet-info')][contains(text(),'OTF')]")
    lateinit var otfWalletType: TextBlock

    @Name("Wallet authorization type")
    @FindBy(xpath = "//span[contains(text(),'SIGNATURE TYPE')]")
    lateinit var walletAuthorizationType: TextBlock

    @Name("Wallet storage")
    @FindBy(xpath = "//span[contains(text(),' STORAGE ')]")
    lateinit var walletStorage: TextBlock

    @Name("Assign button")
    @FindBy(xpath = "//button[@routerlink='assign']")
    lateinit var assign: Button

    @Name("Back to wallet")
    @FindBy(xpath = "//span[contains(text(),'Back to wallet')]")
    lateinit var backToWallet: Button

    @Name("Controller")
    @FindBy(xpath = ".//span[@class='ant-checkbox-inner']//ancestor::label")
    lateinit var controllerCheckbox: CheckBox

    @Name("Fiat token")
    @FindBy(xpath = "//atm-token-item//a[text()='Fiat Token']")
    lateinit var fiatToken: Button

    @Name("Fiat token from menu")
    @FindBy(xpath = "//atm-sub-nav-menu//a[text()='Fiat Token']")
    lateinit var fiatTokenFromMenu: Button

    @Name("Currency coin")
    @FindBy(xpath = "//atm-token-item//a[contains(@href, 'CC')]")
    lateinit var currencyCoin: Button

    @Name("Atomyze token")
    @FindBy(xpath = "//atm-token-item//a[text()='Atomyze USD']")
    lateinit var atomyzeToken: Button

    @Name("Atomyze token from menu")
    @FindBy(xpath = "//atm-sub-nav-menu//a[text()=' Atomyze USD ']")
    lateinit var atomyzeTokenFromMenu: Button

    @Name("Move")
    @FindBy(xpath = "//span[contains(text(),'Move')]")
    lateinit var move: Button

    @Name("Move token quantity")
    @FindBy(xpath = "//atm-amount-input//nz-input-number//input")
    lateinit var moveTokenQuantity: TextInput

    @Name("Transfer")
    @FindBy(xpath = "//span[contains(text(),'Transfer')]")
    lateinit var transfer: Button

    @Name("Transfer ETC")
    @FindBy(xpath = "//a[contains(text(),'Transfer')]")
    lateinit var transferEtc: Button

    @Name("From wallet")
    @FindBy(xpath = "//atm-wallet-address-select-control[@formcontrolname='from']//nz-select")
    lateinit var fromWallet: AtmSelect

    @Name("To wallet")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='to']")
    lateinit var toWallet: TextInput

    @Name("Amount")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='amount']//input")
    lateinit var amountTransfer: TextInput

    @Name("AmountRedeem")
    @FindBy(xpath = "//atm-amount-input//input")
    lateinit var amountRedeem: TextInput

    @Name("Add bank details")
    @FindBy(xpath = "//a[@href='/profile/bank-accounts']")
    lateinit var addBankDetails: TextInput

    @Name("Select bank details")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='bankname']")
    lateinit var selectBankDetails: AtmSelect

    @Name("Transfer note")
    @FindBy(xpath = "//nz-form-control//input[@formcontrolname='note']")
    lateinit var transferNote: TextInput

    @Name("Submit button")
    @FindBy(xpath = "//span[contains(text(),'Submit')]//ancestor::button")
    lateinit var submit: Button

    @Name("Submit for check disabling")
    @FindBy(xpath = "//span[contains(text(),'Submit')]//ancestor::button")
    lateinit var submitForCheckDisabling: Button

    @Name("Block button")
    @FindBy(xpath = "//span[contains(text(),'Block')]")
    lateinit var blockButton: Button

    @Name("Show zero balance button")
    @FindBy(xpath = "//nz-switch//button")
    lateinit var showZeroBalance: Button

    @Name("Wallet employee role")
    @FindBy(xpath = "//span[contains(text(),'YOUR EMPLOYEE ROLE')]")
    lateinit var yourEmployeeRole: TextBlock

    @Name("Wallet role")
    @FindBy(xpath = "//span[contains(text(),'YOUR WALLET ROLE')]")
    lateinit var yourWalletRole: TextBlock

    @Name("token's tiker")
    @FindBy(xpath = "//atm-tokens-list")
    lateinit var tokenTiker: TextBlock

    @Name("Underlying asset")
    @FindBy(xpath = "//span[contains(text(), 'UNDERLYING ASSET')]")
    lateinit var underlyingAsset: TextBlock

    @Name("AVAILABLE BALANCE")
    @FindBy(xpath = "//span[contains(text(), 'AVAILABLE')]")
    lateinit var availableBalance: TextBlock

    @Name("Balance")
    @FindBy(xpath = "//atm-span[contains(text(), 'Balances')]")
    lateinit var walletInsideBalance: TextBlock

    @Name("Block")
    @FindBy(xpath = "//button//span[contains(text(),'Block')]")
    lateinit var blockWallet: Button

    @Name("Select wallet type")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='type']")
    lateinit var typeWallet: Button

    @Name(" Show replenishment details ")
    @FindBy(xpath = "//button[contains(text(),'Show replenishment details')]")
    lateinit var showReplenishmentDetails: Button

    @Name("Wallet ID")
//    @FindBy(xpath = "//atm-qr-wallet-dialog/div[1]/div//div[@class='property__val']")
    @FindBy(xpath = "//atm-qr-wallet-dialog//atm-property-value[3]//atm-span")
    lateinit var walletIDNumber: Button

    @Name("Refresh")
    @FindBy(xpath = "//nz-form-control//button")
    lateinit var refresh: Button

    @Name("Close")
    @FindBy(xpath = "//div//span[contains(text(), 'CLOSE')]")
    lateinit var close: Button

    @Name("Reference number history")
    @FindBy(xpath = "//nz-collapse//atm-collapse-panel")
    lateinit var referenceNumberHistory: Button

    @Name("Reference history")
    @FindBy(xpath = "//atm-collapse-panel//div[2]//div[contains(@class,'ant-collapse-content-box')]")
    lateinit var referenceHistory: Button

    @Name("Select currency")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='currency']")
    lateinit var selectCurrency: AtmSelect

    @Name("Select bic type")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='bicType']")
    lateinit var selectBicType: AtmSelect

    @Name("Submit")
    @FindBy(xpath = "//button//span[contains(text(), 'Submit')]")
    lateinit var submitButton: Button

    @Name("Proceed")
    @FindBy(xpath = "//button//span[contains(text(), 'Proceed')]")
    lateinit var proceedButton: Button

    @Name("Done")
    @FindBy(xpath = "//button//span[contains(text(), 'Done')]")
    lateinit var doneButton: Button

    @Name("Token quantity")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='quantity']//input")
    lateinit var tokenQuantity: TextInput

    @Name("Redemption")
    @FindBy(xpath = "//span[contains(text(),'Redeem')]//ancestor::button")
    lateinit var redemption: Button

    @Name("Withdraw")
    @FindBy(xpath = "//span[contains(text(),'Withdraw')]")
    lateinit var withdraw: Button

    @Name("Trade")
    @FindBy(xpath = "//span[contains(text(),'Trade')]")
    lateinit var trade: Button

    @Name("New order")
    @FindBy(xpath = "//button//span[contains(text(), 'New order')]")
    lateinit var newOrderButton: Button

    @Name("Limit amount")
    @FindBy(xpath = "//span[contains(@class, 'text-grey')]")
    lateinit var amountLimit: AtmAmount

    @Name("Token quantity to buy")
    @FindBy(xpath = "//atm-amount-input//input")
    lateinit var tokenQuantityToBuy: AtmAmount

    @Name("Token quantity to receive")
    @FindBy(xpath = "//atm-amount-input//input")
    lateinit var tokenQuantityToReceive: AtmAmount

    @Name("Token quantity requested to redeem")
    @FindBy(xpath = ".//atm-etc-redemption//span[contains(text(),'Token quantity requested to redeem')]/ancestor::atm-property-value//atm-amount")
    lateinit var tokenQuantityRequestedToRedeem: AtmAmount

    @Name("Balance from wallet page")
    @FindBy(xpath = "//atm-wallet-item//atm-amount")
    lateinit var balanceFromWalletPage: AtmAmount

    @Name("Transfer fee")
    @FindBy(xpath = "//*[text() = ' Transfer fee ']/ancestor::atm-property-value//atm-amount")
    lateinit var transferFee: AtmAmount

    @Name("Withdrawal fee")
    @FindBy(xpath = "//*[text() = ' WITHDRAWAL FEE ']/ancestor::atm-property-value//atm-amount")
    lateinit var withdrawalFee: AtmAmount

    @Name("Fiat requisites")
    @FindBy(xpath = "//atm-fiat-requisites")
    lateinit var fiatRequisites: AtmBankReference

    @Name("Token list")
    @FindBy(xpath = "//atm-tokens-list")
    lateinit var atmTokenList: AtmTokenList

    @Name("Save QR CODE")
    @FindBy(xpath = "//button//span[contains(text(),'Save QR CODE')]")
    lateinit var saveQrCode: Button

    @Name("Apply")
    @FindBy(xpath = ".//button//span[contains(text(),'Apply')]")
    lateinit var apply: Button

    @Name("Cancel for apply")
    @FindBy(xpath = ".//button//span[contains(text(),'Cancel')]")
    lateinit var cancelForApply: Button

    @Name("Items")
    @FindBy(xpath = "//div[contains(text(), 'ITEMS')]")
    lateinit var items: Button

    @Name("Transfer MATURITY DATES ")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='group']")
    lateinit var maturityDatesTransfer: AtmSelect

    @Name("MATURITY DATES")
//    @FindBy(xpath = "//div[contains(text(), 'MATURITY DATES')]")
    @FindBy(xpath = "//atm-token-item//a[text()='Industrial Token']/ancestor::atm-token-item//div[2]//i[1]")
    lateinit var maturityDates: Button

    @Name("Redeem MATURITY DATES ")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='maturity']")
    lateinit var maturityDatesRedemption: AtmSelect

    @Name("ISSUANCE INFO")
    @FindBy(xpath = "//div[contains(text(), 'ISSUANCE INFO')]")
    lateinit var issuanceInfo: Button

    @Name("Issue list")
//    @FindBy(xpath = "//atm-ind-issue-list")
    @FindBy(xpath = "//atm-token-item//a[text()='Industrial Token']/ancestor::atm-token-item//nz-radio-group")
    lateinit var issueList: Button

    @Name("Currency")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='fiat']//nz-select")
    lateinit var currency: AtmSelect

    @Name("Assign employee")
    @FindBy(css = "atm-assign-employees")
    lateinit var assignEmployee: AtmTable<AssignEmployeesItem>

    @Name("Auto redeem ETC")
    @FindBy(xpath = "//span[contains(text(),'Auto')]/ancestor::label")
    lateinit var autoRedeemEtc: AtmRadio

    @Name("Manual redeem ETC")
    @FindBy(xpath = "//*[contains(text(),'Manual')]/ancestor::label")
    lateinit var manualRedeemEtc: AtmRadio

    @Name("Bar selection for ETC")
    @FindBy(xpath = "//atm-manual-bars-selection[@formcontrolname='bars']//input")
    lateinit var barsSelection: TextInput

    @Name("Amount token")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='assetAmount']//div//input")
    lateinit var amountToken: TextInput

    @Name("Usd amount")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='usdAmount']//div//input")
    lateinit var usdAmount: TextInput

    @Name("Authenticator app field")
    @FindBy(css = "[formcontrolname=\"otpCode\"]")
    lateinit var authenticatorAppCodeField: TextInput

    @Name("Trading")
    @FindBy(xpath = "//header//nav/a[@href='/trading']")
    lateinit var tradingHeader: Button

    @Name("Wallets")
    @FindBy(xpath = "//header//nav/a[@href='/wallets']")
    lateinit var walletsHeader: Button

    @Name("Button Cancel For Confirmation Window")
    @FindBy(xpath = "//form//button/span[contains(text(), 'Cancel')]")
    lateinit var cancelButtonConfirmationForm: Button

    // TODO: Duplicate from AtmMarketPlacePage class
    @Name("Select wallet")
    @FindBy(xpath = "//atm-wallet-address-select-control//nz-select")
    lateinit var selectWallet: AtmSelect

    @Name("Limit redemption amount IT")
    @FindBy(xpath = "//div[contains(@class, 'ant-form-item-extra')]//span")
    lateinit var amountRedemptionLimitIt: AtmAmount

    fun <T> retry(repeatCount: Int, body: () -> T): T {
        repeat(repeatCount) {
            try {
                return body()
            } catch (e: Exception) {
                if (it == repeatCount) {
                    throw e
                }
            }
        }
        throw Exception("First argument incorrect type or value")
    }

    @Step("Wait wallets are displayed")
    fun waitWalletsAreDisplayed() {
        retry(3) {
            wait {
                until("Wallets is displayed") {
                    check {
                        isElementWithTextPresented("Register wallet")
                    }
                }
            }
        }
    }

    @Step("Wait wallet num from is displayed")
    fun waitWalletNumAreDisplayed(walletFrom: SimpleWallet) {
        wait(100L) {
            until("Couldn't load wallet from") {
                check { isElementContainsText(fromWallet, walletFrom.publicKey) }
            }
        }
    }


    @Step("Check if wallet with label {label} presented")
    fun isWalletWithLabelPresented(label: String): Boolean {
        waitWalletsAreDisplayed()
        return check {
            isElementPresented(By.xpath(".//atm-wallet-item//div[contains(text(), '$label')]"))
        }
    }

    @Step("Get user alias")
    @Action("Get user alias")
    fun getAlias(): String {
        var alias = ""
        e {
            waitWalletsAreDisplayed()
            click(mainWallet)
            click(depositDetails)
            wait {
                until("Reference number didn't show up") {
                    referenceNumber.text != ""
                }
            }
            alias = referenceNumber.text
        }
        attach("Alias", alias)
        return alias
    }

    @Step("Get user alias")
    @Action("Get user alias")
    fun getAliasForWallet(wallet: String): String {
        var alias = ""
        e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            click(depositDetails)
            wait {
                until("Reference number didn't show up") {
                    referenceNumber.text != ""
                }
            }
            alias = referenceNumber.text
        }
        attach("Alias", alias)
        return alias
    }

    @Step("user register wallet")
    @Action("user register wallet")
    fun registerWallet(walletType: WalletType, publickKey: String, privateKey: String, label: String) {
        e {
            click(registerWalletButton)
            when (walletType) {
                MAIN -> click(mainWalletRadioButton)
                OTF -> click(otfWalletRadioButton)
                ISSUER -> click(issuerWalletRadioButton)
            }

            sendKeys(publicKey, publickKey)
            sendKeys(labelWallet, label)
            click(signMessage)
            click(privateKeyInput)
            sendKeys(privateKeyInput, privateKey)
            click(confirmSignature)
            clickUntilElementIsPresented(nextButton, "Wallets", 1, pollingEveryInSeconds = 5)
        }
    }

    @Step("Get user balance")
    @Action("Get user balance")
    fun getBalance(): String {
        return e {
            waitWalletsAreDisplayed()
            click(mainWallet)
            click(depositDetails)
            balanceUser.amount.toString().also {
                attach("Balance", it)
            }
        }
    }

    @Step("Get user balance")
    @Action("Get user balance")
    fun getBalanceFromWallet(wallet: String): String {
        return e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            click(depositDetails)
            balanceUser.amount.toString()
        }
    }

    @Step("Get user otf balance")
    @Action("Get user otf balance")
    fun getBalanceFromOTFWallet(wallet: String): String {
        return e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            balanceUser.amount.toString()
        }
    }

    fun getBalance(coinType: CoinType, wallet: String): BigDecimal =
        getBalanceFromWalletForToken(coinType, wallet).toBigDecimal()

    @Step("Get user balance")
    @Action("Get user balance")
    fun getBalanceFromWalletForToken(coinType: CoinType, wallet: String): String {
        driver.navigate().refresh()
        return e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            setDisplayZeroBalance(true)
            chooseToken(coinType)
            balanceTokenUser.amount.toString()
        }
    }

    fun getBalanceUsd(coinType: CoinType, wallet: String): BigDecimal =
        getBalanceFromWalletForUSD(coinType, wallet)

    @Step("Get user balance")
    @Action("Get user balance")
    fun getBalanceFromWalletForUSD(coinType: CoinType, wallet: String): BigDecimal {
        return e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            setDisplayZeroBalance(true)
            chooseToken(coinType)
            atmTokenList.getTokenBalance("USD", coinType)
        }
    }

    fun getHeldInOrders(coinType: CoinType, wallet: String): BigDecimal =
        getHeldInOrdersFromWalletForToken(coinType, wallet).toBigDecimal()

    @Step("Get user held in orders")
    @Action("Get user balance")
    fun getHeldInOrdersFromWalletForToken(coinType: CoinType, wallet: String): String {
        return e {
            waitWalletsAreDisplayed()
            chooseWallet(wallet)
            setDisplayZeroBalance(true)
            chooseToken(coinType)
            heldInOrders.amount.toString()
        }
    }

    @Step("Choose and click wallet")
    @Action("Choose and click wallet")
    fun chooseWallet(walletName: String) {
        val wallet = wait {
            untilPresented<WebElement>(By.xpath("//atm-wallet-item//div[text()='$walletName']"))
        }.to<Button>("Wallet '$walletName'")
        e {
            click(wallet)
        }
    }

    @Step("Register wallet with custodian sign in")
    @Action("Register wallet with custodian sign in")
    fun registerWalletWithCustodianSignin(oAuthSecret: String, walletName: String) {
        e {
            click(registerWalletButton)
            click(typeWalletMainCheck)
            sendKeys(labelWallet, walletName)
            click(secretKeyStorageTypeCustody)
            click(nextButton)
            val code = oAuthSecret.let {
                OAuth.generateCode(it)
            }
            sendKeys(authenticatorAppCodeField, code)
            click(atmOtpConfirmationConfirmButton)
        }
    }

    @Step("Check register first Custody wallet")
    @Action("Check register first Custody wallet")
    fun checkRegisterFirstCustodyWallet(walletName: String) {
        e {
            click(registerWalletButton)
            click(typeWalletMainCheck)
            sendKeys(labelWallet, walletName)
            click(secretKeyStorageTypeCustody)
            click(nextButton)
            wait {
                untilPresented<WebElement>(By.xpath("//form//img[@class='qrcode-confirm__qr-img ng-star-inserted']")).to<Button>(
                    "qrCode"
                )
            }
            click(cancelButtonConfirmationForm)
            alert { checkErrorAlert() }
            wait {
                until("dialog for cashout is gone", 20) {
                    check {
                        isElementGone(cancelButtonConfirmationForm)
                    }
                }
            }
        }
    }

    @Step("Click Redemption button and cancel")
    @Action("Click Redemption button and cancel")
    fun clickRedemptionButtonAndCancel() {
        e {
            click(fiatTokenButton)
            wait {
                until("Button 'Redeem' should be enabled") {
                    redemption.getAttribute("disabled") == null
                }
            }
            click(redemption)
            click(cancelButton)
        }
    }

    @Step("Go to transfer field of selected token and set required sum")
    @Action("Go to transfer field of selected token and set required sum")
    fun setSumTransferFieldIT(sum: String) {
        e {
            click(industrialTokenButtonAlt)
            Thread.sleep(5000)
            click(transfer)
            sendKeys(amountTransfer, sum)
        }
    }

    @Step("Go to Redeem field of selected token and set required sum")
    @Action("Go to Redeem field of selected token and set required sum")
    fun setSumRedeemFieldIT(sum: String) {
        e {
            click(currencyCoinButton)
            Thread.sleep(5000)
            click(redemption)
            sendKeys(amountRedeem, sum)
        }
    }

    @Step("Checking field for decimal count")
    fun checkInputFieldEightDigitsDecimal() {
        check {
            assertTrue(
                "Entered amount is not displayed!",
                isElementPresented(By.xpath("//atm-amount-input//span[contains(@class, 'decimal')]"))
            )
        }
        val numberOfDigitsAfterDecimalPoint: Int =
            findElement(By.xpath("//atm-amount-input//span[contains(@class, 'decimal')]")).text.removePrefix(".").length
        check {
            assertTrue("Number of digits after decimal point is not equal 8!", numberOfDigitsAfterDecimalPoint == 8)
        }
    }

    @Step("Wait token list and click zero balance")
    @Action("Wait token list and click zero balance")
    fun setDisplayZeroBalance(state: Boolean) {
        fun currentState() = showZeroBalance.getAttribute("class").contains("ant-switch-checked")
        if (currentState() != state) {
            e {
                until("Couldn't set Show zero balance to $state") {
                    click(showZeroBalance)
                }
                currentState() == state
            }
        }
    }

    @Step("take wallet ID")
    @Action("Take wallet ID")
    fun takeWalletID(): String {
        var walletID = ""
        e {
            waitWalletsAreDisplayed()
            click(otfWalletTicker)
            click(showReplenishmentDetails)
            nonCriticalWait {
                until("") {
                    walletIDNumber.text.isNotEmpty()
                }
            }
            walletID = walletIDNumber.text
        }
        return walletID
    }

    @Step("transfer from wallet to wallet")
    @Action("User make transfer from wallet to wallet")
    fun transferFromWalletToWallet(
        coinType: CoinType,
        walletFrom: SimpleWallet,
        walletTo: SimpleWallet,
        amount: String,
        maturityDate: String?,
        note: String,
        user: User

    ): Pair<BigDecimal, BigDecimal> {
        e {
            chooseWallet(walletFrom.name)
            chooseToken(coinType)
            when (coinType) {
                ETC -> click(transferEtc)
                else -> click(transfer)

            }
            waitWalletNumAreDisplayed(walletFrom)
            select(fromWallet, walletFrom.publicKey)
            when (coinType) {
                ETC -> sendKeys(toWallet, walletTo.walletId)
                else -> sendKeys(toWallet, walletTo.publicKey)
            }
            sendKeys(amountTransfer, amount)
            if (coinType == IT) {
                select(maturityDatesTransfer, maturityDate!!.replace(IT.tokenName + "_", ""))
            }
            sendKeys(transferNote, note)
        }
        Thread.sleep(10000)//в данный момент работает только с этим видом ожидания
        val fee = transferFee.amount
//        val fee = wait(100L) {
//            until("Couldn't load fee") {
//                transferFee.amount != BigDecimal.ZERO
//            }
//            transferFee.amount
//        }

        e {
            click(submit)
        }
        signAndSubmitMessage(user, walletFrom.secretKey)
        val fee2 = wait(15L) {
            until("Couldn't load fee") {
                transferFee.text.isNotEmpty()
            }
            transferFee.amount
        }
        e {
            click(doneButton)
        }
        return fee to fee2
    }

    @Step("choose token")
    @Action("User choose token")
    fun chooseToken(coinType: CoinType) {
        e {
            val currencyLocator = By.xpath("//a[contains(text(), '${coinType.tokenName}')]")
            val currencyElement = wait {
                untilPresented<Button>(currencyLocator).apply {
                    name = "Currency '${coinType.tokenName}'"
                }
            }
            click(currencyElement)
        }
    }

    @Step("redeem token")
    @Action("User make redeem token")
    fun redeemToken(
        coinType: CoinType,
        wallet: SimpleWallet,
        amount: String,
        maturityDate: String,
        user: DefaultUser
    ) {
        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
//            Thread.sleep(3000)
            wait {
                until("Button 'Redeem' should be enabled") {
                    redemption.getAttribute("disabled") == null
                }
            }

            click(redemption)
            select(selectWallet, wallet.publicKey)
            sendKeys(tokenQuantity, amount)
            if (coinType == IT) {
                select(maturityDatesRedemption, maturityDate)
            }
            click(submitButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("redeem etc token")
    @Action("User make redeem Etc token")
    fun redeemEtcToken(
        coinType: CoinType,
        wallet: SimpleWallet,
        redemptionTypeETC: RedemptionTypeETC,
        amount: String,
        barNo: String? = null,
        user: DefaultUser
    ) {
        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
            wait {
                until("Button 'Redeem' should be enabled") {
                    redemption.getAttribute("disabled") == null
                }
            }
            click(redemption)
            when (redemptionTypeETC) {
                AUTO -> {
//                    sendKeys(
//                        usdAmount,
//                        amount
//                    )// поле amountToken полная шляпа поэтому приходится так делать . Заполняется суммой из подложенного файла
//                    sendKeys(amountToken, amount)//сумма в ЕТС
//                    sendKeys(usdAmount, amount)

                    deleteData(usdAmount).also {
                        sendKeys(usdAmount, "0")
                        sendKeys(usdAmount, ((amount + "00000").toBigDecimal() * BigDecimal(1000)).toString())
                        Thread.sleep(1000)
                    }
                    deleteData(amountToken).also {
                        sendKeys(amountToken, "0")
                        sendKeys(amountToken, ((amount + "00000").toBigDecimal() * BigDecimal(1000)).toString())
                        Thread.sleep(1000)
                    }
                    retry(3) {
                        click(proceedButton)
                    }
                    val proceedButton = wait {
                        untilPresented<WebElement>(By.xpath(".//atm-etc-redemption//span[contains(text(),'Proceed')]"))
                    }.to<Button>("Proceed'")
                    click(proceedButton)
                }
                MANUAL -> {
                    click(manualRedeemEtc)
                    sendKeys(barsSelection, barNo!!)
                    pressEnter(barsSelection)

                    val barNoButton = wait {
                        untilPresented<WebElement>(By.xpath(".//atm-bar-item//span[contains(text(),'${barNo}')]"))
                    }.to<Button>("Wallet '$walletName'")

                    click(barNoButton)
                    click(proceedButton)
                }
            }
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("withdrawal token")
    @Action("User make withdraw token")
    fun withdrawToken(
        wallet: SimpleWallet,
        coin: String,
        amount: String,
        user: User
    ) {
        e {
            chooseWallet(wallet.name)
            chooseToken(FIAT)
            click(withdraw)
            sendKeys(amountTransfer, amount)
            select(currency, coin)
            click(submitButton)
            click(submitButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("trade token")
    @Action("User make trade token")
    fun tradeToken(
        walletFrom: SimpleWallet,
        coinType: CoinType,
        amount: String,
        maturityDate: String,
        user: User
    ) {
        e {
            chooseWallet(walletFrom.name)
            chooseToken(coinType)

            click(trade)
            click(newOrderButton)
            select(maturityDatesRedemption, maturityDate)
            select(selectWallet, walletFrom.publicKey)
            sendKeys(tokenQuantity, amount)
            click(submitButton)
        }
        signAndSubmitMessage(user, walletFrom.secretKey)
    }

    @Step("check alias history")
    @Action("User check alias history")
    fun checkNewAliasInHistory(alias: String) {
        val aliasHistory = check {
            isElementPresented(By.xpath("//atm-collapse-panel//div[2]//span[contains(@class,'fiat-requisites__ref-number')][contains(text(),'$alias')]"))
        }
        assertTrue("Bank card with '$alias' is found in", aliasHistory)
    }

    @Step("check reference value")
    @Action("User check reference value")
    fun checkReferenceValue() {
        val recipientName = fiatRequisites.recName
        val recipientAddress = fiatRequisites.recAddress
        val bankName = fiatRequisites.nameOfBank
        val bankAddress = fiatRequisites.addressOfBank
        val bankIdCodeType = fiatRequisites.bankCode
        val accountNumber = fiatRequisites.accNum
        with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            checkBankAccount(
                bankIdCodeType,
                recipientName,
                recipientAddress,
                bankName,
                bankAddress,
                accountNumber
            )
        }
    }

    @Step("check wallet select name")
    @Action("User check wallet select name")
    fun checkWalletName(operationType: OperationType, coinType: CoinType, walletName: String, walletNum: String) {
        val regex = "[.()\\r\\n]".toRegex()
        chooseToken(coinType)
        wait {
            until("Button 'Redeem' should be enabled") {
                redemption.getAttribute("disabled") == null
            }
        }
        e {
            when (operationType) {
                OperationType.REDEMPTION -> click(redemption)
                OperationType.TRANSFER -> click(transfer)
            }
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
        e {
            when (operationType) {
                OperationType.REDEMPTION -> click(cancelButton)
                OperationType.TRANSFER -> click(cancel)
            }
        }
    }

    // TODO: Можно сделать ключик на специализированный wallet
    @Step("Move coins to OTF")
    @Action("Move currencyCoin from Main wallet to OTF")
    fun moveToOTFWallet(amount: String, user: User, wallet: MainWallet) {

        e {
            chooseWallet(wallet.name)

            chooseToken(CC)
            click(move)
            sendKeys(moveTokenQuantity, amount)
            sendKeys(transferNote, "test")
            click(submitButton)

            signAndSubmitMessage(user, wallet.secretKey)
        }
    }

    @Step("Move coins to OTF")
    @Action("Move currencyCoin from Main wallet to OTF")
    fun moveToOTFWalletNew(amount: String, coinType: CoinType, user: User, wallet: MainWallet) {
        // TODO: Можно провести сюда специализированный тип пользователя с необходимым кошельком

        e {
            chooseWallet(wallet.name)
            chooseToken(coinType)
            click(move)
            sendKeys(moveTokenQuantity, amount)
            sendKeys(transferNote, "test")
            click(submitButton)

            signAndSubmitMessage(user, wallet.secretKey)
        }
    }

    // TODO: Можно сделать ключик на специализированный wallet
    @Step("Move coins to Main")
    @Action("Move currencyCoin from OTF wallet to specific MAIN")
    fun moveToMainWallet(token: CoinType, amount: String, user: User, mainWallet: MainWallet, otfWallet: OtfWallet) {

        e {
            chooseWallet(otfWallet.name)
            chooseToken(token)

            click(move)
            select(selectWallet, mainWallet.publicKey)

            sendKeys(moveTokenQuantity, amount)
            sendKeys(transferNote, "test")

            click(submitButton)

            signAndSubmitMessage(user, otfWallet.secretKey)
        }
    }

    @Step("Reverse checkbox state for employee {email}")
    fun reverseCheckboxStatusForEmployee(email: String): Boolean {
        val employee = assignEmployee.find {
            it.emailName == email
        } ?: error("Can't find card with '$email'")
        val newState = !employee.controllerCheckbox.isChecked()
        employee.setCheckboxStateAndApply(newState)
        return newState
    }


    @Step("Find employee card for wallet")
    @Action("Find employee card for wallet")
    fun findEmployeeAndSetControllerCheckBox(email: String, state: Boolean, user: User) {
        val employee = assignEmployee.find {
            it.emailName == email
        } ?: error("Can't find card with '$email'")
        employee.setCheckboxStateAndApply(state)
        submitConfirmationCode(user)
    }

    @Step("Find employee card for wallet")
    @Action("Find employee card for wallet")
    fun checkStateCheckBox(email: String, state: Boolean) {
        val employee = assignEmployee.find {
            it.emailName == email
        } ?: error("Can't find card with '$email'")
        val value = employee.controllerCheckbox.isChecked().toString()
        assertThat(
            "Controller check box  $state",
            value,
            hasToString(state.toString())
        )
    }

    @Step("buy Validation token")
    @Action("User buy and decline or accept token ")
    fun receiveToken(
        coinType: CoinType,
        amount: String,
        wallet: MainWallet,
        user: DefaultUser,
        statusType: StatusType
    ) {
        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addTokenAmountExact(
            coinType,
            "Ticker symbol CC",
            wallet,
            user,
            amount
        )

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(coinType)
            e {
                click(newOrderButton)
                click(receiveButton)
                select(selectWallet, wallet.publicKey)
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(user, wallet.secretKey)
        }

        val id = openPage<AtmOrdersPage>(driver) { submit(user) }.findSubmittedOrder(
            coinType,
            wallet,
            user.email,
            BigDecimal(amount)
        ).requestedId.text

        AtmProfilePage(driver).logout()

        val acceptTokensUser = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        with(openPage<AtmIssuancesPage>(driver) { submit(acceptTokensUser) }) {
//            findOffersForToken(coinType, id, statusType, acceptTokensUser, acceptTokensUser.mainWallet)
        }
    }

    fun generateLocatorForMaturityButton(maturityDateButtonFirst: String, maturityDateButtonSecond: String): String {
        return "//nz-radio-group[@formcontrolname='subIssues']//label//span[contains(text(),'${maturityDateButtonFirst}')] | //nz-radio-group[@formcontrolname='subIssues']//label//span[contains(text(),'${maturityDateButtonSecond}')]"
    }

    @Step("Fill field Token quantity to buy")
    fun setSumTokenQuantityToBuyField(sum: String) {
        e {
            click(newOrderButton)
            sendKeys(tokenQuantityToBuy, sum)
        }
    }

    @Step("Fill field Token quantity to receive")
    fun setSumTokenQuantityToReceiveField(sum: String) {
        e {
            click(newOrderButton)
            sendKeys(tokenQuantityToReceive, sum)
        }
    }
}
