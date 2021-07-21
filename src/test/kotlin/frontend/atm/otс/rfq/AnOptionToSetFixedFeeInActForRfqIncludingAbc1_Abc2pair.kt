package frontend.atm.otс.rfq

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.By
import pages.atm.AtmAdminRfqSettingsPage
import pages.atm.AtmProfilePage
import pages.atm.AtmRFQPage
import pages.atm.AtmWalletPage
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.math.BigDecimal

@Tags(Tag(TagNames.Flow.OTC), Tag(TagNames.Epic.RFQ.NUMBER))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("RFQ")
@Story("RFQ. An option to set fixed fee in ACT for RFQ including ABC1/ABC2 pair")
@TmsLink("ATMCH-2497")
class AnOptionToSetFixedFeeInActForRfqIncludingAbc1Abc2pair : BaseTest() {
    private val userOne = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02
    private val userTwo = Users.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04
    private val firstToken = CoinType.CC
    private val secondToken = CoinType.VT
    private val thirdToken = CoinType.IT

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-3969")
    @Test
    @DisplayName("Make an offer for an incoming request (sell). An option to set fixed fee.")
    fun makeAnOfferForAnIncomingRequestSellAnOptionToSetFixedFee() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val totalOfferAmountCount = BigDecimal("10.0000")

        step("Precondition - setup FEE for ${thirdToken.tokenSymbol}") {
            with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                chooseToken(firstToken.tokenSymbol)
                e {
                    val row = rfqSettingsTable.find {
                        it["Token"]?.text == thirdToken.tokenNameInAdminPanel
                    }?.get("Token")?.to<Button>("token name: $token")
                        ?: error("Row with Ticker symbol ${thirdToken.tokenNameInAdminPanel} not found in table")
                    click(row)

                    click(edit)

                    feeAcceptingAsset.delete()
                    if (check { isElementPresented(feeAcceptClearButton, 5L) }) click(feeAcceptClearButton)
                    feeAcceptingAssetSelect.sendAndSelect(
                        secondToken.tokenSymbol,
                        secondToken.tokenSymbol,
                        page
                    )

                    feePlacingAsset.delete()
                    if (check { isElementPresented(feePlaceClearButton, 5L) }) click(feePlaceClearButton)
                    feePlacingAssetSelect.sendAndSelect(
                        secondToken.tokenSymbol,
                        secondToken.tokenSymbol,
                        page
                    )

                    feeAcceptingAmount.delete()
                    sendKeys(feeAcceptingAmount, "1")
                    feePlacingAmount.delete()
                    sendKeys(feePlacingAmount, "1")

                    select(feeAcceptingMode, "FIXED")
                    select(feePlacingMode, "FIXED")

                    click(confirmDialog)

                }
            }
        }

        step("Precondition - create offer") {
            val companyName = step("Get company name for user ${userTwo.email}") {
                openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            }
            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.SELL,
                        thirdToken,
                        firstToken,
                        amount,
                        "1",
                        userOne,
                        userTwo,
                        companyName,
                        maturityDate = thirdToken.date
                    )

                    assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                assert {
                    elementPresented(createRequest)
                    elementPresented(viewRequest)
                    elementPresented(viewHistory)
                }

                click(viewRequest)
                incomingOffers.find {
                    it.baseAmount == amount
                }?.let { it.open() } ?: error("Can't find offer with base amount '$amount'")

                assertThat(
                    "Make offer should be disabled before send amount",
                    makeOffer.findElement(By.xpath(".//ancestor::button")).getAttribute("disabled") == "true"
                )

                sendKeys(totalOfferAmount, totalOfferAmountCount.toDouble().toString())
                click(limitedTimeOffer)
                sendKeys(expiryDealTime, "1")
                select(limitedTimeOfferSelector, "Hour(s)")

                step("Check make offer state - should be enabled") {
                    click(makeOffer)
                    assert {
                        elementPresented(manualSignatureLabel)
                    }
                    click(atmOtpCancel)
                }

                wait {
                    until("AMOUNT TO SEND FIRST should be amount ${totalOfferAmountCount.toDouble()} to send") {
                        check {
                            amountToSendInForm.amount.toDouble() == totalOfferAmountCount.toDouble()
                        }
                    }
                }

                assertThat(
                    "AMOUNT TO SEND SECOND should be amount transaction fee ${offerFee.amount.toDouble()}",
                    amountToSendSecondValueInForm.amount.toDouble(),
                    Matchers.equalTo(offerFee.amount.toDouble())
                )

                assertThat(
                    "AMOUNT TO RECEIVE should be amount ${amountRequested.amount.toDouble()}",
                    amountToReceiveInForm.amount.toDouble(),
                    Matchers.equalTo(amountRequested.amount.toDouble())
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4372")
    @Test
    @DisplayName("Make an offer for an incoming request (buy). An option to set fixed fee.")
    fun makeAnOfferForAnIncomingRequestBuyAnOptionToSetFixedFee() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val totalOfferAmountCount = BigDecimal("10.0000")

        step("Precondition - setup FEE for ${thirdToken.tokenSymbol}") {
            with(openPage<AtmAdminRfqSettingsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                chooseToken(firstToken.tokenSymbol)
                e {
                    val row = rfqSettingsTable.find {
                        it["Token"]?.text == thirdToken.tokenNameInAdminPanel
                    }?.get("Token")?.to<Button>("token name: $token")
                        ?: error("Row with Ticker symbol ${thirdToken.tokenNameInAdminPanel} not found in table")
                    click(row)

                    click(edit)

                    feeAcceptingAsset.delete()
                    if (check { isElementPresented(feeAcceptClearButton, 5L) }) click(feeAcceptClearButton)
                    feeAcceptingAssetSelect.sendAndSelect(
                        secondToken.tokenSymbol,
                        secondToken.tokenSymbol,
                        page
                    )

                    feePlacingAsset.delete()
                    if (check { isElementPresented(feePlaceClearButton, 5L) }) click(feePlaceClearButton)
                    feePlacingAssetSelect.sendAndSelect(
                        secondToken.tokenSymbol,
                        secondToken.tokenSymbol,
                        page
                    )

                    feeAcceptingAmount.delete()
                    sendKeys(feeAcceptingAmount, "1")
                    feePlacingAmount.delete()
                    sendKeys(feePlacingAmount, "1")

                    select(feeAcceptingMode, "FIXED")
                    select(feePlacingMode, "FIXED")

                    click(confirmDialog)

                }
            }
        }

        step("Precondition - create offer") {
            val companyName = step("Get company name for user ${userTwo.email}") {
                openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            }
            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        thirdToken,
                        firstToken,
                        amount,
                        "1",
                        userOne,
                        userTwo,
                        companyName,
                        maturityDate = thirdToken.date
                    )

                    assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                assert {
                    elementPresented(createRequest)
                    elementPresented(viewRequest)
                    elementPresented(viewHistory)
                }

                click(viewRequest)
                incomingOffers.find {
                    it.baseAmount == amount
                }?.let { it.open() } ?: error("Can't find offer with base amount '$amount'")

                assertThat(
                    "Make offer should be disabled before send amount",
                    makeOffer.findElement(By.xpath(".//ancestor::button")).getAttribute("disabled") == "true"
                )

                sendKeys(totalOfferAmount, totalOfferAmountCount.toDouble().toString())
                click(limitedTimeOffer)
                sendKeys(expiryDealTime, "1")
                select(limitedTimeOfferSelector, "Hour(s)")

                step("Check make offer state - should be enabled") {
                    click(makeOffer)
                    assert {
                        elementPresented(manualSignatureLabel)
                    }
                    click(atmOtpCancel)
                }

                wait {
                    until("AMOUNT TO SEND FIRST should be amount ${amountRequested.amount.toDouble()}") {
                        check {
                            amountToSendInForm.amount.toDouble() == amountRequested.amount.toDouble()
                        }
                    }
                }

                assertThat(
                    "AMOUNT TO SEND SECOND should be amount transaction fee ${offerFee.amount.toDouble()}",
                    amountToSendSecondValueInForm.amount.toDouble(),
                    Matchers.equalTo(offerFee.amount.toDouble())
                )

                assertThat(
                    "AMOUNT TO RECEIVE should be amount ${amountRequested.amount.toDouble()}",
                    amountToReceiveInForm.amount.toDouble(),
                    Matchers.equalTo(totalOfferAmountCount.toDouble())
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02),
        ResourceLock(Constants.ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04)
    )
    @TmsLink("ATMCH-4373")
    @Test
    @DisplayName("Make an offer for an incoming request (buy CC). An option to set fixed fee.")
    fun makeAnOfferForAnIncomingRequestBuyCCAnOptionToSetFixedFee() {
        val amount = BigDecimal("10.0000${RandomStringUtils.randomNumeric(4)}")
        val totalOfferAmountCount = BigDecimal("10.0000")

        prerequisite {
            prerequisitesRfq(firstToken, firstToken)
        }

        step("Precondition - create offer") {
            val companyName = step("Get company name for user ${userTwo.email}") {
                openPage<AtmProfilePage>(driver) { submit(userTwo) }.getCompanyName()
            }
            openPage<AtmWalletPage>(driver).logout()

            with(openPage<AtmRFQPage>(driver) { submit(userOne) }) {
                e {
                    createRFQ(
                        AtmRFQPage.OperationType.BUY,
                        firstToken,
                        secondToken,
                        amount,
                        "1",
                        userOne,
                        userTwo,
                        companyName
                    )

                    assertThat(
                        "Offer with ${amount.toDouble()} should be visible after created",
                        isOfferExistOutgoing(amount)
                    )
                }
            }
            openPage<AtmWalletPage>(driver).logout()
        }

        with(openPage<AtmRFQPage>(driver) { submit(userTwo) }) {
            e {
                assert {
                    elementPresented(createRequest)
                    elementPresented(viewRequest)
                    elementPresented(viewHistory)
                }

                click(viewRequest)
                incomingOffers.find {
                    it.baseAmount == amount
                }?.let { it.open() } ?: error("Can't find offer with base amount '$amount'")

                assertThat(
                    "Make offer should be disabled before send amount",
                    makeOffer.findElement(By.xpath(".//ancestor::button")).getAttribute("disabled") == "true"
                )

                sendKeys(totalOfferAmount, totalOfferAmountCount.toDouble().toString())
                click(limitedTimeOffer)
                sendKeys(expiryDealTime, "1")
                select(limitedTimeOfferSelector, "Hour(s)")

                step("Check make offer state - should be enabled") {
                    click(makeOffer)
                    assert {
                        elementPresented(manualSignatureLabel)
                    }
                    click(atmOtpCancel)
                }

                assertThat(
                    "AMOUNT TO SEND should be amount ${amountRequested.amount.toDouble()} + transaction fee ${offerFee.amount.toDouble()}",
                    amountToSendInForm.amount.toDouble(),
                    Matchers.equalTo(amountRequested.amount.toDouble() + offerFee.amount.toDouble())
                )

                assertThat(
                    "AMOUNT TO RECEIVE should be amount ${totalOfferAmountCount.toDouble()}",
                    amountToReceiveInForm.amount.toDouble(),
                    Matchers.equalTo(totalOfferAmountCount.toDouble())
                )
            }
        }
    }
}