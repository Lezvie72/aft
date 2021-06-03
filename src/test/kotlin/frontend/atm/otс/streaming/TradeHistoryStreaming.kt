package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmStreamingPage
import utils.helpers.Users
import utils.helpers.openPage


@Tags(Tag("OTC"), Tag("Streaming"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Trade History Streaming")
class TradeHistoryStreaming : BaseTest() {

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
        with(openPage<AtmStreamingPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(overview)
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
}