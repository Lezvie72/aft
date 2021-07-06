package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
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
@Story("RFQ. Hide industrial tokens, requests and offers with industrial tokens from non-industrial participants.")
class HideIndustrialTokensRequestsAndOffers : BaseTest() {
    private val industrialUserOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val industrialUserTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val token = CoinType.CC
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = industrialToken.date

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05)
    )
    @TmsLink("ATMCH-4351")
    @Test
    @DisplayName("RFQ. Requests with industrial tokens from non-industrial participant.")
    fun requestsWithIndustrialTokensFromNonIndustrialParticipant() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create offer") {

            with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        token,
                        industrialToken,
                        amount,
                        "1",
                        industrialUserOne,
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

        with(openPage<AtmRFQPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                assert {
                    elementContainingTextPresented("CREATE A REQUEST")
                    elementContainingTextPresented("INCOMING REQUESTS")
                    elementContainingTextPresented("TRADE HISTORY")
                }

                click(viewRequest)
                assertThat(
                    "Offer with ${amount.toDouble()} should not be visible for non industrial user ${nonIndustrialUser.email}",
                    !isOfferExistIncoming(amount)
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4350")
    @Test
    @DisplayName("RFQ. Requests with industrial tokens by industrial participant.")
    fun requestsWithIndustrialTokensByIndustrialParticipant() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val dealAmount = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create offer") {

            with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        token,
                        industrialToken,
                        amount,
                        "1",
                        industrialUserOne,
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

        with(openPage<AtmRFQPage>(driver) { submit(industrialUserTwo) }) {
            assert {
                elementContainingTextPresented("CREATE A REQUEST")
                elementContainingTextPresented("INCOMING REQUESTS")
                elementContainingTextPresented("TRADE HISTORY")
            }

            e {
                createDeal(amount, dealAmount, "1", industrialUserTwo)
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
            e {
                click(myRequest)
                findOutgoingRFQ(amount)

                check {
                    assertThat(
                        "Offer with amount ${amount.toDouble()} and deal amount ${dealAmount.toDouble()} should be visible",
                        isElementContainingTextPresented(dealAmount.toDouble().toString().split(".").last())
                    )
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4349")
    @Test
    @DisplayName("RFQ. Selecting industrial token non-industrial participant.")
    fun selectingIndustrialTokenNonIndustrialParticipant() {
        with(openPage<AtmRFQPage>(driver) { submit(nonIndustrialUser) }) {
            e {
                assert {
                    elementContainingTextPresented("CREATE A REQUEST")
                    elementContainingTextPresented("INCOMING REQUESTS")
                    elementContainingTextPresented("TRADE HISTORY")
                }

                click(createRequest)
                click(iWantToBuyAsset)

                assertThat(
                    "Select base asset should not contain ${industrialToken.tokenSymbol}",
                    !assetToSend.getHeadersAsString(page).contains(industrialToken.tokenSymbol)
                )

                assertThat(
                    "Select quote asset should not contain ${industrialToken.tokenSymbol}",
                    !assetToReceive.getHeadersAsString(page).contains(industrialToken.tokenSymbol)
                )

                click(iWantToSellAsset)
                assertThat(
                    "Select base asset should not contain ${industrialToken.tokenSymbol}",
                    !assetToSend.getHeadersAsString(page).contains(industrialToken.tokenSymbol)
                )
                assertThat(
                    "Select quote asset should not contain ${industrialToken.tokenSymbol}",
                    !assetToReceive.getHeadersAsString(page).contains(industrialToken.tokenSymbol)
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4348")
    @Test
    @DisplayName("RFQ. Selecting industrial token by participants having industrial mark.")
    fun selectingIndustrialTokenByParticipantsHavingIndustrialMark() {
        val amountBaseAsset = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountQuoteAsset = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")

        step("Step 1-7. Create asset with industrial token ${industrialToken.tokenSymbol} as base") {
            with(openPage<AtmRFQPage>(driver) { submit(industrialUserOne) }) {
                e {
                    assert {
                        elementContainingTextPresented("CREATE A REQUEST")
                        elementContainingTextPresented("INCOMING REQUESTS")
                        elementContainingTextPresented("TRADE HISTORY")
                    }

                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        industrialToken,
                        token,
                        amountBaseAsset,
                        "1",
                        industrialUserOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountBaseAsset.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountBaseAsset)
                    )
                }
            }
        }

        step("Step 8-12. Create asset with industrial token ${industrialToken.tokenSymbol} as quote") {
            with(openPage<AtmRFQPage>(driver)) {
                e {
                    assert {
                        elementContainingTextPresented("CREATE A REQUEST")
                        elementContainingTextPresented("INCOMING REQUESTS")
                        elementContainingTextPresented("TRADE HISTORY")
                    }

                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        token,
                        industrialToken,
                        amountQuoteAsset,
                        "1",
                        industrialUserOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountQuoteAsset.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountQuoteAsset)
                    )
                }
            }
        }
    }
}