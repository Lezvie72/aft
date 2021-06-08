package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Cancellation of a placed offer by the participant who placed it in RFQ")
class CancellationOfPlacedOfferByTheParticipantWhoPlacedRFQ : BaseTest() {

    @Disabled("не отменяется по времени")
    @TmsLink("ATMCH-996")
    @Test
    @DisplayName("RFQ. Cancel an offer upon expiration")
    fun rfqCancelAnOfferUponExpiration() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesRfq(
                CC, VT
            )
        }

        with(openPage<AtmRFQPage>(driver) { submit(user) }) {
            createRFQ(BUY, CC, VT, amount, "1", user)
            assert {
                elementNotPresented(openRFQ)//доработать проверку
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET)//скорее всего будет переписан
    @TmsLink("ATMCH-995")
    @Test
    @DisplayName("RFQ. Cancel offer by participant")
    fun rfqCancelOfferByParticipant() {
        val baseAsset = CC
        val quoteAsset = VT
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        with(openPage<AtmRFQPage>(driver) { submit(user) }) {
            createRFQ(BUY, baseAsset, quoteAsset, amount, "1", user)
            e {
                click(myRequest)
            }
            val myOffer = outgoingOffers.find {
                it.baseAmount == amount
            } ?: error("Can't find offer with base amount '$amount'")

            assert {
                elementWithTextPresented(" BASE ASSET/AMOUNT ")
                elementWithTextPresented(" QUOTE ASSET ")
                elementWithTextPresented(" Expiration ")
            }

            myOffer.cancelRfqOffer(user)
            driver.navigate().refresh()
            val cancelledOffer = outgoingOffers.find {
                it.baseAmount == amount
            }
            assertThat(
                "Offer with amount $amount should have been be cancelled",
                cancelledOffer,
                nullValue()
            )
        }
    }

}
