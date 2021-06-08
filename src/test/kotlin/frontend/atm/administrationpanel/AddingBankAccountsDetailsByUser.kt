package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import models.user.classes.DefaultUser
import org.apache.commons.lang3.RandomStringUtils.random
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import pages.atm.*
import ru.yandex.qatools.htmlelements.element.Button
import utils.Constants
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage
import utils.helpers.step
import utils.helpers.to

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Adding bank accounts details by user")
class AddingBankAccountsDetailsByUser : BaseTest() {


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

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2263")
    @Test
    @DisplayName("Delete a user's bank details. Cancellation scenario")
    fun deleteUserBankDetailsCancelation() {
        val bankName = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            chooseBankAccountDetails(bankName)
            val deleteCard = wait {
                untilPresented<WebElement>(By.xpath(".//atm-bank-card//div[contains(text(),'${bankName}')]//ancestor::atm-bank-card//atm-button-svg[contains(@class, 'bank-card__btn-delete')]//button"))
            }.to<Button>("Card '$bankName'")
            e {
                click(deleteCard)
            }
            assert {
                elementContainingTextPresented("Delete account?")
                elementContainingTextPresented("You can't undo this action")
                elementPresented(deleteButton)
                elementPresented(cancelDelete)
            }
            e {
                click(cancelDelete)
            }
            chooseBankAccountDetails(bankName)
            assert { elementPresented(deleteCard) }
            postActionDeleteBankAccount(bankName)
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-2256")
    @Test
    @DisplayName("Delete a user's bank details")
    fun deleteUserBankDetails() {
        val bankName = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_TWO) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            chooseBankAccountDetails(bankName)
            val deleteCard = wait {
                untilPresented<WebElement>(By.xpath(".//atm-bank-card//div[contains(text(),'${bankName}')]//ancestor::atm-bank-card//atm-button-svg[contains(@class, 'bank-card__btn-delete')]//button"))
            }.to<Button>("Card '$bankName'")
            e {
                click(deleteCard)
            }
            assert {
                elementContainingTextPresented("Delete account?")
                elementContainingTextPresented("You can't undo this action")
                elementPresented(deleteButton)
                elementPresented(cancelDelete)
            }
            e { click(deleteButton) }
            wait {
                until("dialog for delete users is gone", 20) {
                    check {
                        isElementGone(deleteEmployeeDialog)
                    }
                }
            }
            assertBankAccountDetailsIsNotPresented(bankName)
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_ONE)
    @TmsLink("ATMCH-2446")
    @Test
    @DisplayName("Changing user's bank details. Platform.")
    fun changingUserBankDetailsPlatform() {
        val bankName = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_ONE) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            editBankAccount(bankName)
            chooseBankAccountDetails("Sberich")

            assert {
                elementWithTextPresented("SWIFT")
                elementWithTextPresented("Test")
                elementWithTextPresented("Sberich")
                elementWithTextPresented("Test")
                elementWithTextPresented("TestTestTestTest")
                elementWithTextPresented("USD")
                elementWithTextPresented("Andropova")
            }
            postActionDeleteBankAccount("Sberich")

        }

    }

    @ResourceLock(Constants.ROLE_USER_2FA_OTF_OPERATION_SECOND)
    @TmsLink("ATMCH-2259")
    @Test
    @DisplayName("Checking a user's UI with no Bank details.")
    fun checkingUserUIWithNoBankDetails() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_2FA_OTF_OPERATION_SECOND) }) {
            e {
                click(bankAccount)
            }
        }
        with(AtmBankAccountsPage(driver)) {
            assert {
                elementPresented(addNew)
                elementWithTextPresented(" No bank accounts added yet ")
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2180")
    @Test
    @DisplayName("Check the rights to adding  user's bank details. Role Administrator")
    fun checkTheRightsToAddingUserBankDetailsRoleAdministrator() {
        val bankName1 = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName1,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            chooseBankAccountDetails(bankName1)
            deleteBankAccountDetails(bankName1)
            assertBankAccountDetailsIsNotPresented(bankName1)
            e {
                click(addNew)
            }
            assert { elementPresented(bicType) }
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2181")
    @Test
    @DisplayName("Entering a first user's bank details. Platform.")
    fun enteringFirstUserBankDetailsPlatform() {

        val bankName1 = random(6, true, true)

        prerequisite {
            bankAccountsListShouldBeEmpty(Users.ATM_USER_FOR_EMPLOYEES_KYC1)
        }

        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_EMPLOYEES_KYC1) }) {

            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName1,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            e { click(usdPanel) }
            assertIsDefault(bankName1)
            e { click(usdPanel) }
            chooseBankAccountDetails(bankName1)
            assert {
                elementWithTextPresented("Russian BIC")
                elementWithTextPresented("1223456")
                elementWithTextPresented(bankName1)
                elementWithTextPresented("Test")
                elementWithTextPresented("1234567890123456")
                elementWithTextPresented("USD")
                elementWithTextPresented("Vavilova")
            }
            postActionDeleteBankAccount(bankName1)

        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_ONE)
    @TmsLink("ATMCH-2260")
    @Test
    @DisplayName("Entering a user's bank details. cancellation scenario. Platform")
    fun enteringUserBankDetailsCancellationScenarioPlatform() {
        val bankName1 = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_ONE) }) {
            e {
                click(addNew)
                select(bicType, "Russian BIC")
                sendKeys(bicCode, "1223456")
                sendKeys(bankName, bankName1)
                sendKeys(accountHoldersName, "Test")
                sendKeys(accountNumber, "1234567890123456")
                select(currency, "USD")
                sendKeys(bankAddress, "Vavilova")
                click(cancel)
            }
            assertBankAccountDetailsIsNotPresented(bankName1)
        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_ONE)
    @TmsLink("ATMCH-2257")
    @Test
    @DisplayName("Entering a user's bank details. Set as default")
    fun enteringUserBankDetailsSetAsDefault() {
        val bankName1 = random(6, true, true)
        val bankName2 = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_ONE) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName1,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName2,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            e { click(usdPanel) }
            clickAsDefault(bankName2)
            assertIsDefault(bankName2)
            checkIsNotDefault(bankName1)
            //чтобы не накапливались лишние карточки
            postActionDeleteBankAccounts(listOf(bankName1, bankName2))

        }

    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC_TWO)
    @TmsLink("ATMCH-2262")
    @Test
    @DisplayName("Entering not first user's bank details. Platform.")
    fun enteringNotFirstUserBankDetailsPlatform() {
        val defaultBankName = "TEst"
        step("GIVEN bank account 'TEst' is created and default") {
            with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_TWO) }) {
                if (!isBankAccountDetailsPresented(defaultBankName)) {
                    addBankAccount(
                        "Russian BIC",
                        "1223456",
                        defaultBankName,
                        "Test",
                        "1234567890123456",
                        "USD",
                        "Vavilova"
                    )
                }
                if (check {
                        isElementPresented(usdPanel)
                    }) {
                    e { click(usdPanel) }
                }
                if (!checkIsDefault(defaultBankName)) {
                    clickAsDefault(defaultBankName)
                }
            }
        }
        val bankName = random(6, true, true)
        with(openPage<AtmBankAccountsPage>(driver) { submit(Users.ATM_USER_FOR_BANK_ACC_TWO) }) {
            addBankAccount(
                "Russian BIC",
                "1223456",
                bankName,
                "Test",
                "1234567890123456",
                "USD",
                "Vavilova"
            )
            e { click(usdPanel) }
            assertIsDefault(defaultBankName)
            checkIsNotDefault(bankName)
            postActionDeleteBankAccount(bankName)
        }
    }
}