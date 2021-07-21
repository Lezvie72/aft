package frontend.e2e


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
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
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

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Blocktrade")
class SmokeBlocktradeE2E : BaseTest() {

    private val baseAsset = CC
    private val quoteAsset = VT

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_FORTH)
    )
    @TmsLink("ATMCH-5996")
    @Test
    @DisplayName("Accept and Cancel Blocktrade Offer")
    fun acceptAndCancelBlocktradeOffer() {

        val amountTemporary = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_FORTH


        val (walletID, companyName) = step("${user2.email} get company name and walletId") {

            val companyName = openPage<AtmProfilePage>(driver) { submit(user2) }.getCompanyName()
            val walletID = openPage<AtmWalletPage>(driver) { submit(user2) }.takeWalletID()

            walletID to companyName
        }

        openPage<AtmWalletPage>(driver).logout()

        step("${user.email} create Blocktrade offer with $amountTemporary") {
            with(openPage<AtmP2PPage>(driver) { submit(user) }) {
                createP2P(
                    companyName,
                    companyName,
                    baseAsset,
                    amountTemporary.toString(),
                    quoteAsset,
                    amountTemporary.toString(),
                    TEMPORARY,
                    user
                )
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("${user2.email} accept Blocktrade offer with $amountTemporary") {
            with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
                acceptP2P(user2, amountTemporary)
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("${user.email} check Blocktrade offer with $amountTemporary in Trade history") {
            with(openPage<AtmP2PPage>(driver) { submit(user) }) {
                val offerInTradeHistory = findBlocktradeOfferInTradeHistory(amountTemporary)

                assertThat(
                    "Offer with amount $amountTemporary should exist",
                    offerInTradeHistory,
                    Matchers.notNullValue()
                )
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_THIRD)
    )
    @TmsLink("ATMCH-5996")
    @Test
    @DisplayName("Cancel Blocktrade Offer")
    fun cancelBlocktradeOffer() {

        val amountGoodTillCancel = BigDecimal("3.0000${RandomStringUtils.randomNumeric(4)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val user2 = Users.ATM_USER_2FA_OTF_OPERATION_THIRD

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
                baseAsset
            )
        }


        val (walletID, companyName) = step("${user.email} get company name and walletId") {

            val companyName = openPage<AtmProfilePage>(driver) { submit(user) }.getCompanyName()
            val walletID = openPage<AtmWalletPage>(driver) { submit(user) }.takeWalletID()

            walletID to companyName
        }

        openPage<AtmWalletPage>(driver).logout()

        step("${user2.email} create Blocktrade offer with $amountGoodTillCancel") {
            with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
                createP2P(
                    walletID,
                    companyName,
                    baseAsset,
                    amountGoodTillCancel.toString(),
                    quoteAsset,
                    amountGoodTillCancel.toString(),
                    GOOD_TILL_CANCELLED,
                    user2
                )
            }
        }

        step("${user2.email} cancel Blocktrade offer with $amountGoodTillCancel") {
            with(openPage<AtmP2PPage>(driver) { submit(user2) }) {
                e {
                    click(viewMyP2P)
                }

                val myOffer = outgoingOffers.find {
                    it.amountToReceive == amountGoodTillCancel
                } ?: error("Can't find offer with amount $amountGoodTillCancel")

                myOffer.cancelOffer()
                e {
                    click(yesButton)
                }
                signAndSubmitMessage(user2, user2.otfWallet.secretKey)

                driver.navigate().refresh()

                val cancelledOffer = outgoingOffers.find {
                    it.amountToReceive == amountGoodTillCancel
                }

                assertThat(
                    "Offer with amount $amountGoodTillCancel should have been be cancelled",
                    cancelledOffer,
                    nullValue()
                )
            }

        }
    }
}