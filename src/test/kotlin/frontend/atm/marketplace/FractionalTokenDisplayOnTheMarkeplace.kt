package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.FT
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminPaymentsPage
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.math.BigDecimal

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Fractional Token display on the marketplace")
class FractionalTokenDisplayOnTheMarkeplace : BaseTest() {

    @TmsLink("ATMCH-1974")
    @Test
    @DisplayName("Fractionalized Token. Page content checking")
    fun fractionalizedTokenPageContentChecking() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(trading)
                click(marketPlace)
            }
            assert {
                urlEndsWith("/trading/market")
            }
        }
        with(AtmMarketplacePage(driver)) {
            assert {
                elementWithTextPresented(FT.tokenSymbol)
            }
            chooseToken(FT)
            assert {
                elementContainingTextPresented("Token type ")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Buy")
                elementContainingTextPresented("Issuer description")
//                elementContainingTextPresented("Deal types")
//                elementContainingTextPresented("Transfer")
                elementContainingTextPresented("Charged in")
                elementContainingTextPresented("Fee rate (%)")
                elementContainingTextPresented("Floor")
                elementContainingTextPresented("Cap")
//                elementContainingTextPresented("Document #1")
                elementPresented(newOrderButton)

            }
        }
    }

    @TmsLink("ATMCH-2286")
    @Test
    @DisplayName("Fractionalized token redemption to CC (Buyback). Wrong signature")
    fun fractionalizedTokenRedemptionWrongSignature() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val userWallet = user.mainWallet
        val tokenSymbolName = FT
        val amount = "0.01"
        val manualSecretKey = RandomStringUtils.randomAlphanumeric(128).toLowerCase()


        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addTokenAmountExact(
            tokenSymbolName,
            "Ticker symbol CC",
            userWallet,
            user,
            amount
        )

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {

            chooseWallet(userWallet.name)
            chooseToken(tokenSymbolName)
            e {
                click(redemption)
                select(selectWallet, userWallet.publicKey)
                sendKeys(tokenQuantity, amount)
                click(submitButton)
            }
            signAndSubmitMessage(manualSecretKey, user.oAuthSecret)
            assert {
                elementContainingTextPresented("Invalid key")
            }
        }
    }

    @TmsLink("ATMCH-2294")
    @Test
    @DisplayName("Redemption Fractionalized token to CC (Buyback) Viewing the result of recording in the blockchain")
    fun fractionalizedTokenRedemptionToBuyback() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET

        val userWallet = user.mainWallet
        val tokenSymbolName = FT
        val fiatSymbolName = CC
        val amount = "1"

        openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }.addTokenAmountExact(
            tokenSymbolName,
            "Ticker symbol CC",
            userWallet,
            user,
            amount
        )

        val preFTBalance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
            tokenSymbolName,
            userWallet.name
        )
        val preFiatBalance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
            fiatSymbolName,
            userWallet.name
        )

        openPage<AtmWalletPage>(driver) { submit(user) }
            .redeemToken(tokenSymbolName, userWallet, amount, "", user)

        val postFiatBalance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
            fiatSymbolName,
            userWallet.name
        )
        val postFTBalance = openPage<AtmWalletPage>(driver) { submit(user) }.getBalanceFromWalletForToken(
            tokenSymbolName,
            userWallet.name
        )

        assertThat(
            "Pre Fiat Balance and Post Fiat Balance changed amount by $amount",
            BigDecimal(preFTBalance),
            equalTo(BigDecimal(postFTBalance) + BigDecimal(amount))
        )

        assertThat(
            "Pre Fiat Balance and Post Fiat Balance changed amount by $amount",
            BigDecimal(preFiatBalance),
            equalTo(BigDecimal(postFiatBalance) - BigDecimal(amount))
        )
    }

}
