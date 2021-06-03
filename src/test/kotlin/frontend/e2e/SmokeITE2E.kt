package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.ResourceAccessMode.READ
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.*
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.LimitType.MIN
import pages.atm.AtmIssuancesPage.OperationType.RECEIVE
import pages.atm.AtmIssuancesPage.OperationType.REDEMPTION
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmIssuancesPage.StatusType.DECLINE
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tag("SmokeE2E")
@Epic("Frontend")
@Feature("E2E")
@Story("IT")
class SmokeITE2E : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Settings the limits for RECEIVE operations: steps 1-4")
    fun settingsTheLimitsForReceiveOperations() {
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA

        val minBefore = BigDecimal("3.01234567")
        val maxBefore = BigDecimal("1000.01234567")

        step("Issuer go to Issuance -> Recieve Limits -> Change Limits") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, minBefore.toString(), itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MAX, maxBefore.toString(), itIssuer, itWallet
                )

                openPage<AtmIssuancesPage>(driver)
                e {
                    chooseToken(IT)
                    click(receiveItLimits)
                }
                val minAfter = minLimitReceiveValue.amount
                val maxAfter = maxLimitReceiveValue.amount

                assertThat(
                    "Expected receive value: $maxBefore, was: $maxAfter",
                    maxAfter,
                    closeTo(maxBefore, BigDecimal("0.01"))
                )

                assertThat(
                    "Expected receive value: $minBefore, was: $minAfter",
                    minAfter,
                    closeTo(minBefore, BigDecimal("0.01"))
                )
            }

        }

        openPage<AtmWalletPage>(driver).logout()

        step("User go to Marketplace and check limits for operation") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                chooseToken(IT)
                e {
                    click(newOrderButton)
                }
                val min = amountLimitIt.minLimitVal.toBigDecimal()
                val max = amountLimitIt.maxLimitVal.toBigDecimal()

                assertThat(
                    "Expected value: $min, was: $minBefore",
                    min,
                    closeTo(minBefore, BigDecimal("0.01"))
                )

                assertThat(
                    "Expected value: $max, was: $maxBefore",
                    maxBefore,
                    closeTo(max, BigDecimal("0.01"))
                )
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("Issuer change receive limit back") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MAX, "100000000.00000000", itIssuer, itWallet
                )
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_ONE, mode = READ),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_ONE, mode = READ)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Submitting RECEIVE requests: 4-6 and Approving RECEIVE requests: steps 9 (except chat), 11-15")
    fun submittingReceiveRequestsAndApprovingReceiveRequests() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_MAIN_FOR_IT_ONE
        val userWallet = user.mainWallet

        step("User buy CC token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", userWallet)
            }
        }

        step("User buy IT token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                buyTokenNew(IT, amount.toString(), user, userWallet)
            }
        }

        val balanceWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, userWallet.name)
        }

        AtmWalletPage(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("User go to Issuance, and get Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }
        step("User change receive limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, "0.00000001", itIssuer, itWallet
                )
            }
        }
        step("Issuer go to Issuance and Approve offer") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeStatusForOfferWithAmount(IT, amount, APPROVE, itIssuer, itWallet)
            }
        }

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("User go to Issuance, and get Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }

        step("User go to Volume 24H and check status Executed for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findOfferForTokenInReceiveVolumeStatisticsAndCheckStatus(
                    IT,
                    "Executed",
                    amount
                )
            }
            AtmWalletPage(driver).logout()
        }

        val balanceWalletAfter = step("User get balance from wallet After operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, userWallet.name)
        }

        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(user) })
            {
                findOrderOpenCardAndCheckData(
                    userWallet,
                    IT,
                    amount,
                    "Executed"
                )
            }
        }
        //TODO transaction list
        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore + amount, BigDecimal("0.01"))
        )

        assertThat(
            "Expected inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore + amount, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore - amount, BigDecimal("0.01"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_SECOND, mode = READ),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND, mode = READ)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Chatting: 7-10, 14-15. Preconditions: any receive request should be submitted (steps 4-5)")
    fun chattingReceiving() {
        val textUser = "Message for User"
        val textIssuer = "Message for Issuer"
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_MAIN_FOR_IT_SECOND
        val userWallet = user.mainWallet

        step("User buy CC token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", userWallet)
            }
        }
        step("User buy IT token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                buyTokenNew(IT, amount.toString(), user, userWallet)
            }
        }

        step("User go to Orders page and check status submitted for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(user) })
            {
                findOrderOpenCardAndCheckData(
                    userWallet,
                    IT,
                    amount,
                    "submitted"
                )
                e {
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
        AtmWalletPage(driver).logout()
        step("User change receive limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, "0.00000001", itIssuer, itWallet
                )
            }
        }
        step("Issuer go to Issuance and check Message from User") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                e {
                    chooseToken(IT)
                    click(requestCurrentQueue)
                }
                val myOffer = requestOffers.find {
                    it.totalRequestedAmount == amount
                } ?: error("Can't find offer with unit price '$amount'")
                myOffer.clickProceedButton()
                e {
                    click(showChat)
//                    click(chatButton)
                    checkTheMessageFromChat(textUser)
                    sendKeys(chatInput, textIssuer)
                    click(chatSendButton)
                    click(approve)
                }
                signAndSubmitMessage(itIssuer, itWallet.secretKey)
            }
        }
        AtmWalletPage(driver).logout()
        step("User go to Orders page, check status Executed for offer and check message from Issuer") {
            with(openPage<AtmOrdersPage>(driver) { submit(user) })
            {
                findOrderOpenCardAndCheckData(
                    userWallet,
                    IT,
                    amount,
                    "Executed"
                )
                checkTheMessageFromChat(textIssuer)
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_THIRD, mode = READ),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_THIRD, mode = READ)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Submitting RECEIVE requests: 4-6 and Declining RECEIVE requests: steps 16-21")
    fun submittingReceiveRequestsAndDecliningReceive() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_THIRD
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_MAIN_FOR_IT_THIRD
        val userWallet = user.mainWallet

        step("User buy CC token") {
            prerequisite {
                addCurrencyCoinToWallet(user, "10", userWallet)
            }
        }
        step("User buy IT token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                buyTokenNew(IT, amount.toString(), user, userWallet)
            }
        }
        val balanceWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, userWallet.name)
        }
        AtmWalletPage(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }
        step("User change receive limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, "0.00000001", itIssuer, itWallet
                )
            }
        }
        step("Issuer go to Issuance and Decline offer") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeStatusForOfferWithAmount(IT, amount, DECLINE, itIssuer, itWallet)
            }
        }
        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }
        step("User go to Volume 24H and check status for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findOfferForTokenInReceiveVolumeStatisticsAndCheckStatus(
                    IT,
                    "Declined",
                    amount
                )
            }
            AtmWalletPage(driver).logout()
        }
        val balanceWalletAfter = step("User get balance from wallet After operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, userWallet.name)
        }
        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(user) })
            {
                findOrderOpenCardAndCheckData(
                    userWallet,
                    IT,
                    amount,
                    "Declined"
                )
            }
        }
        //TODO transaction list

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Setting the limits for the REDEMPTION operations: steps 22-25")
    fun settingTheLimitsForTheRedemptionOperations() {
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val userWallet = user.mainWallet

        val minBefore = BigDecimal("3.01234567")
        val maxBefore = BigDecimal("1000.01234567")

        step("User go to Issuance -> Redemption Limits -> Change limits and checks limits") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                    changeLimitAmount(
                        IT,
                        REDEMPTION,
                        MIN, minBefore.toString(), itIssuer, itWallet
                    )
                    openPage<AtmIssuancesPage>(driver)
                    changeLimitAmount(
                        IT,
                        REDEMPTION,
                        MAX, maxBefore.toString(), itIssuer, itWallet
                    )
                }
                openPage<AtmIssuancesPage>(driver)

                e {
                    chooseToken(IT)
                    click(redemptionItLimits)
                }

                val minAfter = minLimitRedemptionValue.amount
                val maxAfter = maxLimitRedemptionValue.amount
                assertThat(
                    "Expected max LimitRedemptionValue: $maxBefore, was: $maxAfter",
                    maxAfter,
                    closeTo(maxBefore, BigDecimal("0.01"))
                )

                assertThat(
                    "Expected min LimitRedemptionValue: $minBefore, was: $minAfter",
                    minAfter,
                    closeTo(minBefore, BigDecimal("0.01"))
                )
            }

        }

        openPage<AtmWalletPage>(driver).logout()

        step("User go to Wallet -> IT token ->Redeem -> check Limits Value") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(userWallet.name)
                chooseToken(IT)
                e {
                    click(redemption)
                }
                val min = amountRedemptionLimitIt.minLimitVal.toBigDecimal()
                val max = amountRedemptionLimitIt.maxLimitVal.toBigDecimal()

                assertThat(
                    "Expected min RedemptionLimitIt: $min, was: $minBefore",
                    min,
                    closeTo(minBefore, BigDecimal("0.01"))
                )

                assertThat(
                    "Expected max RedemptionLimitIt: $max, was: $maxBefore",
                    maxBefore,
                    closeTo(max, BigDecimal("0.01"))
                )
            }
        }
        openPage<AtmWalletPage>(driver).logout()

        step("User change redemption limit back") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, "1000.00000000", itIssuer, itWallet
                )
            }
        }

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Submitting REDEMPTION requests: steps 25-27 and Fully approving REDEMPTION requests: steps 30 (except chat), 32-36")
    fun submittingRedemptionRequestsAndFullyApprovingRedemptionRequests() {
        val min = BigDecimal("3.01234567")
        val max = BigDecimal("1000.01234567")

        val amount = min + BigDecimal("2")
        val amountForRedemption = min + BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, min.toString(), itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, max.toString(), itIssuer, itWallet
                )
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        step("User buy IT token") {
            prerequisite { addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount) }
            AtmProfilePage(driver).logout()
        }

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }

        AtmProfilePage(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }


        step("Issuer accept redeem and back limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findRedemptionOffers(IT, amountForRedemption, amountForRedemption, itIssuer, itWallet, APPROVE)
                openPage<AtmIssuancesPage>(driver)
            }
        }

        step("Issuer go to Volume 24H and check status Executed for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
                    IT,
                    "Executed",
                    amountForRedemption
                )
            }

        }

        step("Issuer back redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, "1000.00000000", itIssuer, itWallet
                )
            }
        }

        openPage<AtmIssuancesPage>(driver)

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }

        openPage<AtmProfilePage>(driver).logout()

        val (balanceWalletAfter,
            heldInOrdersWalletAfter) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(userBuyer) })
            {
                findOrderOpenCardAndCheckData(
                    mainWallet,
                    IT,
                    amountForRedemption,
                    "Executed"
                )
            }
        }

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore - amountForRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfter",
            heldInOrdersWalletAfter,
            closeTo(heldInOrdersWalletBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore - amountForRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore - amountForRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_THIRD),
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_THIRD)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Submitting REDEMPTION requests: steps 25-27 and Partly approving REDEMPTION requests: steps 37-41")
    fun submittingRedemptionRequestsAndPartlyApprovingRedemptionRequests() {
        val min = BigDecimal("3.01234567")
        val max = BigDecimal("1000.01234567")

        val amount = min + BigDecimal("2.${RandomStringUtils.randomNumeric(8)}")
        val amountForRedemption = min + BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val partlyAmount = min + BigDecimal("0.${RandomStringUtils.randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_MAIN_FOR_IT_THIRD
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_THIRD
        val itWallet = itIssuer.walletList[0]

        step("Issuer change redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, min.toString(), itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, max.toString(), itIssuer, itWallet
                )
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        step("User buy IT token") {
            prerequisite { addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount) }
            AtmProfilePage(driver).logout()
        }

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }

        AtmProfilePage(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }


        step("Issuer accept redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findRedemptionOffers(IT, amountForRedemption, partlyAmount, itIssuer, itWallet, APPROVE)
                openPage<AtmIssuancesPage>(driver)
            }
        }

        step("Issuer go to Volume 24H and check status Executed for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
                    IT,
                    "Executed",
                    amountForRedemption
                )
            }

        }

        step("Issuer back redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, "1000.00000000", itIssuer, itWallet
                )
            }
        }
        openPage<AtmIssuancesPage>(driver)

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }

        openPage<AtmProfilePage>(driver).logout()

        val (balanceWalletAfter,
            heldInOrdersWalletAfter) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(userBuyer) })
            {
                findOrderOpenCardAndCheckData(
                    mainWallet,
                    IT,
                    amountForRedemption,
                    "Executed"
                )
            }
        }

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore - partlyAmount, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfter",
            heldInOrdersWalletAfter,
            closeTo(heldInOrdersWalletBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected  inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore - partlyAmount, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore - partlyAmount, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_IT_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Submitting REDEMPTION requests: steps 25-27 and Declining REDEMPTION requests: steps 42-46")
    fun submittingRedemptionRequestsAndDecliningRedemptionRequests() {
        val min = BigDecimal("3.01234567")
        val max = BigDecimal("1000.01234567")

        val amount = min + BigDecimal("2.${RandomStringUtils.randomNumeric(8)}")
        val amountForRedemption = min + BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
//        val maturityDate = LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale.US)
        val maturityDate = "December 2020"

        val userBuyer = Users.ATM_USER_MAIN_FOR_IT_SECOND
        val mainWallet = userBuyer.mainWallet

        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND
        val itWallet = itIssuer.walletList[0]

        step("Issuer change redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, min.toString(), itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, max.toString(), itIssuer, itWallet
                )
            }
        }
        AtmProfilePage(driver).logout()

        step("User buy IT token") {
            prerequisite { addITToken(userBuyer, itIssuer, "10", mainWallet, itWallet, amount) }
            AtmProfilePage(driver).logout()
        }

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User make redeem") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    amountForRedemption.toString(),
                    maturityDate,
                    userBuyer
                )
            }
        }

        AtmProfilePage(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }


        step("Issuer Decline redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findRedemptionOffers(IT, amountForRedemption, amountForRedemption, itIssuer, itWallet, DECLINE)
                openPage<AtmIssuancesPage>(driver)
            }
        }

        step("Issuer go to Volume 24H and check status Executed for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
                    IT,
                    "Declined",
                    amountForRedemption
                )
            }

        }

        step("Issuer back redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX, "1000.00000000", itIssuer, itWallet
                )
            }
        }

        openPage<AtmIssuancesPage>(driver)

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }.getBalanceSupplyCirculationAndSale(IT)
            }

        openPage<AtmProfilePage>(driver).logout()

        val (balanceWalletAfter,
            heldInOrdersWalletAfter) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(userBuyer) })
            {
                findOrderOpenCardAndCheckData(
                    mainWallet,
                    IT,
                    amountForRedemption,
                    "Declined"
                )
            }
        }

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfter",
            heldInOrdersWalletAfter,
            closeTo(heldInOrdersWalletBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected inCirculation: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected totalSupply: $totalSupplyBefore, was: $totalSupplyAfter",
            totalSupplyAfter,
            closeTo(totalSupplyBefore, BigDecimal("0.01"))
        )

        assertThat(
            "Expected onSale: $onSaleBefore, was: $onSaleAfter",
            onSaleAfter,
            closeTo(onSaleBefore, BigDecimal("0.01"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN, mode = READ),
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND, mode = READ)
    )
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Chatting: 19-20, 22-24, 29-30")
    fun chattingRedemption() {
        val textUser = "Message for User"
        val textIssuer = "Message for Issuer"
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_SECOND
        val userWallet = user.mainWallet
        step("Issuer change redemption limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                changeLimitAmount(
                    IT,
                    RECEIVE,
                    MIN, "0.00000001", itIssuer, itWallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN, "0.00000001", itIssuer, itWallet
                )
            }
        }
        openPage<AtmProfilePage>(driver).logout()
        step("User buy IT token") {
            prerequisite { addITToken(user, itIssuer, "10", userWallet, itWallet, amount) }
            AtmProfilePage(driver).logout()
        }

        step("User make Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                redeemToken(
                    IT,
                    userWallet,
                    amount.toString(),
                    "",
                    user
                )
            }
        }
        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(user) })
            {
                findOrderOpenCardAndCheckData(
                    userWallet,
                    IT,
                    amount,
                    "submitted"
                )
                e {
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
        openPage<AtmWalletPage>(driver).logout()
        step("User go to Orders page and check status for offer") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                e {
                    chooseToken(IT)
                    click(redemptionCurrentQueue)
                }
                val myOffer = redemptionOffers.find {
                    it.requestAmount == amount
                } ?: error("Can't find offer with unit price '$amount'")
                myOffer.clickProceedButton()
                e {
                    click(amountRedemption)
                    deleteData(amountRedemption)
//            sendKeys(amountRedemption, ".")
                    sendKeys(amountRedemption, amount.toString())
                    Thread.sleep(1000)
                    e {
                        click(showChat)
//                        click(chatButton)
                        checkTheMessageFromChat(textUser)
                        sendKeys(chatInput, textIssuer)
                        click(chatSendButton)
                        click(approve)
                    }
                    signAndSubmitMessage(itIssuer, itWallet.secretKey)
                }

            }
            openPage<AtmWalletPage>(driver).logout()
            step("User go to Orders page and check status for offer") {
                with(openPage<AtmOrdersPage>(driver) { submit(user) })
                {
                    findOrderOpenCardAndCheckData(
                        userWallet,
                        IT,
                        amount,
                        "Executed"
                    )
                    checkTheMessageFromChat(textIssuer)
                }
            }
        }
    }


    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Uploading the document: steps 47-50 and Removing the document: steps 51-53, 59")
    fun uploadingTheDocumentAndRemovingTheDocument() {
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_SECOND

        step("Issuer go to Issuance and Upload document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                chooseToken(IT)
                e {
                    click(attachements)
                    click(editAttachmentsButton)
                    uploadDocument(uploadDocument, "ITDocument.png", itIssuer, itWallet)
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        step("User go to Marketplace and check the document is displayed in attachments") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                chooseToken(IT)
                assert {
                    elementWithTextPresentedIgnoreCase("ITDocument.png")
                }
            }
        }
        openPage<AtmProfilePage>(driver).logout()

        step("Issuer go to Issuance, Removing document and check this document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(itIssuer) }) {
                deleteDocumentAndCheckThisDocument(IT, "ITDocument.png", itIssuer, itWallet)
            }
        }
        openPage<AtmProfilePage>(driver).logout()
        step("User go to Marketplace and check the document is displayed in attachments") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                chooseToken(IT)
                assert {
                    elementWithTextNotPresented("ITDocument.png")
                }
            }
        }
    }


    @Disabled
    @TmsLink("ATMCH-5137")
    @Test
    @DisplayName("Adding a new parameter: navigation from step 46, steps 53-57, navigation from step 58, step 59")
    fun addingNewParameter() {
        val itIssuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val itWallet = itIssuer.mainWallet

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        //TODO add test

    }

}

