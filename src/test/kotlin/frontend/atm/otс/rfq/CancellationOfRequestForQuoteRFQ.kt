package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmRFQPage
import pages.atm.AtmRFQPage.OperationType.BUY
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Cancellation of a request for quote (RFQ) by the participant who placed it")
class CancellationOfRequestForQuoteRFQ : BaseTest() {

    @Disabled("Invalid arguments при попытке открыть список запросов")
    @TmsLink("ATMCH-834")
    @Test
    @DisplayName("RFQ. Cancellation of a quote request by participant")
    fun rfqCancellationOfaQuoteRequestByParticipant() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = IT
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        with(openPage<AtmRFQPage>(driver) { submit(user) }) {
            createRFQ(BUY, baseAsset, quoteAsset, amount, "1", user)
        }
    }

}
