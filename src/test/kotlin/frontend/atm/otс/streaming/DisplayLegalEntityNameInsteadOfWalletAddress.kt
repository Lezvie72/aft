package frontend.atm.otс.streaming

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import models.OtfAmounts.AMOUNT_1
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


@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("STREAMING")
@Story("STREAMING. Display legal entity name instead of wallet address")
class DisplayLegalEntityNameInsteadOfWalletAddress : BaseTest() {

    private val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

    private val baseAsset = CC
    private val quoteAsset = VT
    private val amountBaseAsset = AMOUNT_1.amount

    private val taker = Users.ATM_USER_2FA_OTF_OPERATION_EIGHTH
    private val maker = Users.ATM_USER_2FA_OTF_OPERATION_SEVENTH

    private val counterpartyValueMaker = "ATMUSER2FAOTFOPERATIONSEVENTH"

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_EIGHTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SEVENTH)
    )
    @TmsLink("ATMCH-3114")
    @Test
    @Story("Positive test")
    @DisplayName("Streaming. Checking field Counterparty")
    fun streamingCheckingFieldCounterparty() {

        step("${maker.email} check Streaming Sell offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.TEMPORARY, maker
                )
            }
        }

        openPage<AtmProfilePage>(driver).logout()

        step("${taker.email} confirm Streaming Sell offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                    val myOffer = overviewOffersList.find {
                        it.unitPriceAmount == unitPriceBuy
                    } ?: error("Can't find offer with unit price '$unitPriceBuy'")

                    checkCounterparty(counterpartyValueMaker)

                    myOffer.open()

                    checkCounterparty(counterpartyValueMaker)

                    click(acceptOffer)
                    signAndSubmitMessage(taker, taker.otfWallet.secretKey)
                }
            }
        }

        step("${taker.email} check Streaming Sell offer with $unitPriceBuy in history table") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                Thread.sleep(3000)
                e {
                    click(overview)
                    click(tradeHistory)
                    click(showBuyOnly)
                    val offerBuy = tradeHistoryList.find {
                        it.unitPriceAmount == unitPriceBuy
                    } ?: error("Can't find offer with unit price '$unitPriceBuy'")

                    checkCounterparty(counterpartyValueMaker)

                    offerBuy.open()

                    checkCounterparty(counterpartyValueMaker)

                }

            }
        }
    }

}
