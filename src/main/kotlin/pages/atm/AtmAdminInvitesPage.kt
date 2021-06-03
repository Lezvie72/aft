package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasToString
import org.hamcrest.Matchers.notNullValue
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.elements.AtmAdminSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.Environment

@PageUrl("/invites")
class AtmAdminInvitesPage(driver: WebDriver) : AtmAdminPage(driver) {

    companion object Headers {
        const val EMAIL = "e-Mail"
        const val KEY = "Key"
        const val KYC = "Kyc"
    }

    @Name("Menu button")
    @FindBy(xpath = "//button[.//mat-icon[text()='menu']]")
    lateinit var menuButton: Button

    @Name("Email")
    @FindBy(xpath = "//input[@formcontrolname='email']")
    lateinit var emailForInvitation: TextInput

    @Name("Company Id")
    @FindBy(xpath = "//div//input[@formcontrolname='companyName']")
    lateinit var companyId: AtmAdminSelect

    @Name("KYC checkbox")
    @FindBy(xpath = "//span[contains(text(), 'KYC passed')]/ancestor::label")
    lateinit var kycPassedCheckbox: CheckBox

    @Name("Send invite")
    @FindBy(xpath = "//span[contains(text(),' Send invite ')]")
    lateinit var sendInviteButton: Button

    @Name("Send")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),' SEND ')]")
    lateinit var sendButton: Button

    @Name("Cancel")
    @FindBy(xpath = "//mat-dialog-actions//span[contains(text(),'CANCEL')]")
    lateinit var cancelButton: Button

    @Name("Delete invite")
    @FindBy(xpath = "//mat-icon[contains(text(),'delete')]")
    lateinit var deleteInvite: Button

    @Name("Invite table")
    @FindBy(xpath = "//div[@class='mat-elevation-z1']")
    lateinit var inviteTable: SdexTable

    @Name("Search")
    @FindBy(xpath = "//mat-form-field//input")
    lateinit var search: TextInput

    @Name("Active invites tab")
    @FindBy(css = "a[href='/invites']")
    lateinit var invitesTab: Button

    @Name("Active invites tab")
    @FindBy(css = "a[href='/invites'].active")
    lateinit var activeInvitesTab: Button

    @Name("Preloader")
    @FindBy(css = "sdex-invites > sdex-preloader")
    lateinit var preloader: Button

    init {
        if (!check {
                isElementPresented(sendInviteButton)
            }) {
            wait {
                until("Couldn't open menu in 60 seconds", 60L) {
                    e {
                        click(menuButton)
                    }
                    Thread.sleep(2_000)
                    e {
                        click(invitesTab)
                    }
                    wait(15L) {
                        untilPresented(sendInviteButton)
                    }
                }
            }
        }
        wait(15L) {
            untilInvisibility(preloader)
        }
    }

    @Step("Send invitation")
    @Action("Send invitation")
    fun sendInvitation(email: String, kyc: Boolean): AtmAdminPage {
        val companyName = Environment.organizationForRegistration
        e {
            click(sendInviteButton)
            sendKeys(emailForInvitation, email)
            companyId.sendAndSelect(companyName, companyName, this@AtmAdminInvitesPage)
            setCheckbox(kycPassedCheckbox, kyc)
            click(sendButton)
        }
        return this
    }

    fun sendInvitation(email: String, company: String, kyc: Boolean): AtmAdminPage {
        e {
            click(sendInviteButton)
            sendKeys(emailForInvitation, email)
            companyId.sendAndSelect(company, company, this@AtmAdminInvitesPage)//нестабильно работал
            setCheckbox(kycPassedCheckbox, kyc)
            click(sendButton)
        }
        return this
    }

    @Action("check email")
    @Step("User checks email {email} in invite table")
    fun checkEmail(email: String): String {
        e {
            sendKeys(search, email)
            pressEnter(search)
        }
        val row = inviteTable.find {
            it[EMAIL]?.text == email
        } ?: error("Can't find row with email '$email'")
        assertThat("No row found with email '$email'", row, notNullValue())
        return row[EMAIL]?.text ?: ""
    }

    @Action("check email delete")
    @Step("User checks email {email} in invite table")
    fun checkEmailDelete(email: String): String {
        e {
            sendKeys(search, email)
            pressEnter(search)
        }
        val row = inviteTable.table.rows.size.toString()
        assertThat("No row found with email '$email'", row, hasToString("0"))
        return row
    }
//test
}