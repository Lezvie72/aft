package pages.atm

import io.qameta.allure.Step
import models.CoinType
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.apache.commons.lang.RandomStringUtils
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAdminSelect
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.time.LocalDate

@PageUrl("/payments")
class AtmAdminPaymentsPage(driver: WebDriver) : AtmAdminPage(driver) {

    @Name("Add payment dialog")
    @FindBy(xpath = "//sdex-payments//span[contains(text(),'Add payment')]")
    lateinit var addPaymentDialogButton: Button

    @Name("Payment Id")
    @FindBy(xpath = "//input[@formcontrolname='paymentId']")
    lateinit var paymentId: TextInput

    @Name("Payment date")
    @FindBy(xpath = "//input[@formcontrolname='created']")
    lateinit var paymentDate: TextInput

    @Name("Client alias")
    @FindBy(xpath = "//input[@formcontrolname='narrative']")
    lateinit var clientAlias: TextInput

    @Name("Amount")
    @FindBy(xpath = "//input[@formcontrolname='amount']")
    lateinit var amountPayment: TextInput

    @Name("Currency")
    @FindBy(xpath = "//mat-select//span[contains(text(),'Currency')]")
    lateinit var currency: AtmAdminSelect

    @Name("Add payment")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),' ADD ')]")
    lateinit var addPaymentsButton: Button

    @Name("Cancel payment")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),' CANCEL ')]")
    lateinit var cancelPaymentsButton: Button

    @Name("Payment dialog")
    @FindBy(xpath = "//sdex-new-payment-dialog")
    lateinit var paymentsDialog: Button


    @Step("Add payment")
    @Action("Add payment")
    fun addPayment(alias: String, amount: String): AtmAdminPaymentsPage {
        val date = LocalDate.now().toString()
        e {
            click(addPaymentDialogButton)
            select(currency, "USD")
            sendKeys(paymentId, RandomStringUtils.randomNumeric(12).toString())
            sendKeys(paymentDate, date)
            sendKeys(clientAlias, alias)
            sendKeys(amountPayment, amount)
            click(addPaymentsButton)
        }
        return this
    }

    @Step("Add token amount exact")
    @Action("Add token amount")
    fun addTokenAmountExact(
        tokenSymbol: CoinType,
        tokenName: String,
        wallet: SimpleWallet,
        user: DefaultUser,
        amount: String
    ) {
        val alias = step("GIVEN User go to Wallet, get alias and add fiat to wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAlias()
        }

        step("WHEN Admin create send fiat to wallet") {
            val usdEquivalent =
                openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.getTokenEquivalentInformation(
                    tokenSymbol,
                    tokenName
                )
            val textAmount = (usdEquivalent.toBigDecimal() * amount.toBigDecimal()).toString()
            openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addPayment(alias, textAmount)
        }

        step("WHEN User get fiat he want to trade coins") {
            openPage<AtmMarketplacePage>(driver) { submit(user) }.buyToken(
                tokenSymbol,
                wallet.publicKey,
                amount,
                user,
                wallet.secretKey
            )
        }
    }
}