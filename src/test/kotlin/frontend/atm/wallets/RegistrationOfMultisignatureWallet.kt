package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.AtmKeysPage
import pages.atm.AtmWalletPage
import utils.TagNames
import utils.helpers.Users
import utils.helpers.openPage
import java.time.LocalDateTime

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Registration of a multi-signature wallet")
class RegistrationOfMultisignatureWallet : BaseTest() {

    @TmsLink("ATMCH-1285")
    @Test
    @DisplayName("Registration of a multi-signature wallet. Interface")
    fun registrationOfMultiSignatureWalletInterface() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET) }) {
            assert {
                elementPresented(registerWalletButton)
            }
            e {
                click(registerWalletButton)
            }
            assert {
                urlEndsWith("/wallets/new")
                elementContainingTextPresented("Select wallet type")
                elementPresented(mainWalletRadioButton)
                elementPresented(otfWalletRadioButton)
                elementPresented(labelWallet)
                elementContainingTextPresented("Authorization type")
                elementContainingTextPresented("Single authorization")
                elementContainingTextPresented("Secret key storage type")
                elementPresented(secretStorageTypeManual)
                elementContainingTextPresented("Enter your public key")
                elementContainingTextPresented("Your keys and signature must be generated using Ed25519")
                elementPresented(publicKey)
                elementPresented(pasteFromClipboard)
                elementPresented(copyToClipboard)
                elementPresented(generateKeys)
                elementContainingTextPresented("You must sign message to sign")
                elementPresented(messageToSign)
                elementPresented(signMessage)
                elementContainingTextPresented("Enter  signature")
                elementPresented(signatureInput)
                elementPresented(registerWallet)
                elementPresented(cancel)
            }

        }
    }

    @TmsLink("ATMCH-1077")
    @Test
    @DisplayName("Registration of a multi-signature wallet")
    fun registrationOfMultiSignatureWallet() {
        val label = "${LocalDateTime.now()}_${randomAlphanumeric(5)}"
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_FOR_REGISTER_WALLET) }) {
            e {
                click(registerWalletButton)
                click(generateKeys)
                click(refreshMessageToSign)
            }
        }
        val walletPage = driver.windowHandle
        for (windows in driver.windowHandles) {
            driver.switchTo().window(windows)
        }
        val (privateKey, publickKey) = with(AtmKeysPage(driver)) {
            e {
                generatePublicAndPrivateKey()
            }
        }
        driver.close()
        driver.switchTo().window(walletPage)
        with(AtmWalletPage(driver)) {
            e {
                click(mainWalletRadioButton)
                sendKeys(labelWallet, label)
                sendKeys(publicKey, publickKey)
                click(signMessage)
                click(privateKeyInput)
                sendKeys(privateKeyInput, privateKey)
                click(confirmSignature)
                click(nextButton)
            }
            assertThat(
                "Wallet with label $label wasn't found",
                isWalletWithLabelPresented(label),
                equalTo(true)
            )
        }
    }

    @TmsLink("ATMCH-1286")
    @Test
    @DisplayName("Registration of a multi-signature wallet. Validation")
    fun registrationOFaMultiSignatureWalletValidation() {
        val anotherUserPublicKey = Users.ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET.mainWallet.publicKey

        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_FOR_REGISTER_WALLET) }) {
            assert {
                elementPresented(registerWalletButton)
            }
            e {
                click(registerWalletButton)
            }
            assert {
                urlEndsWith("/wallets/new")
            }
            e {
                click(mainWalletRadioButton)
                clear(labelWallet)
                sendKeys(labelWallet, "Test wallet")
            }
            assertThat(
                labelWallet.getAttribute("value"),
                equalTo("Test wallet")
            )
            e {
                sendKeys(publicKey, "publickKey")
            }
            assert { elementContainingTextPresented("Your keys and signature must be generated using Ed25519") }
            e { sendKeys(publicKey, anotherUserPublicKey) }
            assert { elementContainingTextPresented("Your key is not unique") }
            val oldMessageToSign = e { messageToSign.getAttribute("value") }
            e {
                click(refreshMessageToSign)
            }
            val newMessageToSign = e { messageToSign.getAttribute("value") }
            assert { !(oldMessageToSign.equals(newMessageToSign, true)) }
            assert {
                elementPresented(disabledNextButton)
            }
        }

    }

}