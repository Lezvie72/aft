package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.FIAT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("CC details on the marketplace")
class CCDetailsOnTheMarketplace : BaseTest() {

    @TmsLink("ATMCH-854")
    @Test
    @DisplayName("Currency Token (СС) Data")
    fun currencyTokenData() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
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
                elementContainingTextPresented(CC.tokenName)
                elementContainingTextPresented("Token type")
                elementContainingTextPresented("Underlying asset")
                elementContainingTextPresented("Issuer")
                elementContainingTextPresented("Buy")
                elementContainingTextPresented("Sell")
                elementContainingTextPresented(FIAT.tokenSymbol)
            }
            e {
                chooseToken(CC)
            }
            assert {
                elementContainingTextPresented("Issuer description")
//                elementContainingTextPresented("Deal types")
                elementContainingTextPresented("Transfer")
                elementContainingTextPresented("Charged in")
                elementContainingTextPresented("Fee rate (%)")
                elementContainingTextPresented("Floor")
                elementContainingTextPresented("Cap")
//                elementContainingTextPresented("Document #1")
                elementPresented(newOrderButton)
            }
            e {
                click(newOrderButton)
            }
            assert {
                elementPresented(selectWallet)
                elementPresented(tokenQuantity)
                elementPresented(cancelButton)
                elementPresented(submitButton)
                elementContainingTextPresented("Available balance")
                elementContainingTextPresented("Amount to pay")

            }
        }
    }

}
