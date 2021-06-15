package frontend.atm.wallets

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
import org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.Matchers
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import pages.atm.*
import utils.TagNames
import utils.gmail.GmailApi
import utils.helpers.Users
import utils.helpers.openPage

@Tags(Tag(TagNames.Epic.WALLET.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Wallets")
@Story("Wallet/key registration")
class WalletKeyRegistration : BaseTest() {

    @TmsLink("ATMCH-612")
    @Test
    @DisplayName("First step of registration of a public OTF wallet-key. Single key")
    fun firstStepOfRegistrationOfaPublicOTFWalletKeySingleKey() {
        val user = newUser()
        val fullName = "autotestN${RandomStringUtils.randomAlphabetic(8)}"
        val address = "autotest"
        with(openPage<AtmAdminCompaniesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            e { addCompany(fullName, fullName, address, legalCompany = true, issuer = true, validator = true) }
        }
        with(openPage<AtmAdminInvitesPage>(driver) { submit(Users.ATM_ADMIN) }) {
            sendInvitation(user.email, fullName, true)
        }
        openPage<AtmHomePage>(driver)
        val href = GmailApi.getHrefForNewUserATM(user.email)
        driver.navigate().to(href)
        with(AtmLoginPage(driver)) {
            fillRegForm()
        }
        val userWith2FA = with(openPage<AtmProfilePage>(driver) { submit(user) }) {
            switchToGoogleAuth(user)
        }
        val (privateKey, publicKey) = with(openPage<AtmKeysPage>()) {
            e {
                generatePublicAndPrivateKey()
            }
        }
        with(openPage<AtmWalletPage>(driver) { submit(userWith2FA) }) {
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
                elementPresented(this@with.publicKey)
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
            e {
                click(otfWalletRadioButton)
            }
            assert { otfWalletRadioButton.isSelected }
            e {
                sendKeys(this@with.publicKey, publicKey)
                click(signMessage)
                click(privateKeyInput)
                sendKeys(privateKeyInput, privateKey)
                click(confirmSignature)
                click(nextButton)
            }
            assert { elementPresented(otfWalletTicker) }
        }
    }

    @TmsLink("ATMCH-590")
    @Test
    @DisplayName("First step of a public Main wallet-key registration")
    fun firstStepOfPublicMainWalletKeyRegistration() {
        val user = Users.ATM_USER_FOR_REGISTER_WALLET
        val label = randomAlphabetic(8)
        openPage<AtmHomePage>(driver)
        val (privateKey, publickKey) = with(openPage<AtmKeysPage>(driver) { submit(user) }) {
            e {
                generatePublicAndPrivateKey()
            }
        }
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                click(registerWalletButton)
            }
            assert {
                urlEndsWith("/wallets/new")
            }
            val oldMessageToSign = e { messageToSign.getAttribute("value") }
            e {
                click(refreshMessageToSign)
            }
            val newMessageToSign = e { messageToSign.getAttribute("value") }
            assertTrue("Message didn't change after refresh", !oldMessageToSign.equals(newMessageToSign, true))
            e {
                click(mainWalletRadioButton)
                sendKeys(publicKey, publickKey)
                sendKeys(labelWallet, label)
                click(signMessage)
            }
            assert { elementPresented(privateKeyInput) }
            e {
                click(privateKeyInput)
                sendKeys(privateKeyInput, privateKey)
                click(confirmSignature)
                nonCriticalWait {
                    until("", 15L) {
                        check {
                            isElementGone(confirmSignature)
                        }
                    }
                }
                click(nextButton)
            }
            assertTrue(isWalletWithLabelPresented(label), "Wallet with label $label wasn't found")
        }
    }

    @TmsLink("ATMCH-646")
    @Test
    @DisplayName("Field validation of first step of a public  wallet-key registration")
    fun fieldValidationOfFirstStepOfPublicWalletKeyRegistration() {
        val user = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
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
            assertThat(labelWallet.getAttribute("value"), Matchers.equalTo("Test wallet"))
            val oldMessagetoSign = e { messageToSign.getAttribute("value") }
            e {
                click(refreshMessageToSign)
            }
            val newMessageToSign = e { messageToSign.getAttribute("value") }
            assert { !(oldMessagetoSign.equals(newMessageToSign, true)) }
            e {
                sendKeys(publicKey, "1")
            }
            assert { elementContainingTextPresented("Your keys and signature must be generated using Ed25519") }
            e {
                sendKeys(publicKey, user.mainWallet.publicKey)
            }
            assert { elementContainingTextPresented("Your key is not unique") }
        }
    }

    @TmsLink("ATMCH-2592")
    @Test
    @DisplayName("Public wallet-key registration. Wrong signature")
    fun publicWalletKeyRegistrationWrongSignature() {
        val anotherPublicKey = Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET.mainWallet.publicKey
        val user = Users.ATM_USER_FOR_REGISTER_WALLET

        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e {
                click(registerWalletButton)
            }
            e {
                click(mainWalletRadioButton)
                sendKeys(publicKey, anotherPublicKey)
            }
            assert { elementWithTextPresented(" Your key is not unique ") }
        }
    }

    @TmsLink("ATMCH-1076")
    @Test
    @DisplayName("Сheck that can be only one OTF wallet")
    fun checkThatCanBeOnlyOneOTFwallet() {
        with(openPage<AtmWalletPage>(driver) { submit(Users.ATM_USER_2FA_MANUAL_SIG_OTF_WALLET) }) {
            e {
                click(registerWalletButton)
            }
            assert {
                urlEndsWith("/wallets/new")
                elementDisabled(otfWalletRadioButton)
            }
        }
    }
}