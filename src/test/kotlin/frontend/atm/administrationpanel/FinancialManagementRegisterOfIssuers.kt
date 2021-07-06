package frontend.atm.administrationpanel

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.openqa.selenium.By
import pages.atm.AtmAdminRegisterOfIssuersPage
import utils.Constants
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.ADMINPANEL.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Administration panel")
@Story("Register of issuers")
class FinancialManagementRegisterOfIssuers : BaseTest() {

    @TmsLink("ATMCH-2432")
    @Test
    @DisplayName("Financial management. Register of issuers. UI overview")
    fun uiOverviewValidation() {
        with(openPage<AtmAdminRegisterOfIssuersPage>(driver) { submit(Users.ATM_ADMIN) }) {
            assert {
                elementPresented(edit)
                elementPresented(search)
                elementPresented(issuers)
                elementPresented(tokens)
                elementPresented(updateDateForm)
                elementPresented(updateDateTo)
                elementPresented(createDateFrom)
                elementPresented(createDateTo)
                elementContainingTextPresented("ID")
                elementContainingTextPresented("Issuer name")
                elementContainingTextPresented("Legal entity name")
                elementContainingTextPresented("Description")
                elementContainingTextPresented("Tokens")
                elementContainingTextPresented("Additional info")
                elementContainingTextPresented("Created")
                elementContainingTextPresented("Updated")
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2434")
    @Test
    @DisplayName("Financial management. Register of issuers. Mandatory fields")
    fun mandatoryFieldsValidation() {
        with(openPage<AtmAdminRegisterOfIssuersPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val randomText = RandomStringUtils.random(6, true, false)
            e {
                click(firstRowInGrid)
                click(edit)
            }
            assert {
                elementPresented(nameDialog)
                elementPresented(descriptionDialog)
                elementPresented(additionalInfoDialog)
            }
            e {
                nameDialog.delete()
                sendKeys(nameDialog, randomText)
                descriptionDialog.delete()
                sendKeys(descriptionDialog, randomText)
                additionalInfoDialog.delete()
                sendKeys(additionalInfoDialog, randomText)
                click(confirmDialog)
                val id = checkEditableFields(randomText)
                click(id)
                click(edit)
                nameDialog.delete()
                descriptionDialog.delete()
                additionalInfoDialog.delete()
                click(confirmDialog)
                pressEnter(search)
                sendKeys(search, id)
                wait {
                    until("dialog add bank account is gone", 15) {
                        check {
                            isElementPresented(By.xpath("//td[contains(text(),'$id')]"))
                        }
                    }
                }
                assert {
                    elementContainingTextNotPresented(randomText)
                }
            }
        }
    }

    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2433")
    @Test
    @DisplayName("Financial management. Register of issuers. Editing")
    fun editingFields() {
        with(openPage<AtmAdminRegisterOfIssuersPage>(driver) { submit(Users.ATM_ADMIN) }) {
            val randomText = RandomStringUtils.random(6, true, false)
            e {
                click(firstRowInGrid)
                click(edit)
            }
            assert {
                elementPresented(nameDialog)
                elementPresented(descriptionDialog)
                elementPresented(additionalInfoDialog)
            }
            e {
                nameDialog.delete()
                sendKeys(nameDialog, randomText)
                descriptionDialog.delete()
                sendKeys(descriptionDialog, randomText)
                additionalInfoDialog.delete()
                sendKeys(additionalInfoDialog, randomText)
                click(confirmDialog)
                checkEditableFields(randomText)
            }
        }
    }

    @Disabled("Нет возможности проверить логиб тк нужен доступ к кибане")
    @ResourceLock(Constants.USER_FOR_BANK_ACC)
    @TmsLink("ATMCH-2436")
    @Test
    @DisplayName("Financial management. Register of issuers. Logging")
    fun loggingFields() {
        with(openPage<AtmAdminRegisterOfIssuersPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e {
                click(firstRowInGrid)
                click(edit)
            }
            assert {
                elementPresented(nameDialog)
                elementPresented(descriptionDialog)
                elementPresented(additionalInfoDialog)
            }
            e {
                nameDialog.delete()
                sendKeys(nameDialog, "LoggedName")
                descriptionDialog.delete()
                sendKeys(descriptionDialog, "LoggedDescription")
                additionalInfoDialog.delete()
                sendKeys(additionalInfoDialog, "LoggedDescription")
                click(confirmDialog)
            }
        }
    }

    @Disabled("Поле серч работает через раз в ручном и авто режиме - автотест падает")
    @TmsLink("ATMCH-2439")
    @Test
    @DisplayName("Financial management. Register of issuers. Searching")
    fun searchingValidation() {
        with(openPage<AtmAdminRegisterOfIssuersPage>(driver) { submit(Users.ATM_ADMIN) }) {
//            val randomText = RandomStringUtils.random(30, true, false)
            val firstRowId = e { firstRowInGrid.text }
            e {
                search.delete()
                pressEnter(search)
                sendKeys(search, firstRowId)
            }
            assert {
                elementContainingTextPresented(firstRowId)
            }
            val row = registerOfIssuersTable.table.rows.size.toString()
            assertThat("Expected amount of rows is 1", row, Matchers.hasToString("1"))
            e {
                search.delete()
                pressEnter(search)
            }
            assertThat("Expected amount of rows is 0", row, Matchers.hasToString("0"))
        }
    }
}