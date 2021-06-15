package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminInvitesPage
import pages.atm.AtmAdminKycManagementPage
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Implement KYC applications management functionality in administrative panel")
class ImplementKYCApplicationsManagementFunctionalityInAdministrativePanel : BaseTest() {

    private fun createAndRegisterUser(kycPassed: Boolean = false): DefaultUser {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, kycPassed)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        return user
    }

    @TmsLink("ATMCH-1981")
    @Test
    @DisplayName("Changes to KYC model statuses.")
    fun changesToKycModelStatusTest() {
        val user = step("GIVEN user created") {
            createAndRegisterUser()
        }
        with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
            openKycReviewApplicationByEmail(user.email)
            assert {
                elementPresented(markKycAsPassedButton)
                elementPresented(clearKycStatusButton)
                elementPresented(blockKycButton)
            }
            e {
                click(markKycAsPassedButton)
            }
            assert {
                elementWithTextPresented("Mark KYC as passed")
                elementWithTextPresented("This action will give user a Green KYC status. Use it if user has undergone KYC procedure outside of Atomyze flow.")
                elementPresented(noteTextInput)
                elementPresented(confirmDialogButton)
                elementPresented(cancelDialogButton)
            }
            e {
                click(cancelDialogButton)
            }
            openKycReviewApplicationByEmail(user.email)
            e {
                click(clearKycStatusButton)
            }
            assert {
                elementWithTextPresented("Clear KYC status")
                elementWithTextPresented("This action will return user KYC status to Not started. User will be able to start a new KYC application.")
                elementPresented(noteTextFromSetStatusInput)
                elementPresented(confirmDialogButton)
                elementPresented(cancelDialogButton)
            }
            e {
                click(confirmDialogButton)
            }
            assertThat(
                "No error is presented on field Note",
                noteTextFromSetStatusInput.getAttribute("aria-invalid") == "true"
            )
            e {
                click(cancelDialogButton)
            }
            openKycReviewApplicationByEmail(user.email)
            e {
                click(blockKycButton)
            }
            assert {
                elementWithTextPresented("Block KYC")
                elementPresented(noteTextFromSetStatusInput)
                elementPresented(confirmDialogButton)
                elementPresented(cancelDialogButton)
            }
            val note = RandomStringUtils.randomAlphabetic(10)
            e {
                sendKeys(noteTextFromSetStatusInput, note)
                click(confirmDialogButton)
            }
            Thread.sleep(5_000)
            driver.navigate().refresh()
            val status = getKycStatusForUserByEmail(user.email)
            assertThat("Expected status: BLOCKED; actual: $status", status, Matchers.equalTo("BLOCKED"))
        }
    }

}