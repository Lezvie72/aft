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
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User with OTF TVE MANAGER ROLE checks all links")
class AdminRoleOtfTveManagerChecksAllLinks: BaseTest() {

    val user1 = Users.ATM_USER_OTF_TVE_MANAGER_ROLE
    @ResourceLock(Constants.ATM_USER_OTF_TVE_MANAGER_ROLE)
    @TmsLink("ATMCH-5934")
    @Test
    @DisplayName("User with OTF TVE MANAGER ROLE checks all links")
    fun userWithOtfTveManagerRoleChecksTheLinks () {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            allTabsAreDisplayedAndOpensInEditMode()
            changeToggleStatus("RFQ")
            checkingTogglesStatusAndSwitchingToCorrect()
        }
    }
}
