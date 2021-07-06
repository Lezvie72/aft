package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmStreamingPage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Trade History Streaming")
class TradeHistoryStreaming : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT

    @Disabled("OUT OF SCOPE")
    @TmsLink("ATMCH-775")
    @Test
    @DisplayName("Streaming. Offer history. Sell")
    fun streamingOfferHistorySell() {
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            e {
                click(overview)
                click(showSellOnly)
            }
            assert {
                elementWithTextPresentedIgnoreCase("BASE ASSET/AMOUNT")
                elementWithTextPresentedIgnoreCase("QUOTE ASSET/AMOUNT")
                elementWithTextPresentedIgnoreCase("UNIT PRICE")
                elementWithTextPresentedIgnoreCase("EXPIRATION")
                elementWithTextPresentedIgnoreCase("COUNTERPARTY")
                elementWithTextPresentedIgnoreCase("Base asset")
                elementWithTextPresentedIgnoreCase("Quote asset")
                elementWithTextPresentedIgnoreCase("Date from")
                elementWithTextPresentedIgnoreCase("Date to")
                elementWithTextPresentedIgnoreCase("Sort by ")
            }
        }
    }

    @TmsLink("ATMCH-824")
    @Test
    @DisplayName("Streaming. Offer history. Buy")
    fun streamingOfferHistoryBuy() {
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            e {
                click(overview)
                click(showBuyOnly)
            }
            assert {
                elementWithTextPresentedIgnoreCase(" BASE ASSET/AMOUNT ")
                elementWithTextPresentedIgnoreCase(" QUOTE ASSET/AMOUNT ")
                elementWithTextPresentedIgnoreCase(" UNIT PRICE ")
                elementWithTextPresentedIgnoreCase(" EXPIRATION ")
                elementWithTextPresentedIgnoreCase(" COUNTERPARTY ")
                //TODO: wrap to method
                elementPresented(showAllDialDirection)
                elementPresented(showSellOnly)
                elementPresented(showBuyOnly)
                elementPresented(tradingPair)
                elementPresented(sortBy)
//                elementPresented(sortByDirection)
                elementPresented(dateFrom)
                elementPresented(dateTo)
                elementPresented(resetFilters)

            }
            //TODO: add prerequisites for filters checking
            //TODO: проверить работу фильтров
        }
    }

    @TmsLink("ATMCH-830")
    @Test
    @DisplayName("Streaming. Offer history. Cancelled/expired offers")
    fun streamingOfferHistoryExpiredOffers() {
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET) }) {
            e {
                click(overview)
                click(showBuyOnly)
            }
            assert {
                elementContainingTextNotPresented("CANCELLED")
            }
            e {
                click(showSellOnly)
            }
            assert {
                elementContainingTextNotPresented("CANCELLED")
            }

        }
    }

    @TmsLink("ATMCH-777")
    @Test
    @DisplayName("Streaming. List of offers")
    fun streamingListOfOffers() {
        val amount = OtfAmounts.AMOUNT_10.amount
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create buy and sell offers") {
            with(openPage<AtmStreamingPage>(driver) { submit(userOne) }) {
                e {
                    createStreaming(
                        AtmStreamingPage.OperationType.BUY,
                        "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                        "$amount ${quoteAsset.tokenSymbol}",
                        unitPriceBuy.toString(),
                        AtmStreamingPage.ExpireType.TEMPORARY,
                        userOne
                    )
                }
            }

            with(openPage<AtmStreamingPage>(driver)) {
                e {
                    createStreaming(
                        AtmStreamingPage.OperationType.SELL,
                        "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                        "$amount ${quoteAsset.tokenSymbol}",
                        unitPriceSell.toString(),
                        AtmStreamingPage.ExpireType.TEMPORARY,
                        userOne
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmStreamingPage>(driver) { submit(userTwo) }) {
            e {
                click(overview)

                assert {
                    elementWithTextPresentedIgnoreCase("BASE ASSET/AMOUNT")
                    elementWithTextPresentedIgnoreCase("QUOTE ASSET/AMOUNT")
                    elementWithTextPresentedIgnoreCase("UNIT PRICE")
                    elementWithTextPresentedIgnoreCase("EXPIRATION")
                    elementWithTextPresentedIgnoreCase("COUNTERPARTY")
                }

                findAndOpenOfferInOverview(unitPriceBuy)

                assert {
                    elementWithTextPresentedIgnoreCase("DIRECTION")
                    elementWithTextPresentedIgnoreCase("COUNTERPARTY")
                    elementWithTextPresentedIgnoreCase("EXPIRATION DATE")
                    elementWithTextPresentedIgnoreCase("ASSET PAIR")
                    elementWithTextPresentedIgnoreCase("BASE ASSET AMOUNT")
                    elementWithTextPresentedIgnoreCase("PRICE PER UNIT")
                    elementWithTextPresentedIgnoreCase("TOTAL AMOUNT")
                    elementWithTextPresentedIgnoreCase("FEE OPTION")
                    elementWithTextPresentedIgnoreCase("TRANSACTION FEE")
                    elementWithTextPresentedIgnoreCase("AMOUNT TO RECEIVE")
                    elementWithTextPresentedIgnoreCase("AMOUNT TO SEND")
                    elementWithTextPresentedIgnoreCase("CANCEL")
                    elementWithTextPresentedIgnoreCase("ACCEPT OFFER")
                }
                click(cancelOffer)


                step("Check filter working") {
                    assert {
                        elementPresented(showAllDialDirection)
                        elementPresented(showSellOnly)
                        elementPresented(showBuyOnly)
                        elementPresented(tradingPair)
                        elementPresented(counterparty)
                        elementPresented(sortBy)
                        elementPresented(dateFrom)
                        elementPresented(dateTo)
                        elementPresented(resetFilters)
                    }

                    setFilterToday(quoteAsset, baseAsset)

                    click(showSellOnly)
                    assertThat(
                        "After click show sell, should be visible only offers with sell type",
                        isOfferExist(unitPriceSell, overviewOffersList) and !isOfferExist(
                            unitPriceBuy,
                            overviewOffersList
                        )
                    )

                    click(showBuyOnly)
                    assertThat(
                        "After click buy sell, should be visible only offers with buy type",
                        !isOfferExist(unitPriceSell, overviewOffersList) and isOfferExist(
                            unitPriceBuy,
                            overviewOffersList
                        )
                    )

                    click(showAllDialDirection)
                    assertThat(
                        "After click show all, should be visible offers with buy and sell type",
                        isOfferExist(unitPriceSell, overviewOffersList) and isOfferExist(
                            unitPriceBuy,
                            overviewOffersList
                        )
                    )
                }
            }
        }
    }
}