package frontend.atm.validators

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
@Feature("Validator")
@Story("User Validator checks the tabs")
class UserValidatorChecksTabs: BaseTest(){

    val user1 = Users.ATM_USER_VALIDATOR_ROLE
    val validatorsParticipantRole = "Participant, Validator"

    @ResourceLock(Constants.ATM_USER_VALIDATOR_ROLE)
    @TmsLink("ATMCH-5372")
    @Test
    @DisplayName("User Validator checks the tabs")
    fun userValidatorChecksTheTabs() {

        with(utils.helpers.openPage<AtmProfilePage>(driver) { submit(user1) }) {
            step("User Validator checks his company participant role") {
                checksInformationSection(validatorsParticipantRole)
            }
            step("User Validator checks available sections on the platform") {
                checksAvailableSectionsOnThePlatform()
            }
            step("User Validator goes to validators page and adds node") {
                goesToValidatorsPageAndAddsNode()
            }
        }
    }
}
