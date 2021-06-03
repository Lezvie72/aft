package frontend.atm.issuances

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import io.qameta.allure.Story
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import pages.atm.AtmAdminTokensPage
import pages.atm.AtmIssuancesPage
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Issuances")
class IssuancesTests : BaseTest(){

    @TmsLink("ATMCH-4671")
    @Test
    @DisplayName("CC,VT,IT automatic linkage having no keys")
    fun automaticLinkageHaveNoKeys() {
        with(openPage<AtmIssuancesPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
            assert {
                elementContainingTextPresented("You do not have suitable issuer wallet keys. Please")
                elementContainingTextPresented("register")
            }
        }
    }

    @TmsLink("ATMCH-4672")
    @Test
    @DisplayName("CC,VT,IT automatic linkage having 1 key")
    fun automaticLinkageHaveOneKey() {
        with(openPage<AtmIssuancesPage>(driver) { submit(Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS) }) {
            assert {
                elementContainingTextNotPresented("You are allowed to partially manage the smart contract")
            }
        }
    }
}