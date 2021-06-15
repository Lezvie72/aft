package pages.atm

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/profile/change-password")
class AtmChangePasswordPage(driver: WebDriver) : AtmPage(driver) {

    @Name("Old password")
    @FindBy(xpath = "//nz-form-control//input[@formcontrolname='oldPassword']")
    lateinit var oldPassword: TextInput

    @Name("New password")
    @FindBy(xpath = "//nz-form-control//input[@formcontrolname='password']")
    lateinit var new_password: Button

    @Name("Confirm password")
    @FindBy(xpath = "//nz-form-control//input[@formcontrolname='passwordConfirm']")
    lateinit var password_confirm: Button

    @Name("Confirm")
    @FindBy(xpath = "//button//span[contains(text(),'Confirm')]")
    lateinit var confirm: Button


}