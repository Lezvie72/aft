package pages.htmlelements.blocks.atm.bankaccount

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.FindBy
import pages.atm.AtmBankAccountsPage
import pages.htmlelements.blocks.BaseBlock
import pages.htmlelements.elements.AtmSwitch
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextBlock
import pages.core.actions.ElementActions
import pages.htmlelements.elements.AtmAmount
import utils.helpers.scrollIntoView
import utils.helpers.to

@Name("Bank Account Item")
@FindBy(css = "atm-bank-card")
class BankAccountItem : BaseBlock<AtmBankAccountsPage>() {

    //region ELEMENTS
    @Name("Bic code")
    @FindBy(css = "atm-bank-card div")
    private lateinit var bicCodeLocator: TextBlock

    @Name("Bank name")
    @FindBy(css = "atm-bank-card div:nth-child(2)")
    private lateinit var bankNameLocator: TextBlock

    @Name("Account number")
    @FindBy(css = "div:nth-child(3)")
    private lateinit var accountNumberLocator: TextBlock

    @Name("Set default")
    @FindBy(css = "div:nth-child(4)")
    lateinit var setAsDefaultSwitcher: AtmSwitch

    @Name("Delete button")
    @FindBy(css = "atm-button-svg.bank-card__btn-delete button")
    private lateinit var deleteButtonLocator: Button

    @Name("Delete button in confirm window")
    @FindBy(xpath = "//span[contains(text(), 'Delete')]/ancestor::button")
    private lateinit var confirmDeleteButtonLocator: Button

    @Name("Cancel delete bank details in confirm window")
    @FindBy(xpath = "//span[contains(text(), 'Cancel')]/ancestor::button")
    private lateinit var cancelDeleteButtonLocator: Button
    //endregion

    //region ACTIONS
    val bicCode: String
        get() = bicCodeLocator.text

    val bankName: String
        get() = bankNameLocator.getAttribute("innerHTML")

    val accountNumber: String
        get() = accountNumberLocator.text

    fun deleteWithoutConfirm() {
        e {
            click(deleteButtonLocator)
        }
    }

    fun deleteWithConfirm() {
        val balance = wait {
            untilPresented<WebElement>(By.cssSelector("atm-bank-card:first-child atm-button-svg.bank-card__btn-delete button"))
        }.to<AtmAmount>("Token ' is not presented")
        e {
            deleteButtonLocator.click()
//            click(deleteButtonLocator)
            click(confirmDeleteButtonLocator)
        }
    }

    fun deleteWithCancel() {
        e {
            click(deleteButtonLocator)
            click(cancelDeleteButtonLocator)
        }
    }

    fun select() {
        nonCriticalWait(5L) {
            until("Bank account not found") {
                bicCodeLocator.scrollIntoView(page.driver)
                val action = Actions(page.driver)
                action.moveToElement(bicCodeLocator.wrappedElement).perform()
                check {isElementPresented(deleteButtonLocator, 5L)}
            }
        }
    }
    //endregion

    //region ASSERTS
    fun assertBicCodeIsPresented() {
        assert { elementPresented(bicCodeLocator) }
    }

    fun assertBankNameIsPresented() {
        assert { elementPresented(bankNameLocator) }
    }

    fun assertAccountNumberIsPresented() {
        assert { elementPresented(accountNumberLocator) }
    }

    fun assertSetAdDefaultSwitchIsPresented() {
        assert { elementPresented(setAsDefaultSwitcher) }
    }

    fun assertDeleteButtonIsPresented() {
        assert { elementPresented(deleteButtonLocator) }
    }

    fun assertBankAccountCardWithAllElementsIsPresented() {
        assert {
            elementPresented(bicCodeLocator)
            elementPresented(bankNameLocator)
            elementPresented(accountNumberLocator)
            elementPresented(setAsDefaultSwitcher)
            elementPresented(deleteButtonLocator)
        }
    }
    //endregion

}