package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock

@PageUrl("/home")
@PageName("Admin platform page")
class AtmPlatformAdminPage(driver: WebDriver) : AtmAdminPage(driver)  {

    @Name("Invite tab")
    @FindBy(xpath = "//span[contains(text(), 'Invites')]")
    lateinit var inviteTab: Button

    @Name("Send invite button")
    @FindBy(xpath = "//span[contains(text(), 'Send invite')]")
    lateinit var sendInviteBtn: Button

    @Name("Send invite window")
    @FindBy(xpath = "//h1[contains(text(), 'Send invite')]")
    lateinit var sendInviteWindow: TextBlock

    @Name("Employees approval tab")
    @FindBy(xpath = "//span[contains(text(), 'Employees approval')]")
    lateinit var employeesApprovalTab: Button

    @Name("Employees approval input")
    @FindBy(xpath = "(//div[contains(@class,'mat-form-field-wrapper')]//mat-icon[contains(@role, 'img')])[1]")
    lateinit var employeesApprovalInput: TextBlock

    @Name("Employees approval calendar")
    @FindBy(xpath = "//mat-calendar")
    lateinit var employeesApprovalCalendar: Button

    @Name("Companies tab")
    @FindBy(xpath = "//span[contains(text(), 'Companies')]")
    lateinit var companiesTab: Button

    @Name("Add button")
    @FindBy(xpath = "//span[contains(text(), 'ADD')]")
    lateinit var addBtn: Button

    @Name("Add company window")
    @FindBy(xpath = "//h1[contains(text(), 'Add Company')]")
    lateinit var addCompanyWindow: TextBlock

    @Name("Access right tab")
    @FindBy(xpath = "//span[contains(text(), 'Access right')]")
    lateinit var accessRightTab: Button

    @Name("Add user button")
    @FindBy(xpath = "//span[contains(text(), 'Add user')]")
    lateinit var addUserBtn: Button

    @Name("Add user window")
    @FindBy(xpath = "//h1[contains(text(), 'Add new user')]")
    lateinit var addUserWindow: TextBlock

    @Name("User management tab")
    @FindBy(xpath = "//span[contains(text(), 'User management')]")
    lateinit var userManagementTab: Button

    @Name("User list row")
    @FindBy(xpath = "//tbody//td[1]")
    lateinit var userListRow: Button

    @Name("Edit button")
    @FindBy(xpath = "//span[contains(text(), 'EDIT')]")
    lateinit var editBtn: Button

    @Name("Edit user window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit user info')]")
    lateinit var editUserWindow: TextBlock

    @Name("Nodes management tab")
    @FindBy(xpath = "//span[contains(text(), 'Nodes management')]")
    lateinit var nodesManagementTab: Button

    @Name("Edit node window")
    @FindBy(xpath = "//h1[contains(text(), 'Edit node')]")
    lateinit var editNodeWindow: TextBlock

    @Name("General settings tab")
    @FindBy(xpath = "//span[contains(text(), 'General settings')]")
    lateinit var generalSettingsTab: Button

    @Name("Streaming settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Streaming settings')]")
    lateinit var streamingSettingsTab: Button

    @Name("RFQ settings tab")
    @FindBy(xpath = "//span[contains(text(), 'RFQ settings')]")
    lateinit var rFQSettingsTab: Button

    @Name("Blocktrade settings tab")
    @FindBy(xpath = "//span[contains(text(), 'Blocktrade settings')]")
    lateinit var blocktradeSettingsTab: Button

    @Name("TVE settings tab")
    @FindBy(xpath = "//span[contains(text(), 'TVE settings')]")
    lateinit var tVESettingsTab: Button

    @Step("Checking every links works and opens in edit mode for platform admin")
    fun allTabsAreDisplayedAndOpensInEditModeForPlatformAdmin() {
        val settings = mapOf(
            "Invite" to arrayOf(inviteTab, sendInviteBtn, sendInviteWindow),
            "Employees Approval" to arrayOf(employeesApprovalTab, employeesApprovalInput, employeesApprovalCalendar),
            "Companies" to arrayOf(companiesTab, addBtn, addCompanyWindow),
            "Access right" to arrayOf(accessRightTab, addUserBtn, addUserWindow),
            "User Management" to arrayOf(userManagementTab, editBtn, editUserWindow, userListRow),
            "Nodes Management" to arrayOf(nodesManagementTab, editBtn, editNodeWindow, userListRow)
        )
        for ((key, value) in settings) {
            if ((value[0] == userManagementTab)&&(value[0] == nodesManagementTab)) {
                e {
                    click(value[0] as WebElement)
                    click(value[1] as WebElement)
                    click(value[3] as WebElement)
                }
            } else {
                e {
                    click(value[0] as WebElement)
                    click(value[1] as WebElement)
                }
            }
            check {
                isElementPresented(value[2] as WebElement)
            }
            driver.navigate().refresh()
            Thread.sleep(2000)
            e {
                click(inviteTab)
            }
        }
    }

    @Step("User with PLATFORM ADMIN ROLE checks for other sections are not displayed")
    fun platformAdminChecksThatTheLinksForOtherSectionsAreNotDisplayed() {
        val settings = mapOf(
            "General settings" to arrayOf(generalSettingsTab),
            "Streaming settings" to arrayOf(streamingSettingsTab),
            "RFQ settings" to arrayOf(rFQSettingsTab),
            "Blocktrade settings" to arrayOf(blocktradeSettingsTab),
            "TVE settings" to arrayOf(tVESettingsTab)
        )
        for ((key, value) in settings) {
            assert { elementNotPresentedWithCustomTimeout(value[0] as WebElement, 1) }
        }
    }

}
