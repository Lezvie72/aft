package frontend.atm.issuances

import frontend.BaseTest
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.TmsLink
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

@Tags(Tag(TagNames.Epic.ISSUANCE.NUMBER), Tag(TagNames.Flow.MAIN))
@Execution(ExecutionMode.CONCURRENT)
@Epic("Frontend")
@Feature("Issuance")
@Story("Issuer wallet registration")
class IssuerWalletRegistration : BaseTest(){

    @TmsLink("ATMCH-3894")
    @Test
    @DisplayName("Issuer wallet. Registration.")
    fun issuerWalletRegistration() {
        val user = Users.ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA
        val (privateKeyWallet, publicKeyWallet) = openPage<AtmKeysPage>(driver) { submit(user) }.generatePublicAndPrivateKey()
        with(openPage<AtmWalletPage>(driver) { submit(user) }) {
            e{
                click(registerWallet)
                click(issuerWalletRadioButton)
                click(labelWallet)
            }
            assert {
                elementContainsText(labelWallet, "Wallet")
            }
            val walletName = labelWallet.text
            e{
                click(manualRedeemEtc)
                sendKeys(publicKey, publicKeyWallet)
                click(signMessage)
                sendKeys(privateKeyInput, privateKeyWallet)
                click(confirmSignature)
                click(nextButton)
            }
            assert {
                elementContainingTextPresented(walletName)
            }

        }
    }

}