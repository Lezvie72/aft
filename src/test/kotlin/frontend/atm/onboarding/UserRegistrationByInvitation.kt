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
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import pages.atm.AtmProfilePage
import pages.core.actions.AssertActions
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step

@Tags(Tag(TagNames.Epic.ONBOARDING.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("User registration by invitation")
class UserRegistrationByInvitation : BaseTest() {

    @TmsLink("ATMCH-131")
    @Test
    @DisplayName("User registration by invitation (KYC not passed)")
    fun userRegByInvitationKYCNotPassed() {

        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            assert {
                elementPresented(atmUserEmailForRegistration)
                elementPresented(atmUserPassword)
                elementPresented(atmUserConfirmPassword)
                //TODO: there is no check boxes for notifications and accepting terms
//                elementPresented(atmAcceptTerms)
//                elementWithTextPresented("I accept the Terms and Conditions, the Risk Warnings, the Privacy Policy, Terms of Data Use Policy and the Cookie Policy")
//                elementPresented(atmAcceptNews)
//                elementWithTextPresented("I agree to receive email newsletter and the latest news.")
                elementPresented(capcha)
            }
            e {
                sendKeys(atmUserEmailForRegistration, user.email)
                sendKeys(atmUserPassword, user.password)
                sendKeys(atmUserConfirmPassword, user.password)
//                click(atmAcceptTerms)
//                click(atmAcceptNews)
                click(capcha)
            }
            assert {
                elementPresented(registerNewAtmUser)
            }
            e {
                wait {
                    until("Button 'Register' should be enabled") {
                        registerNewAtmUser.getAttribute("disabled") == null
                    }
                }
                click(registerNewAtmUser)
            }
            with(AtmProfilePage(driver)) {
                assert {
                    elementPresented(startKYC)
                }
            }
        }
    }

    @TmsLink("ATMCH-391")
    @Test
    @DisplayName("User registration by invitation with passed KYC")
    fun userRegistrationKYCPassed() {
        val user = newUser()
        step("GIVEN Invitation sent by administrator") {
            with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                sendInvitation(user.email, true)
            }
        }
        step("WHEN user follows link from email") {
            openPage<AtmHomePage>(driver)
            val href = GmailApi.getHrefForNewUserATM(user.email)
            driver.navigate().to(href)
        }
        with(AtmLoginPage(driver)) {
            step("THEN user is redirected to 'New account page'") {
                assert {
                    urlMatches(".*\\/register\\?invite=.*")
                }
            }
            step("AND following elements are displayed") {
                assert {
                    elementPresented(atmUserEmailForRegistration)
                    elementPresented(atmUserPassword)
                    elementPresented(atmUserConfirmPassword)
                    //TODO: there is no check boxes for notifications and accepting terms
//                    elementPresented(atmAcceptTerms)
//                    elementWithTextPresented("I accept the Terms and Conditions, the Risk Warnings, the Privacy Policy, Terms of Data Use Policy and the Cookie Policy")
//                    elementPresented(atmAcceptNews)
//                    elementWithTextPresented("I agree to receive email newsletter and the latest news.")
                    elementPresented(capcha)
                }
            }
            step("WHEN user completes register form") {
                e {
                    sendKeys(atmUserEmailForRegistration, user.email)
                    sendKeys(atmUserPassword, user.password)
                    sendKeys(atmUserConfirmPassword, user.password)
//                    click(atmAcceptTerms)
//                    click(atmAcceptNews)
                    click(capcha)
                }
            }
            step("THEN button 'REGISTER' becomes active") {
                assert {
                    elementPresented(registerNewAtmUser)
                }
                wait {
                    until("Button 'Register' should be enabled") {
                        registerNewAtmUser.getAttribute("disabled") == null
                    }
                }
            }
            step("WHEN user finish registration") {
                e {
                    click(registerNewAtmUser)
                }
            }
            step("THEN user redirected to profile page") {
                assert {
                    urlEndsWith("/profile/info")
                }
            }
            step("AND button 'START KYC' isn't displayed") {
                with(AtmProfilePage(driver)) {
                    assert {
                        elementNotPresented(startKYC)
                    }
                }
            }
        }
    }

    @TmsLink("ATMCH-691")
    @Test
    @DisplayName("Validation field: User registration by invitation")
    fun fieldValidationUserRegistrationByInvite() {
        val user = newUser()
        println(user.email)

        step("GIVEN Invitation sent by administrator") {
            with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                sendInvitation(user.email, false)
            }
        }
        step("WHEN user follows link from email") {
            val href = GmailApi.getHrefForNewUserATM(user.email)

            with(openPage<AtmLoginPage>(driver) { submit(user) }) {
                driver.navigate().to(href)
                assert {
                    validateField(
                        atmUserPasswordForRegistration,
                        AssertActions.PASSWORD_VALIDATION,
                        "Use stronger password"
                    )
                }
                step("Validation of 'Confirm password' field") {
                    e {
                        sendKeys(atmUserPasswordForRegistration, user.password)
                        sendKeys(atmUserConfirmPassword, "123456")
                        assert {
                            elementContainingTextPresented("Passwords do not match")
                        }
                    }
                }
            }
        }
    }
}