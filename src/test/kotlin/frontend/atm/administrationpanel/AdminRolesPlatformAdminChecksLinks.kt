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

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN), Tag(TagNames.Flow.DEBUG))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User with PLATFORM ADMIN ROLE checks all links")
class AdminRolesPlatformAdminChecksLinks: BaseTest() {

    val user1 = Users.PLATFORM_ADMIN_ROLE
    @ResourceLock(Constants.PLATFORM_ADMIN_ROLE)
    @TmsLink("ATMCH-5505")

    @Test
    @DisplayName("User with PLATFORM ADMIN ROLE checks all links")
    fun userWithPlatformAdminRoleChecksTheLinks () {
        with(openPage<AtmAdminGeneralSettingsPage>(driver) {submit(user1)} ) {
            allTabsAreDisplayedAndOpensInEditModeForAmlKycManager()
            changeToggleStatus("RFQ")
            checkingTogglesStatusAndSwitchingToCorrect()
        }
    }
}
