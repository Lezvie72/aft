package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.CC
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Cancellation of a request for quote (RFQ) by the participant who placed it")
class AcceptanceOfTheOfferByTheParticipantAndTransaction : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1037")
    @Test
    @Story("Positive test")
    @DisplayName("RFQ. Acceptance of the offer by the participant and transaction")
    fun rfqAcceptanceOfTheOfferByTheParticipantAndTransaction() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val dealAmount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = CoinType.VT
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseAsset, quoteAsset, amount, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            e {
                click(myRequest)
            }
            findOutgoingRFQ(amount)
            val myOfferDeal = outgoingDealOffers.find {
                it.offerAmount == dealAmount
            } ?: error("Can't find offer with offer amount'$dealAmount'")
            assert {
                elementWithTextPresented(" BASE ASSET/AMOUNT ")
                elementWithTextPresented(" QUOTE ASSET ")
                elementWithTextPresented(" Expiration ")
                elementWithTextPresented(" COUNTERPARTY ")
                elementWithTextPresented(" OFFERS ")
                elementWithTextPresented(" OFFER EXPIRATION ")
            }
            myOfferDeal.openChat()
            assert {
                elementWithTextPresented("REQUEST DETAILS")
                elementWithTextPresented(" BASE ASSET/AMOUNT ")
                elementWithTextPresented(" QUOTE ASSET ")
                elementWithTextPresented(" Expiration ")
                elementWithTextPresented("OFFER DETAILS")
                elementWithTextPresented(" OFFER AMOUNT ")
                elementWithTextPresented(" UNIT PRICE ")
                elementWithTextPresented(" Counterparty ")
//                elementWithTextPresented(" OFFER EXPIRATION ")
                elementWithTextPresented(" Fee option ")
                elementWithTextPresented(" Transaction fee ")
                elementWithTextPresented(" AMOUNT TO RECEIVE ")
                elementWithTextPresented(" AMOUNT TO SEND ")
            }
            e {
                click(acceptOffer)
            }
            signAndSubmitMessage(user1, user1.otfWallet.secretKey)
        }

    }
}
