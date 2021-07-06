package frontend.atm.otс.blocktrade

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminBlocktradeSettingsPage
import pages.atm.AtmP2PPage
import pages.atm.AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED
import pages.atm.AtmP2PPage.ExpireType.TEMPORARY
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Tags(Tag(TagNames.Flow.OTC),Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Cancellation of a Blocktrade offer to conclude a deal by the recipient of the offer")
class CancellationOfBlocktradeOfferToConcludeDealByTheParticipantWhoPlacedIt : BaseTest() {

    @TmsLink("ATMCH-935")
    @Test
    @DisplayName("Cancellation P2P offer by placer. Interface")
    fun cancellationP2POfferByPlacerInterface() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

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
                baseAsset,quoteAsset
            )
        }

        step("GIVEN user has outgoing p2p offer") {
            val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
            val walletID =
                openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()
            openPage<AtmProfilePage>().logout()

            with(openPage<AtmP2PPage>(driver) { submit(user) }) {
                createP2P(companyName, companyName, baseAsset, amount.toString(), quoteAsset, amount.toString(), TEMPORARY, user)
            }
        }

        with(openPage<AtmP2PPage>(driver) { submit(user) }) {
            e {
                click(viewMyP2P)
            }
            assert {
                elementIsDisplayed("Create P2P from My P2P")
                urlEndsWith("/trading/p2p/outgoing")
            }
            e { click(openP2P) }
            assert {
                elementWithTextPresented(" Doc ID ")
                elementWithTextPresented(" Submission date ")
                elementWithTextPresented(" Destination wallet ")
//                elementWithTextPresented(" Status ")
                elementWithTextPresented(" AMOUNT TO RECEIVE ")
                elementWithTextPresented(" AMOUNT TO SEND ")
                elementPresented(closeBlockTradeCard)
                elementPresented(cancelOpenBlockTrade)
            }
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-939")
    @Test
    @DisplayName("Cancellation P2P offer by placer. Success cancelation")
    fun cancelP2POfferByPlacerSuccess() {
        val amount = BigDecimal("3.${RandomStringUtils.randomNumeric(8)}")
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
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
                baseAsset,quoteAsset
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
                CC,
                amount.toString(),
                VT,
                amount.toString(),
                GOOD_TILL_CANCELLED,
                user1
            )
        }
        with(openPage<AtmP2PPage>(driver)) {
            e {
                click(viewMyP2P)
            }
            val myOffer = outgoingOffers.find {
                it.amountToReceive == amount
            } ?: error("Can't find offer with amount $amount")

            myOffer.cancelOffer()
            e {
                click(yesButton)
            }
            signAndSubmitMessage(user1, user1.otfWallet.secretKey)

            driver.navigate().refresh()
            val cancelledOffer = outgoingOffers.find {
                it.amountToReceive == amount
            }
            assertThat(
                "Offer with amount $amount should have been be cancelled",
                cancelledOffer,
                nullValue()
            )
        }
    }


}


