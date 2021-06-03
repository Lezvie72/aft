package pages.htmlelements.blocks.atm.transactions

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmAmount
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Link
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Transactions Item")
@FindBy(css = "atm-transaction-item")
class TransactionsItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = ".//span[contains(text(), 'Tx id')]/ancestor::atm-property-value//atm-span")
    @Name("Transaction id")
    private lateinit var transactionId: TextBlock

    @FindBy(className = "transaction__link")
    @Name("Transaction link")
    private lateinit var transactionLink: Link

    @FindBy(xpath = ".//span[contains(text(), 'Transaction amount')]/ancestor::atm-property-value//atm-amount")
    @Name("Transaction amount")
    private lateinit var transactionAmount: AtmAmount

    @FindBy(xpath = ".//span[contains(text(), 'Balance after transaction')]/ancestor::atm-property-value//atm-amount")
    @Name(" Balance after transaction ")
    private lateinit var balanceAfterTransaction: AtmAmount

    val id: String
        get() = transactionId.text
    
    val transaction
        get() = transactionAmount.amount

    val afterTransaction
        get() = balanceAfterTransaction.amount

}