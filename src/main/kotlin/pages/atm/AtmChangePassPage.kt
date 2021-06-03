package pages.atm

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.TextInput

@PageUrl("/profile/change-password")
class AtmChangePassPage(driver: WebDriver) : AtmPage(driver) {

    @Name("Old password")
    @FindBy(xpath = "//input[@formcontrolname='oldPassword']")
    lateinit var oldPassword: TextInput

    @Name("New password")
    @FindBy(xpath = "//input[@formcontrolname='password']")
    lateinit var newPassword: TextInput

    @Name("Confirm password")
    @FindBy(xpath = "//input[@formcontrolname='passwordConfirm']")
    lateinit var confirmPassword: TextInput

    @Name("Confirm button")
    @FindBy(xpath = "//button//span[contains(text(),'Confirm')]")
    lateinit var confirm: Button

    @Name("Cancel button")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var cancel: Button


}

