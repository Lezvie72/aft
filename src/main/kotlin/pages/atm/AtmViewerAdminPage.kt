package pages.atm

import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.htmlelements.elements.AtmSelect
import pages.htmlelements.elements.SdexTable
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

class AtmViewerAdminPage(driver: WebDriver): AtmAdminPage(driver) {

    @Name("Invite tab")
    @FindBy(xpath = "//span[contains(text(), 'Invites')]")
    lateinit var inviteTab: Button

    @Name("Employees approval tab")
    @FindBy(xpath = "//span[contains(text(), 'Employees approval')]")
    lateinit var employeesApprovalTab: Button

    @Name("Payments tab")
    @FindBy(xpath = "//span[contains(text(), 'Payments')]")
    lateinit var paymentsTab: Button

    @Name("Fiat withdraw tab")
    @FindBy(xpath = "//span[contains(text(), 'Fiat withdraw')]")
    lateinit var fiatWithdrawTab: Button

    @Name("Companies tab")
    @FindBy(xpath = "//span[contains(text(), 'Companies')]")
    lateinit var companiesTab: Button

    @Name("Bank details tab")
    @FindBy(xpath = "//span[contains(text(), 'Bank details')]")
    lateinit var bankDetailsTab: Button

    @Name("Tokens tab")
    @FindBy(xpath = "//span[contains(text(), 'Tokens')]")
    lateinit var tokensTab: Button

    @Name("Register of issuers tab")
    @FindBy(xpath = "//span[contains(text(), 'Register of issuers')]")
    lateinit var registerOfIssuersTab: Button

    @Name("Employee table")
    @FindBy(css = "sdex-employees")
    lateinit var inviteTable: SdexTable

    @Name("Reject invite")
    @FindBy(xpath = "//span[text()=' REJECT ']/ancestor::button")
    lateinit var rejectInvite: TextInput

    @Name("Accept invite")
    @FindBy(xpath = "//span[text()=' APPROVE ']/ancestor::button")
    lateinit var acceptInvite: TextInput

    @Name("Cancel dialog with invite request")
    @FindBy(xpath = "//span[text()=' CANCEL ']/ancestor::button")
    lateinit var cancel: TextInput

    @Name("Search by email input")
    @FindBy(css = "input[formcontrolname='searchString']")
    lateinit var filterSearchByEmailInput: TextInput

    @Name("Update Date From")
    @FindBy(xpath = "//input[@formcontrolname='updateDateFrom']")
    lateinit var updateDateFrom: TextInput

    @Name("Update Date To")
    @FindBy(xpath = "//input[@formcontrolname='updateDateTo']")
    lateinit var updateDateTo: TextInput

    @Name("Request status dropdown")
    @FindBy(xpath = "//mat-select[@formcontrolname='type']")
    lateinit var requestStatusSelect: AtmSelect

    @Step("Check all tabs are changed for admin with viewer role")
    fun checkAllTabsAreChangedForAdminWithViewerRole() {
        e {
            click(inviteTab)
        }
        assert {
            elementWithTextPresented(" Invites ")
        }
        e {
            click(employeesApprovalTab)
        }
        assert {
            elementWithTextPresented(" Employees approval ")
        }
        e {
            click(paymentsTab)
        }
        assert {
            elementWithTextPresented(" Payments ")
        }
        e {
            click(fiatWithdrawTab)
        }
        assert {
            elementWithTextPresented(" Fiat withdraw ")
        }
        e {
            click(companiesTab)
        }
        assert {
            elementWithTextPresented(" Companies ")
        }
        e {
            click(bankDetailsTab)
        }
        assert {
            elementWithTextPresented(" Bank details ")
        }
        e {
            click(tokensTab)
        }
        assert {
            driver.findElement(By.xpath("//label/mat-label[contains(text(), 'Update date from')]"))
        }
        assert {
            (driver.findElement(By.xpath("//mat-icon/following-sibling::text()")).text.contains(" Tokens "))
        }
        e {
            click(registerOfIssuersTab)
        }
//        assert {
//            elementWithTextPresented(" Register of issuers ")
//        }
        assert {
            driver.findElement(By.xpath("//label/mat-label[contains(text(), 'Issuers')]"))
        }
    }
}