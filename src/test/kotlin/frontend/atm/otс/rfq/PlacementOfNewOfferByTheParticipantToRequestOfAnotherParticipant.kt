package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminRfqSettingsPage
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("RFQ"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Placement of a new offer by the participant to a request of another participant")
class PlacementOfNewOfferByTheParticipantToRequestOfAnotherParticipant : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-994")
    @Test
    @DisplayName("Adding a new offer at the request of another participant.")
    fun addingNewOfferAtTheRequestOfAnotherParticipant() {
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
