package frontend.e2e

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import pages.atm.*
import pages.atm.AtmAdminKycManagementPage.StatusType.*
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tag(TagNames.Flow.SMOKEE2E)
@Epic("Frontend")
@Feature("E2E")
@Story("KYC")
class SmokeKYCE2E : BaseTest() {

    private fun createAndRegisterUser(kycPassed: Boolean): DefaultUser {
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


    @TmsLink("ATMCH-5275")
    @Test
    @DisplayName("Kyc Status Validating to Decline")
    fun kycValidatingToDecline() {
        val user = step("Create new User") {
            createAndRegisterUser(false)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
        step("Admin wait for change status for User check status and change status to DECLINE") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    clickUntilElementIsPresented(applyFiltersButton, "VALIDATING", 120, 15)
                }
                val status = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", status, Matchers.equalTo("VALIDATING"))

                openKycApplicationByEmailAndSetNewStatus(user.email, DECLINE, "DECLINE")

                val newStatus = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", newStatus, Matchers.equalTo("RED"))

            }
        }
    }

    @TmsLink("ATMCH-5275")
    @Test
    @DisplayName("Kyc Status Validating to Approve")
    fun kycValidatingToApprove() {
        val user = step("Create new User") {
            createAndRegisterUser(false)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
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
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    sendKeys(emailSearch, user.email)
                    clickUntilElementIsPresented(applyFiltersButton, "VALIDATING", 120, 15)
                }
                val status = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", status, Matchers.equalTo("VALIDATING"))

                openKycApplicationByEmailAndSetNewStatus(user.email, APPROVE, "APPROVE")

                val newStatus = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", newStatus, Matchers.equalTo("GREEN"))
            }
        }
    }

    @TmsLink("ATMCH-5275")
    @Test
    @DisplayName("Kyc Status passed change name")
    fun kycPassedTrueSetName() {
        val user = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin change name for user") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                openKycReviewApplicationByEmailAndSetName(user.email, "Ivan", "Ivanov")
                openPage<AtmAdminKycManagementPage>(driver)
                checkName(user.email, "Ivan", "Ivanov")
            }
        }

    }

    @TmsLink("ATMCH-5275")
    @Test
    @DisplayName("Kyc Status False to Approve")
    fun kycFalseToApprove() {
        val user = step("Create new User") {
            createAndRegisterUser(false)
        }
        step("Admin wait for change status for User check status and change status to Approve") {
            with(openPage<AtmAdminKycManagementPage>(driver) { submit(Users.ATM_ADMIN) }) {
                val status = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", status, Matchers.equalTo("NOT STARTED"))

                openKycApplicationByEmailAndSetNewStatus(user.email, PASSED, "PASSED")

                val newStatus = getKycStatusForUserByEmail(user.email)
                assertThat("Wrong status", newStatus, Matchers.equalTo("GREEN AUTO"))
            }
        }
    }

}
