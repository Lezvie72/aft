package pages.htmlelements.blocks.atm.assign

import org.openqa.selenium.By
import org.openqa.selenium.support.FindBy
import pages.atm.AtmPage
import pages.htmlelements.blocks.BaseBlock
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextInput
import utils.isChecked

@Name("Assign Employees Item")
@FindBy(css = "atm-assign-item")
class AssignEmployeesItem : BaseBlock<AtmPage>() {

    private val emailLocator =
        By.xpath(".//span[contains(text(), 'E-mail')]//ancestor::atm-property-value//div[contains(@class, 'property__wrapper-val')]")
    private val roleLocator =
        By.xpath(".//span[contains(text(), 'Role')]//ancestor::atm-property-value//div[contains(@class, 'property__wrapper-val')]")

    @FindBy(css = "[class='status']")
    @Name("Status")
    private lateinit var statusLocator: TextInput

    @Name("Role")
    @FindBy(xpath = "//atm-property-value//span[contains(text(), 'Role')]")
    private lateinit var role: Button

    @Name("Email")
    @FindBy(xpath = "//atm-property-value//span[contains(text(), 'E-mail')]")
    private lateinit var email: Button

    @Name("Controller")
    @FindBy(xpath = ".//span[@class='ant-checkbox-inner']//ancestor::label")
    lateinit var controllerCheckbox: CheckBox

    @Name("Apply")
    @FindBy(xpath = ".//button//span[contains(text(),'Apply')]")
    lateinit var apply: Button

    private fun getEmailMethod(): String {
        return findElement(emailLocator).text
    }

    fun setCheckboxStateAndApply(state: Boolean) {
        if (controllerCheckbox.isChecked() != state) {
            e {
                setCheckbox(controllerCheckbox, state)
                click(apply)
            }
        }
    }

    fun setCheckBox(state: Boolean) {
        e {
            setCheckbox(controllerCheckbox, state)
        }
    }

    val emailName: String
        get() = getEmailMethod()

    private fun getRoleMethod(): String {
        return findElement(roleLocator).text
    }


    val roleName: String
        get() = getRoleMethod()

}