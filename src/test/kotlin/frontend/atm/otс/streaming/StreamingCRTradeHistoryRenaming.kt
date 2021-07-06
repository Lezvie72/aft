package frontend.atm.otc.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("OTF Streaming. CR Trade history renaming")
class StreamingCRTradeHistoryRenaming : BaseTest() {

    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountBaseAsset = OtfAmounts.AMOUNT_10.amount


    @TmsLink("ATMCH-6220")
    @Test
    @DisplayName("OTF Streaming.CR Trade history renaming")
    fun otfStreamingCRTradeHistoryRenaming() {

        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val taker = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH
        val maker = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

        prerequisite {
            prerequisitesStreaming(
                baseAsset, quoteAsset, "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        step("${maker.email} check Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, maker
                )
            }
        }

        openPage<AtmProfilePage>(driver).logout()

        step("${taker.email} confirm Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                }
                findAndOpenOfferInOverview(unitPriceBuy)
                acceptOffer(taker)
            }
        }


        step("${taker.email} check Accepted Streaming Buy offer with $unitPriceSell in history table") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                    click(tradeHistory)
                }
                assert {
                    elementContainingTextPresented("CONFIRMED DEAL")
                    elementContainingTextPresented("ACCEPTED OFFER")
                    elementContainingTextPresented("BUY")
                    elementContainingTextPresented("BASE ASSET/AMOUNT")
                    elementContainingTextPresented("QUOTE ASSET/AMOUNT")
                    elementContainingTextPresented("UNIT PRICE")
                    elementContainingTextPresented("Deal timestamp")
                    elementContainingTextPresented("Counterparty")
                }
                val offerBuy = findOfferInHistory(unitPriceBuy)

                MatcherAssert.assertThat(
                    "Offer with amount $unitPriceBuy should exists",
                    offerBuy,
                    Matchers.notNullValue()
                )
                e {
                    click(offerBuy)
                }
                assert {
                    elementContainingTextPresented("CONFIRMED DEAL")
                    elementContainingTextPresented("ACCEPTED OFFER")
                    elementContainingTextPresented("BUY")
                    elementContainingTextPresented("Counterparty")
                    elementContainingTextPresented("Deal timestamp")
                    elementContainingTextPresented("Asset pair")
                    elementContainingTextPresented("Base asset amount")
                    elementContainingTextPresented("Price per unit")
                    elementContainingTextPresented("Total Amount")
                    elementContainingTextPresented("Fee option")
                    elementContainingTextPresented("Transaction fee")
                }
            }

        }
    }
}
