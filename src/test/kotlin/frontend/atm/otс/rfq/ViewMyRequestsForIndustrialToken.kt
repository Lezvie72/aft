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
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. View my requests for Industrial token")
@TmsLink("ATMCH-3198")
class ViewMyRequestsForIndustrialToken : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val baseAsset = CoinType.CC
    private val industrialToken = CoinType.IT
    private val maturityDateInnerDate = industrialToken.date


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3609")
    @Test
    @DisplayName("RFQ - IT. View my Sell request - IT as base asset")
    fun viewMySellRequestItAsBaseAsset() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            e {
                createRFQ(
                    AtmRFQPage.OperationType.SELL,
                    industrialToken,
                    baseAsset,
                    amount,
                    "1",
                    userOne,
                    maturityDate = maturityDateInnerDate,
                    manualCompleted = true
                )

                assert {
                    elementPresented(availableBalance)
                    elementPresented(offerMaturityDate)
                    elementPresented(expiresIn)
                }

                click(createRequestFromForm)

                assert {
                    elementPresented(manualSignatureLabel)
                }

                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                alert { checkErrorAlert() }

                assertThat(
                    "Offer with ${amount.toDouble()} should be visible after created",
                    isOfferExistOutgoing(amount)
                )

                with(outgoingOffers.find { it.baseAmount == amount }
                    ?: error("Can't find offer with base amount '$amount'")) {
                    assert {
                        elementPresented(labelSell)
                        elementPresented(expirationDate)
                    }
                    assertThat(
                        "BASE ASSET/AMOUNT should contain $amount",
                        baseAmount == amount
                    )

                    assertThat(
                        "MATURITY DATE should contain ${industrialToken.date}",
                        maturityDate.text.contains(industrialToken.dateShortWrite)
                    )

                    assertThat(
                        "Expiration should contain ${industrialToken.dateShortWrite}",
                        expirationDate.text.contains(
                            DateTimeFormatter
                                .ofPattern("MM.dd.yyyy")
                                .format(
                                    LocalDate
                                        .now()
                                        .plusDays(1)
                                )
                        )
                    )
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3611")
    @Test
    @DisplayName("RFQ - IT. View my Buy request - IT as base asset")
    fun viewMyBuyRequestItAsBaseAsset() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            e {
                createRFQ(
                    AtmRFQPage.OperationType.BUY,
                    industrialToken,
                    baseAsset,
                    amount,
                    "1",
                    userOne,
                    maturityDate = maturityDateInnerDate,
                    manualCompleted = true
                )

                assert {
                    elementPresented(offerMaturityDate)
                    elementPresented(expiresIn)
                }

                click(createRequestFromForm)

                assert {
                    elementPresented(manualSignatureLabel)
                }

                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                alert { checkErrorAlert() }

                assertThat(
                    "Offer with ${amount.toDouble()} should be visible after created",
                    isOfferExistOutgoing(amount)
                )

                with(outgoingOffers.find { it.baseAmount == amount }
                    ?: error("Can't find offer with base amount '$amount'")) {
                    assert {
                        elementPresented(labelBuy)
                        elementPresented(expirationDate)
                    }
                    assertThat(
                        "BASE ASSET/AMOUNT should contain $amount",
                        baseAmount == amount
                    )

                    assertThat(
                        "MATURITY DATE should contain ${industrialToken.date}",
                        maturityDate.text.contains(industrialToken.dateShortWrite)
                    )

                    assertThat(
                        "Expiration should contain ${industrialToken.dateShortWrite}",
                        expirationDate.text.contains(
                            DateTimeFormatter
                                .ofPattern("MM.dd.yyyy")
                                .format(
                                    LocalDate
                                        .now()
                                        .plusDays(1)
                                )
                        )
                    )
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3612")
    @Test
    @DisplayName("RFQ - IT. View my Buy request - IT as quote asset")
    fun viewMyBuyRequestItAsQuoteAsset() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            e {
                createRFQ(
                    AtmRFQPage.OperationType.BUY,
                    baseAsset,
                    industrialToken,
                    amount,
                    "1",
                    userOne,
                    maturityDate = maturityDateInnerDate,
                    manualCompleted = true
                )

                assert {
                    elementPresented(offerMaturityDate)
                    elementPresented(expiresIn)
                }

                click(createRequestFromForm)

                assert {
                    elementPresented(manualSignatureLabel)
                }

                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                alert { checkErrorAlert() }

                assertThat(
                    "Offer with ${amount.toDouble()} should be visible after created",
                    isOfferExistOutgoing(amount)
                )

                with(outgoingOffers.find { it.baseAmount == amount }
                    ?: error("Can't find offer with base amount '$amount'")) {
                    assert {
                        elementPresented(labelBuy)
                        elementPresented(expirationDate)
                    }
                    assertThat(
                        "BASE ASSET/AMOUNT should contain $amount",
                        baseAmount == amount
                    )

                    assertThat(
                        "MATURITY DATE should contain ${industrialToken.date}",
                        maturityDate.text.contains(industrialToken.dateShortWrite)
                    )

                    assertThat(
                        "Expiration should contain ${industrialToken.dateShortWrite}",
                        expirationDate.text.contains(
                            DateTimeFormatter
                                .ofPattern("MM.dd.yyyy")
                                .format(
                                    LocalDate
                                        .now()
                                        .plusDays(1)
                                )
                        )
                    )
                }
            }
        }
    }


    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3615")
    @Test
    @DisplayName("RFQ - IT. View my Sell request - IT as quote asset")
    fun viewMySellRequestItAsQuoteAsset() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")

        with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
            e {
                createRFQ(
                    AtmRFQPage.OperationType.SELL,
                    baseAsset,
                    industrialToken,
                    amount,
                    "1",
                    userOne,
                    maturityDate = maturityDateInnerDate,
                    manualCompleted = true
                )

                assert {
                    elementPresented(availableBalance)
                    elementPresented(offerMaturityDate)
                    elementPresented(expiresIn)
                }

                click(createRequestFromForm)

                assert {
                    elementPresented(manualSignatureLabel)
                }

                alert { checkErrorAlert() }
                signAndSubmitMessage(userOne, userOne.otfWallet.secretKey)
                alert { checkErrorAlert() }

                assertThat(
                    "Offer with ${amount.toDouble()} should be visible after created",
                    isOfferExistOutgoing(amount)
                )

                with(outgoingOffers.find { it.baseAmount == amount }
                    ?: error("Can't find offer with base amount '$amount'")) {
                    assert {
                        elementPresented(labelSell)
                        elementPresented(expirationDate)
                    }
                    assertThat(
                        "BASE ASSET/AMOUNT should contain $amount",
                        baseAmount == amount
                    )

                    assertThat(
                        "MATURITY DATE should contain ${industrialToken.date}",
                        maturityDate.text.contains(industrialToken.dateShortWrite)
                    )

                    assertThat(
                        "Expiration should contain ${industrialToken.dateShortWrite}",
                        expirationDate.text.contains(
                            DateTimeFormatter
                                .ofPattern("MM.dd.yyyy")
                                .format(
                                    LocalDate
                                        .now()
                                        .plusDays(1)
                                )
                        )
                    )
                }
            }
        }
    }
}