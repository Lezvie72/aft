package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import org.apache.commons.lang.RandomStringUtils.randomNumeric
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Industrial token receive request")
class IndustrialTokenReceiveRequest : BaseTest() {

    @TmsLink("ATMCH-2810")
    @Test
    @DisplayName("Industrial token receive request by one maturity date. Wrong Signature")
    fun negativeMultipleMaturityDateIndustrialToken() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val manualPublicKey = user.mainWallet.publicKey
        val manualSecretKey = randomAlphanumeric(128).toLowerCase()

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(CoinType.IT)

            assert {
                elementPresented(newOrderButton)
            }

            e {
                click(newOrderButton)
            }

            assert {
                elementPresented(allTokensButton)
            }

            e {
                click(allTokensButton)
                select(selectWallet, manualPublicKey)

                tokenMultipleQuantity.forEach {
                    deleteData(it)
                    sendKeys(it, "100")
                }
            }

            e {
                click(submitButton)
                click(privateKey)
                sendKeys(
                    privateKey,
                    manualSecretKey
                )
                click(confirmPrivateKeyButton)
            }

            assert {
                elementContainingTextPresented("INVALID KEY")
            }
        }
    }

    @TmsLink("ATMCH-2809")
    @Test
    @DisplayName("Industrial token receive request under one contract")
    fun multipleMaturityDateIndustrialToken() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val manualPublicKey = user.mainWallet.publicKey
        val manualSecretKey = user.mainWallet.secretKey

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(CoinType.IT)

            assert {
                elementPresented(newOrderButton)
            }

            e {
                click(newOrderButton)
            }

            assert {
                elementPresented(allTokensButton)
            }

            e {
                click(allTokensButton)
                select(selectWallet, manualPublicKey)

                tokenMultipleQuantity.forEach {
                    deleteData(it)
                    sendKeys(it, "100")
                }
            }

            e {
                click(submitButton)
                signAndSubmitMessage(manualSecretKey, user.oAuthSecret)
            }
        }
    }

    @TmsLink("ATMCH-2807")
    @Test
    @DisplayName("Industrial token receive request by one maturity date.")
    fun oneMaturityDateIndustrialToken() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val manualPublicKey = user.mainWallet.publicKey
        val manualSecretKey = user.mainWallet.secretKey


        // TODO: Maturity Date bug min/max
        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(CoinType.IT)

            assert {
                elementPresented(newOrderButton)
            }

            e {
                click(newOrderButton)
            }

            assert {
                elementPresented(allTokensButton)
                elementPresented(selectMaturityDate)
                elementPresented(selectWallet)
                elementPresented(tokenQuantity)

                elementPresented(submitButton)
                elementPresented(cancelButton)
            }

            e {
                select(selectMaturityDate, "092020")
                select(selectWallet, manualPublicKey)
                sendKeys(tokenQuantity, "100")
            }

            assert {
                elementContainingTextPresented("Available tokens to receive")
            }

            e {
                click(submitButton)
            }

            e {
                signAndSubmitMessage(manualSecretKey, user.oAuthSecret)
            }
        }
    }


    // TODO: ATMCH-2812 ???????????????? SMS-????????????, ?? ???????? ???????????? 2FA Google Auth
    @TmsLink("ATMCH-2812")
    @Test
    @DisplayName("Industrial token receive request by one maturity date. Wrong 2FA Google Auth")
    fun negative2FAMultipleMaturityDateIndustrialToken() {
        // TODO: 2FA sms
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val manualPublicKey = user.mainWallet.publicKey
        val manualSecretKey = user.mainWallet.secretKey

        val incorrectSmsCode = randomNumeric(6)

        with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
            chooseToken(CoinType.IT)

            assert {
                elementPresented(newOrderButton)
            }

            e {
                click(newOrderButton)
            }

            assert {
                elementPresented(allTokensButton)
            }

            e {
                click(allTokensButton)
                select(selectWallet, manualPublicKey)

                tokenMultipleQuantity.forEach {
                    deleteData(it)
                    sendKeys(it, "100")
                }
            }

            e {
                click(submitButton)
                click(privateKey)
                sendKeys(
                    privateKey,
                    manualSecretKey
                )
                click(confirmPrivateKeyButton)

                e {
                    sendKeys(atmOtpConfirmationInput, incorrectSmsCode)
                    click(atmOtpConfirmationConfirmButton)
                }
            }

            assert {
                elementContainingTextPresented("Wrong code")
            }
        }
    }
}
