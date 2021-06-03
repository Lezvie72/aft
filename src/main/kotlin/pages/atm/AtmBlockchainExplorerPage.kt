package pages.atm

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.blockchain.BlockchainBlockItem
import pages.htmlelements.blocks.atm.blockchain.BlockchainChannelItem
import pages.htmlelements.blocks.atm.blockchain.BlockchainTransactionDateFilterItem
import pages.htmlelements.blocks.atm.blockchain.BlockchainTransactionIncludedItem
import pages.htmlelements.elements.AtmTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput

@PageName("Blockchain explorer")
@PageUrl("/explorer/channels")
class AtmBlockchainExplorerPage(driver: WebDriver) : AtmPage(driver) {

    //region TEXT CONSTANTS

    companion object {
        const val TEXT_BLOCKCHAIN_EXPLORER = "Blockchain explorer"
        const val TEXT_SEARCH = "Search"
        const val TEXT_CHANNELS = "Channels"
        const val TEXT_SEARCH_BY = "Search by Tx ID, Block Hash, Request ID, Method, Endorser"
        const val TEXT_EXPLORER = "Explorer"
        const val TEXT_WALLET_BALANCES = "Wallet balances"
        const val TEXT_CHANNEL_INFORMATION = "Channel information"
        const val TEXT_CHANNEL_BLOCKS = "Channel blocks"
        const val TEXT_BLOCK = "BLOCK"
        const val TEXT_BLOCK_INFORMATION = "Block information"

        const val TEXT_BLOCK_HASH = "BLOCK HASH"
        const val TEXT_PREVIOUS_BLOCK = "PREVIOUS BLOCK"
        const val TEXT_DATA_HASH = "DATA HASH"
        const val TEXT_SIGNED_BY = "SIGNED BY"
        const val TEXT_NUMBER_OF_TXS = "NUMBER OF TXS"

        const val TEXT_BLOCK_TRANSACTION = "Block transactions"
        const val TEXT_CHANNEL = "CHANNEL"
        const val TEXT_ADDUSER = "ADDUSER"
        const val TEXT_CREATEREDEEMREQUEST = "CREATEREDEEMREQUEST"

        const val TEXT_TRANSACTION_INFORMATION = "Transaction information"
        const val TEXT_TIMESTAMP = "TIMESTAMP"
        const val TEXT_TX_ID = "TX ID"
        const val TEXT_PROPOSAL_HASH = "PROPOSAL HASH"
        const val TEXT_REQUEST_ID = "REQUEST ID"
        const val TEXT_ENDORSED_BY = "ENDORSED BY"
        const val TEXT_CHAINCODE = "CHAINCODE"
        const val TEXT_ARGS = "ARGS"
        const val TEXT_CODE = "CODE"
        const val TEXT_EVENTS_NAME = "EVENTS NAME"

        const val TEXT_TRANSACTION_DETAILS = "Transaction details"

        const val TEXT_SEARCH_RESULTS = "Search results"
        const val TEXT_QUERY_ENTERED = "Query entered"
        const val TEXT_ENDORSER = "Endorser"
        const val TEXT_ENDORSER_ID = "ENDORSER ID"
        const val TEXT_TRANSACTION_INCLUDED = "Transaction included"

        const val TEXT_ACCEPTREDEEMREQUEST = "ACCEPTREDEEMREQUEST"
    }

    //endregion


    //region ELEMENTS

    //region common elements

    //region left block
    @Name("Blockchain explorer header")
    @FindBy(xpath = "//aside//h2")
    lateinit var blockchainExplorerHeaderLabel: TextBlock

    @Name("Explorer button")
    @FindBy(xpath = "//aside//a[@href='/explorer']")
    lateinit var explorerButton: Button

    @Name("Wallet balances button")
    @FindBy(xpath = "//aside//a[@href='/wallets']")
    lateinit var walletBalancesButton: Button
    //endregion

    //region search
    @Name("Search header")
    @FindBy(xpath = "//div[@class='blockchain-explorer__block']//div[@class='card__left']//h2")
    lateinit var searchHeaderLabel: TextBlock

    @Name("Search by... text")
    @FindBy(xpath = "//div[@class='blockchain-explorer__block']//div[@class='card__left']//p")
    lateinit var searchByTextLabel: TextBlock

    @Name("Search by... text")
    @FindBy(xpath = "//div[@class='blockchain-explorer__block']//div[@class='card__right']//input")
    lateinit var searchInput: TextInput

    @Name("Search by... text")
    @FindBy(xpath = "//div[@class='blockchain-explorer__block']//div[@class='card__right']//button/span")
    lateinit var searchButton: Button

    @Name("Search by... text")
    @FindBy(xpath = "//div[@class='blockchain-explorer__block']//div[@class='card__right']//button")
    lateinit var searchButtonForCheckDisabled: Button
    //endregion

    //region filter
    @Name("Date filter")
    @FindBy(xpath = "//atm-date-filter-form")
    lateinit var transactionDateFilter: BlockchainTransactionDateFilterItem
    //endregion

    //endregion


    //region dynamic elements

    //region channels
    @Name("Channels header")
    @FindBy(xpath = "//atm-channels//h2")
    lateinit var channelsHeaderLabel: TextBlock

    @Name("Channels list")
    @FindBy(xpath = "//atm-channels//div[@class='card__right']/div")
    lateinit var channelsList: AtmTable<BlockchainChannelItem>
    //endregion

    //region channel information
    @Name("Channel information")
    @FindBy(xpath = "//div[@class='channel__data']/div[1]")
    lateinit var channelInformation: BlockchainChannelItem
    //endregion

    //region blocks list
    @Name("Channel blocks header")
    @FindBy(xpath = "//div[@class='channel__data']/div[2]/h3")
    lateinit var channelBlocksHeaderLabel: TextBlock

    @Name("Blocks filter")
    @FindBy(xpath = "//input[@formcontrolname='filterText']")
    lateinit var blockFilterInput: TextInput

    @Name("Blocks list")
    @FindBy(xpath = "//div[@class='channel__data']/div[2]")
    lateinit var blocksList: AtmTable<BlockchainBlockItem>

    //TODO: create new item?
    @Name("Block list pagination")
    @FindBy(xpath = "//nz-pagination")
    lateinit var blocksListPagination: Button
    //endregion

    //region block
    @Name("Block header")
    @FindBy(xpath = "//atm-block/atm-separated-card//h2")
    lateinit var blockHeaderLabel: TextBlock

    @Name("Block information header")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//h3")
    lateinit var blockInformationHeaderLabel: TextBlock

    @Name("Block number label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[1]//span")
    lateinit var blockNumberLabel: TextBlock

    @Name("Block number data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[1]//atm-span")
    lateinit var blockNumberDataLabel: TextBlock

    @Name("Block hash label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[2]//span")
    lateinit var blockHashLabel: TextBlock

    @Name("Block hash data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[2]//atm-span")
    lateinit var blockHashDataLabel: TextBlock

    @Name("Previous block label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[3]//span")
    lateinit var previousBlockLabel: TextBlock

    @Name("Previous block data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[3]//atm-span")
    lateinit var previousBlockDataLabel: TextBlock

    @Name("Data hash label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[4]//span")
    lateinit var dataHashLabel: TextBlock

    @Name("Data hash data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[4]//atm-span")
    lateinit var dataHashDataLabel: TextBlock

    @Name("Signed by label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[5]//span")
    lateinit var signedByLabel: TextBlock

    @Name("Signed by data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[5]//atm-span")
    lateinit var signedByDataLabel: TextBlock

    @Name("Number of txs label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[6]//span")
    lateinit var numberOfTxsLabel: TextBlock

    @Name("Number of txs data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[6]//atm-span")
    lateinit var numberOfTxsDataLabel: TextBlock

    @Name("Block transaction header")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]/h3")
    lateinit var blockTransactionHeaderLabel: TextBlock

    @Name("Block transaction status")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item//nz-tag")
    lateinit var blockTransactionStatusLabel: TextBlock

    //TODO: wrap to element
    @Name("Block transaction timestamp")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div/span")
    lateinit var blockTransactionTimestampLabel: TextBlock

    @Name("Channel label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[1]")
    lateinit var channelLabel: TextBlock

    @Name("Channel data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[1]/a")
    lateinit var channelDataLabel: TextBlock

    @Name("Block label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[3]")
    lateinit var blockLabel: TextBlock

    @Name("Block data")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[3]/a")
    lateinit var blockDataLabel: TextBlock

    @Name("Adduser label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[5]")
    lateinit var adduserLabel: TextBlock

    @Name("Adduser label")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[2]/div[5]")
    lateinit var createredeemrequestLabel: TextBlock

    @Name("Transaction id button")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[2]//atm-tx-item/div[3]/a")
    lateinit var transactionId: Button

    //TODO: create new item?
    @Name("Transaction pagination")
    @FindBy(xpath = "//atm-block/atm-separated-card/div[@class='card__right']/div/div[1]//atm-property-value[6]//span")
    lateinit var pagination: Button

    @Name("Transaction information header")
    @FindBy(xpath = "//atm-transaction-info//h3[1]")
    lateinit var transactionInformationHeader: TextBlock

    @Name("Transaction status")
    @FindBy(xpath = "//atm-transaction-info/div[1]/nz-tag")
    lateinit var transactionStatusLabel: TextBlock

    @Name("Timestamp label")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[1]/span[1]")
    lateinit var timestampLabel: TextBlock

    @Name("Timestamp data")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[1]/span[2]")
    lateinit var timestampData: TextBlock

    @Name("Tx id label")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[2]/span[1]")
    lateinit var txIdLabel: TextBlock

    @Name("Tx id data")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[2]/span[2]")
    lateinit var txIdData: TextBlock

    @Name("Proposal hash label")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[3]/span[1]")
    lateinit var proposalHashLabel: TextBlock

    @Name("Proposal hash data")
    @FindBy(xpath = "//atm-transaction-info/div[2]/div[3]/span[2]")
    lateinit var proposalHashData: TextBlock

    @Name("Channel label")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[1]/span")
    lateinit var transactionChannelLabel: TextBlock

    @Name("Channel data")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[1]/a")
    lateinit var channelData: TextBlock

    @Name("Channel label")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[2]/span")
    lateinit var transactionBlockLabel: TextBlock

    @Name("Channel data")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[2]/a")
    lateinit var transactionBlockData: TextBlock

    @Name("Block hash label")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[3]/span")
    lateinit var transactionBlockHashLabel: TextBlock

    @Name("Block hash data")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[3]/a")
    lateinit var transactionBlockHashData: TextBlock

    @Name("Request id label")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[4]/span")
    lateinit var requestIdLabel: TextBlock

    @Name("Request id label")
    @FindBy(xpath = "//atm-transaction-info/div[3]/div[4]/a")
    lateinit var requestIdDataLabel: Button

    @Name("Transaction details header")
    @FindBy(xpath = "//atm-transaction-info//h3[2]")
    lateinit var transactionDetailsHeader: TextBlock

    @Name("Transaction signed by label")
    @FindBy(xpath = "//atm-transaction-info/div[4]/div[1]/span[1]")
    lateinit var transactionSignedByLabel: TextBlock

    @Name("Transaction signed by data")
    @FindBy(xpath = "//atm-transaction-info/div[4]/div[1]//a")
    lateinit var transactionSignedByData: TextBlock

    @Name("Transaction signed by data")
    @FindBy(xpath = "//atm-transaction-info/div[4]/div[1]/span[2]")
    lateinit var transactionSignedByDataLabel: TextBlock

    @Name("Transaction signed by label")
    @FindBy(xpath = "//atm-transaction-info/div[4]/div[2]/span")
    lateinit var endorsedByLabel: TextBlock

    //TODO: wrap to element or list?
    @Name("Transaction signed by data")
    @FindBy(xpath = "//atm-transaction-info/div[4]/div[2]/div//div")
    lateinit var endorsedByData: TextBlock

    @Name("Chain code label")
    @FindBy(xpath = "//atm-transaction-info/div[5]//span[1]")
    lateinit var chainCodeLabel: TextBlock

    @Name("Chain code data")
    @FindBy(xpath = "//atm-transaction-info/div[5]//span[2]")
    lateinit var chainCodeData: TextBlock

    @Name("Args label")
    @FindBy(xpath = "//atm-transaction-info/div[6]//span[1]")
    lateinit var argsLabel: TextBlock

    //TODO: wrap to element or list?
    @Name("Args data")
    @FindBy(xpath = "//atm-transaction-info/div[6]//cdk-virtual-scroll-viewport/div/div")
    lateinit var argsData: TextBlock

    @Name("Response collapse")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[1]/div[1]")
    lateinit var responseCollapseButton: Button

    @Name("Response code label")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[1]/div[2]/div/div/span")
    lateinit var responseCodeLabel: TextBlock

    @Name("Response code data")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[1]/div[2]/div/div/div/div")
    lateinit var responseCodeData: TextBlock

    @Name("Rwset collapse")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[2]/div[1]")
    lateinit var rwsetCollapseButton: Button

    @Name("Rwset code label")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[2]/div[2]/div/div/span")
    lateinit var rwsetCodeLabel: TextBlock

    //TODO: wrap to element or list?
    @Name("Rwset code data")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[2]//cdk-virtual-scroll-viewport/div[1]")
    lateinit var rwsetCodeData: TextBlock

    @Name("Events name label")
    @FindBy(xpath = "//atm-transaction-info/div[7]//span[1]")
    lateinit var eventsNameLabel: TextBlock

    //TODO: wrap to element or list?
    @Name("Events name data")
    @FindBy(xpath = "//atm-transaction-info/div[7]//cdk-virtual-scroll-viewport/div[1]")
    lateinit var eventsNameData: TextBlock

    @Name("Events payload collapse")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[3]/div[1]")
    lateinit var eventsPayloadCollapseButton: Button

    @Name("Events payload code label")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[3]/div[2]/div/div/span")
    lateinit var eventsPayloadCodeLabel: TextBlock

    //TODO: wrap to element or list?
    @Name("Events payload code data")
    @FindBy(xpath = "(//atm-transaction-info//atm-collapse-panel)[3]//cdk-virtual-scroll-viewport/div[1]")
    lateinit var eventsPayloadCodeData: TextBlock
    //end region
    //endregion

    //region search result endorser
    @Name("Search result header")
    @FindBy(xpath = "//atm-endorser//h2")
    lateinit var endorserSearchResultHeaderLabel: TextBlock

    @Name("Query entered header")
    @FindBy(xpath = "(//atm-endorser//h3)[1]")
    lateinit var endorserQueryEnteredHeaderLabel: TextBlock

    @Name("Search text label")
    @FindBy(xpath = "//div[@class='card__left']//span")
    lateinit var endorserSearchStringLabel: TextBlock

    @Name("Txs count label")
    @FindBy(xpath = "//div[@class='card__left']//atm-span")
    lateinit var endorserTxsCountLabel: TextBlock

    @Name("Endorser header")
    @FindBy(xpath = "(//atm-endorser//h3)[2]")
    lateinit var endorserHeaderLabel: TextBlock

    @Name("Endorser id label")
    @FindBy(xpath = "//atm-endorser//div[@class='card__right']//atm-property-value//span")
    lateinit var endorserIdLabel: TextBlock

    @Name("Endorser id data")
    @FindBy(xpath = "//atm-endorser//div[@class='card__right']//atm-property-value//a")
    lateinit var endorserIdDataLabel: TextBlock

    @Name("Endorser transaction included header")
    @FindBy(xpath = "(//atm-endorser//h3)[3]")
    lateinit var endorserTransactionIncludedHeaderLabel: TextBlock

    @Name("Endorser transaction included list")
    @FindBy(xpath = "//atm-endorser//div[@class='card__right']//div[@class='endorser__data']/div[2]")
    lateinit var endorserTransactionIncludedList: AtmTable<BlockchainTransactionIncludedItem>

    //endregion

    //region search result method
    @Name("Search result header")
    @FindBy(xpath = "//atm-method//h2")
    lateinit var methodSearchResultHeaderLabel: TextBlock

    @Name("Query entered header")
    @FindBy(xpath = "(//atm-method//h3)[1]")
    lateinit var methodQueryEnteredHeaderLabel: TextBlock

    @Name("Search text label")
    @FindBy(xpath = "//div[@class='card__left']//span")
    lateinit var methodSearchStringLabel: TextBlock

    @Name("Txs count label")
    @FindBy(xpath = "//div[@class='card__left']//atm-span")
    lateinit var methodTxsCountLabel: TextBlock

    @Name("Method transaction included header")
    @FindBy(xpath = "(//atm-method//h3)[2]")
    lateinit var methodTransactionIncludedHeaderLabel: TextBlock

    @Name("Method transaction included list")
    @FindBy(xpath = "//atm-method//div[@class='card__right']//div[@class='method__data']")
    lateinit var methodTransactionIncludedList: AtmTable<BlockchainTransactionIncludedItem>

    //endregion

    //region search result request
    @Name("Search result header")
    @FindBy(xpath = "//atm-request//h2")
    lateinit var requestSearchResultHeaderLabel: TextBlock

    @Name("Query entered header")
    @FindBy(xpath = "(//atm-request//h3)[1]")
    lateinit var requestQueryEnteredHeaderLabel: TextBlock

    @Name("Search text label")
    @FindBy(xpath = "//div[@class='card__left']//span")
    lateinit var requestSearchStringLabel: TextBlock

    @Name("Txs count label")
    @FindBy(xpath = "//div[@class='card__left']//atm-span")
    lateinit var requestTxsCountLabel: TextBlock

    @Name("Request transaction included header")
    @FindBy(xpath = "(//atm-request//h3)[2]")
    lateinit var requestTransactionIncludedHeaderLabel: TextBlock

    @Name("Request transaction included status label")
    @FindBy(xpath = "//nz-tag")
    lateinit var requestTransactionStatusLabel: TextBlock

    @Name("Request transaction channel label")
    @FindBy(xpath = "//atm-tx-item/div[2]/div[1]")
    lateinit var requestTransactionChannelLabel: TextBlock

    @Name("Request transaction channel data")
    @FindBy(xpath = "//atm-tx-item/div[2]/div[1]/a")
    lateinit var requestTransactionChannelDataLabel: TextBlock

    @Name("Request transaction block label")
    @FindBy(xpath = "//atm-tx-item/div[2]/div[3]")
    lateinit var requestTransactionBlockLabel: TextBlock

    @Name("Request transaction block data")
    @FindBy(xpath = "//atm-tx-item/div[2]/div[3]/a")
    lateinit var requestTransactionBlockDataLabel: TextBlock

    @Name("Request transaction acceptredeemrequest label")
    @FindBy(xpath = "//atm-tx-item/div[2]/div[5]")
    lateinit var requestTransactionAcceptredeemrequestLabel: TextBlock

    @Name("Request transaction acceptredeemrequest button")
    @FindBy(xpath = "//atm-tx-item/div[3]/a")
    lateinit var requestTransactionIdButton: Button

    @Name("Channel button")
    @FindBy(xpath = "(//atm-channel//div[2])[7]//a[contains(@class,'atomyze-a-link channel__link channel__value')]")
    lateinit var channelButton: Button

    @Name("Token button")
    @FindBy(xpath = "//atm-channel-item//atm-span[contains(text(),'etc')]")
    lateinit var tokenButton: Button

    //endregion

    //endregion

    //endregion


    //region ACTIONS
    fun getFirstChannel(channelsList: AtmTable<BlockchainChannelItem>): BlockchainChannelItem {
        return channelsList.content.first()
    }

    fun getFirstBlock(blocksList: AtmTable<BlockchainBlockItem>): BlockchainBlockItem {
        return blocksList.content.first()
    }

    fun searchBy(textForSearch: String) {
        e {
            sendKeys(searchInput, textForSearch)
            click(searchButton)
        }
    }
    //endregion

}