package frontend.atm.issuances

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
@Feature("Issuer")
@Story("User Issuer checks tabs")
class UserIssuerChecksTabs: BaseTest(){

    val user1 = Users.ATM_USER_ISSUER_ROLE
    val user2 = Users.ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS
    val issuerParticipantRole = "Participant, Issuer"

    @ResourceLock(Constants.ATM_USER_ISSUER_ROLE)
    @TmsLink("ATMCH-5373")
    @Test
    @DisplayName("User Issuer checks tabs")
    fun userIssuerChecksTheTabs() {

        with(utils.helpers.openPage<AtmProfilePage>(driver) { submit(user1) }) {
            step("User Issuer checks his company participant role") {
                checksInformationSection(issuerParticipantRole)
            }
            step("User Issuer checks available sections on the platform") {
                issuerChecksAvailableSectionsOnThePlatform()
            }
            step("User Issuer goes to issuance page and checks that no any tokens") {
                goesToIssuerGoesToIssuancePageAndChecksThatPageIsOpen()
            }
        }
    }

    @ResourceLock(Constants.ROLE_USER_FOR_ACCEPT_IT_TOKEN)
    @TmsLink("ATMCH-5373")
    @Test
    @DisplayName("User Issuer checks linked tokens")
    fun userIssuerChecksTheLinkedTokens() {
        with(utils.helpers.openPage<AtmProfilePage>(driver) { submit(user2) }) {
            step("User Issuer goes to issuances page and checks linked tokens") {
                goesToIssuerGoesToIssuancePageAndSeeLinkedTokens()
            }
        }
    }
}
