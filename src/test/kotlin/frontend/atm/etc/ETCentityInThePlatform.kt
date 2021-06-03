package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.helpers.FileHelper
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step


@Epic("Frontend")
@Feature("ETC")
@Story("ETC entity in the platform")
class ETCentityInThePlatform : BaseTest() {


    @TmsLink("ATMCH-4356")
    @Test
    @DisplayName("ETC. Token unavailability for non-ETC companies")
    fun etcTokenUnavailabilityForNonETCCompanies() {
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val mainWallet = user.mainWallet
        val otfWallet = user.otfWallet
        step("User go to Trading, check available operation") {
            with(openPage<AtmProfilePage>(driver) { submit(user) }) {
                e {
                    click(trading)
                }
                assert {
                    elementContainingTextPresented("Blocktrade")
                    elementContainingTextPresented("Streaming")
                    elementContainingTextPresented("RFQ")
                    elementContainingTextPresented("Marketplace")
                }
            }
        }
        step("User go to Marketplace, check token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                assert {
                    elementContainingTextPresented(IT.tokenName)
                    elementContainingTextPresented(CC.tokenName)
                    elementContainingTextPresented(FT.tokenName)
                    elementContainingTextPresented(VT.tokenName)
                }
            }
            step("User go to wallet, check token") {
                with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                    e {
                        chooseWallet(mainWallet.name)
                        setDisplayZeroBalance(true)
                    }
                    assert {
                        elementContainingTextPresented(IT.tokenName)
                        elementContainingTextPresented(CC.tokenName)
                        elementContainingTextPresented(FT.tokenName)
                        elementContainingTextPresented(VT.tokenName)

                    }
                    openPage<AtmWalletPage>(driver)
                    e {
                        chooseWallet(otfWallet.name)
                        setDisplayZeroBalance(true)
                    }
                    assert {
                        elementContainingTextPresented(IT.tokenName)
                        elementContainingTextPresented(CC.tokenName)
                        elementContainingTextPresented(FT.tokenName)
                        elementContainingTextPresented(VT.tokenName)

                    }
                }
            }
        }

    }

    @TmsLink("ATMCH-4363")
    @Test
    @DisplayName("ETC. Token availability for ETC companies")
    fun etcTokenAvailabilityForETCcompanies() {
        val user = Users.ATM_USER_FOR_ETC_TOKENS_ONE
        val mainWallet = user.mainWallet

        step("User go to Marketplace, check token") {
            with(openPage<AtmMarketplacePage>(driver) { submit(user) }) {
                assert {
                    elementContainingTextNotPresented(IT.tokenName)
                    elementContainingTextNotPresented(CC.tokenName)
                    elementContainingTextNotPresented(FT.tokenName)
                    elementContainingTextNotPresented(VT.tokenName)
                    elementContainingTextPresented(ETC.tokenName)
                }
            }
            step("User go to wallet, check token") {
                with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                    e {
                        chooseWallet(mainWallet.name)
                        setDisplayZeroBalance(true)
                    }
                    assert {
                        elementContainingTextNotPresented(IT.tokenName)
                        elementContainingTextNotPresented(CC.tokenName)
                        elementContainingTextNotPresented(FT.tokenName)
                        elementContainingTextNotPresented(VT.tokenName)
                        elementContainingTextPresented(ETC.tokenName)
                    }
                }
            }
            step("User go to wallet, check type for wallet") {
                with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                    e {
                        click(registerWallet)
                    }
                    assert {
                        elementNotPresented(otfWalletRadioButton)
                        elementPresented(mainWalletRadioButton)
                    }
                }
            }
        }

    }
}

