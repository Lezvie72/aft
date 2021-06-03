package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmAdminInvitesPage
import pages.atm.AtmHomePage
import pages.atm.AtmLoginPage
import pages.atm.AtmProfilePage
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("User Profile Page Structure")
class UserProfilePageStructure : BaseTest() {

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

    @Disabled("Подписание участником новой версии Terms and Conditions не входит в объем реализации для сентября 2020")
    @TmsLink("ATMCH-1383")
    @Test
    @DisplayName("Changing user's bank details. Platform.")
    fun lineOfTermsAndConditionsForSigning() {
        val user = newUser()
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, false)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            assert {
                elementWithTextPresented("Terms and Conditions")
                elementWithTextPresented("Sign")

            }
        }
    }

    @TmsLink("ATMCH-1378")
    @Test
    @DisplayName("Account settings. Profile. Interface")
    fun accountSettingsProfileInterface() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            assert {
                elementPresented(startKYC)
                elementContainingTextPresented("E-MAIL")
                elementContainingTextPresented("EMPLOYEE ROLE")
                elementContainingTextPresented("LEGAL ENTITY NAME")
                elementContainingTextPresented("PARTICIPANT TYPE")
                elementContainingTextPresented("PARTICIPANT TYPE")
                elementContainingTextPresented("REGISTRATION COUNTRY")
                elementContainingTextPresented("ADDRESS")
                elementContainingTextPresented("REGISTRATION DATE")
                elementContainingTextPresented("REGISTRATION DOCUMENT NUMBER")
                elementContainingTextPresented("REGISTRATION DOCUMENT DATE")
                elementContainingTextPresented("User")
                elementContainingTextPresented("COMPANY")
                elementContainingTextPresented("STATUS KYC")
                elementContainingTextPresented("None")
                elementContainingTextPresented("SMS")
                elementContainingTextPresented("2FA APP")
                //TODO: displaying is changed
                elementContainingTextPresented("Terms and Conditions")
//                elementContainingTextPresented("Validator agreement")
                elementContainingTextPresented("Risk warnings")
                elementContainingTextPresented("Privacy policy")
                elementContainingTextPresented("Terms of data use policy")
                elementContainingTextPresented("Cookie policy")
            }
        }
    }


}