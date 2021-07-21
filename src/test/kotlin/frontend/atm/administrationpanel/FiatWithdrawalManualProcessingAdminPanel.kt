package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminFiatWithdrawalPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@TmsLink("ATMCH-1420")
@Epic("Frontend")
@Feature("Administration panel")
@Story("Fiat withdrawal (manual processing) / Admin panel")
class FiatWithdrawalManualProcessingAdminPanel : BaseTest() {

    @TmsLink("ATMCH-2519")
    @Test
    @DisplayName("Fiat withdraw (manual processing). Interface.")
    fun fiatWithdrawManualProcessingInterface() {
        with(openPage<AtmAdminFiatWithdrawalPage>(driver) { submit(Users.ATM_USER_FINANCIAL_MANAGER) }) {
            step("User checks that the fiat money withdrawal statuses are displayed") {
                checkingTheDisplayOfFiatMoneyWithdrawalStatuses()
            }
            step("User checks that the elements are displayed in the popup for fiat withdraw section") {
                checksThatTheElementsAreDisplayedInThePopupForFiatWithdrawSection()
            }
        }
    }
}
