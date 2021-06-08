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
import org.hamcrest.Matchers.closeTo
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
import pages.atm.AtmRFQPage.OperationType.SELL
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
@Story("Check of possibility of transaction performance in RFQ")
class CheckOfPossibilityOfTransactionPerformanceInRFQ : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1322")
    @Test
    @DisplayName("RFQ. User1 wants to sell ABC. User 2 has insufficient tokens for fee")
    fun rfqUser1WantsToSellABCUser2HasInsufficientTokensForFee() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION

        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (_, quoteBefore2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(SELL, baseAsset, quoteAsset, amount, "1", user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, quoteBefore2 + BigDecimal.ONE, "1", user2)
            assert {
                elementWithTextPresented("Insufficient balance")
            }
        }
    }

    //    @Disabled("ATMCH-4285")
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    )
    @TmsLink("ATMCH-1271")
    @Test
    @DisplayName("RFQ. User wants to buy Token")
    fun rfqUserWantsToBuyToken() {
        val amount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val dealAmount = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val firstWallet = user1.otfWallet
        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, baseAsset
            )
        }

        val (baseBefore2, quoteBefore2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()

        val (baseBefore1, quoteBefore1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseAsset, quoteAsset, amount, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()
        val feeDeal = with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()
        val fee = with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)
        }

        val (baseAfter1, quoteAfter1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        assertThat(
            baseBefore1 + (amount - fee),
            closeTo(baseAfter1, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore1 - dealAmount,
            closeTo(quoteAfter1, BigDecimal("0.1"))
        )
        openPage<AtmWalletPage>(driver).logout()

        val (baseAfter2, quoteAfter2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        assertThat(
            baseBefore2 - (amount + feeDeal),//tak
            closeTo(baseAfter2, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore2 + dealAmount,
            closeTo(quoteAfter2, BigDecimal("0.1"))
        )

    }

    //    @Disabled("ATMCH-4285")
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1320")
    @Test
    @DisplayName("RFQ. User wants to sell Token")
    fun rfqUserWantsToSellToken() {
        val amount = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val dealAmount = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        val firstWallet = user1.otfWallet
        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, baseAsset
            )
        }

        val (baseBefore2, quoteBefore2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()

        val (baseBefore1, quoteBefore1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(SELL, baseAsset, quoteAsset, amount, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()
        val feeDeal = with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()
        val fee = with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)
        }

        val (baseAfter1, quoteAfter1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        assertThat(
            baseBefore1 - (amount + fee),
            closeTo(baseAfter1, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore1 + dealAmount,
            closeTo(quoteAfter1, BigDecimal("0.1"))
        )
        openPage<AtmWalletPage>(driver).logout()

        val (baseAfter2, quoteAfter2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        assertThat(
            baseBefore2 + (amount - feeDeal),
            closeTo(baseAfter2, BigDecimal("0.1"))
        )
        assertThat(
            quoteBefore2 - dealAmount,
            closeTo(quoteAfter2, BigDecimal("0.1"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1323")
    @Test
    @DisplayName("RFQ. User1 wants to sell ABC. User 1 has insufficient tokens for placing request")
    fun rfqUserWantsToSellTokenUserHasInsufficientTokensForPlacingRequest() {

        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        val firstWallet = user1.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (baseBefore1, _) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(SELL, baseAsset, quoteAsset, baseBefore1 + BigDecimal.ONE, "1", user1)
            assert {
                elementWithTextPresented("Insufficient balance")
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1315")
    @Test
    @DisplayName("RFQ. User1 wants to buy ABS. User 2 has insufficient tokens for offer")
    fun rfqUser1WantsToBuyABSUser2HasInsufficientTokensForOffer() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (baseBefore2, _) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseAsset, quoteAsset, baseBefore2 + BigDecimal.ONE, "1", user1)
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(baseBefore2 + BigDecimal.ONE, amount, "1", user2)
            assert {
                elementWithTextPresented(" INSUFFICIENT BALANCE ")
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    )
    @TmsLink("ATMCH-1316")
    @Test
    @DisplayName("RFQ. User1 wants to buy ABS. User 2 has insufficient tokens for fee")
    fun rfqUser1WantsToBuyABSUser2HasInsufficientTokensForFee() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (baseBefore2, _) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }
        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseAsset, quoteAsset, baseBefore2 + amount, "1", user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(baseBefore2 + amount, amount, "1", user2)
            assert {
                elementWithTextPresented(" INSUFFICIENT BALANCE ")
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_OTF_WALLET),
        ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF)
    )
    @TmsLink("ATMCH-1317")
    @Test
    @DisplayName("RFQ. User1 wants to buy ABC. User 1 has insufficient tokens for accepting offer")
    fun rfqUser1WantsToBuyABCUser1HasInsufficientTokensForAcceptingOffer() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF

        val firstWallet = user1.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (_, quoteBefore1) = with(openPage<AtmWalletPage>(driver) { submit(user1) }) {
            val base = getBalance(baseAsset, firstWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, firstWallet.name)
            base to quote
        }

        val dealAmount = quoteBefore1 + BigDecimal.ONE

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(BUY, baseAsset, quoteAsset, amount, "1", user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
        }
        openPage<AtmWalletPage>(driver).logout()
        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            acceptOffer(amount, dealAmount, user1)
            assert {
                elementWithTextPresented(" Insufficient balance ")
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-1321")
    @Test
    @DisplayName("RFQ. User1 wants to sell ABC. User 2 has insufficient tokens for offer")
    fun rfqUser1WantsToSellABCUser2HasInsufficientTokensForOffer() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val baseAsset = CC
        val quoteAsset = VT
        val user1 = Users.ATM_USER_2FA_OTF_OPERATION
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        val secondWallet = user2.otfWallet

        prerequisite {
            prerequisitesRfq(
                baseAsset, quoteAsset
            )
        }

        val (_, quoteBefore2) = with(openPage<AtmWalletPage>(driver) { submit(user2) }) {
            val base = getBalance(baseAsset, secondWallet.name)
            openPage<AtmWalletPage>(driver)
            val quote = getBalance(quoteAsset, secondWallet.name)
            base to quote
        }

        openPage<AtmWalletPage>(driver).logout()
        val dealAmount = quoteBefore2 + BigDecimal.ONE

        with(openPage<AtmRFQPage>(driver) { submit(user1) }) {
            createRFQ(SELL, baseAsset, quoteAsset, amount, "1", user1)
        }

        openPage<AtmWalletPage>(driver).logout()

        with(openPage<AtmRFQPage>(driver) { submit(user2) }) {
            createDeal(amount, dealAmount, "1", user2)
            assert {
                elementWithTextPresented("Insufficient balance")
            }
        }
        //TODO дописать последних два шага
    }


}
