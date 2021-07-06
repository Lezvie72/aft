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
import pages.atm.AtmStreamingPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("Offer Cancellation Streaming")
class OfferCancellationStreaming : BaseTest() {


    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountValue = OtfAmounts.AMOUNT_10.amount

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET)
    @TmsLink("ATMCH-615")
    @Test
    @DisplayName("Streaming. Cancel buy offer")
    fun streamingCancelBuyOffer() {
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val user =
             Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesStreaming(
                baseAsset, quoteAsset, "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            createStreaming(
                AtmStreamingPage.OperationType.BUY,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountValue ${quoteAsset.tokenSymbol}",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user
            )
        }
        with(AtmStreamingPage(driver)) {
            cancelOffer(unitPrice, user)
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-625")
    @Test
    @DisplayName("Streaming. Cancel sell offer")
    fun streamingCancelSellOffer() {
        val unitPrice = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val user =
             Users.ATM_USER_2FA_OTF_OPERATION

        prerequisite {
            prerequisitesStreaming(
                baseAsset, quoteAsset, "1",
                "1", "1",
                "FIXED", "FIXED",
                true
            )
        }

        with(openPage<AtmStreamingPage>(driver) { submit(user) }) {
            createStreaming(
                AtmStreamingPage.OperationType.SELL,
                "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountValue ${quoteAsset.tokenSymbol}",
                unitPrice.toString(),
                AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                user
            )
        }
        with(AtmStreamingPage(driver)) {
            cancelOffer(unitPrice, user)
        }

    }
}