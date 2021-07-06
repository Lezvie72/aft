package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import models.CoinType.*
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmAdminTokensPage
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
@Story("Transfer of industrial tokens")
class TransferOfIndustrialTokens : BaseTest() {

    private val maturityDate = IT.maturityDateMonthNumber

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION),
        ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET)
    )
    @TmsLink("ATMCH-2948")
    @Test
    @DisplayName("Transfer of industrial tokens. Invalid signature")
    fun transferOfIndustrialTokensInvalidSignature() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")
        val wrongSignature = "MIIEpQIBAAKCAQEA3Tz2mr7SZiAMfQyuvBjM9Oi..Z1BjP5CE/Wm/Rr500P"

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        var balanceFromWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }
        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amountToTransfer) {
                prerequisite { placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1,maturityDate) }
                AtmProfilePage(driver).logout()
            }

            balanceFromWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

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
                    click(submitButton)
                    sendKeys(privateKey, wrongSignature)
                    assert { elementContainingTextPresented(" Invalid key ") }

                    deleteData(privateKey)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    val code = OAuth.generateCode(user.oAuthSecret)
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert { elementWithTextPresentedIgnoreCase("Transfer completed successfully") }
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
            "Expected balance: $balanceFromWalletBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceFromWalletBefore - BigDecimal.ONE, BigDecimal("0.01"))
        )

        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF), ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET))
    @TmsLink("ATMCH-2951")
    @Test
    @DisplayName("Transfer of industrial tokens. Using 2FA.")
    fun transferOfIndustrialTokensUsing2FA() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")


        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        var balanceFromWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }
        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amountToTransfer) {
                prerequisite { placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1,maturityDate) }
                AtmProfilePage(driver).logout()
            }

            balanceFromWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalance(IT, secondMainWallet.name)
        }

        AtmWalletPage(driver).logout()

        step("User make transfer from to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                transferFromWalletToWallet(
                    IT,
                    mainWallet,
                    secondMainWallet,
                    amountToTransfer.toString(),
                    maturityDate,
                    "note",
                    user
                )
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
            "Expected balance: $balanceFromWalletBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceFromWalletBefore - BigDecimal.ONE, BigDecimal("0.01"))
        )
        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }


    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-2949")
    @Test
    @DisplayName("Transfer of industrial tokens without 2fa")
    fun transferOfIndustrialTokensWithout2fa() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")


        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        var balanceFromWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amountToTransfer) {
                prerequisite { placeAndProceedTokenRequest(IT, mainWallet, wallet, amount, APPROVE, user, user1,maturityDate) }
                AtmProfilePage(driver).logout()
            }

            balanceFromWalletBefore =
                openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)

            AtmProfilePage(driver).logout()
        }

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user2) }.getBalance(IT, secondMainWallet.name)
        }

        AtmWalletPage(driver).logout()

       step("User make transfer from to wallet") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                transferFromWalletToWallet(
                    IT,
                    mainWallet,
                    secondMainWallet,
                    amountToTransfer.toString(),
                    maturityDate,
                    "note",
                    user
                )
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
            "Expected balance: $balanceFromWalletBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceFromWalletBefore - BigDecimal.ONE, BigDecimal("0.01"))
        )
        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + BigDecimal.ONE, BigDecimal("0.01"))
        )
    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION)
    @TmsLink("ATMCH-2945")
    @Test
    @DisplayName("Transfer of industrial tokens. Interface.")
    fun transferOfIndustrialTokensInterface() {
        val amount = BigDecimal("1.${randomNumeric(8)}")

        val user = Users.ATM_USER_2FA_OTF_OPERATION
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0") {
//
            prerequisite {
                placeAndProceedTokenRequest(
                    IT, mainWallet, wallet, amount,
                    APPROVE, user, user1,maturityDate
                )
            }
        }
        AtmProfilePage(driver).logout()

        val balanceFromWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }

        step("User check element in IT token page") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                chooseToken(IT)
                e {
                    click(transfer)
                    select(fromWallet, mainWallet.name)
                }

                assert {
                    elementPresented(fromWallet)
                    elementPresented(toWallet)
                    elementPresented(amountTransfer)
                    elementPresented(transferNote)
                    elementPresented(maturityDatesTransfer)
                    elementWithTextPresentedIgnoreCase("AVAILABLE BALANCE")
                    elementWithTextPresentedIgnoreCase("TRANSFER FEE")
                    elementIsDisabled("Submit")
                    elementPresented(cancel)
                }
                val balanceInOperation = balanceInOperation.amount

                assertThat(
                    "Expected base balance: $balanceFromWalletBefore, was: $balanceInOperation",
                    balanceFromWalletBefore,
                    closeTo(balanceInOperation, BigDecimal("0.01"))
                )
            }
        }
    }

    @ResourceLocks(ResourceLock(Constants.ROLE_USER_MANUAL_SIG_OTF_WALLET_FOR_OTF), ResourceLock(Constants.ROLE_USER_2FA_MANUAL_SIG_MAIN_WALLET))
    @TmsLink("ATMCH-2952")
    @Test
    @DisplayName("Transfer of industrial tokens. Using wrong 2FA.")
    fun transferOfIndustrialTokensUsingWrong2FA() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")

        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet

//        var balanceFromWalletBefore = step("User get balance from wallet before operation") {
//            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
//        }
        step("User buy, accepted and get balance from wallet IT token if balance for IT = 0 || < amountToTransfer") {
//            if (balanceFromWalletBefore == BigDecimal.ZERO || balanceFromWalletBefore < amountToTransfer) {
            prerequisite { addITToken(user, user1, mainWallet, wallet, amount, maturityDate) }
            AtmProfilePage(driver).logout()

//            }
//            balanceFromWalletBefore =
//                openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)

        }
        var balanceFromWalletBefore = step("User get balance from wallet before operation") {
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
            "Expected balance: $balanceFromWalletBefore, was: $balanceWalletFromAfter",
            balanceWalletFromAfter,
            closeTo(balanceFromWalletBefore, BigDecimal("0.01"))
        )
        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore, BigDecimal("0.01"))
        )
    }

    @TmsLink("ATMCH-2947")
    @Test
    @DisplayName("Transfer of industrial tokens. Validation.")
    fun transferOfIndustrialTokensValidation() {
        val amount = BigDecimal("1.${randomNumeric(8)}")
        val amountToTransfer = BigDecimal("1")
        val valueMoreThan256 = RandomStringUtils.randomAlphanumeric(257)

        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet

        val user1 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val wallet = user1.walletList[0]

        val user2 = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET
        val secondMainWallet = user2.mainWallet


        step("User buy, accepted and get balance from wallet IT token") {
            prerequisite { addITToken(user, user1,  mainWallet, wallet, amount, maturityDate) }
            AtmProfilePage(driver).logout()

        }

        val balanceFromWalletBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(user) }.getBalance(IT, mainWallet.name)
        }
        AtmProfilePage(driver).logout()
        step("Admin user change token type fee for IT token") {
            openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeForToken(
                IT,
                IT,
                "100",
                "1",
                "1200"
            )
        }

        step("User make transfer and check validation") {
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
                        "SomeWalletID"
                    )
                    assert {
                        elementContainingTextPresented("No wallet with this address")
                    }

                    sendKeys(
                        toWallet,
                        secondMainWallet.publicKey
                    )

                    sendKeys(amountTransfer, "0.123456789")
                    assert {
                        elementContainsValue(amountTransfer, "0.12345678")
                    }
                    deleteData(amountTransfer)

                    sendKeys(amountTransfer, "test")
                    assert {
                        elementContainsValue(amountTransfer, "0")
                    }

                    sendKeys(amountTransfer, "!@#/$%^&*()_+=-<>?/.,")
                    assert {
                        elementContainsValue(amountTransfer, "0")
                    }
                    deleteData(amountTransfer)

                    sendKeys(amountTransfer, balanceFromWalletBefore.toString())
                    assert {
                        elementContainingTextPresented("Not enough IT to pay fee for this transfer. Buy IT")
                    }
                    deleteData(amountTransfer)

                    sendKeys(amountTransfer, (balanceFromWalletBefore + BigDecimal.ONE).toString())
                    assert {
                        elementContainingTextPresented("Not enough IT to transfer")
                    }
                    deleteData(amountTransfer)

                    sendKeys(amountTransfer, amountToTransfer.toString())
                    assert { elementPresented(submit) }

                    sendKeys(transferNote, valueMoreThan256)
                    assert {
                        elementContainingTextPresented("Maximum length 256 characters")
                    }
                    deleteData(transferNote)
                    sendKeys(transferNote, "Transfer note")
                    click(submit)
                    assert {
                        elementPresented(privateKey)
                    }
                }
            }

        }
        step("Admin user change token type fee for IT token") {
            openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.changeFeeForToken(
                IT,
                CC,
                "100",
                "1",
                "1200"
            )

        }
    }
}

