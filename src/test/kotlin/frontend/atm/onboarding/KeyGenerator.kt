package frontend.atm.onboarding

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmKeysPage
import pages.atm.AtmProfilePage
import utils.helpers.Users
import utils.helpers.openPage

@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Onboarding")
@Story("Key Generator")
class KeyGenerator : BaseTest() {

    @TmsLink("ATMCH-582")
    @Test
    @DisplayName("Key and Mnemonic phrase with Passphrase generation")
    fun keyAndMnemonicPhraseWithPassphraseGeneration() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(keySignatureGenerator)
            }
        }
        with(AtmKeysPage(driver)) {
            assert {
                urlEndsWith("/profile/keys")
//                elementWithTextPresented("Keys management")
                elementWithTextPresented("Key generator")
                elementWithTextPresented("Enter your mnemonic phrase (12 words) or generate a new one to generate keys for your wallet")
                elementPresented(publicKeyInput)
                elementPresented(privateKeyInput)
                elementPresented(generatePhraseButton)
                elementPresented(mnemmonicPhraseInput)
                elementPresented(publicKeyPaste)
                elementPresented(privateKeyPaste)
                elementPresented(passphraseInput)
                elementPresented(repeatePassphraseInput)
            }
            e {
                sendKeys(mnemmonicPhraseInput, "apple orange")
            }
            assert {
                elementWithTextPresented(" Insufficient entropy in mnemonic, please generate new one ")
            }
            assert { elementWithTextPresented(" Should be exactly 12 words in mnemonic phrase ") }
            e {
                click(generatePhraseButton)
                checkCountWords(mnemmonicPhraseInput)
                sendKeys(passphraseInput, "312dqdddmedei")
            }
            assert {
                elementWithTextPresented(" Please, use at least 8 roman type characters, lower and upper case, and numbers ")
            }
            e {
                sendKeys(passphraseInput, "Qw123tuy")
                click(passphraseEyeButton)
                sendKeys(repeatePassphraseInput, "312dqdddmedei")
            }
            assert {
                elementWithTextPresented(" Passwords do not match ")
            }
            e {
                sendKeys(repeatePassphraseInput, "Qw123tuy")
            }
            assert {
                elementPresented(generateKeysButton)
            }
            e {
                click(generateKeysButton)
            }
            assert {
                elementWithTextPresented("Save your mnemonic phrase")
                elementWithTextPresented(" Please save your mnemonic phrase on paper to be able to recover your keys based on the phrase. ")
                elementPresented(mnemonicPhraseGenField)
                elementPresented(clickToClipboardButton)
                elementPresented(mnemonicPhraseCheckbox)
                elementPresented(cancelButtonFromDialog)
                elementPresented(continueButton)
            }
            e {
                setCheckbox(mnemonicPhraseCheckbox, true)
                click(continueButton)
                checkFieldIsNotEmpty(publicKeyInput)
                checkFieldIsNotEmpty(privateKeyInput)
            }

        }

    }

    @TmsLink("ATMCH-174")
    @Test
    @DisplayName("Key and Mnemonic generation")
    fun keyAndMnemonicGeneration() {
        with(openPage<AtmProfilePage>(driver) { submit(Users.ATM_USER_KYC0) }) {
            e {
                click(keySignatureGenerator)
            }
        }
        with(AtmKeysPage(driver)) {
            assert {
                urlEndsWith("/profile/keys")
//                elementWithTextPresented("Key generator")
                elementWithTextPresented("Key generator")
                elementWithTextPresented("Keys")
                elementWithTextPresented("Enter your mnemonic phrase (12 words) or generate a new one to generate keys for your wallet")
                elementPresented(publicKeyInput)
                elementPresented(privateKeyInput)
                elementPresented(publicKeyPaste)
                elementPresented(privateKeyPaste)
                elementPresented(generatePhraseButton)
                elementPresented(mnemmonicPhraseInput)
                elementPresented(passphraseInput)
                elementPresented(repeatePassphraseInput)
            }
            e {
                sendKeys(mnemmonicPhraseInput, "apple")
            }
            assert {
                elementWithTextPresented(" Insufficient entropy in mnemonic, please generate new one ")
                elementWithTextPresented(" Should be exactly 12 words in mnemonic phrase ")
            }
            e {
                click(generatePhraseButton)
            }
            assert {
                elementPresented(generateKeysButton)
            }
            e {
                click(generateKeysButton)
            }
            assert {
                elementWithTextPresented("Save your mnemonic phrase")
                elementWithTextPresented(" Please save your mnemonic phrase on paper to be able to recover your keys based on the phrase. ")
                elementPresented(mnemonicPhraseGenField)
                elementPresented(clickToClipboardButton)
                elementPresented(mnemonicPhraseCheckbox)
                elementPresented(cancelButtonFromDialog)
                elementPresented(continueButton)
            }
            e {
                setCheckbox(mnemonicPhraseCheckbox, true)
                click(continueButton)
                checkFieldIsNotEmpty(publicKeyInput)
                checkFieldIsNotEmpty(privateKeyInput)
            }

        }

    }


}