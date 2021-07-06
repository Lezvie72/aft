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
import pages.atm.AtmViewerAdminPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Admin with viewer role checks links")
class AdminRolesVIEWER: BaseTest(){

    val user1 = Users.ATM_USER_VIEWER_ROLE

    @ResourceLock(Constants.ATM_USER_VIEWER_ROLE)
    @TmsLink("ATMCH-5510")
    @Test
    @DisplayName("Admin with viewer role checks links")
    fun adminWithViewerRoleChecksLinks() {

        with(utils.helpers.openPage<AtmViewerAdminPage>(driver) { submit(user1) }) {
            step("Check all tabs are changed for admin with viewer role") {
                checkAllTabsAreChangedForAdminWithViewerRole()
            }
        }
    }
}
