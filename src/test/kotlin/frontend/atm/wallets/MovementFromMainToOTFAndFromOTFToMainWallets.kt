package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.user.classes.DefaultUser
import models.user.interfaces.SimpleWallet
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.*
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Movement from main to OTF and from OTF to main wallets")
class MovementFromMainToOTFAndFromOTFToMainWallets : BaseTest() {


    @ResourceLock(Constants.ROLE_USER_MAIN_OTF_MOVE)
    @TmsLink("ATMCH-2530")
    @Test
    @DisplayName("Movement from Main to OTF wallet - Cancellation")
    fun movementFromMainToOTFWalletCancellation() {
        val amount = "100"
        val user = Users.ATM_USER_MAIN_OAUTH_OTF_MOVE

        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        prerequisite {
            addCurrencyCoinToWallet(user, amount, mainWallet)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            step("Go into СС token in OTF wallet") {
                chooseWallet(otfWallet.name)
                chooseToken(CC)
            }
            val expectedCCBalanceFromOTFWallet: String = balanceTokenUser.amount.toString()

            step("Go into CC token in MAIN wallet") {
                openPage<AtmWalletPage> {}
                chooseWallet(mainWallet.name)
                chooseToken(CC)
            }
            val expectedTransactionList: List<String> = transactionsList.content.map { it.id }
            val expectedCCBalanceFromMainWallet: String = balanceTokenUser.amount.toString()

            e {
                step("Cancel movement on transfer page") {
                    click(move)
                    click(cancel)
                }

                step("Cancel movement on secret key pop-up") {
                    click(move)
                    sendKeys(amountTransfer, amount)
                    click(submitButton)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(cancelButton)
                    //TODO: specify requirements (while refresh page)
                    driver.navigate().refresh()
                }

                step("Cancel movement on 2FA pop-up") {
                    sendKeys(amountTransfer, amount)
                    click(submitButton)
                    sendKeys(privateKey, mainWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    click(cancelButton)
                }

                step("Check transaction list") {
                    openPage<AtmWalletPage>(driver)
                    chooseWallet(mainWallet.name)
                    chooseToken(CC)
                    assertThat(expectedTransactionList, equalTo(transactionsList.content.map { it.id }))
                }

                step("Check balance in Main wallet") {
                    assertThat(expectedCCBalanceFromMainWallet, equalTo(balanceTokenUser.amount.toString()))
                }

                step("Check balance in OTF wallet") {
                    openPage<AtmWalletPage> {}
                    chooseWallet(otfWallet.name)
                    chooseToken(CC)
                    assertThat(expectedCCBalanceFromOTFWallet, equalTo(balanceTokenUser.amount.toString()))
                }

            }

        }
    }

    @TmsLink("ATMCH-2534")
    @Test
    @DisplayName("Movement from OTF to Main wallet - Cancellation")
    fun movementFromOTFToMainWalletCancellation() {
        val amount = "100"
        val user = Users.ATM_USER_MAIN_OAUTH_OTF_MOVE
        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        prerequisite {
            addCurrencyCoinToWallet(user, amount, mainWallet)
            moveCurrencyCoinFromMainToOTFWallet(user, amount, mainWallet)
        }

        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_MAIN_OAUTH_OTF_MOVE) }) {
            step("Go into СС token in MAIN wallet") {
                chooseWallet(mainWallet.name)
                chooseToken(CC)
            }
            val expectedCCBalanceFromMainWallet: String = balanceTokenUser.amount.toString()

            step("Go into CC token in OTF wallet") {
                openPage<AtmWalletPage>(driver) {}
                chooseWallet(otfWallet.name)
                chooseToken(CC)
            }
            val expectedTransactionList: List<String> = transactionsList.content.map { it.id }
            val expectedCCBalanceFromOTFWallet: String = balanceTokenUser.amount.toString()

            e {
                step("Cancel movement on transfer page") {
                    click(move)
                    click(cancel)
                }

                step("Cancel movement on secret key pop-up") {
                    click(move)
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(amountTransfer, amount)
                    click(submitButton)
                    sendKeys(privateKey, otfWallet.secretKey)
                    click(cancelButton)
                    //TODO: specify requirements (while refresh page)
                    driver.navigate().refresh()
                }

                step("Cancel movement on 2FA pop-up") {
                    select(selectWallet, mainWallet.publicKey)
                    sendKeys(amountTransfer, amount)
                    click(submitButton)
                    sendKeys(privateKey, otfWallet.secretKey)
                    click(confirmPrivateKeyButton)
                    click(cancelButton)
                }

                step("Check transaction list") {
                    openPage<AtmWalletPage>(driver)
                    chooseWallet(otfWallet.name)
                    chooseToken(CC)
                    assertThat(expectedTransactionList, equalTo(transactionsList.content.map { it.id }))
                }

                step("Check balance in OTF wallet") {
                    assertThat(expectedCCBalanceFromOTFWallet, equalTo(balanceTokenUser.amount.toString()))
                }

                step("Check balance in MAIN wallet") {
                    openPage<AtmWalletPage>(driver)
                    chooseWallet(mainWallet.name)
                    chooseToken(CC)
                    assertThat(expectedCCBalanceFromMainWallet, equalTo(balanceTokenUser.amount.toString()))
                }

            }

        }

    }

    @TmsLink("ATMCH-2522")
    @Test
    @DisplayName("Movement from Main wallet to OTF")
    fun movementFromMainWalletToOTF() {
        val amount = "100"

        val user = Users.ATM_USER_MAIN_OTF_MOVE

        val otfWallet = user.otfWallet
        val mainWallet = user.mainWallet

        presetForMovement(user, amount, mainWallet)

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
            equalTo(mainBalance - amount.toBigDecimal())
        )

        assertThat(
            otfBalanceAfterMove,
            equalTo(otfBalance + amount.toBigDecimal())
        )

        // TODO: Steps from 10 to 12 are missing cause of transactions part
    }

    @ResourceLock(Constants.ROLE_USER_MAIN_OTF_MOVE)
    @TmsLink("ATMCH-2532")
    @Test
    @DisplayName("Movement from OTF to Main wallet")
    fun movementFromOTFtoMainWallet() {
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
                equalTo(mainBalance + amount.toBigDecimal())
            )
        }

        step("AND user's balance on OTF wallet has increased") {
            assertThat(
                "Main wallet balance check failed",
                otfBalanceAfterMove,
                equalTo(otfBalance - amount.toBigDecimal())
            )
        }

        // TODO: Steps from 10 to 12 are missing cause of transactions parts
    }

    private fun presetForMovement(user: DefaultUser, amount: String, wallet: SimpleWallet) {
        val alias = step("GIVEN User go to Wallet, get alias and add fiat to wallet") {
            openPage<AtmWalletPage>(driver) { submit(user) }
            openPage<AtmWalletPage>().getAlias()
        }

        step("WHEN Admin create send fiat to wallet") {
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, amount)
            }
        }

        step("WHEN User get fiat he want to trade coins") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                buyOrReceiveToken(CC, amount, user, wallet)
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_MAIN_OTF_MOVE)
    @TmsLink("ATMCH-2520")
    @Test
    @DisplayName("Movement between OTF and Main wallets - UI cheking")
    fun movementBetweenOtfAndMainWalletsUiCheking() {
        val user = Users.ATM_USER_MAIN_OAUTH_OTF_MOVE

        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        step("Go into OTF wallet -> Move Check elements") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(otfWallet.name)
                    chooseToken(CC)
                    click(move)
                }
                assert {
                    elementPresented(amountTransfer)
                    elementPresented(submitButton)
                    elementPresented(cancel)
                    elementPresented(transferNote)
                }
            }
        }

        openPage<AtmWalletPage>(driver)

        step("Go into Main wallet -> Move Check elements") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                e {
                    chooseWallet(mainWallet.name)
                    chooseToken(CC)
                    click(move)
                }
                assert {
                    elementPresented(amountTransfer)
                    elementPresented(submitButton)
                    elementPresented(cancel)
                    elementPresented(transferNote)
                }
            }
        }

    }

    @TmsLink("ATMCH-2533")
    @Test
    @DisplayName("Movement from OTF to Main wallet - Validation")
    fun movementFromOTFToMainWalletValidation() {
        val amount = "1.${RandomStringUtils.randomNumeric(8)}"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_EIGHTH
        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        prerequisite {
            addCurrencyCoinToWallet(user, amount, mainWallet)
            moveCurrencyCoinFromMainToOTFWallet(user, amount, mainWallet)
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            step("Go into СС token in OTF wallet and check validation messages") {
                chooseWallet(otfWallet.name)
                chooseToken(CC)
                e {
                    click(move)
                }
                assert {
                    elementIsDisabled("Submit button")
                }
                e {
                    select(selectWallet, mainWallet.name)
                }
                assert {
                    elementIsDisabled("Submit button")
                }
                e {
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, "#$%^&&*^&")
                    assert {
                        elementContainsValue(moveTokenQuantity, "0")
                    }
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, "TEST")
                    assert {
                        elementContainsValue(moveTokenQuantity, "0")
                    }
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, amount)

                    click(submitButton)
                    click(privateKey)
                    sendKeys(
                        privateKey,
                        mainWallet.secretKey
                    )
                    click(confirmPrivateKeyButton)
                }

                assert {
                    elementContainingTextPresented("INVALID KEY")
                }
                e {
                    click(privateKey)
                    deleteData(privateKey)
                    sendKeys(
                        privateKey,
                        otfWallet.secretKey
                    )
                    click(confirmPrivateKeyButton)

                    val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementWithTextPresented(" Wrong code ")
                }
            }

        }

    }

    @TmsLink("ATMCH-2524")
    @Test
    @DisplayName("Movement from Main to OTF wallet - Validation")
    fun movementFromMainToOTFWalletValidation() {
        val amount = "1.${RandomStringUtils.randomNumeric(8)}"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_SEVENTH
        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet

        prerequisite {
            addCurrencyCoinToWallet(user, "10", mainWallet)
        }

        val balance = with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            getBalanceFromWalletForToken(CC, mainWallet.name).toBigDecimal()
        }
        step("Go into СС token in MAIN wallet and check validation messages") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                chooseWallet(mainWallet.name)
                chooseToken(CC)
                e {
                    click(move)
                }
                assert {
                    elementIsDisabled("Submit button")
                }
                e {
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, "#$%^&&*^&")
                    assert {
                        elementContainsValue(moveTokenQuantity, "0")
                    }
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, "TEST")
                    assert {
                        elementContainsValue(moveTokenQuantity, "0")
                    }
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, "0")
                    assert {
                        elementWithTextPresented(" Entered token quantity is below min value required to proceed with this order ")
                    }
                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, (balance + BigDecimal("1")).toString())
                    assert {
                        elementWithTextPresented(" Not enough ${CC.tokenSymbol} to transfer ")
                    }

                    deleteData(moveTokenQuantity)
                    sendKeys(moveTokenQuantity, amount)

                    click(submitButton)
                    click(privateKey)
                    sendKeys(
                        privateKey,
                        otfWallet.secretKey
                    )
                    click(confirmPrivateKeyButton)
                }

                assert {
                    elementContainingTextPresented("INVALID KEY")
                }
                e {
                    click(privateKey)
                    deleteData(privateKey)
                    sendKeys(
                        privateKey,
                        mainWallet.secretKey
                    )
                    click(confirmPrivateKeyButton)

                    val code = if (OAuth.generateCode(user.oAuthSecret) == "123456") "123457" else "123456"
                    sendKeys(atmOtpConfirmationInput, code)
                    click(atmOtpConfirmationConfirmButton)
                }
                assert {
                    elementWithTextPresented(" Wrong code ")
                }
            }

        }
    }
}

