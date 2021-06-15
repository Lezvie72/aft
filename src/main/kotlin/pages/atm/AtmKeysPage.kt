package pages.atm

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import pages.core.annotations.Action
import pages.core.annotations.PageName
import pages.core.annotations.PageUrl
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.element.Button
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.TextBlock
import ru.yandex.qatools.htmlelements.element.TextInput
import java.util.*


@PageUrl("/profile/keys")
@PageName("Key & Signature page")
class AtmKeysPage(driver: WebDriver) : AtmPage(driver) {

    @Name("Public key")
    @FindBy(xpath = "//label//span[contains(text(),'Public key')]//ancestor::form//input")
    lateinit var publicKeyInput: TextInput

    @Name("Public key paste from clipboard")
    @FindBy(xpath = "//label//span[contains(text(),'Public key')]//ancestor::form//button")
    lateinit var publicKeyPaste: Button

    @Name("Private key")
    @FindBy(xpath = "//span[contains(text(),'Private key')]//ancestor::nz-form-control//textarea")
    lateinit var privateKeyInput: TextInput

    @Name("Private key paste from clipboard")
    @FindBy(xpath = "//span[contains(text(),'Private key')]//ancestor::nz-form-control//button[contains(text(),' Copy to clipboard ')]")
    lateinit var privateKeyPaste: Button

    @Name("Mnemonic phrase")
    @FindBy(xpath = "//span[contains(text(),'Mnemonic phrase')]//ancestor::nz-form-item//textarea")
    lateinit var mnemmonicPhraseInput: TextInput

    @Name("Generate phrase")
    @FindBy(xpath = "//button//span[contains(text(),'Generate phrase')]")
    lateinit var generatePhraseButton: Button

    @Name("Generate keys")
    @FindBy(xpath = "//button//span[contains(text(),'Generate keys')]")
    lateinit var generateKeysButton: Button

    @Name("Passphrase")
    @FindBy(xpath = "//span[contains(text(),'Passphrase')]//ancestor::nz-form-item//input")
    lateinit var passphraseInput: TextInput

    @Name("Passphrase eye button")
    @FindBy(xpath = "//span[contains(text(),'Passphrase')]//ancestor::nz-form-item//i[1]")
    lateinit var passphraseEyeButton: Button

    @Name("Repeat passphrase")
    @FindBy(xpath = "//span[contains(text(),'Repeat passphrase')]//ancestor::nz-form-item//input")
    lateinit var repeatePassphraseInput: TextInput

    @Name("Passphrase eye button")
    @FindBy(xpath = "//span[contains(text(),'Passphrase (optional)')]//ancestor::nz-form-item//i[1]")
    lateinit var repeatePassphraseEyeButton: Button

    @Name("Mnemonic phrase generate field")
    @FindBy(xpath = "//p[contains(text(),'Please')]//ancestor::atm-save-key-dialog//textarea")
    lateinit var mnemonicPhraseGenField: TextInput

    @Name("Click to clipboard")
    @FindBy(xpath = "//atm-save-key-dialog//button[contains(text(),'Copy to clipboard')]")
    lateinit var clickToClipboardButton: Button

    @Name("I've securely stored my mnemonic phrase")
    @FindBy(xpath = "//atm-save-key-dialog//span[contains(text(),'ve securely stored my mnemonic phrase')]/ancestor::label")
    lateinit var mnemonicPhraseCheckbox: CheckBox

    @Name("Cancel")
    @FindBy(xpath = "//span[contains(text(),'Cancel')]")
    lateinit var cancelButton: Button

    @Name("Cancel")
    @FindBy(xpath = "//button//span[contains(text(),'Cancel')]")
    lateinit var cancelButtonFromDialog: Button

    @Name("Continue")
    @FindBy(xpath = "//button//span[contains(text(),'Continue')]")
    lateinit var continueButton: Button

    @Name("Continue")
    @FindBy(xpath = "//button[@disabled='true']//span[contains(text(),'Continue')]")
    lateinit var disabledContinueButton: Button

    @Name("Public Key")
    @FindBy(xpath = "//label//span[contains(text(),'Public key')]//ancestor::div[1]//input")
    lateinit var publicKey: TextInput

    @Name("Private key eye button")
    @FindBy(xpath = "//span[contains(text(),'Private key')]//ancestor::nz-form-item//i[1]")
    lateinit var privateKeyEyeButton: Button

    @Name("Private key")
    @FindBy(xpath = "//span[contains(text(),'Private key')]/ancestor::nz-form-label/following-sibling::atm-hide-value-input//textarea")
    lateinit var privateKeyTextInput: TextInput

    @Name("App code")
    @FindBy(xpath = "//input[@formcontrolname='appCode']")
    lateinit var appCodeInput: TextInput

    @Name("Code save confirm")
    @FindBy(xpath = "//nz-form-control//span[contains(text(),'Confirm that you have written down or save secret code')]/ancestor::label")
    lateinit var codeSaveConfirmCheckbox: CheckBox

    @Name("Google Oauth link")
    @FindBy(xpath = "//atm-mobile-app//a[contains(text(), 'Google OAuth')]")
    lateinit var googleOauthLink: Button

    @Name("RedHat Free OTP link")
    @FindBy(xpath = "//atm-mobile-app//a[contains(text(), ' RedHat Free OTP ')]")
    lateinit var redHatFreeOtpLink: Button

    @Name("Auth secret code value")
    @FindBy(xpath = "//form/div/span[2]")
    lateinit var authSecretCodeValue: TextBlock


    @Action("check count of words")
    @Step("User checks count of words in field {field}")
    fun checkCountWords(field: WebElement): Int {
        val words = StringTokenizer(field.getAttribute("value")).countTokens()
        assertThat("count", words, equalTo(12))
        return words
    }

    @Action("check field is not empty")
    @Step("User checks field {field} is not empty")
    fun checkFieldIsNotEmpty(field: WebElement) {
        val value = field.getAttribute("value")
        assertThat("value", value, notNullValue())
    }

    @Step("Generate public and private key")
    fun generatePublicAndPrivateKey(): Pair<String, String> {
        return e {
            click(generatePhraseButton)
            click(generateKeysButton)
            setCheckbox(mnemonicPhraseCheckbox, true)
            click(continueButton)
            click(privateKeyEyeButton)
            val privateKeyValue = privateKeyTextInput.getAttribute("value")
            val publicKeyValue = publicKey.getAttribute("value")
            privateKeyValue to publicKeyValue
        }
    }

    @Step("Get auth secret code")
    fun getAuthSecretCode(): String {
        return authSecretCodeValue.text
    }

}