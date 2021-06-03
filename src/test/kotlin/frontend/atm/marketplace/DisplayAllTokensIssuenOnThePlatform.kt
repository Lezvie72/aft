package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.CC
import models.CoinType.VT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Display all tokens issuen on the platform (main page of the marketplace)")
class DisplayAllTokensIssuenOnThePlatform : BaseTest() {

    @TmsLink("ATMCH-862")
    @Test
    @DisplayName("Token's list. Marketplace.")
    fun tokensListMarketplace() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
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
                elementContainingTextPresented(VT.tokenName)
            }
            e {
                chooseToken(VT)
            }
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
}
