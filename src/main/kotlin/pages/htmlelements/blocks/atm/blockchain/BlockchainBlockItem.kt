package pages.htmlelements.blocks.atm.blockchain

import org.openqa.selenium.support.FindBy
import pages.atm.AtmBlockchainExplorerPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock

@Name("Blockchain Channel Item")
@FindBy(css = "div.channel__value")
class BlockchainBlockItem: BaseBlock<AtmBlockchainExplorerPage>() {

    //region TEXT CONSTANTS

    companion object {
        const val TEXT_BLOCK_NUMBER = "BLOCK NUMBER"
    }

    //endregion


    //region ELEMENTS

    @Name("Block number label")
    @FindBy(xpath = ".//span")
    lateinit var blockNumberLabel: TextBlock

    @Name("Block number data")
    @FindBy(xpath = ".//atm-span/a")
    lateinit var blockNumberDataLabel: TextBlock

    @Name("Block id button")
    @FindBy(xpath = ".//div/a")
    lateinit var blockIdButton: Button

    //endregion


    //region ACTIONS
    val blockNumber: String
        get() = blockNumberDataLabel.text

    val blockId: String
        get() = blockIdButton.text
    //endregion

}