package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmIssuancesPage
import pages.atm.AtmOrdersPage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import pages.atm.AtmWalletPage.RedemptionTypeETC.AUTO
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Epic("Frontend")
@Feature("ETC")
@Story("ETC. Redemption order processing")
class ETCRedemptionOrderProcessing : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_THIRD),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-4465")
    @Test
    @DisplayName("ETC. Redemption order. Cancel work with order")
    fun etcRedemptionOrderCancelWorkWithOrder() {
        val fileName = "ETC_Nomenclature2"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND
        val etcWalletRedemption = etcIssuer.walletList[1]
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_THIRD
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, _, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Auto Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("User Cancel redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                e {
                    chooseToken(ETC)
                    click(redemptionCurrentQueue)
                }
                val myOffer = redemptionOffers.find {
                    it.tokenQuantityRequestToRedeem == amountEtcRedemption
                } ?: error("Can't find offer with unit price '$amountEtcRedemption'")
                myOffer.clickProceedButton()
                e {
                    click(cancelButton)
                }
                assert { urlEndsWith("/issuances/ETT/redemption") }
            }
        }
    }


    @TmsLink("ATMCH-4434")
    @Test
    @DisplayName("ETC. Redemption order. Interface of the request queue. ")
    fun ETCRedemptionOrderInterfaceOfTheRequestQueue() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD

        step("User check redeem request elements") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                e {
                    chooseToken(ETC)
                    click(redemptionCurrentQueue)
                }
                assert {
                    elementContainingTextPresented(ETC.tokenName)
                    elementContainingTextPresented("TOKEN TYPE")
                    elementContainingTextPresented("UNDERLYING ASSET")
                    elementContainingTextPresented("ISSUER")
                    elementContainingTextPresented("Filter by status")
                    elementContainingTextPresented("SUBMITTED")
                    elementContainingTextPresented("REDEMPTION")
                    elementContainingTextPresented("Token quantity requested to redeem")
                    elementContainingTextPresented("Submitted")
                    elementContainingTextPresented("Request ID")
                    elementContainingTextPresented("Requestor")
                    elementPresented(proceed)
                }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_ONE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-4408")
    @Test
    @DisplayName("ETC. Displaying ETC redemption in ORDER section")
    fun etcDisplayingEtcRedemptionInOrderSection() {
        val fileName = "ETC_Nomenclature4"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_ONE
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, _, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Auto Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
            }
        }
        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(etcUser) })
            {
                findOrderOpenCardAndCheckData(
                    wallet,
                    ETC,
                    amountEtcRedemption,
                    "submitted"
                )
                e {
                    click(itemsDetails)
                }
                assert { elementPresented(barList) }
            }
        }
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-4495")
    @Test
    @DisplayName("ETC. Redemption order. Approval. User has 2FA")
    fun etcRedemptionOrderApprovalUserHas2FA() {
        val fileName = "ETC_Nomenclature3"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND
        val etcWalletRedemption = etcIssuer.walletList[1]
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_SECOND
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, _, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Auto Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
            }
        }
        openPage<AtmWalletPage>(driver)

        val (balanceWalletAfterCreate,
            heldInOrdersWalletAfterCreate) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }
        openPage<AtmWalletPage>(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("User approve redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                findRedemptionOffersForEtcTokenAndSetStatus(
                    amountEtcRedemption,
                    etcIssuer,
                    etcWalletRedemption,
                    AtmIssuancesPage.StatusType.APPROVE
                )
                openPage<AtmIssuancesPage>(driver)
            }
        }

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("User go to Volume 24H and check status Executed for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
                    ETC,
                    "Executed",
                    amountEtcRedemption
                )
                openPage<AtmIssuancesPage>(driver)
            }
        }

        openPage<AtmProfilePage>(driver).logout()

        step("User go to Orders page and  check for offer") {
            openPage<AtmOrdersPage>(driver) { submit(etcUser) }.findOrderAndCheckStatus(
                wallet,
                ETC,
                amountEtcRedemption,
                "executed"
            )
        }

        val (balanceWalletAfterApprove,
            heldInOrdersWalletAfterApprove) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

        assertThat(
            "Expected balance: $balanceWalletAfterCreate, was: $balanceWalletAfterApprove",
            balanceWalletAfterApprove,
            closeTo(balanceWalletAfterCreate, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletAfterCreate, was: $heldInOrdersWalletAfterApprove",
            heldInOrdersWalletAfterApprove,
            closeTo(heldInOrdersWalletAfterCreate - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_THIRD),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_2FA)
    )
    @TmsLink("ATMCH-4435")
    @Test
    @DisplayName("ETC. Redemption order. Check the interface of order processing")
    fun etcRedemptionOrderCheckTheInterfaceOfOrderProcessing() {
        val fileName = "ETC_Nomenclature11"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_THIRD
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, _, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Auto Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        step("User approve redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                e {
                    chooseToken(ETC)
                    click(redemptionCurrentQueue)
                }
                val myOffer = redemptionOffers.find {
                    it.tokenQuantityRequestToRedeem == amountEtcRedemption
                } ?: error("Can't find offer with unit price '$amountEtcRedemption'")
                myOffer.clickProceedButton()
                assert {
                    elementContainingTextPresented(ETC.tokenName)
                    elementContainingTextPresented("TOKEN TYPE")
                    elementContainingTextPresented("UNDERLYING ASSET")
                    elementContainingTextPresented("ISSUER")
                    elementContainingTextPresented("ITEMS")
                    elementContainingTextPresented("SUBMITTED")
                    elementContainingTextPresented("REDEMPTION")
                    elementContainingTextPresented("Token quantity requested to redeem")
                    elementContainingTextPresented("Submitted")
                    elementContainingTextPresented("Request ID")
                    elementContainingTextPresented("Requestor")
                    elementContainingTextPresented(" TOTAL BARS ")
                    elementContainingTextPresented(" Total tokenized weight ")
                    elementContainingTextPresented(" Total fine weight ")
                    elementContainingTextPresented(" Total gross weight ")

                    elementPresented(approve)
                    elementPresented(decline)
                    elementPresented(cancelButton)
                    elementPresented(showChat)
                }
            }
        }
    }

}

