package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.user.classes.DefaultUser
import models.user.classes.OtfWallet
import models.user.interfaces.SimpleWallet
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmSelect
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import java.math.BigDecimal

//TODO: there is no more this page
@PageName("DM")
@PageUrl("/DM")
class AtmDMPage(driver: WebDriver) : AtmPage(driver) {

    @FindBy(xpath = "//button//span[contains(text(), 'SEND MONEY')]")
    @Name("Send money")
    lateinit var sendMoneyButton: Button

    @FindBy(xpath = "//button//span[contains(text(), 'GET BALANCE')]")
    @Name("Get balance")
    lateinit var getBalanceButton: Button

    @FindBy(xpath = "//nz-input-number[@formcontrolname='amount']//input")
    @Name("Throw money")
    lateinit var throwMoney: TextInput

    @FindBy(xpath = "//input[@placeholder='Enter your private key']")
    @Name("Signature")
    lateinit var signature: TextInput

    @FindBy(xpath = "//nz-select[@formcontrolname='wallet']")
    @Name("Select wallet")
    lateinit var selectWallet: AtmSelect

    @FindBy(xpath = "//nz-select[@formcontrolname='asset']")
    @Name("Asset")
    lateinit var assets: AtmSelect

    @FindBy(xpath = "//button//span[contains(text(),' Confirm ')]")
    @Name("Confirm")
    lateinit var confirmButton: Button

    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'Confirmation')]")
    @Name("Confirmation dialog")
    lateinit var confirmationDialog: TextInput

    @Step("User adds {amount} to {coin}")
    @Action("add coin")
    fun addBalance(coin: CoinType, amount: String, wallet: SimpleWallet, user: DefaultUser) {
        e {
            wait {
                until("Wallets is displayed", 20) {
                    check {
                        isElementPresented(By.xpath("//a[@href='/profile']"))
                    }
                }
            }

            select(assets, coin.tokenName)
            select(
                selectWallet, wallet.publicKey
            )
            sendKeys(throwMoney, amount)
            click(sendMoneyButton)
        }
        signAndSubmitMessage(user, wallet.secretKey)
    }

    @Step("User adds {amount} to {coin} without 2FA")
    @Action("add coin without 2FA")
    fun addBalanceWithout2FA(
        coin: String,
        amount: String,
        // Теоретически сюда можно прокинуть типизированный кошелек
        otfWallet: SimpleWallet
    ) {
        e {
            wait {
                until("Wallets is displayed", 20) {
                    check {
                        isElementPresented(By.xpath("//a[@href='/profile']"))
                    }
                }
            }

            select(assets, coin)
            select(selectWallet, otfWallet.publicKey)
            sendKeys(throwMoney, amount)
            click(sendMoneyButton)
            sendKeys(signature, otfWallet.secretKey)
            click(confirmButton)
        }
    }

    @Step("User get balance for {coin}")
    @Action("get balance")
    fun getBalance(
        coin: CoinType,
        otfWallet: OtfWallet
    ): BigDecimal {
        var balance = ""
        driver.navigate().refresh()
        e {
            select(selectWallet, otfWallet.publicKey)
            click(getBalanceButton)
            wait {
                until("Assets is displayed", 20) {
                    check {
                        isElementWithTextPresented("Free")
                    }
                }
            }
            balance = wait {
                untilPresented<TextBlock>(By.xpath(("//td[text()= '${coin}']//ancestor::tr/td[@class='ant-table-cell'][2]")))
            }.text
        }
        return balance.toBigDecimal()
    }

    override fun getTimeoutInSeconds(): Long {
        return 10L
    }
}