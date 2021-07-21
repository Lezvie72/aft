package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmP2PPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Display legal entity name instead of wallet address")
class DisplayLegalEntityNameInsteadOfWalletAddress : BaseTest() {

    @ResourceLocks(ResourceLock(Constants.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET))
    @TmsLink("ATMCH-2953")
    @Test
    @DisplayName("Blocktrade. Checking field Counterparty")
    fun blockTradeCheckingFieldCounterparty() {

        val baseAsset = CoinType.CC
        val quoteAsset = CoinType.VT

        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val senderUser = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val takerUser = Users.ATM_USER_2FA_OTF_OPERATION_SECOND


        prerequisite {
            prerequisitesBlocktrade(
                baseAsset.tokenSymbol,
                true,
                "1",
                baseAsset.tokenSymbol,
                "FIXED",
                baseAsset.tokenSymbol,
                "1",
                "FIXED",
                baseAsset
            )
        }

        val companyName = openPage<AtmProfilePage>(driver) { submit(takerUser) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(takerUser) }.takeWalletID()

        prerequisite {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                AtmP2PPage.ExpireType.TEMPORARY,
                senderUser
            )
            acceptP2P(takerUser, amount)
        }

        with(openPage<AtmP2PPage>(driver) { submit(takerUser) }) {
            e {
                click(viewMyP2P)
            }
            assert {
                elementContainingTextPresented("OTFWITHOUTOAUTHAUTOTEST")
            }
            e {
                click(incomingP2PS)
            }
            assert {
                elementContainingTextPresented("OTFWITHOUTOAUTHAUTOTEST")
            }
            e {
                click(dealHistoryP2P)
            }
            assert {
                elementContainingTextPresented("OTFWITHOUTOAUTHAUTOTEST")
            }
        }
    }

}


