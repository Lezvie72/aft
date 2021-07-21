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
import pages.atm.AtmSuperAdminPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Admin with SUPER ADMIN ROLE checks links")
class AdminRolesSuperAdmin: BaseTest(){

    val user1 = Users.ATM_USER_SUPER_ADMIN_ROLE

    @ResourceLock(Constants.ATM_USER_SUPER_ADMIN_ROLE)
    @TmsLink("ATMCH-5509")
    @Test
    @DisplayName("Admin with SUPER ADMIN ROLE checks links")
    fun adminWithSuperAdminRoleChecksLinks() {

        with(utils.helpers.openPage<AtmSuperAdminPage>(driver) { submit(user1) }) {
            allTabsAreDisplayedAndOpensInEditModeForSuperAdmin()
        }
    }
}
