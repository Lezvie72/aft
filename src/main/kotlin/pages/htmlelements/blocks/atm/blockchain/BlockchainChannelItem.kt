package pages.htmlelements.blocks.atm.blockchain

import org.openqa.selenium.support.FindBy
import pages.atm.AtmBlockchainExplorerPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Blockchain Channel Item")
@FindBy(css = ".channels__data-item")
class BlockchainChannelItem: BaseBlock<AtmBlockchainExplorerPage>() {

    //region TEXT CONSTANTS

    companion object {
        const val TEXT_CHANNEL_NAME = "CHANNEL NAME"
        const val TEXT_LEDGER_HEIGHT = "LEDGER HEIGHT"
        const val TEXT_LAST_BLOCK_HASH = "LAST BLOCK HASH"
    }

    //endregion


    //region ELEMENTS

    @Name("Channel name header")
    @FindBy(xpath = ".//h3")
    lateinit var channelNameHeaderLabel: TextBlock

    @Name("Channel name label")
    @FindBy(xpath = ".//atm-property-value[1]//div[@class='property__wrapper-key']//span")
    lateinit var channelNameLabel: TextBlock

    @Name("Channel name data")
    @FindBy(xpath = ".//atm-property-value[1]//div[@class='property__wrapper-val']//atm-span")
    lateinit var channelNameDataLabel: TextBlock

    @Name("Ledger height label")
    @FindBy(xpath = ".//atm-property-value[2]//div[@class='property__wrapper-key']//span")
    lateinit var ledgerHeightLabel: TextBlock

    @Name("Ledger height data")
    @FindBy(xpath = ".//atm-property-value[2]//div[@class='property__wrapper-val']//atm-span")
    lateinit var ledgerHeightDataLabel: TextBlock


    @Name("Last bloch hash label")
    @FindBy(xpath = ".//atm-property-value[3]//div[@class='property__wrapper-key']//span")
    lateinit var lastBlockHashLabel: TextBlock

    @Name("Last bloch hash data")
    @FindBy(xpath = ".//atm-property-value[3]//div[@class='property__wrapper-val']//atm-span")
    lateinit var lastBlockHashDataLabel: TextBlock

    //endregion


    //region ACTIONS

    //endregion



}