package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.Constants
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Change password")
class ChangePassword : BaseTest() {

    @TmsLink("ATMCH-151")
    @Test
    @DisplayName("Change Password")
    fun changePassword() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, true)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            e {
                click(changePassword)
            }
            assert {
                urlEndsWith("/profile/change-password")
            }
            with(openPage<AtmChangePassPage>(driver)) {
                assert {
                    elementPresented(oldPassword)
                    elementPresented(newPassword)
                    elementPresented(confirmPassword)
                    elementPresented(confirm)
                    elementPresented(cancel)
                }
                e {
                    sendKeys(oldPassword, Constants.DEFAULT_PASSWORD)
                    sendKeys(newPassword, Constants.DEFAULT_NEW_PASSWORD)
                    sendKeys(confirmPassword, Constants.DEFAULT_NEW_PASSWORD)
                    click(confirm)
                }
                assert {
                    elementContainingTextPresented("Password has been changed")
                    urlEndsWith("/profile/info")
                }
                val textMessage = GmailApi.getMessagePassChangedUserATM(user.email)
                assert { textMessage.contains("Your password has been successfully changed. If you did not request this change, you should recover access by following the link.") }
            }
        }
    }

    @TmsLink("ATMCH-836")
    @Test
    @DisplayName("Field validation: Change Password")
    fun fieldValidationChangePassword() {
        val password = "Aa1!${RandomStringUtils.random(10, true, true)}"
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_FOR_CHANGE_PASSWORD) }) {
            e {
                click(change_password)
            }
            assert {
                urlEndsWith("/profile/change-password")
            }
            with(AtmChangePasswordPage(driver)) {
                e {
                    sendKeys(oldPassword, password)
                    sendKeys(new_password, "1qaz!QAZ")
                    sendKeys(password_confirm, "1qaz!QAZ")
                    click(confirm)
                }
                assert {
                    elementWithTextPresented(" Wrong password ")
                }
                e {
                    sendKeys(new_password, "1123")
                }
                assert {
                    elementWithTextPresented(" Use stronger password ")
                }
                e {
                    sendKeys(oldPassword, "1qaz!QAZ")
                    sendKeys(new_password, "1qaz!QAZ1")
                    sendKeys(password_confirm, "1qaz!QAZ")
                    click(confirm)
                }
                assert {
                    elementWithTextPresented(" Passwords do not match ")
                }
            }

        }

    }

}