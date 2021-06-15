package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import pages.atm.AtmBlockchainExplorerPage
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ACCEPTREDEEMREQUEST
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ADDUSER
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ARGS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_BLOCK
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_BLOCKCHAIN_EXPLORER
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_BLOCK_HASH
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_BLOCK_INFORMATION
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_BLOCK_TRANSACTION
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CHAINCODE
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CHANNEL
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CHANNELS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CHANNEL_BLOCKS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CHANNEL_INFORMATION
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CODE
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_CREATEREDEEMREQUEST
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_DATA_HASH
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ENDORSED_BY
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ENDORSER
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_ENDORSER_ID
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_EVENTS_NAME
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_EXPLORER
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_NUMBER_OF_TXS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_PREVIOUS_BLOCK
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_PROPOSAL_HASH
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_QUERY_ENTERED
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_REQUEST_ID
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_SEARCH
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_SEARCH_BY
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_SEARCH_RESULTS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_SIGNED_BY
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_TIMESTAMP
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_TRANSACTION_DETAILS
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_TRANSACTION_INCLUDED
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_TRANSACTION_INFORMATION
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_TX_ID
import pages.atm.AtmBlockchainExplorerPage.Companion.TEXT_WALLET_BALANCES
import pages.htmlelements.blocks.atm.blockchain.BlockchainBlockItem.Companion.TEXT_BLOCK_NUMBER
import pages.htmlelements.blocks.atm.blockchain.BlockchainChannelItem.Companion.TEXT_CHANNEL_NAME
import pages.htmlelements.blocks.atm.blockchain.BlockchainChannelItem.Companion.TEXT_LAST_BLOCK_HASH
import pages.htmlelements.blocks.atm.blockchain.BlockchainChannelItem.Companion.TEXT_LEDGER_HEIGHT
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Blockchain Explorer")
class SmokeBlockchainExplorerE2E : BaseTest() {

    @TmsLink("ATMCH-5248")
    @Test
    @DisplayName("Blockchain Explorer. Smoke")
    fun transferToClientWallet() {

        //TODO: change to not constant?
        val user = Users.ATM_USER_2FA_OAUTH

//        val trxId = "578dd53cb3e657fdfc0469b6334f37baf7554b4a2ebf6a25caa8e09167d83d50"
//        val requestId = "94228fed-573d-48a8-bd68-364053fa25b6"
//        val blockId = "43272bd64f6b9c8419cd761c18321da8921241c1bee573a1d5ae0405afa9f7e4"
        val endorser = "peer0.traxys.uat.dlt.atomyze.ch"
        val method = "batchExecute"

//        with(openPage<AtmBlockchainExplorerPage>(driver) { submit(user) }) {
//            e {
//                click(tokenButton)
//                click(channelData)
//                click(requestTransactionIdButton)
//            }
//            val trxId = txIdData.text
//            val requestId = requestIdDataLabel.text
//            val blockId = transactionBlockHashData.text
//        }
        with(openPage<AtmBlockchainExplorerPage>(driver) { submit(user) }) {
            e {
                click(tokenButton)
                click(channelButton)
                click(requestTransactionIdButton)
            }
            val trxId = txIdData.text
            val requestId = requestIdDataLabel.text
            val blockId = transactionBlockHashData.text

            openPage<AtmBlockchainExplorerPage>(driver)

            val firstChannel = getFirstChannel(channelsList)

            step("#1 Go to explorer tab and check it") {

                assert {

                    //region LEFT SECTION
                    elementContainsTextWithIgnoreCase(
                        blockchainExplorerHeaderLabel,
                        TEXT_BLOCKCHAIN_EXPLORER
                    )
                    elementContainsTextWithIgnoreCase(explorerButton, TEXT_EXPLORER)
                    elementContainsTextWithIgnoreCase(walletBalancesButton, TEXT_WALLET_BALANCES)
                    //endregion

                    //region CENTRAL SECTION

                    //region SEARCH SUBSECTION
                    elementContainsTextWithIgnoreCase(searchHeaderLabel, TEXT_SEARCH)
                    elementPresented(searchInput)
                    elementPresented(searchButton)
                    elementContainsTextWithIgnoreCase(searchByTextLabel, TEXT_SEARCH_BY)
                    //endregion

                    //region CHANNELS SUBSECTION
                    elementContainsTextWithIgnoreCase(channelsHeaderLabel, TEXT_CHANNELS)
                    elementPresented(channelsList)
                    elementPresented(firstChannel.channelNameHeaderLabel)

                    elementContainsTextWithIgnoreCase(firstChannel.channelNameLabel, TEXT_CHANNEL_NAME)
                    elementPresented(firstChannel.channelNameDataLabel)

                    elementContainsTextWithIgnoreCase(firstChannel.ledgerHeightLabel, TEXT_LEDGER_HEIGHT)
                    elementPresented(firstChannel.ledgerHeightDataLabel)

                    elementContainsTextWithIgnoreCase(firstChannel.lastBlockHashLabel, TEXT_LAST_BLOCK_HASH)
                    elementPresented(firstChannel.lastBlockHashDataLabel)
                    //endregion

                    //endregion
                }
            }

            step("#2 Go into channel and check the tab") {

                e {
                    click(firstChannel.channelNameDataLabel)
                }

                assert {
                    elementContainsTextWithIgnoreCase(
                        channelInformation.channelNameHeaderLabel,
                        TEXT_CHANNEL_INFORMATION
                    )
                    elementContainsTextWithIgnoreCase(channelBlocksHeaderLabel, TEXT_CHANNEL_BLOCKS)
                    elementPresented(blockFilterInput)
                    blocksList.forEach {
                        assert {
                            elementContainsTextWithIgnoreCase(it.blockNumberLabel, TEXT_BLOCK_NUMBER)
                            elementPresented(it.blockNumberDataLabel)
                            elementPresented(it.blockIdButton)
                        }
                    }
                }
            }

            step("#3 Go into block and check the tab") {

                e {
                    click(getFirstBlock(blocksList).blockIdButton)
                }

                assert {
                    elementContainsTextWithIgnoreCase(blockHeaderLabel, TEXT_BLOCK)
                    elementContainsTextWithIgnoreCase(blockInformationHeaderLabel, TEXT_BLOCK_INFORMATION)

                    elementContainsTextWithIgnoreCase(blockNumberLabel, TEXT_BLOCK_NUMBER)
                    elementPresented(blockNumberDataLabel)

                    elementContainsTextWithIgnoreCase(blockHashLabel, TEXT_BLOCK_HASH)
                    elementPresented(blockHashDataLabel)

                    elementContainsTextWithIgnoreCase(previousBlockLabel, TEXT_PREVIOUS_BLOCK)
                    elementPresented(previousBlockDataLabel)

                    elementContainsTextWithIgnoreCase(dataHashLabel, TEXT_DATA_HASH)
                    elementPresented(dataHashDataLabel)

                    elementContainsTextWithIgnoreCase(signedByLabel, TEXT_SIGNED_BY)
                    elementPresented(signedByDataLabel)

                    elementContainsTextWithIgnoreCase(numberOfTxsLabel, TEXT_NUMBER_OF_TXS)
                    elementPresented(numberOfTxsDataLabel)

                    elementContainsTextWithIgnoreCase(blockTransactionHeaderLabel, TEXT_BLOCK_TRANSACTION)

                    elementPresented(blockTransactionStatusLabel)
                    elementPresented(blockTransactionTimestampLabel)

                    elementContainsTextWithIgnoreCase(channelLabel, TEXT_CHANNEL)
                    elementPresented(channelDataLabel)

                    elementContainsTextWithIgnoreCase(blockLabel, TEXT_BLOCK)
                    elementPresented(blockDataLabel)

                    elementContainsTextWithIgnoreCase(adduserLabel, TEXT_ADDUSER)
                    elementPresented(transactionId)
                }
            }

            step("#4 Go into transaction and check the tab") {

                e {
                    click(transactionId)
                }

                assert {
                    elementWithTextPresentedIgnoreCase(TEXT_TRANSACTION_INFORMATION)

                    elementPresented(transactionStatusLabel)

                    elementContainsTextWithIgnoreCase(timestampLabel, TEXT_TIMESTAMP)
                    elementPresented(timestampData)

                    elementContainsTextWithIgnoreCase(txIdLabel, TEXT_TX_ID)
                    elementPresented(txIdData)

                    elementContainsTextWithIgnoreCase(proposalHashLabel, TEXT_PROPOSAL_HASH)
                    elementPresented(proposalHashData)

                    elementContainsTextWithIgnoreCase(transactionChannelLabel, TEXT_CHANNEL)
                    elementPresented(channelData)

                    elementContainsTextWithIgnoreCase(transactionBlockLabel, TEXT_BLOCK)
                    elementPresented(transactionBlockData)

                    elementContainsTextWithIgnoreCase(transactionBlockHashLabel, TEXT_BLOCK_HASH)
                    elementPresented(transactionBlockHashData)

                    elementContainsTextWithIgnoreCase(requestIdLabel, TEXT_REQUEST_ID)

                    elementContainsTextWithIgnoreCase(transactionDetailsHeader, TEXT_TRANSACTION_DETAILS)

                    elementContainsTextWithIgnoreCase(transactionSignedByLabel, TEXT_SIGNED_BY)
                    elementPresented(transactionSignedByData)

                    elementContainsTextWithIgnoreCase(endorsedByLabel, TEXT_ENDORSED_BY)
                    elementPresented(endorsedByData)

                    elementContainsTextWithIgnoreCase(chainCodeLabel, TEXT_CHAINCODE)
                    elementPresented(chainCodeData)

                    elementContainsTextWithIgnoreCase(argsLabel, TEXT_ARGS)
                    elementPresented(argsData)

                    e {
                        click(responseCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(responseCodeLabel, TEXT_CODE)
                    elementPresented(responseCodeData)

                    e {
                        click(rwsetCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(rwsetCodeLabel, TEXT_CODE)
                    elementPresented(rwsetCodeData)

                    elementContainsTextWithIgnoreCase(eventsNameLabel, TEXT_EVENTS_NAME)
                    elementPresented(eventsNameData)

                    e {
                        click(eventsPayloadCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(eventsPayloadCodeLabel, TEXT_CODE)
                    elementPresented(eventsPayloadCodeData)
                }
            }

            step("#6 Search by transaction id and check the tab") {

                searchBy(trxId)

                assert {
                    elementContainsTextWithIgnoreCase(transactionInformationHeader, TEXT_TRANSACTION_INFORMATION)

                    elementPresented(transactionStatusLabel)

                    elementContainsTextWithIgnoreCase(timestampLabel, TEXT_TIMESTAMP)
                    elementPresented(timestampData)

                    elementContainsTextWithIgnoreCase(txIdLabel, TEXT_TX_ID)
                    elementPresented(txIdData)

                    elementContainsTextWithIgnoreCase(proposalHashLabel, TEXT_PROPOSAL_HASH)
                    elementPresented(proposalHashData)

                    elementContainsTextWithIgnoreCase(transactionChannelLabel, TEXT_CHANNEL)
                    elementPresented(channelData)

                    elementContainsTextWithIgnoreCase(transactionBlockLabel, TEXT_BLOCK)
                    elementPresented(transactionBlockData)

                    elementContainsTextWithIgnoreCase(transactionBlockHashLabel, TEXT_BLOCK_HASH)
                    elementPresented(transactionBlockHashData)

                    elementContainsTextWithIgnoreCase(requestIdLabel, TEXT_REQUEST_ID)

                    elementContainsTextWithIgnoreCase(transactionDetailsHeader, TEXT_TRANSACTION_DETAILS)

                    elementContainsTextWithIgnoreCase(transactionSignedByLabel, TEXT_SIGNED_BY)
                    elementPresented(transactionSignedByData)

                    elementContainsTextWithIgnoreCase(endorsedByLabel, TEXT_ENDORSED_BY)
                    elementPresented(endorsedByData)

                    elementContainsTextWithIgnoreCase(chainCodeLabel, TEXT_CHAINCODE)
                    elementPresented(chainCodeData)

                    elementContainsTextWithIgnoreCase(argsLabel, TEXT_ARGS)
                    elementPresented(argsData)

                    e {
                        click(responseCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(responseCodeLabel, TEXT_CODE)
                    elementPresented(responseCodeData)

                    e {
                        click(rwsetCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(rwsetCodeLabel, TEXT_CODE)
                    elementPresented(rwsetCodeData)

                    elementContainsTextWithIgnoreCase(eventsNameLabel, TEXT_EVENTS_NAME)
                    elementPresented(eventsNameData)

                    e {
                        click(eventsPayloadCollapseButton)
                    }

                    elementContainsTextWithIgnoreCase(eventsPayloadCodeLabel, TEXT_CODE)
                    elementPresented(eventsPayloadCodeData)
                }
            }

            step("#5 Go into request id and check the tab") {

                e {
                    click(requestIdDataLabel)
                }

                assert {
                    elementContainsTextWithIgnoreCase(requestSearchResultHeaderLabel, TEXT_SEARCH_RESULTS)
                    elementContainsTextWithIgnoreCase(requestQueryEnteredHeaderLabel, TEXT_QUERY_ENTERED)

                    elementContainsTextWithIgnoreCase(requestSearchStringLabel, requestId)

                    elementPresented(requestTxsCountLabel)

                    elementContainsTextWithIgnoreCase(
                        requestTransactionIncludedHeaderLabel,
                        TEXT_TRANSACTION_INCLUDED
                    )

                    elementPresented(requestTransactionStatusLabel)

                    elementContainsTextWithIgnoreCase(requestTransactionChannelLabel, TEXT_CHANNEL)
                    elementPresented(requestTransactionChannelDataLabel)

                    elementContainsTextWithIgnoreCase(requestTransactionBlockLabel, TEXT_BLOCK)
                    elementPresented(requestTransactionBlockDataLabel)

//                    elementContainsTextWithIgnoreCase(
//                        requestTransactionAcceptredeemrequestLabel,
//                        TEXT_ACCEPTREDEEMREQUEST
//                    )

                    elementPresented(requestTransactionIdButton)
                }
            }

            step("#8 Search by block id and check the tab") {

                searchBy(blockId)

                assert {
                    elementContainsTextWithIgnoreCase(blockHeaderLabel, TEXT_BLOCK)
                    elementContainsTextWithIgnoreCase(blockInformationHeaderLabel, TEXT_BLOCK_INFORMATION)

                    elementContainsTextWithIgnoreCase(blockNumberLabel, TEXT_BLOCK_NUMBER)
                    elementPresented(blockNumberDataLabel)

                    elementContainsTextWithIgnoreCase(blockHashLabel, TEXT_BLOCK_HASH)
                    elementPresented(blockHashDataLabel)

                    elementContainsTextWithIgnoreCase(previousBlockLabel, TEXT_PREVIOUS_BLOCK)
                    elementPresented(previousBlockDataLabel)

                    elementContainsTextWithIgnoreCase(dataHashLabel, TEXT_DATA_HASH)
                    elementPresented(dataHashDataLabel)

                    elementContainsTextWithIgnoreCase(signedByLabel, TEXT_SIGNED_BY)
                    elementPresented(signedByDataLabel)

                    elementContainsTextWithIgnoreCase(numberOfTxsLabel, TEXT_NUMBER_OF_TXS)
                    elementPresented(numberOfTxsDataLabel)

                    elementContainsTextWithIgnoreCase(blockTransactionHeaderLabel, TEXT_BLOCK_TRANSACTION)

                    elementPresented(blockTransactionStatusLabel)
                    elementPresented(blockTransactionTimestampLabel)

                    elementContainsTextWithIgnoreCase(channelLabel, TEXT_CHANNEL)
                    elementPresented(channelDataLabel)

                    elementContainsTextWithIgnoreCase(blockLabel, TEXT_BLOCK)
                    elementPresented(blockDataLabel)

//                    elementContainsTextWithIgnoreCase(createredeemrequestLabel, TEXT_CREATEREDEEMREQUEST)
                    elementPresented(transactionId)
                }
            }

            step("#7 Search by request id and check the tab") {

                searchBy(requestId)

                assert {
                    elementContainsTextWithIgnoreCase(requestSearchResultHeaderLabel, TEXT_SEARCH_RESULTS)
                    elementContainsTextWithIgnoreCase(requestQueryEnteredHeaderLabel, TEXT_QUERY_ENTERED)

                    elementContainsTextWithIgnoreCase(requestSearchStringLabel, requestId)

                    elementPresented(requestTxsCountLabel)

                    elementContainsTextWithIgnoreCase(
                        requestTransactionIncludedHeaderLabel,
                        TEXT_TRANSACTION_INCLUDED
                    )

                    elementPresented(requestTransactionStatusLabel)

                    elementContainsTextWithIgnoreCase(requestTransactionChannelLabel, TEXT_CHANNEL)
                    elementPresented(requestTransactionChannelDataLabel)

                    elementContainsTextWithIgnoreCase(requestTransactionBlockLabel, TEXT_BLOCK)
                    elementPresented(requestTransactionBlockDataLabel)

//                    elementContainsTextWithIgnoreCase(
//                        requestTransactionAcceptredeemrequestLabel,
//                        TEXT_ACCEPTREDEEMREQUEST
//                    )

                    elementPresented(requestTransactionIdButton)
                }
            }

            step("#9 Search by endorser and check the tab") {

                searchBy(endorser)

                assert {
                    elementContainsTextWithIgnoreCase(endorserSearchResultHeaderLabel, TEXT_SEARCH_RESULTS)

                    elementContainsTextWithIgnoreCase(endorserQueryEnteredHeaderLabel, TEXT_QUERY_ENTERED)

                    elementContainsTextWithIgnoreCase(endorserSearchStringLabel, endorser)

                    elementPresented(endorserTxsCountLabel)

                    elementContainsTextWithIgnoreCase(endorserHeaderLabel, TEXT_ENDORSER)

                    elementContainsTextWithIgnoreCase(endorserIdLabel, TEXT_ENDORSER_ID)
                    elementContainsTextWithIgnoreCase(endorserIdDataLabel, endorser)

                    elementContainsTextWithIgnoreCase(
                        endorserTransactionIncludedHeaderLabel,
                        TEXT_TRANSACTION_INCLUDED
                    )

                    transactionDateFilter.assertTransactionDateFilterIsPresented()

                    endorserTransactionIncludedList.forEach {
                        assert {
                            elementPresented(it.transactionStatusLabel)
                            elementPresented(it.transactionTimestampLabel)
                            elementPresented(it.transactionIdLabel)

                        }
                    }
                }
            }

            step("#10 Search by method and check the tab") {

                searchBy(method)

                assert {
                    elementContainsTextWithIgnoreCase(methodSearchResultHeaderLabel, TEXT_SEARCH_RESULTS)

                    elementContainsTextWithIgnoreCase(methodQueryEnteredHeaderLabel, TEXT_QUERY_ENTERED)

                    elementContainsTextWithIgnoreCase(methodSearchStringLabel, method)

                    elementPresented(methodTxsCountLabel)

                    elementContainsTextWithIgnoreCase(
                        methodTransactionIncludedHeaderLabel,
                        TEXT_TRANSACTION_INCLUDED
                    )

                    transactionDateFilter.assertTransactionDateFilterIsPresented()

                    methodTransactionIncludedList.forEach {
                        assert {
                            elementPresented(it.transactionStatusLabel)
                            elementPresented(it.transactionTimestampLabel)
                            elementPresented(it.transactionIdLabel)

                        }
                    }
                }
            }
        }
    }

}