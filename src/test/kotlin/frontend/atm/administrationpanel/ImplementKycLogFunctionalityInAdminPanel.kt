package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.apache.commons.lang3.RandomStringUtils.random
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import pages.atm.AtmAdminKycManagementPage.StatusType.CLEAR
import pages.atm.AtmAdminKycManagementPage.StatusType.PASSED
import ru.yandex.qatools.htmlelements.element.Button
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users.Companion.ATM_ADMIN
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Implement KYC log functionality in admin panel")
class ImplementKycLogFunctionalityInAdminPanel : BaseTest() {

    private fun createAndRegisterUser(kycPassed: Boolean = false): Pair<DefaultUser, String> {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(ATM_ADMIN) }) {
            sendInvitation(user.email, kycPassed)
        }
        val since =
            LocalDateTime.now(ZoneOffset.UTC).plusHours(3).truncatedTo(ChronoUnit.SECONDS).toString().replace("T", " ")
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        return user to since
    }

    @TmsLink("ATMCH-1579")
    @Test
    @DisplayName("Journal KYC. Interface")
    fun journalKycInterface() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    click(applyFiltersButton)
                }
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                assert {
                    elementContainingTextPresented("LOG")
                    elementPresented(noteTextInput)
                    elementPresented(logList)
                    elementContainingTextPresented("Logged by ")
                    elementContainingTextPresented("New status ")
                    elementContainingTextPresented("Date ")
                    elementContainingTextPresented("Note ")

                }
            }
        }

    }

    @TmsLink("ATMCH-1579")
    @Test
    @DisplayName("Log. Adding a comment to entry in journal KYC")
    fun logAddingCommentToEntryInJournalKyc() {
        val noteText = random(8, true, true)
        val (user, _) = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    click(applyFiltersButton)
                }
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                    sendKeys(noteTextInput, noteText)
                    click(addButton)
                }
                checkNote(user.email, noteText)

            }
        }

    }

    @TmsLink("ATMCH-2023")
    @Test
    @DisplayName("Log.  Check status after sending invitation without marked checkbox Passed KYC")
    fun logCheckStatusAfterSendingInvitationWithoutMarkedCheckboxPassedKyc() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(false)
        }

        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    click(applyFiltersButton)
                }
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable("SYSTEM", "NOT STARTED", "")
            }
        }

    }

    @TmsLink("ATMCH-1814")
    @Test
    @DisplayName("Log.  Check status after receive a response from the KYC agent.")
    fun logCheckStatusAfterReceiveResponseFromTheKycAgent() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(false)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                openKycReviewApplicationByEmailAndSetName(user.email, "X-AUTOTEST-HAPPYPATH", "X-AUTOTEST-HAPPYPATH")
            }
        }
        step("User go to Profile and Start KYC") {
            with(openPage<AtmProfilePage>(driver) { submit(user) }) {
                e {
                    click(startKYC)
                    click(start)
                }
            }
        }

        step("Admin wait for change status for User check status and change status to APPROVE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    clickUntilElementIsPresented(applyFiltersButton, "VALIDATING", 120, 15)
                }

                val status = getKycStatusForUserByEmail(user.email)

                MatcherAssert.assertThat("Wrong status", status, Matchers.equalTo("VALIDATING"))

                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable("SYSTEM", "VALIDATING", "")
            }
        }

    }

    @Disabled("есть вопросики")
    @TmsLink("ATMCH-1813")
    @Test
    @DisplayName(" Log.  Check status after sending request for passing KYC")
    fun logCheckStatusAfterSendingRequestForPassingKYC() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                openKycReviewApplicationByEmailAndSetName(user.email, "X-AUTOTEST-HAPPYPATH", "X-AUTOTEST-HAPPYPATH")
            }
        }
        step("User go to Profile and Start KYC") {
            with(openPage<AtmProfilePage>(driver) { submit(user) }) {
                e {
                    click(startKYC)
                    click(start)
                }
            }
        }

        step("Admin wait for change status for User check status and change status to APPROVE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    clickUntilElementIsPresented(applyFiltersButton, "VALIDATING", 120, 15)
                }

                val status = getKycStatusForUserByEmail(user.email)

                MatcherAssert.assertThat("Wrong status", status, Matchers.equalTo("VALIDATING"))

                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable("SYSTEM", "VALIDATING", "")
            }
        }

    }

    @TmsLink("ATMCH-1576")
    @Test
    @DisplayName("Log. Check status after changing status via admin Panel")
    fun logCheckStatusAfterChangingStatusViaAdminPanel() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(false)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                openKycApplicationByEmailAndSetNewStatus(user.email, PASSED, "PASSED")
            }
        }
        step("Admin wait for change status for User check status and change status to APPROVE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    click(applyFiltersButton)
                }
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable(ATM_ADMIN.email, "GREEN AUTO", "PASSED")
            }
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                openKycApplicationByEmailAndSetNewStatus(user.email, CLEAR, "CLEAR")
            }
        }
        step("Admin wait for change status for User check status and change status to APPROVE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    click(applyFiltersButton)
                }
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable("SYSTEM", "NOT STARTED", "")
            }
        }

    }

    @TmsLink("ATMCH-1384")
    @Test
    @DisplayName("Log. Check status after sending invitation with marked checkbox Passed KYC")
    fun logCheckStatusAfterSendingInvitationWithMarkedCheckboxPassedKYC() {
        val (user, _) = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin wait for change status for User check status and change status to APPROVE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(ATM_ADMIN) }) {
                val emailElement = userList.find {
                    it[AtmAdminKycManagementPage.EMAIL]?.text?.contains(user.email) ?: false
                }?.get(AtmAdminKycManagementPage.EMAIL)?.to<Button>(user.email)
                    ?: error("No user with email ${user.email} found in table")
                e {
                    click(emailElement)
                }
                checkDataInLogTable("SYSTEM", "GREEN AUTO", "")
            }
        }

    }


}