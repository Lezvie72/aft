package frontend.atm.otс.blocktrade

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
import pages.atm.AtmP2PPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.BLOCKTRADE.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("P2P Blocktrade")
@Story("Blocktrade. Implement additional fields for Industrial tokens")
class ImplementAdditionalFieldsForIndustrialTokens : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val nonIndustrialUser = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05
    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = "22 September 2020"

    @Tag(TagNames.Flow.DEBUG)
    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4392")
    @Test
    @DisplayName("Blocktade.Maturity date - IT as token to send")
    fun selectionIndustrialTokensOnlyByParticipantsHavingIndustrialMarkInAdminPanel() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val checkedValue = "Maturity date"
        var fee = BigDecimal("0.0000")

        val (walletID, companyName) = step("GIVEN $userTwo walletID is known") {
            val companyName = utils.helpers.openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            val walletID = utils.helpers.openPage<AtmWalletPage>(driver).takeWalletID()
            walletID to companyName
        }

        utils.helpers.openPage<AtmProfilePage>(driver).logout()
        step("Step 1-3. Maturity date field doesn't appear and appear") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userOne) }) {
                e {
                    softAssert { elementContainingTextPresented("MY BLOCKTRADES") }

                    click(createBlockTrade)
                    click(goodTillCancelled)

                    if (check { isElementContainsText(assetToReceive, baseAsset.tokenSymbol) }) {
                        select(assetToSend, quoteAsset.tokenSymbol)
                    } else {
                        select(assetToSend, baseAsset.tokenSymbol)
                        select(assetToReceive, quoteAsset.tokenSymbol)
                    }

                    assert {
                        elementWithTextNotPresented(checkedValue)
                    }

                    step("Step 3. Select any Industrial token as Token to send") {
                        if (!check { isElementPresented(offerMaturityDate) }) select(
                            assetToSend,
                            industrialToken.tokenSymbol
                        )

                        assert {
                            elementPresented(offerMaturityDate)
                        }
                    }
                }
            }
        }


        step("Step 4-5. Create IT offer") {
            with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                e {
                    fee =
                        createP2P(
                            walletID,
                            companyName,
                            industrialToken,
                            amount.toString(),
                            baseAsset,
                            amount.toString(),
                            AtmP2PPage.ExpireType.GOOD_TILL_CANCELLED,
                            userOne, maturityDateInnerDate
                        )
                    alert { checkErrorAlert(5L) }
                    signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)

                    assert {
                        elementShouldBeDisappeared(createDeal, 10L)
                    }

                    assertThat(
                        "Offer $amount for user ${userTwo.email} should be exist",
                        isOfferExist(amount, outgoingOffers)
                    )
                }
            }
            utils.helpers.openPage<AtmProfilePage>(driver).logout()
        }

        step("Step 6-7. Check Maturity date field") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userTwo) }) {
                {
                    e {
                        setFilterToday()
                        assert {
                            elementContainingTextPresented(checkedValue)
                        }

                        findIncomingP2P(amount)

                        assert {
                            elementPresented(offerMaturityDate)
                        }
                    }
                }
            }
        }

        step("Step 8. Accept offer") {
            with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                acceptP2P(userTwo, amount)

                assertThat(
                    "Offer $amount for user ${userTwo.email} should be not exist",
                    !isOfferExist(amount, incomingOffers)
                )
            }
        }

        step("Step 10-11. Go to Trade history $userTwo section and check the card of accepted offer") {
            with(utils.helpers.openPage<AtmP2PPage>(driver)) {
                e {
                    click(viewHistoryP2P)
                    setFilterToday()
                    assert {
                        elementContainingTextPresented(checkedValue)
                    }

                    val row = tradeHistory.find {
                        it.receivedAmount == amount
                    } ?: error("Can't find offer with unit price '$amount'")
                    click(row)

                    assert {
                        elementContainingTextPresented(checkedValue)
                    }
                }
            }
            utils.helpers.openPage<AtmProfilePage>(driver).logout()
        }

        step("Step 12-13. Go to Trade history $userOne section and check the card of accepted offer") {
            with(utils.helpers.openPage<AtmP2PPage>(driver) { submit(userOne) }) {
                e {
                    click(viewHistoryP2P)
                    assert {
                        elementContainingTextPresented(checkedValue)
                    }

                    val row = tradeHistory.find {
                        it.amountToSendHistory == amount
                    } ?: error("Can't find offer with unit price '$amount'")
                    click(row)

                    assert {
                        elementContainingTextPresented(checkedValue)
                    }
                }
            }
        }
    }
}
