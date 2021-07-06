package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.OtfAmounts
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import org.openqa.selenium.JavascriptExecutor
import pages.atm.AtmProfilePage
import pages.atm.AtmStreamingPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Streaming")
class SmokeStreamingE2E : BaseTest() {

    private val baseAsset = CoinType.CC
    private val quoteAsset = CoinType.VT
    private val amountBaseAsset = OtfAmounts.AMOUNT_10.amount

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FIFTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SIXTH)
    )
    @TmsLink("ATMCH-5991")
    @Test
    @DisplayName("Place Streaming offer")
    fun placeStreamingOffer() {

        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val taker = Users.ATM_USER_2FA_OTF_OPERATION_FIFTH
        val maker = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

        step("${maker.email} check Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceSell.toString(),
                    AtmStreamingPage.ExpireType.TEMPORARY, maker
                )
            }
        }

        step("${maker.email} check Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, maker
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        step("${taker.email} confirm Streaming Sell offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                }
                findAndOpenOfferInOverview(unitPriceBuy)
                e {
                    val fee = wait(15L) {
                        until("Couldn't load fee") {
                            offerFee.text.isNotEmpty()
                        }
                        offerFee.amount
                    }
                    click(confirmTradeButton)
                    (this@SmokeStreamingE2E.driver as JavascriptExecutor).executeScript("document.body.style.zoom = '100%';")
                    signAndSubmitMessage(taker, taker.otfWallet.secretKey)
                    fee
                }
            }
        }

        step("${maker.email} confirm Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                }
                findAndOpenOfferInOverview(unitPriceSell)
                e {
                    val fee = wait(15L) {
                        until("Couldn't load fee") {
                            offerFee.text.isNotEmpty()
                        }
                        offerFee.amount
                    }
                    click(confirmTradeButton)
                    (this@SmokeStreamingE2E.driver as JavascriptExecutor).executeScript("document.body.style.zoom = '100%';")
                    signAndSubmitMessage(taker, taker.otfWallet.secretKey)
                    fee
                }
            }
        }

        step("${taker.email} check Streaming Sell offer with $unitPriceSell in history table") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                    click(tradeHistory)
                }
                val offerSell = findOfferInHistory(unitPriceSell)
                MatcherAssert.assertThat(
                    "Offer with amount $unitPriceSell should exists",
                    offerSell,
                    Matchers.notNullValue()
                )
            }
        }

        step("${taker.email} check Streaming Sell offer with $unitPriceSell in history table") {
            with(openPage<AtmStreamingPage>(driver) { submit(taker) }) {
                e {
                    click(overview)
                    click(tradeHistory)
                }
                val offerBuy = findOfferInHistory(unitPriceBuy)
                MatcherAssert.assertThat(
                    "Offer with amount $unitPriceBuy should exists",
                    offerBuy,
                    Matchers.notNullValue()
                )
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FIFTH),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SIXTH)
    )
    @TmsLink("ATMCH-5991")
    @Test
    @DisplayName("Cancel Streaming offer")
    fun cancelStreamingOffer() {

        val unitPriceSell = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")
        val unitPriceBuy = BigDecimal("1.0000${RandomStringUtils.randomNumeric(4)}")

        val maker = Users.ATM_USER_2FA_OTF_OPERATION_SIXTH

        step("${maker.email} check Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.SELL,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceSell.toString(),
                    AtmStreamingPage.ExpireType.TEMPORARY, maker
                )
            }
        }

        step("${maker.email} create Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                createStreaming(
                    AtmStreamingPage.OperationType.BUY,
                    "${baseAsset.tokenSymbol}/${quoteAsset.tokenSymbol}",
                    "$amountBaseAsset ${baseAsset.tokenSymbol}",
                    unitPriceBuy.toString(),
                    AtmStreamingPage.ExpireType.GOOD_TILL_CANCELLED, maker
                )
            }
        }

        step("${maker.email} cancel Streaming Buy offer with $unitPriceBuy") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                e {
                    click(overview)
                }
                cancelOffer(unitPriceBuy, maker)
            }
        }

        step("${maker.email} cancel Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {
                e {
                    click(overview)
                }
                cancelOffer(unitPriceSell, maker)
            }
        }

        step("${maker.email} check Streaming Sell offer with $unitPriceSell") {
            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {

                e {
                    click(overview)
                    click(myOffer)
                    click(showExpiresAndCancel)
                }

                val offerSell = myOffersList.find {
                    it.unitPriceAmount == unitPriceSell
                } ?: error("Can't find offer with unit price '$unitPriceSell'")

                MatcherAssert.assertThat(
                    "Offer with amount $unitPriceSell should exists",
                    offerSell,
                    Matchers.notNullValue()
                )
            }
        }
        step("${maker.email} check Streaming Buy offer with $unitPriceBuy") {

            with(openPage<AtmStreamingPage>(driver) { submit(maker) }) {

                e {
                    click(overview)
                    click(myOffer)
                    click(showExpiresAndCancel)
                }

                val offerBuy = myOffersList.find {
                    it.unitPriceAmount == unitPriceBuy
                } ?: error("Can't find offer with unit price '$unitPriceBuy'")

                MatcherAssert.assertThat(
                    "Offer with amount $unitPriceBuy should exists",
                    offerBuy,
                    Matchers.notNullValue()
                )
            }
        }
    }
}