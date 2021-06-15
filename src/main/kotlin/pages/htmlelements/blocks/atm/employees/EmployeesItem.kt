package pages.htmlelements.blocks.atm.employees

import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput

@Name("Employees Item")
@FindBy(css = "atm-employee-item")
class EmployeesItem : BaseBlock<AtmPage>() {

    @FindBy(xpath = "//span[contains(@class, 'status')]")
    @Name("Status")
    private lateinit var statusLocator: TextBlock

    @FindBy(xpath = "//div[contains(@class, 'employee-item__caption')]")
    @Name("Type")
    private lateinit var type: TextBlock

    @FindBy(xpath = ".//span[contains(text(), ' USER EMAIL ')]/ancestor::atm-property-value//div[contains(@class, 'val')]")
    @Name("User email")
    private lateinit var userEmail: TextBlock

    val useremail: String
        get() = userEmail.text

//    val bankName: String
//        get() = bankNameLocator.getAttribute("innerHTML")
//
//    val accountNumber: String
//        get() = accountNumberLocator.text

    fun deleteWithoutConfirm() {
        e {

        }
    }


}