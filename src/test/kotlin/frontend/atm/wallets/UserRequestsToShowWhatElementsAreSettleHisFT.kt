package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CoinType.FT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmMarketplacePage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("User requests to show what elements are settle his FT")
class UserRequestsToShowWhatElementsAreSettleHisFT : BaseTest() {

    @TmsLink("ATMCH-2813")
    @Test
    @DisplayName("Elements are settlement FT")
    fun elementsAreSettlementFT() {
        val user = Users.ATM_USER_2MAIN_WALLET
        val mainWallet = user.mainWallet

        prerequisite {
            addCurrencyCoinToWallet(user, "1000", mainWallet)
        }
        openPage<AtmMarketplacePage>(driver) { submit(user) }.buyTokenNew(FT, "1", user, mainWallet)

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            chooseWallet(mainWallet.name)
            setDisplayZeroBalance(true)
            e {
                click(items)
            }
            assert {
                //Here was " BAR # 1 "
                elementWithTextPresented(" TOTAL WEIGHT ")
            }
        }
    }

}