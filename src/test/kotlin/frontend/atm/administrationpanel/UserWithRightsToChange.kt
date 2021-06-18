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
import org.junit.jupiter.api.parallel.ResourceLocks
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

    val user1 = Users.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE
    val user2 = Users.ATM_USER_FINANCE_MANAGER_ROLE
    val user3 = Users.ATM_USER_OTF_TVE_MANAGER_ROLE
    @ResourceLocks(
        ResourceLock(Constants.ATM_USER_PLATFORM_ADMINISTRATOR_ROLE),
        ResourceLock(Constants.ATM_USER_FINANCE_MANAGER_ROLE),
        ResourceLock(Constants.ATM_USER_OTF_TVE_MANAGER_ROLE)
    )
    @TmsLink("ATMCH-4091")
    @Test
    @DisplayName("User with rights to change")
    fun userWithRightsToChange() {
        with(utils.helpers.openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            togglesAreDisplayed()
            checkingTogglesInitialStatus()
            changeStreamingStatusAndCheckResult()
        }
    }
}