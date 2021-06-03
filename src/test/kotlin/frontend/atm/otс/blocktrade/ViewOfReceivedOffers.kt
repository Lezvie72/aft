package frontend.atm.otс.blocktrade

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
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal


@Tags(Tag("OTC"), Tag("Blocktrade"))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("View of received offers")
class ViewOfReceivedOffers : BaseTest() {

    @ResourceLock(Constants.USER_BALANCE_LOCK)
    @TmsLink("ATMCH-930")
    @Test
    @DisplayName("P2P. Check incoming offers. Good till cancel offer")
    fun p2pCheckIncomingOffersGoodTillCancel() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val baseAsset = CC
        val quoteAsset = VT

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
                baseAsset, quoteAsset
            )
        }
        val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()

        val walletID =
            openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user1) }) {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                ExpireType.TEMPORARY,
                user1
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
            e {
                findIncomingP2P(amount)
            }
            assert {
                elementContainingTextPresented("Doc ID")
                elementContainingTextPresented("Counterparty")
                elementContainingTextPresented("Valid for")
                elementContainingTextPresented("To wallet")
                elementContainingTextPresented("Incoming amount")
                elementContainingTextPresented("Requested amount")
                elementContainingTextPresented("Comment")
                elementContainingTextPresented("From my wallet")
                elementContainingTextPresented("AVAILABLE BALANCE")
                elementContainingTextPresented("Fee option")
                elementContainingTextPresented("AMOUNT TO RECEIVE")
                elementContainingTextPresented("AMOUNT TO SEND")
                elementPresented(incomingP2PS)
            }
            e {
                click(incomingP2PS)
            }
            assert {
                urlEndsWith("/incoming")
            }
            e {
                click(myP2Ps)
            }
            assert {
                urlEndsWith("/outgoing")
                elementPresented(createFromMyBlockTrade)
            }
            e {
                click(incomingP2PS)
            }
            assert {
                urlEndsWith("/incoming")
            }
        }

    }

    @TmsLink("ATMCH-931")
    @Test
    @DisplayName("P2P. Check incoming offers. Temporary offer")
    fun p2pCheckIncomingTemporaryOffers() {

        val baseAsset = CC
        val quoteAsset = VT

        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")

        val senderUser = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        val companyName = openPage<AtmProfilePage>(driver) { submit(user) }.getCompanyName()
        val walletID = openPage<AtmWalletPage>(driver) { submit(user) }.takeWalletID()

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
                baseAsset, quoteAsset
            )
        }

        prerequisite {
            createP2P(
                walletID,
                companyName,
                baseAsset,
                amount.toString(),
                quoteAsset,
                amount.toString(),
                ExpireType.TEMPORARY,
                senderUser
            )
        }

        openPage<AtmProfilePage>(driver).logout()
        with(openPage<AtmP2PPage>(driver) { submit(user) }) {
            e {
                click(viewIncomingP2P)

            }
            //TODO: add assertions for displaying
            assert { urlEndsWith("/trading/p2p/incoming") }
            e {
                click(openIncomingP2P)
            }
            assert {
                elementContainingTextPresented("Doc ID")
                elementContainingTextPresented("Counterparty")
                elementContainingTextPresented("Valid for")
                elementContainingTextPresented("To wallet")
                elementContainingTextPresented("Incoming amount")
                elementContainingTextPresented("Requested amount")
                elementContainingTextPresented("Comment")
                elementContainingTextPresented("From my wallet")
                elementContainingTextPresented("AVAILABLE BALANCE")
                elementContainingTextPresented("Fee option")
                elementContainingTextPresented("AMOUNT TO RECEIVE")
                elementContainingTextPresented("AMOUNT TO SEND")
            }

        }

    }

}


