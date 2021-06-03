package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pages.atm.AtmWalletPage
import utils.helpers.Users
import utils.helpers.openPage


@Epic("Frontend")
@Feature("Wallets")
@Story("Replenishment detail (update wallet id display)")
class ReplenishmentDetail : BaseTest() {

    @TmsLink("ATMCH-2331")
    @Test
    @DisplayName("Wallet ID. Replenishment details display for Main and OTF wallets.")
    fun walletIdReplenishmentDetailsDisplayForMainAndOtfWallets() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        val wallet = user.mainWallet
        val walletOtf = user.otfWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            e {
                click(showReplenishmentDetails)
            }
            assert {
                elementWithTextPresented("REPLENISHMENT DETAILS")
                elementWithTextPresented(" WALLET NAME ")
                elementWithTextPresented(" PUBLIC KEY ")
                elementWithTextPresented(" WALLET ID ")
                elementPresented(copyWalletIDToClipboard)
                elementPresented(saveQrCode)
                elementPresented(cancelButton)
            }
        }

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(walletOtf.name)
            e {
                click(showReplenishmentDetails)
            }
            assert {
                elementWithTextPresented("REPLENISHMENT DETAILS")
                elementWithTextPresented(" WALLET NAME ")
                elementWithTextPresented(" PUBLIC KEY ")
                elementWithTextPresented(" WALLET ID ")
                elementPresented(copyWalletIDToClipboard)
                elementPresented(saveQrCode)
                elementPresented(cancelButton)
            }
        }

    }

    @TmsLink("ATMCH-2303")
    @Test
    @DisplayName("Wallet ID. Replenishment details. Interface.")
    fun walletIDReplenishmentDetailsInterface() {
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val wallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            chooseToken(CoinType.FIAT)
            e {
                click(showReplenishmentDetails)
            }
            assert {
                elementWithTextPresentedIgnoreCase("REPLENISHMENT DETAILS")
                elementWithTextPresentedIgnoreCase("WALLET NAME")
                elementWithTextPresentedIgnoreCase("PUBLIC KEY")
                elementWithTextPresentedIgnoreCase("WALLET ID")
                elementPresented(copyWalletIDToClipboard)
                elementPresented(saveQrCode)
                elementPresented(cancelButton)
            }
        }
    }

    @TmsLink("ATMCH-2305")
    @Test
    @DisplayName("Wallet ID. Replenishment details. Functional.")
    fun walletIDReplenishmentDetailsFunctional() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET

        val wallet = user.mainWallet

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(wallet.name)
            e {
                click(showReplenishmentDetails)
                click(copyWalletIDToClipboard)
            }
            assert {
                elementWithTextPresented("REPLENISHMENT DETAILS")
                elementPresented(copyWalletIDToClipboard)
                elementPresented(saveQrCode)
            }
            e {
                click(cancelButton)
                until("dialog REPLENISHMENT DETAILS is gone", 15) {
                    check {
                        isElementGone(saveQrCode)
                    }
                }
            }
            assert {
                elementWithTextNotPresented("REPLENISHMENT DETAILS")
            }
//TODO save QR code
        }
    }
}

