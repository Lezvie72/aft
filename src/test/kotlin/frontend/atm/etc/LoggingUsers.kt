package frontend.atm.etc

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
import pages.atm.*
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administrator's logs")
@Story("Admin checks logs in kibana")
class LoggingUsers: BaseTest(){

    val user1 = Users.ATM_USER_FOR_KIBANA

    @ResourceLock(Constants.ATM_USER_FOR_KIBANA)
    @TmsLink("ATMCH-1690")
    @Test
    @DisplayName("Admin checks logs in kibana")
    fun adminChecksLogsInKibana() {

        with(utils.helpers.openPage<AtmAdminKibanaPage>(driver) { submit(user1) }) {
            step("Check the General tracking of actions") {
            Thread.sleep(20000)
            }
        }
    }
}
