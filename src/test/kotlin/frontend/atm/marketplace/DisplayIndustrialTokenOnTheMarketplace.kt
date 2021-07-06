package frontend.atm.marketplace

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.AtmMarketplacePage
import ru.yandex.qatools.htmlelements.element.Button
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.to

@Tags(Tag(TagNames.Epic.MARKETPLACE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Marketplace")
@Story("Display industrial token on the marketplace")
class DisplayIndustrialTokenOnTheMarketplace : BaseTest() {

    @TmsLink("ATMCH-2777")
    @Test
    @DisplayName("Industrial tokens. Interface.")
    fun interfaceIndustrialTokens() {
        with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
            val coinButton = wait {
                untilPresented<WebElement>(By.xpath("//div[contains(text(), '${CoinType.IT.tokenSymbol}')]"))
            }.to<Button>("Coin button '${CoinType.IT.tokenSymbol}'")

            assert {
                elementPresented(coinButton)
            }

            e {
                chooseToken(CoinType.IT)
            }

            assert {
                elementContainingTextPresented("Token type ")
//                elementContainingTextPresented("Ticker")
                elementContainingTextPresented("Issuer")

                elementContainingTextPresented("Maturity date")

                elementPresented(newOrderButton)
                elementPresented(datesRadio)

                elementContainingTextPresented("Supply amount")
                elementContainingTextPresented("Issuer description")

                elementPresented(detailsButton)


            }
        }
    }

    @TmsLink("ATMCH-2778")
    @Test
    @DisplayName("Industrial tokens. Functional.")
    fun tabsIndustrialTokens() {
        with(openPage<AtmMarketplacePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            val coinButton = wait {
                untilPresented<WebElement>(By.xpath("//div[contains(text(), '${CoinType.IT.tokenSymbol}')]"))
            }.to<Button>("Coin button '${CoinType.IT.tokenSymbol}'")

            assert {
                elementPresented(coinButton)
            }

            e {
                chooseToken(CoinType.IT)
            }

            assert {
                elementPresented(detailsButton)
                elementPresented(datesRadio)

                elementNotPresented(detailsText)
            }

            e {
                click(detailsButton)
                click(datesRadio)
            }

            assert {
                elementPresented(detailsButton)
                elementPresented(datesRadio)

                elementPresented(detailsText)
            }
        }
    }
}
