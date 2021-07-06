package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.hamcrest.MatcherAssert.assertThat
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
class FinancialManagementTokens : BaseTest() {


    @TmsLink("ATMCH-4259")
    @Test
    @DisplayName("Adm.platform.Tokens. Transfer fee distribution. Checking the section by NOT finance manager.")
    fun checkingTheSectionByNotFinanceManager() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_USER_VIEWER_ROLE) }) {
            assert {
                elementNotPresented(addToken)
                elementNotPresented(editToken)
                elementNotPresented(transferFeeDistribution)
            }
        }
    }

    @TmsLink("ATMCH-4256")
    @Test
    @DisplayName("Adm.platform.Tokens. Transfer fee distribution. Check the interface.")
    fun transferFeeDistributionCheckInterface() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(addToken)
                elementPresented(editToken)
                elementPresented(transferFeeDistribution)
            }
            e {
                click(transferFeeDistribution)
            }
            assert {
                elementPresented(save)
                elementPresented(closeTransferFeeDistPopup)
                elementContainingTextPresented("Validator share")
                elementContainingTextPresented("Platform share")
                elementContainingTextPresented("Date")
                elementContainingTextPresented("Approver ID")
                elementContainingTextPresented("Approver e-mail")
            }
        }
    }

    @TmsLink("ATMCH-4280")
    @Test
    @DisplayName("Adm.platform.Tokens. Transfer fee distribution. Change validator share, specifying invalid values.")
    fun transferFeeDistributionValidatorShareInvalidValues() {
        with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val errorText = "The field must be less than 100"
            val errorText1 = "Field is required"
            val errorText2 = "The field must be greater than 0"
            assert {
                elementPresented(addToken)
                elementPresented(editToken)
                elementPresented(transferFeeDistribution)
            }
            e {
                click(transferFeeDistribution)
                sendKeys(validatorShare, "150")
            }
            assertThat(
                "Expected error text: $errorText",
                validatorShare.errorText == errorText
            )
            e {
                validatorShare.delete()
            }
            assertThat(
                "Expected error text: $errorText1",
                validatorShare.errorText == errorText1
            )
            e {
                sendKeys(validatorShare, "-15")
            }
            assertThat(
                "Expected error text: $errorText2",
                validatorShare.errorText == errorText2
            )
        }
    }
}