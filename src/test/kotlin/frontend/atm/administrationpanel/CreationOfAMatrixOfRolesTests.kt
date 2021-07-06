package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.*
import models.CoinType.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmMarketplacePage
import pages.atm.AtmProfilePage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User roles")
class CreationOfAMatrixOfRolesTests : BaseTest() {

    private val maturityDate = IT.maturityDateMonthNumber

    @ResourceLock(Constants.ROLE_USER_ETC_TOKEN)
    @TmsLink("ATMCH-5375")
    @Test
    @Issue("ATMCH-5774")
    @DisplayName(
        "The user is an Employee of a company with a switched-on “ETC company” feature in Admin Panel." +
                "\n ETC tokens are added to the Platform."
    )
    fun employeeUserSwitchedToETCTokensPage() {
        val etcEmployee = Users.ATM_USER_FOR_ETC_TOKENS
        with(openPage<AtmProfilePage>(driver) { submit(etcEmployee) }) {
            assertThat(
                "Unexpected participant role", getParticipantRole(),
                equalTo("Participant, ETC company")
            )
            assert {
                elementPresented(trading)
                elementPresented(orders)
                elementPresented(wallets)
                elementPresented(explorer)
            }
            assert {
                elementNotPresented(validator)
                elementNotPresented(issuances)
            }
        }
        with(openPage<AtmMarketplacePage>(driver)) {
            val etc = tokensList.content.map { it.tokenType.text }.toSet()
            assertThat("Unexpected token type", etc, containsInAnyOrder("ETC"))
        }
    }

    @ResourceLock(Constants.ROLE_USER_ETC_TOKEN)
    @TmsLink("ATMCH-5374")
    @Test
    @DisplayName(
        "The user is an Employee of a company with a switched-on “Industrial” feature in Admin Panel." +
                "\n IT tokens are added to the Platform."
    )
    fun employeeUserSwitchedOnIndustrialFeature() {
        val user = Users.ATM_USER_FOR_INDUSTRIAL_COMPANY
        val wallet = user.mainWallet
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            assertThat(
                "Unexpected participant role", getParticipantRole(),
                equalTo("Participant, Industrial")
            )
            assert {
                elementPresented(trading)
                elementPresented(orders)
                elementPresented(wallets)
                elementPresented(explorer)
            }
            assert {
                elementNotPresented(validator)
                elementNotPresented(issuances)
            }
        }
        with(openPage<AtmMarketplacePage>(driver)) {
            val tokens = (tokensList.content.map { it.tokenType.text }.toSet().sorted()).toString()
            val expectedTokens = listOf("INDUSTRIAL TOKEN", "VALIDATION TOKEN", "FRACTIONAL TOKEN", "CURRENCY TOKEN")
                .sorted().toString()
            assertThat(
                "One or more tokens type are incorrect", tokens, equalTo(expectedTokens)
            )
            buyOrReceiveToken(IT, "1",user, wallet)
            assert {
                elementContainingTextPresented("Order submitted successfully")
            }
        }
    }

}