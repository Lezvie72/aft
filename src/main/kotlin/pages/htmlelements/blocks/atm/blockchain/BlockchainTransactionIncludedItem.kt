package pages.htmlelements.blocks.atm.blockchain

import org.openqa.selenium.support.FindBy
import pages.atm.AtmBlockchainExplorerPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Blockchain Transaction Included Item")
@FindBy(css = ".endorser__value, .method__value")
class BlockchainTransactionIncludedItem : BaseBlock<AtmBlockchainExplorerPage>() {

    //region TEXT CONSTANTS

    //endregion


    //region ELEMENTS

    @Name("Transaction status label")
    @FindBy(xpath = ".//nz-tag")
    lateinit var transactionStatusLabel: TextBlock

    @Name("Transaction timestamp label")
    @FindBy(xpath = ".//atm-tx-item/div[1]/span")
    lateinit var transactionTimestampLabel: TextBlock

    @Name("Transaction id label")
    @FindBy(xpath = ".//a")
    lateinit var transactionIdLabel: TextBlock

    //endregion


    //region ACTIONS

    //endregion

}