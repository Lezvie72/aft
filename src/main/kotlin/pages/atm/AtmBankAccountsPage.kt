package pages.atm

import io.qameta.allure.Step
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageUrl
import pages.htmlelements.blocks.atm.bankaccount.BankAccountItem
import pages.htmlelements.elements.AtmSelect
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/profile/bank-accounts")
class AtmBankAccountsPage(driver: WebDriver) : AtmPage(driver) {

    //<editor-fold desc="ELEMENTS">
    @Name("Add new")
    @FindBy(xpath = "//span[contains(text(),'Add new')]")
    lateinit var addNew: Button

    @Name("Bic type")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='bictype']//nz-select")
//    @FindBy(xpath = "//nz-select[@formcontrolname='bictype']")
    lateinit var bicType: AtmSelect

    @Name("Bank name")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='bankname']")
    lateinit var bankName: TextInput

    @Name("Account Holders Name")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='accountholdernumber']")
    lateinit var accountHoldersName: TextInput

    @Name("Account Number")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='accountnumber']")
    lateinit var accountNumber: TextInput

    @Name("Bank address")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='bankaddress']")
    lateinit var bankAddress: TextInput

    @Name("Bic code")
    @FindBy(xpath = "//nz-form-item//input[@formcontrolname='biccode']")
    lateinit var bicCode: TextInput

    @Name("Unit price")
    @FindBy(xpath = "//atm-amount-input[@formcontrolname='price']//input")
    lateinit var unitPrice: TextInput

    @Name("Currency")
    @FindBy(xpath = "//atm-custom-select[@formcontrolname='fiat']//nz-select")
    lateinit var currency: AtmSelect

    @Name("Save")
    @FindBy(xpath = "//span[contains(text(),'Save')]")
    lateinit var save: Button

    @Name("Button save disabled")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(),'Save')]")
    lateinit var disabledSave: Button

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var cancel: Button

    @Name("Edit")
    @FindBy(xpath = "//span[contains(text(),'Edit')]")
    lateinit var edit: Button

    @Name("Delete account")
    @FindBy(xpath = "//atm-bank-card//button[contains(@class, 'del_account')]")
    lateinit var deleteAccount: Button

    @Name("Bank card")
    @FindBy(xpath = "//atm-bank-card")
    lateinit var bankCard: Button

    @Name("Delete button")
    @FindBy(xpath = "//nz-modal-container//button//span[contains(text(), ' Delete ')]")
    lateinit var deleteButton: Button

    @Name("Cancel delete bank details")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var cancelDelete: Button

    @Name("Delete")
    @FindBy(xpath = "//nz-modal-container//div[contains(text(), 'DELETE EMPLOYEE ?')]")
    lateinit var deleteEmployeeDialog: Button

    @Name("USD")
    @FindBy(xpath = "(//atm-collapse-panel//div)[1]")
    lateinit var usdPanel: Button

    @Name("Bank accounts list")
    @FindBy(css = "atm-bank-card")
    lateinit var bankAccountsList: List<BankAccountItem>
    //</editor-fold>

    //<editor-fold desc="ACTIONS">
    @Step("User add bank account")
    @Action("User add bank account")
    fun addBankAccount(
        bicTypeValue: String,
        bicCodeValue: String,
        bankNameValue: String,
        accountHolder: String,
        accountNumberValue: String,
        currencyValue: String,
        bankAddressValue: String
    ) {
        e {
            click(addNew)
            select(bicType, bicTypeValue)
            sendKeys(bicCode, bicCodeValue)
            sendKeys(bankName, bankNameValue)
            sendKeys(accountHoldersName, accountHolder)
            sendKeys(accountNumber, accountNumberValue)
            select(currency, currencyValue)
            sendKeys(bankAddress, bankAddressValue)
            click(save)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(save)
                }
            }
        }
    }

    @Step("Edit {bankNam} account")
    fun editBankAccount(bankNam: String) {
        chooseBankAccountDetails(bankNam)
        e {
            click(edit)
            select(bicType, "SWIFT")
            sendKeys(bicCode, "Test")
            sendKeys(bankName, "Sberich")
            sendKeys(accountHoldersName, "Test")
            sendKeys(accountNumber, "TestTestTestTest")
            select(currency, "USD")
            sendKeys(bankAddress, "Andropova")
            click(save)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(save)
                }
            }
        }

    }

    @Step("Edit {bankNam} account")
    fun editBankAccount(
        bankNam: String,
        bicTypeValue: String,
        bicCodeValue: String,
        bankNameValue: String,
        accountHolder: String,
        accountNumberValue: String,
        currencyValue: String,
        bankAddressValue: String
    ) {
        chooseBankAccountDetails(bankNam)
        e {
            click(edit)
            select(bicType, bicTypeValue)
            sendKeys(bicCode, bicCodeValue)
            sendKeys(bankName, bankNameValue)
            sendKeys(accountHoldersName, accountHolder)
            sendKeys(accountNumber, accountNumberValue)
            select(currency, currencyValue)
            sendKeys(bankAddress, bankAddressValue)
            click(save)
        }
        wait {
            until("dialog add bank account is gone", 15) {
                check {
                    isElementGone(save)
                }
            }
        }

    }

    fun checkBankAccountDetails() {
        //TODO сделать реализацию листа, после реализации ветки ParametirizedBlocks
    }

    @Step("Choose bank account {bankName}")
    fun chooseBankAccountDetails(bankName: String) {
        if (check {
                isElementPresented(usdPanel)
            }) {
            e { setStateForCollapsePanel(usdPanel, true) }
        }
        val card = bankAccountsList.find { it.bankName.contains(bankName) } ?: error("Bank account $bankName not found")
        e {
            click(card)
        }
    }

    @Step("Delete bank account {bankName}")
    fun deleteBankAccountDetails(bankName: String) {
        val card = bankAccountsList.find { it.bankName.contains(bankName) } ?: error("Bank account $bankName not found")
        e {
            card.deleteWithConfirm()
        }
        wait {
            until("dialog for delete users is gone", 20) {
                check {
                    isElementGone(deleteEmployeeDialog)
                }
            }
        }
    }

    @Step("Set {bankName} default")
    fun clickAsDefault(bankName: String) {
        val card = bankAccountsList.find { it.bankName.contains(bankName) } ?: error("Bank account $bankName not found")
        e {
            card.setAsDefaultSwitcher.switch()
        }
    }

    @Step("Check: is {bankName} default")
    fun checkIsDefault(bankName: String): Boolean {
        val card = bankAccountsList.find { it.bankName.contains(bankName) } ?: error("Bank account $bankName not found")
        return card.setAsDefaultSwitcher.switchState
    }

    @Step("Assertion: {bankName} is default")
    fun assertIsDefault(bankName: String) {
        assertTrue("Bank card with '$bankName' should be default", checkIsDefault(bankName))
    }

    @Step("Assertion: {bankName} is not default")
    fun checkIsNotDefault(bankName: String) {
        assertFalse("Bank card with '$bankName' shouldn't be default", checkIsDefault(bankName))
    }

    @Step("Assertion: {bankName} is presented")
    fun isBankAccountDetailsPresented(bankName: String): Boolean {
        return bankAccountsList.contains(bankAccountsList.find { it.bankName.contains(bankName) })

    }

    @Step("Assertion: {bankName} is not presented")
    fun assertBankAccountDetailsIsNotPresented(bankName: String) {
        assertFalse("Bank card with '$bankName' should not be presented", isBankAccountDetailsPresented(bankName))
    }

    @Step("Post test cleaning")
    fun postActionDeleteBankAccounts(accounts: List<String>) {
        accounts.forEach { e ->
            postActionDeleteBankAccount(e)
        }
    }

    @Step("Post test cleaning")
    fun postActionDeleteBankAccount(bankName: String) {
        try {
            chooseBankAccountDetails(bankName)
            deleteBankAccountDetails(bankName)
        } catch (e: Exception) {
            print("Card with '${bankName}' not found")
            false
        }
    }

    @Step("set state for collapse panel")
    fun setStateForCollapsePanel(e: WebElement, state: Boolean) {
        if (e.getAttribute("aria-expanded") != state.toString()) {
            e {
                until("Couldn't set $e to $state") {
                    click(e)
                }
            }
        }
    }
    //</editor-fold>

}