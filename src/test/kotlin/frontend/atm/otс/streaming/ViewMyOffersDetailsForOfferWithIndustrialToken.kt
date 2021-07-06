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
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.STREAMING.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Streaming")
@Story("View my offers/ offer details for offer with Industrial token.")
class
ViewMyOffersDetailsForOfferWithIndustrialToken : BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val industrialUserOne2FA = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.IT
    private val amountCount = OtfAmounts.AMOUNT_10.amount
    private val maturityDateInnerDate = quoteAsset.date
    private val invalid2FaKey = "123456"
    private val invalidPrivateKey = "12345678bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"
    private val wallet = "OTF 1"


    @ResourceLocks(ResourceLock(Constants.ROLE_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-3706")
    @Test
    @DisplayName("Check My offers and offer details")
    fun checkMyOffersAndOfferDetails() {
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountCount ${quoteAsset.tokenSymbol}",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }

        with(openPage<AtmStreamingPage>(driver)) {
            e {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "${quoteAsset.tokenSymbol}/${baseAsset.tokenSymbol}",
                "$amountCount ${quoteAsset.tokenSymbol}",
                    unitPriceSell.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED,
                    industrialUserOne, maturityDateInnerDate
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        with(openPage<AtmStreamingPage>(driver) { submit(industrialUserOne) }) {
            e {
                click(overview)
                click(myOffers)
                wait {
                    untilPresented(myOffersList)
                }
                setFilterToday(quoteAsset, baseAsset, maturityDateInnerDate)
                select(tradingPair, "$quoteAsset/$baseAsset")
                softAssert { elementPresented(buyLabel) }
                softAssert { elementPresented(sellLabel) }
                softAssert { elementPresented(baseAssetAmountLabel) }
                softAssert { elementPresented(quoteAssetAmountLabel) }
                softAssert { elementPresented(unitPriceLabel) }
                softAssert { elementPresented(maturityDateLabel) }
                softAssert { elementPresented(baseMaturityDate) }
                softAssert { elementPresented(counterpartyLabel) }
                softAssert { elementPresented(expirationLabel) }
                softAssert { elementPresented(cancelOffer) }

                findAndOpenOfferInOfferList(unitPriceBuy)
                wait { untilPresented(offerDetailsLabel) }

                softAssert { elementContainingTextPresented("COUNTERPARTY") }
                softAssert { elementContainingTextPresented("EXPIRATION DATE") }
                softAssert { elementContainingTextPresented("ASSET PAIR") }
                softAssert { elementContainingTextPresented("BASE ASSET AMOUNT") }
                softAssert { elementContainingTextPresented("MATURITY DATE") }
                softAssert { elementContainingTextPresented("PRICE PER UNIT") }
                softAssert { elementContainingTextPresented("TOTAL AMOUNT") }
                softAssert { elementContainingTextPresented("FEE OPTION") }
                softAssert { elementContainingTextPresented("AMOUNT TO RECEIVE") }
                softAssert { elementContainingTextPresented("AMOUNT TO SEND") }
                softAssert { elementContainingTextPresented("TRANSACTION FEE") }
                softAssert { elementContainingTextPresented("DIRECTION") }
            }
        }
    }
}