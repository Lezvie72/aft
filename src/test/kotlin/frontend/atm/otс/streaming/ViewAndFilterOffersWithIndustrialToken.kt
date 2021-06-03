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
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tag("IT OTC")
@Tags(Tag("OTC"), Tag("Streaming"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("View and filter offers with industrial token.")
class ViewAndFilterOffersWithIndustrialToken: BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountBuy = OtfAmounts.AMOUNT_1.amount

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4357")
    @Test
    @DisplayName("Availability of viewing industrial offers only for industrial participants.")
    fun availabilityOfViewingIndustrialOffersOnlyForIndustrialParticipants() {
        // preconditions
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}") //1.97179569
        val filter = "IT"
        // create industrial token offer
        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$quoteAsset/$baseAsset",
                    "$amountBuy $quoteAsset",
                    unitPrice.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                click(overview)
                click(showAllDialDirection)
                assert {
                    hasLeastOneCommonMeaning(tradingPair.getHeadersAsString(page), mutableSetOf(filter),
                        "Token $filter wasn't found when used industrial account $industrialUserTwo")
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                click(overview)
                click(showAllDialDirection)
                assert {
                    hasNotLeastOneCommonMeaning(tradingPair.getHeadersAsString(page), mutableSetOf(filter),
                        "Token $filter wasn't found when used industrial account $industrialUserTwo")
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_OTF))
    @TmsLink("ATMCH-4365")
    @Test
    @DisplayName("The filter section include maturity date filters for industrial token as base asset and quote asset.")
    fun filterSectionIncludeMaturityDateFiltersForIndustrialTokenAsBaseAndQuoteAsset() {
        // preconditions
        val checkParameterOne = "Base asset"
        val checkParameterTwo = "Quote asset"
        val pair = "IT/CC"

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserTwo) }) {
            e {
                click(overview)
                click(resetFilters)
                click(showAllDialDirection)
                select(sortBy, checkParameterOne)
                select(tradingPair, pair)
                assert {
                    elementPresented(baseMaturityDate)
                }
                click(resetFilters)
                select(sortBy, checkParameterTwo)
                select(tradingPair, pair)
                assert {
                    elementPresented(baseMaturityDate)
                }
            }
        }
    }
}