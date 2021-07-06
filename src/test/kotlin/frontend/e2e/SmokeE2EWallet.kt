package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import models.user.classes.DefaultUser
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.*
import pages.atm.AtmWalletPage.WalletType.*
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.attach
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("Wallet")
class SmokeE2EWallet : BaseTest() {

    private fun createAndRegisterUser(fullName: String, kycPassed: Boolean = false): DefaultUser {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, fullName, kycPassed)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        return user
    }

    private val maturityDateForBuy = IT.maturityDateMonthNumber
    private val maturityDateForRedemption = IT.maturityDateMonthString

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Main wallet, OTF wallet, Issuer wallet registration (1-5), (6-9), (10-13) and Assign wallet roles (44-52)")
    fun walletRegistrationAndAssignWalletRoles() {
        val fullName = "smokeE2E${org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(8)}"
        val address = "smokeE2E"

        openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }.addCompany(
            fullName,
            fullName,
            address,
            legalCompany = true,
            issuer = true,
            validator = true
        )

        val user = step("User created") {
            createAndRegisterUser(fullName, true)
        }

        step("Wallets registration") {
            val labelMain = "Main"
            val labelOtf = "OTF"
            val labelIssuer = "ISSUER"
            val (privateKeyMain, publicKeyMain) = openPage<AtmKeysPage>(driver) { submit(user) }.generatePublicAndPrivateKey()
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                registerWallet(MAIN, publicKeyMain, privateKeyMain, labelMain)
            }
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(labelMain),
                    "Wallet with label $labelMain wasn't found"
                )
            }
            openPage<AtmProfilePage>(driver)
            val (privateKeyOtf, publicKeyOtf) = openPage<AtmKeysPage>(driver) { submit(user) }.generatePublicAndPrivateKey()
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                registerWallet(OTF, publicKeyOtf, privateKeyOtf, labelOtf)
            }
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                Assertions.assertTrue(isWalletWithLabelPresented(labelOtf), "Wallet with label $labelOtf wasn't found")
            }
            openPage<AtmProfilePage>(driver)
            val (privateKeyIssuer, publicKeyIssuer) = openPage<AtmKeysPage>(driver) { submit(user) }.generatePublicAndPrivateKey()
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                registerWallet(ISSUER, publicKeyIssuer, privateKeyIssuer, labelIssuer)
            }
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                Assertions.assertTrue(
                    isWalletWithLabelPresented(labelIssuer),
                    "Wallet with label $labelIssuer wasn't found"
                )
            }
            openPage<AtmProfilePage>(driver)
            step("Assign Main wallet roles") {
                with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                    chooseWallet("Main")
                    e {
                        click(assign)
                    }
                    findEmployeeAndSetControllerCheckBox(user.email, false, user)
                    assert {
                        elementContainingTextPresented("Your updates have been applied")
                        elementContainingTextPresented("Not assigned")
                    }
                    findEmployeeAndSetControllerCheckBox(user.email, true, user)
                    Thread.sleep(1000)
                    assert {
                        elementContainingTextPresented("Your updates have been applied")
                        elementContainingTextPresented("Controller")
                        elementWithTextNotPresented("Not assigned")
                    }

                }
                openPage<AtmProfilePage>(driver)
            }
        }
        step("Assign Otf wallet roles") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet("OTF")
                e {
                    click(assign)
                }
                findEmployeeAndSetControllerCheckBox(user.email, false, user)
                assert {
                    elementContainingTextPresented("Your updates have been applied")
                    elementContainingTextPresented("Not assigned")
                }
                findEmployeeAndSetControllerCheckBox(user.email, true, user)
                Thread.sleep(1000)
                assert {
                    elementContainingTextPresented("Your updates have been applied")
                    elementContainingTextPresented("Controller")
                    elementContainingTextNotPresented("Not assigned")
                }

            }
            openPage<AtmProfilePage>(driver)
        }
        step("Assign ISSUER wallet roles") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet("ISSUER")
                e {
                    click(assign)
                }
                findEmployeeAndSetControllerCheckBox(user.email, false, user)
                assert {
                    elementContainingTextPresented("Your updates have been applied")
                    elementContainingTextPresented("Not assigned")
                }
                findEmployeeAndSetControllerCheckBox(user.email, true, user)
                Thread.sleep(1000)
                assert {
                    elementContainingTextPresented("Your updates have been applied")
                    elementContainingTextPresented("Controller")
                    elementContainingTextNotPresented("Not assigned")
                }

            }
        }
    }


    @ResourceLock(Constants.ROLE_USER_MAIN_OTF_MOVE)
    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Move CC Token And Check Balance steps 14,15-18")
    fun moveCCTokenToOtfAndCheckBalance() {
        val amount = "10"

        val user = Users.ATM_USER_MAIN_OTF_MOVE

        val otfWallet = user.otfWallet
        val mainWallet = user.mainWallet

        prerequisite { addCurrencyCoinToWallet(user, amount, mainWallet) }

        val mainBalance = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, mainWallet.name).toBigDecimal()
        }

        val otfBalance = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, otfWallet.name).toBigDecimal()
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            moveToOTFWalletNew(amount, CC, user, mainWallet)
        }

        val mainBalanceAfterMove = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, mainWallet.name).toBigDecimal()
        }

        val otfBalanceAfterMove = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, otfWallet.name).toBigDecimal()
        }

        assertThat(
            mainBalanceAfterMove,
            Matchers.equalTo(mainBalance - amount.toBigDecimal())
        )

        assertThat(
            otfBalanceAfterMove,
            Matchers.equalTo(otfBalance + amount.toBigDecimal())
        )
    }

    @ResourceLock(Constants.ROLE_USER_MAIN_OTF_MOVE)
    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Move CC Token From OTF to Main And Check Balance steps 14,19-23")
    fun moveCCTokenFromOtfToMainAndCheckBalance() {

        val amount = "10"

        val user = Users.ATM_USER_MAIN_OTF_MOVE

        val otfWallet = user.otfWallet
        val mainWallet = user.mainWallet

        step("Admin change fee for CC") {
            openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeForToken(
                CC,
                CC,
                "0",
                "1",
                "1"
            )

        }
        //region prerequisites
//        presetForMovement(user, amount, mainWallet)
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            moveToOTFWallet(amount, user, mainWallet)
        }
        //endregion

        val mainBalance = step("GIVEN user remembers balance on his Main wallet") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, mainWallet.name).also {
                attach("Main wallet balance before", it)
            }.toBigDecimal()
        }

        val otfBalance = step("AND user remembers balance on his OTF wallet") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, otfWallet.name).also {
                attach("OTF wallet balance before", it)
            }.toBigDecimal()
        }

        step("WHEN user moves CC to his Main wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                moveToMainWallet(CC, amount, user, mainWallet, otfWallet)
            }
        }

        val (mainBalanceAfterMove, otfBalanceAfterMove) = step("AND checks his balances after movement") {
            val mainBalanceAfterMove =
                openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, mainWallet.name).toBigDecimal()
            val otfBalanceAfterMove =
                openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, otfWallet.name).toBigDecimal()
            mainBalanceAfterMove to otfBalanceAfterMove
        }

        step("THEN user's balance on Main wallet has decreased") {
            assertThat(
                "Main wallet balance check failed",
                mainBalanceAfterMove,
                Matchers.equalTo(mainBalance + amount.toBigDecimal())
            )
        }

        step("AND user's balance on OTF wallet has increased") {
            assertThat(
                "Main wallet balance check failed",
                otfBalanceAfterMove,
                Matchers.equalTo(otfBalance - amount.toBigDecimal())
            )
        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Transfer CC Token to another user Check Balance steps 24-28")
    fun transferCCTokenToAnotherUserAndCheckBalance() {
        val amount = "10"
        val user = Users.ATM_USER_2MAIN_WALLET
        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val firstMainWallet = user.mainWallet
        val secondMainWallet = user2.mainWallet

        val alias = step("GIVEN User go to Wallet, get alias and add fiat in your wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(firstMainWallet.name)
        }
        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
            openPage<AtmAdminTokensPage>(driver).changeFeeForToken(CC, CC, "100", "1.34", "1.432")
        }
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            buyOrReceiveToken(CC, "2", user, firstMainWallet)
        }
        val balanceWalletFromBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val balanceWalletToBefore = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalanceFromWalletForToken(CC, secondMainWallet.name)
                .toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val (_, fee2) = openPage<AtmWalletPage>(driver) { submit(user) }.transferFromWalletToWallet(
            CC,
            firstMainWallet,
            secondMainWallet,
            "1",
            "",
            "",
            user

        )

        val balanceWalletFromAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>().getBalanceFromWalletForToken(CC, firstMainWallet.name).toBigDecimal()
        }
        openPage<AtmWalletPage>(driver).logout()
        val balanceWalletToAfter = step("AND User go to Wallet get balance") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalanceFromWalletForToken(CC, secondMainWallet.name)
                .toBigDecimal()
        }
        assertThat(
            "Expected base balance: $balanceWalletFromBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            Matchers.closeTo(balanceWalletFromBefore - BigDecimal.ONE - fee2, BigDecimal("0.01"))
        )
        assertThat(
            "Expected base balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            Matchers.closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }

    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Trade IT Token 29-32")
    fun tradeITToken() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0") {
            prerequisite {
                placeAndProceedTokenRequest(
                    IT, mainWallet, wallet, amount,
                    AtmIssuancesPage.StatusType.APPROVE, user, user1, maturityDateForBuy
                )
            }
            AtmProfilePage(driver).logout()
        }

        step("User Trade IT token") {
            with(openPage<AtmWalletPage>(driver) { submit(user) })
            {
                tradeToken(mainWallet, IT, "1", maturityDateForBuy, user)
                assert {
                    elementContainingTextPresented("Order submitted successfully")
                }
            }

        }

    }


    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("REDEEM (34-37)")
    fun redeem() {
        val amount = BigDecimal("1.${RandomStringUtils.randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        val heldInOrdersFromWalletBefore = step("User get balance and held from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getHeldInOrders(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            prerequisite {
                addITToken(user, user1, mainWallet, wallet, amount, maturityDateForBuy)
                AtmProfilePage(driver).logout()
            }
        }

        step("User make redeem to wallet and check elements in operation page") {
            openPage<AtmWalletPage>(driver) { submit(user) }.redeemToken(
                IT,
                mainWallet,
                amount.toString(),
                maturityDateForRedemption,
                user
            )
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
            Matchers.closeTo(heldInOrdersFromWalletBefore + amount, BigDecimal("0.01"))
        )

    }

    @TmsLink("ATMCH-5146")
    @Test
    @DisplayName("Withdrawal (38-43)")
    fun withdrawal() {
        val amount = "10"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val wallet = user.mainWallet

        val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
        with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
            addPayment(alias, amount)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(FIAT)
                click(withdraw)
                click(addBankDetails)
            }
        }
        openPage<AtmBankAccountsPage>(driver).addBankAccount(
            "Russian BIC",
            "1223456",
            "Tesst",
            "Test",
            "1234567890123456",
            "USD",
            "Vavilova"
        )
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                chooseWallet(wallet.name)
                chooseToken(FIAT)
                click(withdraw)
                sendKeys(amountTransfer, amount)
                select(currency, "USD")
                select(selectBankDetails, "Tesst")
                click(submitButton)
                click(submitButton)
            }
            signAndSubmitMessage(user, wallet.secretKey)
            assert { elementWithTextPresented(" Your withdrawal request registered successfully ") }
        }
        with(openPage<AtmBankAccountsPage>(driver) { submit(user) }) {
            try {
                step("Post test cleaning") {
                    chooseBankAccountDetails("Tesst")
                    deleteBankAccountDetails("Tesst")
                }
            } catch (e: Exception) {
                print("Card with Tesst not found")
                false
            }

        }
    }
}


