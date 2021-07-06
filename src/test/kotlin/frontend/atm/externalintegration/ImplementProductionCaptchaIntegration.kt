package frontend.atm.externalintegration

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.*
import pages.atm.AtmAdminInvitesPage
import pages.atm.AtmLoginPage
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.EXTERNALINTEGRATION.NUMBER), Tag(TagNames.Flow.MAIN))
@Epic("Frontend")
@Feature("External integration")
@Story("Implement production CAPTCHA integration")
class ImplementProductionCaptchaIntegration : BaseTest() {

    @TmsLink("ATMCH-5200")
    @Test
    @DisplayName("User's registration with CAPTCHA")
    fun userRegistrationWithCaptcha() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                sendInvitation(user.email, true)
            }
            val href = GmailApi.getHrefForNewUserATM(user.email)
            with(openPage<AtmLoginPage>(driver) { submit(user) }) {
                driver.navigate().to(href)
                softAssert {
                    elementPresented(atmUserPasswordForRegistration)
                    elementPresented(atmUserConfirmPassword)
                    elementPresented(atmUserEmailForRegistration)
                    elementPresented(capcha)
                    elementPresented(registerNewAtmUser)
                }
                e {
                    fillRegForm()
                    assert { urlEndsWith("/profile/info") }
                }
            }
        }
    }


    @TmsLink("ATMCH-5202")
    @Test
    @DisplayName("User's registration without CAPTCHA")
    fun userRegistrationWithoutCaptcha() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(sendInviteButton)
                click(cancelButton)
                sendInvitation(user.email, true)
            }
            val href = GmailApi.getHrefForNewUserATM(user.email)
            with(openPage<AtmLoginPage>(driver) { submit(user) }) {
                driver.navigate().to(href)
                softAssert {
                    elementPresented(atmUserPasswordForRegistration)
                    elementPresented(atmUserConfirmPassword)
                    elementPresented(atmUserEmailForRegistration)
                    elementPresented(capcha)
                    elementPresented(registerNewAtmUser)
                }
                e {
                    sendKeys(atmUserPasswordForRegistration, Constants.DEFAULT_PASSWORD)
                    sendKeys(atmUserConfirmPassword, Constants.DEFAULT_PASSWORD)
                    Assertions.assertTrue(
                        registerNewAtmUser.getAttribute("disabled") != null,
                        "Button Register is not disabled"
                    )
                }
            }
        }
    }
}
