package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import pages.atm.AtmAdminGeneralSettingsPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User with rights to change")
class UserWithRightsToChange : BaseTest() {
    private val admin1 = Users.ATM_ADMIN
    @ResourceLock(Constants.ROLE_ADMIN)
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change")
    fun userWithRightsToChange() {
        with(utils.helpers.openPage<AtmAdminGeneralSettingsPage>(driver) {submit(admin1)} ) {
            togglesAreDisplayed()
            checkingTogglesInitialStatus()
            changeStreamingStatusAndCheckResult()
        }
    }
}