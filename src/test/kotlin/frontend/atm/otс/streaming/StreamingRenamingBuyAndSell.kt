package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
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
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Offer Acceptance Streaming")
class StreamingRenamingBuyAndSell : BaseTest() {

    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountBaseAsset = OtfAmounts.AMOUNT_10.amount

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FIFTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SIXTH)
    )
    @TmsLink("ATMCH-6265")
    @Test
    @DisplayName("Streaming. Checking the offer type in Overview section")
    fun streamingCheckingTheOfferTypeInOverviewSection() {

        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val taker = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH
        val maker = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

        prerequisite {
            prerequisitesStreaming(
                baseAsset.toString(), quoteAsset.toString(), "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        step("${maker.email} check Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "$baseAsset/$quoteAsset",
                    "$amountBaseAsset $baseAsset",
                    unitPriceSell.toString(),
                    AtmStreamingPage.ExpireType.TEMPORARY, maker
                )
            }
        }

        step("${maker.email} check Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "$baseAsset/$quoteAsset",
                    "$amountBaseAsset $baseAsset",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, maker
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        step("${taker.email} check Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                }
                val myOffer = overviewOffersList.find {
                    it.unitPriceAmount == unitPriceBuy
                } ?: error("Can't find offer with unit price '$unitPriceBuy'")

                assert {
                    elementPresented(participantBuys)
                    elementContainingTextPresented("PARTICIPANT BUYS")
                }
                myOffer.open()
                assert {
                    elementPresented(participantBuys)
                    elementContainingTextPresented("PARTICIPANT BUYS")
                }
            }
        }

        step("${maker.email} check Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                }
                val myOffer = overviewOffersList.find {
                    it.unitPriceAmount == unitPriceSell
                } ?: error("Can't find offer with unit price '$unitPriceSell'")

                assert {
                    elementPresented(participantSells)
                    elementContainingTextPresented("PARTICIPANT SELLS")
                }
                myOffer.open()
                assert {
                    elementPresented(participantSells)
                    elementContainingTextPresented("PARTICIPANT SELLS")
                }

            }
        }

    }

}