package frontend.atm.onboarding

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
import pages.atm.AtmAdminInvitesPage
import pages.atm.AtmAdminPage
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import pages.core.actions.AssertActions
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Invitation")
class Invitation : BaseTest() {

    @TmsLink("ATMCH-689")
    @Test
    @DisplayName("Validation field of sending invitations to register")
    fun validationFieldOfSendingInvitationToReg() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(sendInviteButton)
            }
            assert {
                validateField(emailForInvitation, AssertActions.EMAIL_VALIDATION, "Invalid e-mail format", companyId)
            }
            e {
                sendKeys(emailForInvitation, Users.ATM_USER_KYC0.email)
                companyId.sendAndSelect("autotesst", "autotesst", this@with)
                click(sendButton)
            }
            assert {
                elementWithTextPresented("Email already exists")
            }
            e {
                click(cancelButton)
            }
            sendInvitation(user.email, false)
            checkEmail(user.email)
        }
    }

    @TmsLink("ATMCH-690")
    @Test
    @DisplayName("Delete invitation from the list of sent invitations")
    fun delInvitationFromListOfInvitation() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
            nonCriticalWait {
                untilInvisibility(sendButton)
            }
            e {
                sendKeys(search, user.email)
                click(deleteInvite)
            }
            checkEmailDelete(user.email)
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefForNewUserATM(user.email)
            driver.navigate().to(href)
            e {
                assert {
                    elementWithTextPresented("You must have valid invite to register!")
                }
            }
        }
        with(openPage<AtmAdminPage>(driver)) {
            clickInvites()
        }
        with(AtmAdminInvitesPage(driver)) {
            e {
                sendInvitation(user.email, false)
            }
            openPage<AtmHomePage>(driver)
            val since = LocalDateTime.now(ZoneOffset.UTC)
            val hrefSec = GmailApi.getHrefForNewUserATM(user.email, since)
            driver.navigate().to(hrefSec)
            e {
                assert {
                    elementWithTextPresented("Create your profile")
                    elementWithTextPresented("New account")
                }
            }
        }
    }

    @TmsLink("ATMCH-128")
    @Test
    @DisplayName("Sending invitations to register on the platform")
    fun sendingInvitationsToRegisterOnThePlatform() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                assert { elementPresented(inviteTable) }
                assert { elementPresented(sendInviteButton) }
                click(sendInviteButton)
                assert { elementPresented(emailForInvitation) }
                assert { elementPresented(kycPassedCheckbox) }
                click(cancelButton)
                sendInvitation(user.email, true)
            }
            val href = GmailApi.getHrefForNewUserATM(user.email)
            with(openPage<AtmLoginPage>(driver) { submit(user) }){
                e {
                    driver.navigate().to(href)
                    fillRegForm()
                    assert { urlEndsWith("/profile/info") }
                }
            }
        }
    }

}