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
@Story("RFQ. View incoming requests for Industrial Token")
@TmsLink("ATMCH-3200")
class ViewIncomingRequestsForIndustrialToken : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val baseAsset = CoinType.CC
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = industrialToken.date


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3644")
    @Test
    @DisplayName("RFQ - IT. View incoming request - IT as quote asset")
    fun viewIncomingRequestItAsQuoteAsset() {
        val amountBuy = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountSell = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create buy and sell offers") {
            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        baseAsset,
                        industrialToken,
                        amountBuy,
                        "1",
                        userOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountBuy.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountBuy)
                    )
                }
            }

            with(openPage<AtmRFQPage>(driver)) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.SELL,
                        baseAsset,
                        industrialToken,
                        amountSell,
                        "1",
                        userOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountBuy.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountBuy)
                    )
                }
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        step("Check offer with type Buy") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
                e {
                    click(viewRequest)
                }
                with(incomingOffers.find {
                    it.baseAmount == amountBuy
                } ?: error("Can't find offer with base amount '$amountBuy'")) {
                    assert {
                        elementPresented(labelBuy)
                        elementPresented(expirationDate)
                        elementPresented(baseLocator)
                        elementPresented(quoteLocator)
                        elementPresented(maturityDate)
                        elementPresented(counterpartyValue)
                    }
                    open()

                    assert {
                        elementPresented(maturityDate)
                    }
                }
            }

        }

        step("Check offer with type Sell")
        {
            with(openPage<AtmRFQPage>(driver)) {
                e {
                    click(viewRequest)
                }
                with(incomingOffers.find {
                    it.baseAmount == amountSell
                } ?: error("Can't find offer with base amount '$amountBuy'")) {
                    assert {
                        elementPresented(labelSell)
                        elementPresented(expirationDate)
                        elementPresented(baseLocator)
                        elementPresented(quoteLocator)
                        elementPresented(maturityDate)
                        elementPresented(counterpartyValue)
                    }
                    open()

                    assert {
                        elementPresented(maturityDate)
                    }
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3635")
    @Test
    @DisplayName("RFQ - IT. View incoming request - IT as base asset")
    fun viewIncomingRequestItAsBaseAsset() {
        val amountBuy = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val amountSell = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        step("Precondition - create buy and sell offers") {
            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        industrialToken,
                        baseAsset,
                        amountBuy,
                        "1",
                        userOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountBuy.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountBuy)
                    )
                }
            }

            with(openPage<AtmRFQPage>(driver)) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.SELL,
                        industrialToken,
                        baseAsset,
                        amountSell,
                        "1",
                        userOne,
                        maturityDate = maturityDateInnerDate
                    )

                    assertThat(
                        "Offer with ${amountBuy.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amountBuy)
                    )
                }
            }

            openPage<AtmWalletPage>(driver).logout()
        }

        step("Check offer with type Buy") {
            with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
                e {
                    click(viewRequest)
                }
                val offer = incomingOffers.find {
                    it.baseAmount == amountBuy
                } ?: error("Can't find offer with base amount '$amountBuy'")
                assert {
                    elementPresented(offer.labelBuy)
                    elementPresented(offer.expirationDate)
                    elementPresented(offer.baseLocator)
                    elementPresented(offer.quoteLocator)
                    elementPresented(offer.maturityDate)
                    elementPresented(offer.counterpartyValue)
                }
                offer.open()

                assert {
                    elementPresented(maturityDate)
                }
            }
        }

        step("Check offer with type Sell") {
            with(openPage<AtmRFQPage>(driver)) {
                e {
                    click(viewRequest)
                }
                val offer = incomingOffers.find {
                    it.baseAmount == amountSell
                } ?: error("Can't find offer with base amount '$amountBuy'")
                assert {
                    elementPresented(offer.labelSell)
                    elementPresented(offer.expirationDate)
                    elementPresented(offer.baseLocator)
                    elementPresented(offer.quoteLocator)
                    elementPresented(offer.maturityDate)
                    elementPresented(offer.counterpartyValue)
                }
                offer.open()

                assert {
                    elementPresented(maturityDate)
                }
            }
        }
    }
}