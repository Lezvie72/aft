package frontend.atm.etc

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.ETC
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.ResourceLocks
import pages.atm.AtmIssuancesPage
import pages.atm.AtmWalletPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.ETC.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("ETC")
@Story("ETC entity in the platform")
class ETCTransferFromIssuerWalletToETCclient : BaseTest() {

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_2FA)
    )
    @TmsLink("ATMCH-4393")
    @Test
    @DisplayName("ETC. Token unavailability for non-ETC companies")
    fun etcTokenUnavailabilityForNonETCCompanies() {
        val amountToTransfer = BigDecimal("0.0001")
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = etcUser.walletList[0]

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }
        openPage<AtmWalletPage>(driver).logout()

        val inCirculationBefore = step("User go to Issuance, save in circulation before operation") {
            openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getInCirculation(ETC)
        }

        val (_, _) = step("User go to Trading, check available operation") {
            with(openPage<AtmWalletPage>(driver) { submit(etcIssuer) }) {
                transferFromWalletToWallet(ETC, etcWallet, wallet, amountToTransfer.toString(), "", "etc", etcIssuer)
            }
        }

        openPage<AtmWalletPage>(driver)

        val inCirculationAfter = step("User go to Issuance, save in circulation after operation") {
            openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getInCirculation(ETC)
        }
        openPage<AtmWalletPage>(driver).logout()

        val balanceWalletToAfter = step("User get balance from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }

        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + amountToTransfer, BigDecimal("0.01"))
        )

        assertThat(
            "Expected balance: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore + amountToTransfer, BigDecimal("0.01"))
        )
    }

    @ResourceLocks(
        ResourceLock(Constants.ROLE_USER_ETC_TOKEN_SECOND),
        ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_ETC_TOKEN_THIRD)
    )
    @TmsLink("ATMCH-4401")
    @Test
    @DisplayName("ETC token. Transfer from issuer wallet to ETC client (without 2FA)")
    fun etcTokenTransferFromIssuerWalletToETCclientWithout2FA() {
        val amountToTransfer = BigDecimal("0.0001")
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD
        val etcWallet = etcIssuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS_SECOND
        val wallet = etcUser.walletList[0]

        val balanceWalletToBefore = step("User get balance from wallet before operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }
        openPage<AtmWalletPage>(driver).logout()

        val (inCirculationBefore, _, _) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }

        step("User go to Trading, check available operation") {
            with(openPage<AtmWalletPage>(driver) { submit(etcIssuer) }) {
                transferFromWalletToWallet(ETC, etcWallet, wallet, amountToTransfer.toString(), "", "etc", etcIssuer)
            }
        }

        openPage<AtmWalletPage>(driver)

        val (inCirculationAfter, _, _) =
            step("User go to Issuance, Balance Supply,Circulation And Sale") {
                openPage<AtmIssuancesPage>(driver) { submit(etcIssuer) }.getBalanceSupplyCirculationAndSale(ETC)
            }
        openPage<AtmWalletPage>(driver).logout()

        val balanceWalletToAfter = step("User get balance from wallet after operation") {
            openPage<AtmWalletPage>(driver) { submit(etcUser) }.getBalance(ETC, wallet.name)
        }

        assertThat(
            "Expected balance: $inCirculationBefore, was: $inCirculationAfter",
            inCirculationAfter,
            closeTo(inCirculationBefore + amountToTransfer, BigDecimal("0.01"))
        )

        assertThat(
            "Expected balance: $balanceWalletToBefore, was: $balanceWalletToAfter",
            balanceWalletToAfter,
            closeTo(balanceWalletToBefore + amountToTransfer, BigDecimal("0.01"))
        )
    }


    @ResourceLock(Constants.ROLE_USER_ETC_TOKEN)
    @TmsLink("ATMCH-4417")
    @Test
    @DisplayName("ETC token. Transfer from issuer wallet to ETC client using another private key")
    fun etcTokenTransferFromIssuerWalletToEtcClientUsingAnotherPrivateKey() {
        val amountToTransfer = BigDecimal("0.0001")
        val etcIssuer = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA
        val etcWallet = etcIssuer.mainWallet

        val issuer = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
        val issuerWallet = issuer.mainWallet

        val etcUser = Users.ATM_USER_FOR_ETC_TOKENS
        val wallet = etcUser.walletList[0]


        step("Issuer make Transfer with another secret key") {
            with(openPage<AtmWalletPage>(driver) { submit(etcIssuer) }) {
                chooseWallet(etcWallet.name)
                chooseToken(ETC)
                e {
                    click(transferEtc)
                }
                waitWalletNumAreDisplayed(etcWallet)
                e {
                    select(fromWallet, etcWallet.publicKey)
                    sendKeys(toWallet, wallet.walletId)
                    sendKeys(amountTransfer, amountToTransfer.toString())
                    sendKeys(transferNote, "note")
                    click(submit)
                }
                signAndSubmitMessage(issuer, issuerWallet.secretKey)
                assert { elementContainingTextPresented("Invalid key") }
            }

        }
    }

}


