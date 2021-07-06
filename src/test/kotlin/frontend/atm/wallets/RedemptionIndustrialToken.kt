package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.IT
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmIssuancesPage
import pages.atm.AtmIssuancesPage.LimitType.MAX
import pages.atm.AtmIssuancesPage.LimitType.MIN
import pages.atm.AtmIssuancesPage.OperationType.REDEMPTION
import pages.atm.AtmIssuancesPage.StatusType.APPROVE
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.OAuth
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("Wallets")
@Story("Redemption industrial token")
class RedemptionIndustrialToken : BaseTest() {

    private val maturityDateForBuy = IT.maturityDateMonthNumber
    private val maturityDateForRedemption = IT.maturityDateMonthString

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-3382")
    @Test
    @DisplayName("IT redemption. Amount of redemption is out of limits")
    fun itRedemptionAmountOfRedemptionIsOutOfLimits() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val mainWallet = user.mainWallet

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MIN, "0.000000001", user1, wallet)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        step("User buy, accepted and get balance from wallet IT token") {
            prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy) }
            AtmProfilePage(driver).logout()
        }

        val balanceFromWalletWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }
        AtmProfilePage(driver).logout()

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalance(IT, secondMainWallet.name)
        }
        AtmWalletPage(driver).logout()

        step("User make transfer from to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    click(transfer)
                    select(
                        fromWallet,
                        mainWallet.publicKey
                    )
                    sendKeys(
                        toWallet,
                        secondMainWallet.publicKey
                    )
                    sendKeys(amountTransfer, amountToTransfer.toString())
                    sendKeys(transferNote, "note")
                    click(submit)
                    click(privateKey)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }
        }

        val balanceWalletFromAfter = step("User get balance from wallet after operation") {
            openPage<AtmWalletPage>().getBalance(IT, mainWallet.name)
        }

        openPage<AtmWalletPage>(driver).logout()

        val balanceWalletToAfter = step("User get balance from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalance(IT, secondMainWallet.name)
        }

        assertThat(
            "Expected balance: $balanceFromWalletWalletBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceFromWalletWalletBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-3298")
    @Test
    @DisplayName("Redemption industrial tokens in wallet.")
    fun redemptionIndustrialTokensInWallet() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MIN, "0.000000001", user1, wallet)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        val (balanceFromWalletBefore,
            heldInOrdersFromWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amount) {
                prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy) }
                AtmProfilePage(driver).logout()
            }
        }

        step("User make redeem to wallet and check elements in operation page") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                }
                assert {
                    elementPresented(selectWallet)
                    elementPresented(tokenQuantity)
                    elementPresented(maturityDatesRedemption)
                    elementWithTextPresentedIgnoreCase("AVAILABLE BALANCE")
                    elementWithTextPresentedIgnoreCase("TOTAL METAL REQUESTED")
                    elementPresented(submitButton)
                    elementPresented(cancelButton)
                }
                e {
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(tokenQuantity, amount.toString())
                    select(maturityDatesRedemption, maturityDateForRedemption)
                    click(submitButton)
                }
                signAndSubmitMessage(user, mainWallet.secretKey)
                assert {
                    elementWithTextPresentedIgnoreCase("Your redemption request has been successfully created")
                    elementWithTextPresentedIgnoreCase("ORDER ID")
                    elementWithTextPresentedIgnoreCase("SUBMISSION DATE")
                    elementWithTextPresentedIgnoreCase("EXECUTED")
                    elementWithTextPresentedIgnoreCase("TOKEN QUANTITY REDEEMED")
                    elementContainingTextPresented("TO RECEIVE")
                    elementPresented(doneButton)
                }
                e {
                    click(doneButton)
                }
                assert {
                    urlMatches(".*/orders$")
                }
            }
        }
        val heldInOrdersFromWalletAfter = step("User get held from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(
                IT,
                mainWallet.name
            )
        }

        assertThat(
            "Expected balance: $heldInOrdersFromWalletBefore, was: $heldInOrdersFromWalletAfter",
            heldInOrdersFromWalletAfter,
            closeTo(heldInOrdersFromWalletBefore + amount, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-3319")
    @Test
    @DisplayName("Redemption industrial tokens in wallet. Wrong Signature")
    fun redemptionIndustrialTokensInWalletWrongSignature() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val wrongSignature = "MIIEpQIBAAKCAQEA3Tz2mr7SZiAMfQyuvBjM9Oi..Z1BjP5CE/Wm/Rr500P"

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MIN, "0.000000001", user1, wallet)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        val (balanceFromWalletBefore,
            heldInOrdersFromWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amount) {
                prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy) }
                AtmProfilePage(driver).logout()
            }
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(tokenQuantity, amount.toString())
                    select(maturityDatesRedemption, maturityDateForRedemption)
                    click(submitButton)
                    click(privateKey)
                    sendKeys(privateKey, wrongSignature)
                    assert { elementContainingTextPresented(" Invalid key ") }
                }

            }
        }

        val heldInOrdersFromWalletAfter = step("User get held from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(
                IT,
                mainWallet.name
            )
        }

        assertThat(
            "Expected balance: $heldInOrdersFromWalletBefore, was: $heldInOrdersFromWalletAfter",
            heldInOrdersFromWalletAfter,
            closeTo(heldInOrdersFromWalletBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-3320")
    @Test
    @DisplayName("Redemption industrial tokens in wallet. Wrong 2FA APP")
    fun redemptionIndustrialTokensInWalletWrong2FAAPP() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MIN, "0.000000001", user1, wallet)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        val (balanceFromWalletBefore,
            heldInOrdersFromWalletBefore) = step("User get balance and held from wallet before operation") {
            val balance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
            openPage<AtmWalletPage>(driver)
            val held = openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(IT, mainWallet.name)
            balance to held
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amount) {
                prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy) }
                AtmProfilePage(driver).logout()
            }
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(tokenQuantity, amount.toString())
                    select(maturityDatesRedemption, maturityDateForRedemption)
                    click(submitButton)
                    click(privateKey)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementContainingTextPresented("Wrong code")
                }
            }
        }

        val heldInOrdersFromWalletAfter = step("User get held from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(
                IT,
                mainWallet.name
            )
        }

        assertThat(
            "Expected balance: $heldInOrdersFromWalletBefore, was: $heldInOrdersFromWalletAfter",
            heldInOrdersFromWalletAfter,
            closeTo(heldInOrdersFromWalletBefore, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-3379")
    @Test
    @DisplayName("IT redemption. Insufficient funds")
    fun itRedemptionInsufficientFunds() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MIN, "0.000000001", user1, wallet)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        var balanceFromWalletBefore = step("User get balance and held from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO) {
                prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy) }
                AtmProfilePage(driver).logout()
            }

            balanceFromWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MAX, balanceFromWalletBefore.toString(), user1, wallet)
                openPage<AtmProfilePage>(driver).logout()
            }
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(tokenQuantity, (balanceFromWalletBefore + BigDecimal.ONE).toString())
                    select(maturityDatesRedemption, maturityDateForRedemption)
                    assert {
                        elementContainingTextPresented("Amount to redeem exceeds available balance")
                    }
                }
                openPage<AtmProfilePage>(driver).logout()

            }

        }
        step("User change limit for order") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(IT, REDEMPTION, MAX, "1000.00000000", user1, wallet)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-3380")
    @Test
    @DisplayName("IT redemption. Min amount of redemption")
    fun itRedemptionMinAmountOfRedemption() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val limitValue = BigDecimal("0.${randomNumeric(8)}")

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = userBuyer.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for redemption") {
            openPage<AtmIssuancesPage>(driver) { submit(user1) }.changeLimitAmount(
                IT,
                REDEMPTION,
                MIN,
                limitValue.toString(),
                user1,
                wallet
            )
        }
        AtmProfilePage(driver).logout()
        var balanceITWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceITWalletBefore == BigDecimal.ZERO || balanceITWalletBefore < limitValue) {
                prerequisite { addITToken(userBuyer, user1, mainWallet, wallet, amount, maturityDateForBuy) }
                AtmProfilePage(driver).logout()
            }

            balanceITWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    limitValue.toString(),
                    maturityDateForRedemption,
                    userBuyer
                )
            }
        }

        AtmProfilePage(driver).logout()

        step("User accept redeem and back limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                findRedemptionOffers(IT, limitValue, limitValue, user1, wallet, APPROVE)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT, REDEMPTION,
                    MIN,
                    "0.00000001", user1, wallet
                )
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        val balanceITWalletAfter = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
        }

        assertThat(
            "Expected balance: $balanceITWalletBefore, was: $balanceITWalletAfter",
            balanceITWalletAfter,
            closeTo(balanceITWalletBefore - limitValue, BigDecimal("0.01"))
        )

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-3381")
    @Test
    @DisplayName("IT redemption. Max amount of redemption")
    fun itRedemptionMaxAmountOfRedemption() {
        val limitValue = BigDecimal("1.${randomNumeric(8)}")

        val userBuyer = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = userBuyer.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for redemption") {
            openPage<AtmIssuancesPage>(driver) { submit(user1) }.changeLimitAmount(
                IT,
                REDEMPTION,
                MAX,
                limitValue.toString(),
                user1,
                wallet
            )
        }
        AtmProfilePage(driver).logout()
        var balanceITWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceITWalletBefore == BigDecimal.ZERO || balanceITWalletBefore < limitValue) {
                prerequisite {
                    addITToken(
                        userBuyer,
                        user1,
                        mainWallet,
                        wallet,
                        limitValue + BigDecimal.ONE,
                        maturityDateForBuy
                    )
                }
                AtmProfilePage(driver).logout()
            }

            balanceITWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(userBuyer) }) {
                redeemToken(
                    IT,
                    mainWallet,
                    limitValue.toString(),
                    maturityDateForRedemption,
                    userBuyer
                )
            }
        }

        AtmProfilePage(driver).logout()

        step("User accept redeem and back limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                findRedemptionOffers(IT, limitValue, limitValue, user1, wallet, APPROVE)
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT, REDEMPTION, MAX,
                    "1_000.00000000", user1, wallet
                )
            }
        }

        openPage<AtmWalletPage>(driver).logout()

        val balanceITWalletAfter = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(userBuyer) }.getBalance(IT, mainWallet.name)

        }

        assertThat(
            "Expected balance: $balanceITWalletBefore, was: $balanceITWalletAfter",
            balanceITWalletAfter,
            closeTo(balanceITWalletBefore - limitValue, BigDecimal("0.01"))
        )

    }


    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_WITHOUT2FA)
    @TmsLink("ATMCH-3382")
    @Test
    @DisplayName("IT redemption. Amount of redemption is out of limits")
    fun itRedemptionAmountOfRedemtionIsOutOfLimits() {
        val limitValueMin = BigDecimal("0.${randomNumeric(8)}")
        val limitValueMax = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User change limit for redemption") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MIN,
                    limitValueMin.toString(),
                    user1,
                    wallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT,
                    REDEMPTION,
                    MAX,
                    limitValueMax.toString(),
                    user1,
                    wallet
                )
            }
        }

        AtmProfilePage(driver).logout()

        var balanceFromWalletBefore = step("User get balance and held from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < limitValueMax) {
                prerequisite {
                    addITToken(
                        user,
                        user1,
                        mainWallet,
                        wallet,
                        limitValueMax + BigDecimal.ONE,
                        maturityDateForBuy
                    )
                }
                AtmProfilePage(driver).logout()
            }
        }

        step("User make redeem to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(IT)
                    wait {
                        until("Button 'Redeem' should be enabled") {
                            redemption.getAttribute("disabled") == null
                        }
                    }
                    click(redemption)
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(tokenQuantity, (limitValueMin - BigDecimal("0.00000001")).toString())
                    assert {
                        elementContainingTextPresented("Entered token quantity is below min value required to proceed with this order")
                    }
                    deleteData(tokenQuantity)
                    sendKeys(tokenQuantity, (limitValueMax + BigDecimal("0.1")).toString())
                    assert {
                        elementContainingTextPresented("Entered token quantity is above max value required to proceed with this order")
                    }
                }
            }
        }

        AtmProfilePage(driver).logout()

        step("User back limit") {
            with(openPage<AtmIssuancesPage>(driver) { submit(user1) }) {
                changeLimitAmount(
                    IT, REDEMPTION, MIN,
                    "0.00000001", user1, wallet
                )
                openPage<AtmIssuancesPage>(driver)
                changeLimitAmount(
                    IT, REDEMPTION, MAX,
                    "1000.00000000", user1, wallet
                )
            }
        }
    }

}