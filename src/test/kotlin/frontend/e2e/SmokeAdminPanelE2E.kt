import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.CompanyDetails
import models.user.classes.DefaultUser
import org.apache.commons.lang.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import pages.atm.*
import pages.atm.AtmAdminFiatWithdrawalPage.StatusType.REJECT
import pages.atm.AtmAdminFiatWithdrawalPage.StatusType.WITHDRAWAL
import pages.atm.AtmAdminTokensPage.EquivalentType.FIXED
import pages.atm.AtmAdminTokensPage.EquivalentType.FROM_MARKET
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import java.time.LocalDateTime
import java.time.ZoneOffset

@Tag("SmokeE2E")
@Epic("Frontend")
@Feature("E2E")
@Story("Admin panel")
class SmokeAdminPanelE2E : BaseTest() {

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

    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Bank Details And Companies")
    fun bankDetailsAndCompanies() {
        step("Admin elements on Bank Details Page") {
            with(openPage<AtmAdminBankDetailsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                assert {
                    elementPresented(addNew)
                    elementPresented(editButton)
                    elementPresented(search)

                    elementContainingTextPresented("Bank details")
                    elementContainingTextPresented("Recipient Name")
                    elementContainingTextPresented("Recipient Address")
                    elementContainingTextPresented("Bank Name")
                    elementContainingTextPresented("Bank Address")
                    elementContainingTextPresented("Beneficiary’s Acc. №")
                    elementContainingTextPresented("Correspondent bank")
                    elementContainingTextPresented("Correspondent account №")
                    elementContainingTextPresented("Payment System")
                    elementContainingTextPresented("Active")
                    elementContainingTextPresented("Additional")
                }
            }
        }
        step("Admin elements on Companies Page") {
            with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e { click(addButton) }
                assert {
                    elementPresented(fullNameInput)
                    elementPresented(shortNameInput)
                    elementPresented(addressInput)
                    elementPresented(registrationCountry)
                    elementPresented(registrationDocNum)
                    elementPresented(regDocDate)
                    elementPresented(onboardingDate)
                    elementPresented(legalEntityCheckbox)
                    elementPresented(issuerCheckbox)
                    elementPresented(validatorCheckbox)
                    elementPresented(industrialCheckbox)
                    elementPresented(etcCompanyCheckbox)
                    elementPresented(showOnOtfCheckbox)
                    elementPresented(addCompanyButton)
                }
            }
        }
        step("Admin create new Company ana check new company after create") {
            with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
                val newCompanyDetails = CompanyDetails.generate()
                addCompany(newCompanyDetails, legalCompany = true, issuer = true, validator = true)
                checkCompanyDetails(newCompanyDetails)
            }
        }
    }


    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Register User And Access Right")
    fun registerUserAndAccessRight() {
        val user = step("Create new User") {
            createAndRegisterUser(true)
        }
        step("Admin add new user to Access Right, change role and check the role") {
            with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    click(addUser)
                    sendKeys(emailInput, user.email)
                    click(addButtonDialog)
                    click(navigateLastPage)
                    selectRole(user.email, " SUPPORT ")
                    saveUser(user.email)
                }
                assert {
                    val role = getRoleForUser(user.email)
                    assertThat("", role, Matchers.equalTo("SUPPORT"))
                }
            }
        }
        step("Admin send request for change password for new User and user login with new password") {

            val since = LocalDateTime.now(ZoneOffset.UTC)
            val password = "Aa1!${RandomStringUtils.random(10, true, true)}"

            with(openPage<AtmAdminAccessRightPage>(driver) { submit(Users.ATM_ADMIN) }) {
                e {
                    findUser(user.email)
                    sendNewPassword(user.email)
                }
                assert {
                    elementWithTextPresented("A link was successfully sent to the user’s email")
                }
                logout()
            }
            with(openPage<AtmAdminLoginPage>(driver)) {
                val href = GmailApi.getHrefPassRecoveryUserATM(user.email, since)
                driver.navigate().to(href)
                val sinceRec = LocalDateTime.now(ZoneOffset.UTC)
                enterNewPassword(password)
                openPage<AtmAdminLoginPage>(driver)
                submit(user.email, password)
            }
        }
    }

    //ATMCH-5306
    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Withdrawal In Completed Status 23-24")
    fun withdrawalInCompletedStatus() {
        val amount = "1.${RandomStringUtils.randomNumeric(8)}"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val wallet = user.mainWallet

        step("Admin send fiat to wallet") {
            val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "10")
            }
        }
        step("Admin edit USD equivalent in admin panel") {
            openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.editUsdEquivalent(
                "FIAT",
                FROM_MARKET,
                "0.0001",
                "Ag"
            )
        }
        step("User create withdraw order") {
            with(openPage<AtmWalletPage>(driver) { submit(user) })
            {
                withdrawToken(wallet, "USD", amount, user)
                assert { elementWithTextPresentedIgnoreCase("Your withdrawal request registered successfully") }
            }
        }
        step("Admin change status for withdrawal order and check the status") {
            with(openPage<AtmAdminFiatWithdrawalPage>(driver) { submit(Users.ATM_ADMIN) })
            {
                addStatusToWithdrawalOrder(amount, WITHDRAWAL)
                checkStatusToWithdrawalOrder(amount, "Completed")
            }
        }
        //TODO после фикса бага добавить проверку почты
        step("Admin edit USD equivalent in admin panel back") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                editUsdEquivalent(
                    "FIAT",
                    FIXED,
                    "1",
                    ""
                )
            }
        }

    }

    //ATMCH-5306
    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Withdrawal In Reject Status 25-26")
    fun withdrawalInRejectStatus() {
        val amount = "1.${RandomStringUtils.randomNumeric(8)}"
        val user = Users.ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA
        val wallet = user.mainWallet
        step("Admin send fiat to wallet") {
            val alias = openPage<AtmWalletPage>(driver) { submit(user) }.getAliasForWallet(wallet.name)
            with(openPage<AtmAdminPaymentsPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addPayment(alias, "10")
            }
        }
        step("Admin edit USD equivalent in admin panel") {
            openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }.editUsdEquivalent(
                "FIAT",
                FROM_MARKET,
                "0.0001",
                "Pt"
            )
        }
        step("User create withdraw order") {
            with(openPage<AtmWalletPage>(driver) { submit(user) }) {
                withdrawToken(wallet, "USD", amount, user)
                assert { elementWithTextPresentedIgnoreCase("Your withdrawal request registered successfully") }
            }
        }
        step("Admin change status for withdrawal order and check the status") {
            with(openPage<AtmAdminFiatWithdrawalPage>(driver) { submit(Users.ATM_ADMIN) }) {
                addStatusToWithdrawalOrder(amount, REJECT)
                checkStatusToWithdrawalOrder(amount, "Rejected")
            }
        }
//TODO после фикса бага добавить проверку почты
        step("Admin edit USD equivalent in admin panel back") {
            with(openPage<AtmAdminTokensPage>(driver) { submit(Users.ATM_ADMIN) }) {
                editUsdEquivalent(
                    "FIAT",
                    FIXED,
                    "1",
                    ""
                )
            }
        }
    }

    @TmsLink("ATMCH-5227")
    @Test
    @DisplayName("Change transfer fee distribution 16-18")
    fun feeDistribution() {
        //TODO add test
        //сейчас не работает
    }
}
