package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmIssuancesPage.StatusType.DECLINE
import pages.atm.AtmMarketplacePage
import pages.atm.AtmOrdersPage
import pages.atm.AtmProfilePage
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal


@Epic("Frontend")
@Feature("Wallets")
@Story("Display user orders for industrial tokens in orders section")
class DisplayUserOrdersForIndustrialTokensInOrdersSection : BaseTest() {

    @ResourceLock(Constants.ROLE_USER_OTF_FOR_OTF)
    @TmsLink("ATMCH-3291")
    @Test
    @DisplayName("Industrial tokens. Receive order in orders section in status Submitted")
    fun industrialTokensReceiveOrderInOrdersSectionInStatusSubmitted() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val mainWallet = user.mainWallet

        step("User buy IT token") {
            prerequisite { addCurrencyCoinToWallet(user, "10", mainWallet) }
            openPage<AtmMarketplacePage>(driver) { submit(user) }.buyTokenNew(IT, amount.toString(), user, mainWallet)
        }
        step("User go to Orders page and  check for offer") {
            openPage<AtmOrdersPage>(driver) { submit(user) }.findOrderAndCheckStatus(
                mainWallet,
                IT,
                amount,
                "submitted"
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-3268")
    @Test
    @DisplayName("Industrial tokens. Receive order in orders section in status Executed")
    fun industrialTokensReceiveOrderInOrdersSectionInStatusExecuted() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User buy IT token") {
            prerequisite { addITToken(user, user1, "10", mainWallet, wallet, amount) }
            AtmProfilePage(driver).logout()
        }
        step("User go to Orders page and  check for offer") {
            openPage<AtmOrdersPage>(driver) { submit(user) }.findOrderAndCheckStatus(
                mainWallet,
                IT,
                amount,
                "executed"
            )
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-3293")
    @Test
    @DisplayName("Industrial tokens. Recieve order in orders section in status DECLINED")
    fun industrialTokensReceiveOrderInOrdersSectionInStatusDECLINED() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.mainWallet

        step("User buy IT token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", mainWallet)
                placeAndProceedTokenRequest(
                    IT, mainWallet, wallet, amount,
                    DECLINE, user, user1
                )
            }
            AtmProfilePage(driver).logout()
        }
        step("User go to Orders page and  check for offer") {
            openPage<AtmOrdersPage>(driver) { submit(user) }.findOrderAndCheckStatus(
                mainWallet,
                IT,
                amount,
                "declined"
            )
        }
    }

}