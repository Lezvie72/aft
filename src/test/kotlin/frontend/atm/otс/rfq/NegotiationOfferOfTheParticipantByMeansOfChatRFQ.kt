package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import pages.atm.AtmWalletPage
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("RFQ"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Negotiation of the offer of the participant by means of chat")
class NegotiationOfferOfTheParticipantByMeansOfChatRFQ : BaseTest() {

    @Disabled("не работатет чат")
    @TmsLink("ATMCH-1036")
    @Test
    @DisplayName("RFQ. Accepting a participant’s request via chat")
    fun rfqAcceptingParticipantRequestViaChat() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val dealAmount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

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
            findDealOffers(amount, dealAmount)
        }

    }

}
