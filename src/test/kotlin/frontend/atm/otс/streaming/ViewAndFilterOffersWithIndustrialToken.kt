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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import pages.atm.AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("View and filter offers with industrial token.")
class ViewAndFilterOffersWithIndustrialToken : BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountBuy = OtfAmounts.AMOUNT_10.amount
    private val maturityDateInnerDate = quoteAsset.date

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-4357")
    @Test
    @DisplayName("Availability of viewing industrial offers only for industrial participants.")
    fun availabilityOfViewingIndustrialOffersOnlyForIndustrialParticipants() {
        // preconditions
        val unitPrice = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val filter = "IT"
        // create industrial token offer
        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                    "$amountBuy ${quoteAsset.tokenSymbol}",
                    unitPrice.toString(),
                    GOOD_TILL_CANCELLED,
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
                    hasLeastOneCommonMeaning(
                        tradingPair.getHeadersAsString(page), mutableSetOf(filter),
                        "Token $filter wasn't found when used industrial account $industrialUserTwo"
                    )
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                click(overview)
                click(showAllDialDirection)
                assert {
                    hasNotLeastOneCommonMeaning(
                        tradingPair.getHeadersAsString(page), mutableSetOf(filter),
                        "Token $filter wasn't found when used industrial account ${industrialUserTwo.email}"
                    )
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-4365")
    @Test
    @DisplayName("The filter section include maturity date filters for industrial token as base asset and quote asset.")
    fun filterSectionIncludeMaturityDateFiltersForIndustrialTokenAsBaseAndQuoteAsset() {
        // preconditions
        val checkParameterOne = "Base asset"
        val checkParameterTwo = "Quote asset"

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserTwo) }) {
            e {
                click(overview)
                click(resetFilters)
                click(showAllDialDirection)
                select(sortBy, checkParameterOne)
                select(tradingPair, "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}")
                assert {
                    elementPresented(baseMaturityDate)
                }
                click(resetFilters)
                select(sortBy, checkParameterTwo)
                select(tradingPair, "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}")
                assert {
                    elementPresented(baseMaturityDate)
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4359")
    @Test
    @DisplayName("Streaming. Cards in overview include maturity date for IT")
    fun cardsInOverviewIncludeMaturityDateDorIt() {
        val unitPriceBaseQuote = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceQuoteBase = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        // create industrial token offer
        step("Preconditions - create offer $quoteAsset/$baseAsset and $baseAsset/$quoteAsset. Step 0") {
            with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
                e {
                    createStreaming(
                        AtmStreamingPage.OperationType.BUY,
                        "$quoteAsset/$baseAsset",
                        "$amountBuy $quoteAsset",
                        unitPriceBaseQuote.toString(),
                        GOOD_TILL_CANCELLED,
                        industrialUserOne
                    )
                }
            }

            with(openPage<AtmStreamingPage>(driver)) {
                e {
                    createStreaming(
                        AtmStreamingPage.OperationType.BUY,
                        "$baseAsset/$quoteAsset",
                        "$amountBuy $baseAsset",
                        unitPriceQuoteBase.toString(),
                        GOOD_TILL_CANCELLED,
                        industrialUserOne
                    )
                }
            }
            openPage<AtmProfilePage>(driver).logout()
        }

        step("Compare results $quoteAsset/$baseAsset maturity date from overview should be equal in open card. Step 1-8") {
            with(openPage<AtmStreamingPage>(driver) { submit(industrialUserTwo) }) {
                e {
                    click(overview)
                    setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                    val row = findOfferBy(unitPriceBaseQuote, overviewOffersList)
                    val cardTextMaturityDate = row.maturityDateText.trim()
                    click(row)
                    val openCardTextMaturityDate = offerMaturityDateInOpenCard.text.trim()

                    assertThat(
                        "Maturity date from overview $cardTextMaturityDate should be equal in open card $openCardTextMaturityDate",
                        cardTextMaturityDate.contains(openCardTextMaturityDate)
                    )

                    click(overviewBreadcrumbs)
                }
            }
        }

        step("Compare results $baseAsset/$quoteAsset maturity date from overview should be equal in open card. Step 9-12") {
            with(openPage<AtmStreamingPage>(driver)) {
                e {
                    click(overview)
                    setFilterToday(baseAsset, quoteAsset, maturityDateInnerDate)
                    val row = findOfferBy(unitPriceQuoteBase, overviewOffersList)
                    val cardTextMaturityDate = row.maturityDateText.trim()
                    click(row)
                    val openCardTextMaturityDate = offerMaturityDateInOpenCard.text.trim()

                    assertThat(
                        "Maturity date from overview $cardTextMaturityDate should be equal in open card $openCardTextMaturityDate",
                        cardTextMaturityDate.contains(openCardTextMaturityDate)
                    )

                    click(overviewBreadcrumbs)
                }
            }
        }
    }
}