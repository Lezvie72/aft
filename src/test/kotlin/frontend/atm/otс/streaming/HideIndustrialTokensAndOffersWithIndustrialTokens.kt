package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag("OTC"), Tag("Streaming"))
@Tag("IT OTC")
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Hide industrial tokens and offers with industrial tokens from non-industrial participants")
class HideIndustrialTokensAndOffersWithIndustrialTokens: BaseTest() {
    // precondition
    private val maturityDateInnerDate = "21 September 2020"
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountSell = OtfAmounts.AMOUNT_10.amount
    private val amountBuy = OtfAmounts.AMOUNT_1.amount

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4342")
    @Test
    @DisplayName("Offers with industrial tokens by industrial participant")
    fun offersWithIndustrialTokensByIndustrialParticipant() {
        val pair = "IT/CC"
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        // create industrial token offer
        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountSell $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDateInnerDate
                )
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        with(openPage<AtmStreamingPage>(driver){submit(industrialUserTwo)}) {
            e {
                click(overview)
                select(tradingPair, pair)
                select(baseMaturityDate, maturityDateInnerDate)
                findAndOpenOfferInOverview(unitPriceOffer)
                acceptOffer(industrialUserTwo)
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4343")
    @Test
    @DisplayName("Offers with industrial tokens by non industrial participant")
    fun offersWithIndustrialTokensByNonIndustrialParticipant() {
        // precondition
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        // create industrial token offer
        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountSell $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDateInnerDate
                )
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        //generate actual token list and filter by condition
        val key = AtmAdminTokensPage.TICKER_SYMBOL
        val value = AtmAdminTokensPage.TOKEN_TYPE
        var industrialTokens: MutableSet<String> = mutableSetOf()
        val filterValue = "Industrial Token"
        var tradingFormListValues: MutableSet<String> = mutableSetOf()

        with(openPage<AtmAdminTokensPage>(driver){submit(Users.ATM_ADMIN)}) {
            var rowData = getColumnAsList(key, value)
            if (rowData.isNullOrEmpty()) throw Exception("Table is empty")
            for (num in 0..(rowData[key]?.size?.minus(1) ?: throw Exception("Table is empty")))
                if (rowData[value]?.get(num)?.contains(filterValue) == true) rowData[key]?.get(num)?.let { industrialTokens.add(it) }
        }

        with(openPage<AtmStreamingPage>(driver){submit(nonIndustrialUser)}) {
            e {
                //get actual token list from trading form
                click(createOffer)
                wait {
                    until("Active element loaded", 15) {
                        check {
                            isElementPresented(selectAssetPair)
                        }
                    }
                }
                tradingFormListValues = selectAssetPair.getHeadersAsString(page)
                assert {
                    hasNotLeastOneCommonMeaning(industrialTokens, tradingFormListValues)
                }
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4336")
    @Test
    @DisplayName("Selection industrial tokens only by participants having industrial mark in admin panel")
    fun selectionIndustrialTokensOnlyByParticipantsHavingIndustrialMarkInAdminPanel() {
        // precondition
        val unitPriceOffer = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        //generate actual token list and filter by condition
        val key = AtmAdminTokensPage.TICKER_SYMBOL
        val value = AtmAdminTokensPage.TOKEN_TYPE
        var industrialTokens: MutableSet<String> = mutableSetOf()
        val filterValue = "Industrial Token"
        var tradingFormListValues: MutableSet<String> = mutableSetOf()

        with(openPage<AtmAdminTokensPage>(driver){submit(Users.ATM_ADMIN)}) {
            var rowData = getColumnAsList(key, value)
            if (rowData.isNullOrEmpty()) throw Exception("Table is empty")
            for (num in 0..(rowData[key]?.size?.minus(1) ?: throw Exception("Table is empty")))
                if (rowData[value]?.get(num)?.contains(filterValue) == true) rowData[key]?.get(num)?.let { industrialTokens.add(it) }
        }

        with(openPage<AtmStreamingPage>(driver){submit(industrialUserOne)}) {
            e {
                //get actual token list from trading form
                click(createOffer)
                wait {
                    until("Active element loaded", 15) {
                        check {
                            isElementPresented(selectAssetPair)
                        }
                    }
                }
                tradingFormListValues = selectAssetPair.getHeadersAsString(page)
                // check goal
                assert {
                    hasLeastOneCommonMeaning(industrialTokens, tradingFormListValues)
                }
                click(tradingHeader)
                wait {
                    until("", 10) {
                        untilPresented(createOffer)
                    }
                }

                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDateInnerDate
                )

                click(tradingHeader)
                wait {
                    until("", 10) {
                        untilPresented(createOffer)
                    }
                }
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$quoteAsset/$baseAsset",
                    "$amountSell $quoteAsset",
                    unitPriceOffer.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne,
                    maturityDateInnerDate
                )
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4339")
    @Test
    @DisplayName("Selection industrial tokens by non-industrial participants")
    fun selectionIndustrialTokensByNonIndustrialParticipants() {
        val key = AtmAdminTokensPage.TICKER_SYMBOL
        val value = AtmAdminTokensPage.TOKEN_TYPE
        var industrialTokens: MutableSet<String> = mutableSetOf()
        val filterValue = "Industrial Token"
        var tradingFormListValues: MutableSet<String> = mutableSetOf()

        //generate actual token list and filter by condition
        with(openPage<AtmAdminTokensPage>(driver){submit(Users.ATM_ADMIN)}) {
            var rowData = getColumnAsList(key, value)
            if (rowData.isNullOrEmpty()) throw Exception("Table is empty")
            for (num in 0..(rowData[key]?.size?.minus(1) ?: throw Exception("Table is empty")))
                if (rowData[value]?.get(num)?.contains(filterValue) == true) rowData[key]?.get(num)?.let { industrialTokens.add(it) }
        }

        with(openPage<AtmStreamingPage>(driver){submit(nonIndustrialUser)}) {
            e {
                //get actual token list from trading form
                click(createOffer)
                wait {
                    until("Active element loaded", 15) {
                        check {
                            isElementPresented(selectAssetPair)
                        }
                    }
                }
                tradingFormListValues = selectAssetPair.getHeadersAsString(page)
                assert {
                    hasNotLeastOneCommonMeaning(industrialTokens, tradingFormListValues)
                }
            }
        }
    }
}