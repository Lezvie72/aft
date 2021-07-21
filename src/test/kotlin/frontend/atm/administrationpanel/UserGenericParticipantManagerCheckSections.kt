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
import pages.atm.AtmProfilePage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Profile page")
@Story("User with GENERIC PARTICIPANT MANAGER ROLE checks all links")
class UserGenericParticipantManagerCheckSections: BaseTest() {

    val user1 = Users.ATM_USER_GENERIC_PARTICIPANT_MANAGER_ROLE
    val issuerParticipantRole = "Participant"
    val usersEmployeeRole = "Manager"
    @ResourceLock(Constants.ATM_USER_GENERIC_PARTICIPANT_MANAGER_ROLE)
    @TmsLink("ATMCH-5371")
    @Test
    @DisplayName("User with GENERIC PARTICIPANT MANAGER ROLE checks all links")
    fun userGenericParticipantManagerCheckSections () {
        with(utils.helpers.openPage<AtmProfilePage>(driver) { submit(user1) }) {
            step("User Generic Participant Manager checks his company participant role") {
                checksInformationSection(issuerParticipantRole)
            }
            step("User Generic Participant Manager checks his company participant role") {
                checksUserInformationSection(usersEmployeeRole)
            }
            step("User Generic Participant Manager checks available sections on the platform") {
                genericParticipantUserChecksAvailableSectionsOnThePlatform()
            }
            step("User Generic Participant Manager checks available sections him") {
                checksAvailableSectionsForGenericParticipantManager()
            }
        }
    }
}