package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminTokensPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Tokens")
class Tokens : BaseTest() {


    @TmsLink("ATMCH-4282")
    @Test
    @DisplayName("Adm.platform.Tokens. Transfer fee distribution. Cancel change validator share.")
    fun tokensTransferFeeCancelChangeValidatorShare() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(addToken)
                elementPresented(editToken)
                elementPresented(transferFee)
            }
            e {
                click(transferFee)
            }
            val validatorShareValue = validatorShare.value
            e{
                validatorShare.delete()
                sendKeys(validatorShare, "90")
            }
            assert {
                elementPresented(save)
            }
            e{
                click(close)
                click(transferFee)
            }
            assertThat("Changes not applied", validatorShareValue, Matchers.containsString(validatorShare.value))
        }
    }


}