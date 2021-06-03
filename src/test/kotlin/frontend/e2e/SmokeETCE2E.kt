package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.*
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmIssuancesPage.StatusType.DECLINE
import pages.atm.AtmWalletPage.RedemptionTypeETC.AUTO
import pages.atm.AtmWalletPage.RedemptionTypeETC.MANUAL
import utils.Constants
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tag("SmokeE2E")
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(OrderAnnotation::class)
@Epic("Frontend")
@Feature("E2E")
@Story("ETC")
class SmokeETCE2E : BaseTest() {
/*
        val amountEtcRedemption = amountRedemption.toBigDecimal() * BigDecimal(1000)
        В тех местах где происходит данное умножение это происходит из за того, что при загрузке файла мы грузим вес металла
        например " 0.089 OZ" . Для того что бы получить величину ЕТС токена необходимо умножить на 1000 ,то есть мы получим 89 ЕТС.
*/

    @Order(1)
    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Increase of total supply (steps 1-6)")
    fun increaseOfTotalSupply() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("Issuer go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("Issuer go to Issuance -> Manage -> Add Volume and check Volume Management") {
            with(openPage<AtmIssuancesPage>(driver)) {
                chooseToken(ETC)
                e {
                    click(manageVolume)
                    click(addVolume)
                }
                assert {
                    elementWithTextPresentedIgnoreCase("Volume management")
                    elementPresented(blankFormForUploadDocuments)
                }
            }
        }
        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

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

        val balanceWallet = step("User get balance from etc issuer wallet") {
            openPage<AtmWalletPage>(driver) { submit(etcIssuer) }.getBalance(ETC, etcWallet.name)
        }

        assertThat(
            "Expected balance: $balanceWallet, was: $onSaleBefore",
            onSaleBefore,
            closeTo(balanceWallet, BigDecimal("0.01"))
        )

    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_ONE),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Transfer to Client wallet: steps 6-10")
    fun transferToClientWallet() {
        val amountToTransfer = BigDecimal("0.0001")
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_ONE
        val wallet = etcUser.mainWallet

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }

        openPage<AtmWalletPage>(driver).logout()

        val balanceWallet = step("User get balance from etc issuer wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(etcIssuer) }.getBalance(ETC, etcWallet.name)
        }

        step("User go to Issuance -> Manage -> Add Volume and check Volume Management") {
            with(openPage<AtmWalletPage>(driver)) {
                chooseWallet(etcWallet.name)
                chooseToken(ETC)
                assert {
                    elementPresented(transferEtc)
                }
            }

        }
        step("User go to Wallet and make transfer") {
            with(openPage<AtmWalletPage>(driver) { submit(etcIssuer) }) {
                transferFromWalletToWallet(
                    ETC,
                    etcWallet,
                    wallet,
                    amountToTransfer.toString(),
                    "",
                    "etc",
                    etcIssuer
                )
            }
        }


        val balanceWalletAfter = step("User get balance from etc issuer wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(etcIssuer) }.getBalance(ETC, etcWallet.name)
        }

        assertThat(
            "Expected balance: $balanceWallet, was: $balanceWalletAfter",
            balanceWallet,
            closeTo(balanceWalletAfter - amountToTransfer, BigDecimal("0.01"))
        )

        openPage<AtmWalletPage>(driver).logout()

        val balanceWalletToAfter = step("User get balance from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }

        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + amountToTransfer, BigDecimal("0.01"))
        )
//TODO Transaction list

    }

    @Order(2)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Creating REDEMPTION requests in Auto mode: steps 11-14")
    fun creatingRedemptionRequestsInAutoMode() {
        val fileName = "ETC_Nomenclature"
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = etcUser.mainWallet

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

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

        step("User make Auto Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                chooseWallet(wallet.name)
                chooseToken(ETC)
                e {
                    click(redemption)
                }
                //todo проработать из селектед
//                Assert.assertTrue("Auto is checked", autoRedeemEtc.isChecked())
            }

            openPage<AtmWalletPage>(driver)

            openPage<AtmWalletPage>(driver)
                .redeemEtcToken(
                    ETC,
                    wallet,
                    AUTO,
                    amountRedemption,
                    "",
                    etcUser
                )
        }

        openPage<AtmWalletPage>(driver)

        val (balanceWalletAfter,
            heldInOrdersWalletAfter) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfter",
            heldInOrdersWalletAfter,
            closeTo(heldInOrdersWalletBefore + amountEtcRedemption, BigDecimal("0.01"))
        )

    }

    @Order(2)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Creating REDEMPTION requests in Manual mode:steps 15-18")
    fun creatingRedemptionRequestsInManualMode() {
        val fileName = "ETC_Nomenclature2342"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_SECOND
        val wallet = etcUser.walletList[0]

        val (amountRedemption, _, _, barNo, _, _) = prerequisite {
            prerequisiteForEtc(
                etcWallet,
                wallet,
                fileName,
                etcIssuer
            )
        }
        openPage<AtmWalletPage>(driver).logout()

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

        val amountEtcRedemption = (amountRedemption + "00000").toBigDecimal() * BigDecimal(1000)

        step("User make Manual Redemption") {
            with(openPage<AtmWalletPage>(driver) { submit(etcUser) }) {
                redeemEtcToken(
                    ETC,
                    wallet,
                    MANUAL,
                    "",
                    barNo,
                    etcUser
                )
            }
        }

        val (balanceWalletAfter,
            heldInOrdersWalletAfter) = step("User get balance and held from wallet after operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfter",
            balanceWalletAfter,
            closeTo(balanceWalletBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfter",
            heldInOrdersWalletAfter,
            closeTo(heldInOrdersWalletBefore + amountEtcRedemption, BigDecimal("0.01"))
        )

    }

    @Order(3)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_THIRD),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_SECOND)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Approving REDEMPTION requests: steps 21-22 (except chat), 25-29")
    fun approvingRedemptionRequests() {
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

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

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

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfterCreate",
            balanceWalletAfterCreate,
            closeTo(balanceWalletBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfterCreate",
            heldInOrdersWalletAfterCreate,
            closeTo(heldInOrdersWalletBefore + amountEtcRedemption, BigDecimal("0.01"))
        )

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
                    APPROVE
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

    @Order(4)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Declining REDEMPTION requests: steps 31-35")
    fun decliningRedemptionRequests() {
        val fileName = "ETC_Nomenclature3"

        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWalletRedemption = etcIssuer.walletList[1]
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
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

        val (balanceWalletBefore,
            heldInOrdersWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(etcUser) }.getHeldInOrders(ETC, wallet.name)
            balance to held
        }

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

        assertThat(
            "Expected balance: $balanceWalletBefore, was: $balanceWalletAfterCreate",
            balanceWalletAfterCreate,
            closeTo(balanceWalletBefore - amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletBefore, was: $heldInOrdersWalletAfterCreate",
            heldInOrdersWalletAfterCreate,
            closeTo(heldInOrdersWalletBefore + amountEtcRedemption, BigDecimal("0.01"))
        )

        openPage<AtmWalletPage>(driver).logout()

        val (inCirculationBefore, totalSupplyBefore, onSaleBefore) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("User decline redeem") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                findRedemptionOffersForEtcTokenAndSetStatus(
                    amountEtcRedemption,
                    etcIssuer,
                    etcWalletRedemption,
                    DECLINE
                )
                openPage<AtmIssuancesPage>(driver)
            }
        }

        val (inCirculationAfter, totalSupplyAfter, onSaleAfter) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("User go to Volume 24H and check status Declined for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                findOfferForTokenInRedemptionVolumeStatisticsAndCheckStatus(
                    ETC,
                    "Declined",
                    amountEtcRedemption

                )
                openPage<AtmIssuancesPage>(driver)
            }
        }

        openPage<AtmProfilePage>(driver).logout()

        step("User go to Orders page and check status for offer") {
            openPage<AtmOrdersPage>(driver) { submit(etcUser) }.findOrderAndCheckStatus(
                wallet,
                ETC,
                amountEtcRedemption,
                "Declined"
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
            closeTo(balanceWalletAfterCreate + amountEtcRedemption, BigDecimal("0.01"))
        )

        assertThat(
            "Expected heldInOrdersWallet: $heldInOrdersWalletAfterCreate, was: $heldInOrdersWalletAfterApprove",
            heldInOrdersWalletAfterApprove,
            closeTo(heldInOrdersWalletAfterCreate - amountEtcRedemption, BigDecimal("0.01"))
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

    @Order(2)
    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA)
    )
    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Chatting: 19-20, 22-24, 29-30")
    fun chatting() {
        val fileName = "ETC_Nomenclature4"

        val textUser = "Message for User"
        val textIssuer = "Message for Issuer"
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWalletRedemption = etcIssuer.walletList[1]
        val etcWallet = etcIssuer.walletList[0]

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
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
                    sendKeys(chatInput, textUser)
                    click(chatSendButton)
                }
            }
        }
//                //TODO необходимо дорабтать проверку так как приходить письмо с автосгенерированным линком
////                val href = GmailApi.getBodyMessageFromIssuer(etcUser.email)
////                Assert.assertTrue(href.contains("Dear Atomyze participant! Issuer etcAutotest has sent you new message."));
////
////                assert {
////                    textEmail(
////                        href,
////                        "Dear Atomyze participant! Issuer etcAutotest has sent you new message."
////                    )
////                }
        openPage<AtmWalletPage>(driver).logout()
        step("User go to Orders page and check status for offer") {
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
                    click(showChat)
                    click(chatButton)
                    checkTheMessageFromChat(textUser)
                    sendKeys(chatInput, textIssuer)
                    click(chatSendButton)
                    click(approve)
                }
                signAndSubmitMessage(etcUser, etcWalletRedemption.secretKey)
            }

        }
        openPage<AtmWalletPage>(driver).logout()
        step("User go to Orders page and check status for offer") {
            with(openPage<AtmOrdersPage>(driver) { submit(etcUser) })
            {
                findOrderOpenCardAndCheckData(
                    wallet,
                    ETC,
                    amountEtcRedemption,
                    "Executed"
                )
                checkTheMessageFromChat(textIssuer)
            }
        }
//        TODO необходимо дорабтать проверку так как приходить письмо с автосгенерированным линком
//                val href = GmailApi.getBodyMessageFromIssuer(etcUser.email)
//                Assert.assertTrue(href.contains("Dear Atomyze participant! Issuer etcAutotest has sent you new message."));
//                assert {
//                    textEmail(
//                        href,
//                        "Dear Atomyze participant! Issuer etcAutotest has sent you new message."
//                    )
//                }
    }


    @TmsLink("ATMCH-5259")
    @Test
    @DisplayName("Uploading the document: steps 36-39 and Removing the document: steps 40-43")
    fun uploadingTheDocumentAndRemovingTheDocument() {
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD
        val etcWallet = etcIssuer.walletList[0]

        step("User go to Issuance and Upload document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                chooseToken(ETC)
                e {
                    click(attachements)
                    click(editAttachmentsButton)
                    uploadDocument(uploadDocument, "ETCDocument.png", etcIssuer, etcWallet)
                }
            }
        }

        step("User go to Marketplace and check the document is displayed in attachments") {
            with(openPage<AtmMarketplacePage>(driver) { submit(etcIssuer) }) {
                chooseEtcToken(ETC)
                assert {
                    elementWithTextPresentedIgnoreCase("ETCDocument.png")
                }
            }
        }

        step("User go to Issuance, Removing document and check this document") {
            with(openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }) {
                deleteDocumentAndCheckThisDocument(ETC, "ETCDocument.png", etcIssuer, etcWallet)
            }
            with(openPage<AtmMarketplacePage>(driver) { submit(etcIssuer) }) {
                chooseEtcToken(ETC)
                assert {
                    elementWithTextNotPresented("ETCDocument.png")
                }
            }
        }
    }

}

