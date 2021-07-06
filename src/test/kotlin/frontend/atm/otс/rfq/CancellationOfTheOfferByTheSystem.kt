package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. Cancellation of the offer by the system")
@TmsLink("ATMCH-325")
class CancellationOfTheOfferByTheSystem : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val userThree = Users.ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET
    private val baseAsset = CoinType.CC
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = industrialToken.date


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4809")
    @Test
    @DisplayName("RFQ. Cancellation of an offer because request completed by another trade")
    fun cancellationOfAnOfferBecauseRequestCompletedByAnotherTrade() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountDealFirst = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountDealSecond = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        var initBalance = ""
        var afterBalance: String

        step("Precondition - create offer") {
            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        baseAsset,
                        industrialToken,
                        amount,
                        "1",
                        userOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Init balance for ${userTwo.email}") {
            with(openPage<AtmWalletPage>(driver) { submit(userTwo) }) {
                e {
                    Assertions.assertTrue(
                        isWalletWithLabelPresented(userTwo.otfWallet.name),
                        "Wallet with label $userTwo.otfWallet.name not found"
                    )
                    initBalance = getBalanceFromWalletForToken(baseAsset, userTwo.otfWallet.name)
                }
            }
        }

        step("Create deal from ${userTwo.email}") {
            with(openPage<AtmRFQPage>(driver)) {
                createDeal(amount, amountDealFirst, "1", userTwo)
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Create deal from ${userThree.email}") {
            with(openPage<AtmRFQPage>(driver) { submit(userThree) }) {
                createDeal(amount, amountDealSecond, "1", userThree)
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Accept deal from ${userThree.email}") {
            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                acceptOffer(amount, amountDealSecond, userOne, true)
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        step("Check state deal from ${userTwo.email}") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
                e {
                    click(viewRequest)
                    assertThat(
                        "Offer and deal should not be exist",
                        !isOfferExistIncoming(amount)
                    )
                }
            }
        }

        step("After balance for ${userTwo.email}") {
            with(openPage<AtmWalletPage>(driver)) {
                e {
                    Assertions.assertTrue(
                        isWalletWithLabelPresented(userTwo.otfWallet.name),
                        "Wallet with label $userTwo.otfWallet.name not found"
                    )
                    afterBalance = getBalanceFromWalletForToken(baseAsset, userTwo.otfWallet.name)
                    assertThat(
                        "Balance user ${userTwo.email} for ${baseAsset.tokenSymbol} init:$initBalance should be same as after:$afterBalance after offer with amount $amount was cancel after accept other",
                        initBalance.contains(afterBalance)
                    )
                }
            }
        }
    }
}