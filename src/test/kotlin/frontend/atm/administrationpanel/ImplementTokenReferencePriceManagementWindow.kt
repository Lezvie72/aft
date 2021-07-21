package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminTokensPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@TmsLink("ATMCH-2965")
@Epic("Frontend")
@Feature("Administration panel")
@Story("Implement token reference price management window")
class ImplementTokenReferencePriceManagementWindow : BaseTest() {
    private val coefficientValue = "2"
    @TmsLink("ATMCH-4766")
    @Test
    @DisplayName("Platform. USD equivalent for tokens. Add equivalent for token. From market data")
    fun platformUSDEquivalentForTokensAddEquivalentForTokenFromMarketData() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_USER_FINANCIAL_MANAGER) }) {
            step("User works with USD equivalent settings window") {
                userWorksWithUSDEquivalentSettingsWindow(coefficientValue)
            }
        }
    }

    @TmsLink("ATMCH-4767")
    @Test
    @DisplayName("Platform. USD equivalent for tokens. Add equivalent for IT token")
    fun platformUSDEquivalentForTokensAddEquivalentForITToken() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_USER_FINANCIAL_MANAGER) }) {
            step("User works with USD equivalent settings window for IT token") {
                userWorksWithUSDEquivalentSettingsWindowForITToken(coefficientValue)
            }
        }
    }

    @TmsLink("ATMCH-4765")
    @Test
    @DisplayName("Platform. USD equivalent for tokens. Add equivalent for token. Fixed")
    fun platformUSDEquivalentForTokensAddEquivalentForTokenToken() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_USER_FINANCIAL_MANAGER) }) {
            step("User works with USD equivalent settings window for token. Fixed") {
                userWorksWithUSDEquivalentSettingsWindowForTokenFixed(coefficientValue)
            }
        }
    }
}
